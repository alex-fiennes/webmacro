/*
 * @(#)ServerSideConnector.java	1.0 98/10/01
 * Copyright (c) 1998 Open Doors Software, Inc. All Rights Reserved.
 * 
 * Open Doors Software MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. OPEN DOORS SOFTWARE SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 */
package org.opendoors.instant.lcis.kernel;

import java.net.*;
import java.io.*;
import java.util.*;

import org.opendoors.util.*;
import org.opendoors.instant.lcis.ObjectListener;
import org.opendoors.instant.lcis.Connection;
import org.opendoors.instant.lcis.server.LCIServer;
import org.opendoors.instant.lcis.server.ServerServices;

/**
 * The ServerSideConnector creates the LCIS Socket service on the server side.
 * <p>
 * The ServerSideConnector is a multi-threaded service. Each new client is assigned
 * their own socket service, a ServerSideClientConnection.
 * <p>
 * Thus, a separate connection thread is satisfied for each connection request.
 * <p>
 * The ServerSideConnector keeps a list of the connected clients.
 * This makes it possible, among other things, to provide broadcasting services
 * to all connected clients.
 * @see ServerSideClientConnection
 */
public class ServerSideConnector extends Thread implements ServerServices {

	/**
	 * The default timeout of 10 seconds (in millis) to complete an atomic transfer.
	 */
	public static int TRANSFER_TIMEOUT = 10000;


	LCIServer server;
	ObjectListener serverListener;
	Vector clients;
	ServerSocket serverSocket;
	boolean continueAccepting;
	int port = Connection.DEFAULT_LCIS_PORT;
	
	/** 
	 * Constructs the connector using the current default port.
	 * @param The object implementing the server services.
	 * @param The listener for the server.
	 */
	public ServerSideConnector(LCIServer server, ObjectListener listener) {
		init(server, Connection.DEFAULT_LCIS_PORT, listener);
	}

	/** 
	 * Constructs the connector using the port supplied.
	 * @param The object implementing the server services.
	 */
	public ServerSideConnector(LCIServer server, int port, ObjectListener listener) {
		init(server, port, listener);
	}

	/**
	 * Initializes the port on behalf of the constructor.
	 */
	private void init(LCIServer server, int port, ObjectListener listener) {

		this.server = server;
		this.serverListener = listener;
		this.port = port;
		continueAccepting = false;
		clients = new Vector();
		try {
	  		serverSocket = new ServerSocket(port);
	  		// without timeout accept will block forever and server will never exit.
	  		serverSocket.setSoTimeout(TRANSFER_TIMEOUT);
	  		continueAccepting = true;
		} 
		catch (Exception e) {
	  		Console.error("ServerSideConnector. Unable to open ServerSocket on port " + port, null, e);
		}
	}

	/**
	 * Sets the listener for every subsequent connection on the server side.
	 */
	public void setListener(ObjectListener serverListener) {
		this.serverListener = serverListener;
	}

	/**
	 * Informs the server connector that to stop listening to incoming connection requests.
	 */
	public void stopListening() {
		continueAccepting = false;
	}

	/**
	 * States whether the server is actively listening for connection requests.
	 * @return True if the server is listening at this time.
	 */
	public boolean isListening() {
		return continueAccepting;
	}

	/**
	 * Runs the server thread to listen to listen to connection requests.
	 */
	public void run() {
		accept();
	}

	/**
	 * Waits for a connection request. Upon a connection request:
	 * <ul>
	 * <li>It creates a ServerSideClientConnection object.
	 * <li>Starts this object in its own separate thread space.
	 * <li>Assigns the requesting client an ID
	 * <li>Adds the client to the collection of connected clients.
	 * <li>Notifies the server via a call back of the new client added (ServerSideClientConnection).
	 * </li>
	 * <p>
	 * The method returns as soon as the state variable, continueAccepting, 
	 * goes false.
	 * @see LCIServer
	 * @see LCIServer#addClient
	 * @see ServerSideConnector#stopReceiving
 	 */
	public void accept() {
		while (continueAccepting == true) {
	  		try {
				Socket clientSocket = serverSocket.accept();
				ServerSideClientConnection client = new ServerSideClientConnection(this, serverListener, clientSocket);
				clients.addElement(client);
				client.start();  // spawn thread to listen to client's socket.
				server.addClient(client);
	  		} 
			catch (InterruptedIOException e) {
				// This means we've timed out without connecting.
				// we'll loop back for more.
	  		} 
			catch (Exception e) {
				Console.error("ServerSideConnector: Unable to accept client socket.", serverSocket, e);
	  		}
		}
		
		// if we're here, continueAccepting is false, lets clean up.
		try {
			disconnectAllClients(); // disconnect all clients.
	  		serverSocket.close();
		} 
		catch (Exception e) {
	  		Console.error("ServerSideConnector: trouble closing socket. ", serverSocket,  e);
		}
		clients = null;
		serverSocket = null;
	}

	/** 
	 * Broadcast to all currently connected clients an object.
	 * @param The object to send.
 	 */
	public void sendToAllClients(Object object) throws Exception {
		Enumeration elements = clients.elements();
		while (elements.hasMoreElements()) {
	  		ServerSideClientConnection cc = (ServerSideClientConnection) elements.nextElement();
	  		cc.sendObject(object);
		}
	}

	/** 
	 * Disconnects all clients from server.
	 * Use this call to permit a normal server shutdown.
	 * Each individual connection is given a chance to shut itself
	 * down normally.
	 */
	public void disconnectAllClients() {
		Enumeration elements = clients.elements();
		while (elements.hasMoreElements()) {
	  		ServerSideClientConnection cc = (ServerSideClientConnection) elements.nextElement();
	  		cc.shutDown();
		}
	}

	/**
	 * Gets a count of the connected clients.
	 * @return The number of clients currently connected.
	 */
	public int getClientConnectionCount() {
		return clients.size();
	}

	/**
	 * A sequence of the clients currently connected.
	 * @return The enumeration of currently connected clients (ServerSideClientConnection).
	 */
	public Enumeration getClients() {
		return clients.elements();
	}

	/**
	 * Removes the client from the list of currently connected clients,
	 * and notifies the server that a specific client has been deactivated.
	 * @param client the client which is no longer connected.
	 */
	public void removeClient(ServerSideClientConnection client) {
		clients.removeElement(client);
		server.removeClient(client);
	}

	/**
	 * Get the port for the current connection.
	 * @return the port used for establishing connections
	 */
	public int getPort() {
		return port;
	}

}
