/*
 * @(#)ClientServer.java	1.0 98/10/01
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

import java.io.*;
import java.net.*;
import org.opendoors.instant.lcis.*;
import org.opendoors.util.Console;


/**
 * This class is the base class for all socket connections.
 * <p>
 * It provides the send/receive semantics. It always has a peer on
 * the other side of the wire.
 * <p>
 * Methods are general and symmetric for client and server alike.
 * This class handles all of the basic plumbing for moving
 * objects over the wire and for dispatching to the appropriate delegate.
 * <p>
 * As a connection object on either the server or the client,
 * it is a "connection-oriented" object in that it has also
 * a live connection on the other end of the wire. That is its peer.
 * <p>
 * Lastly, if the connection dies or is shutdown, its delegated listener is informed of its demise.
 */
public abstract class ClientServerConnection extends Thread implements Connection {

	/**
	 * The input side of the stream.
	 */
	ObjectInputStream input;

	/**
	 * The output side of the stream.
	 */
	ObjectOutputStream output;

	/**
	 * Are we connected?
	 */
	boolean connected = false;

	/**
	 * Shutdown signal.
	 */
	boolean shutDown;

	/**
	 * The listener for objects received.
	 */
	ObjectListener listener;

	/**
	 * The socket for the connection.
	 */
	Socket socket;

	/**
	 * The port the server is listening on or the port
	 * the client connected to on the server.
	 */
	int port;
	

	/** 
	 * Default constructor a connection.
	 */
	public ClientServerConnection() {	}		

	/**
	 * Starts the connection thread as a daemon thread.
	 */
	public void start() {
		System.out.println("Connection Starting=" + this);
		setDaemon(true);
		super.start();
	}

	/**
	 * On a new connection set the the socket
 	 * and initialize the streams.
	 */
	synchronized void initSocket(Socket socket) {
		connected = false;
		shutDown = false;
		try {
		  	// open output streams first, input streams block to read header
			OutputStream outputStream = socket.getOutputStream();
			output = new ObjectOutputStream(outputStream);
			// output.flush();
			InputStream inputStream = socket.getInputStream();
			input = new ObjectInputStream (inputStream);
			socket.setTcpNoDelay(true);
			connected = true;
			
		} 
		catch (Exception e) {
		  Console.error("ClientServerConnection. Unable to open IO streams", socket, e);
		}
	}

	/**
	 * Starts the connection thread which basically waits to read an object.
	 * @see ClientServerConnection#shutDown
	 */
	public void run() {
		while (!shutDown) {
			getNextObject();
		}
	}

	/** 
	 * Reads an object from the inputstream and dispatches it to the
	 * listener.
	 * @see ObjectListener
	 */
	private void getNextObject() {
		if ( !isShutDown() ) {
	  		try {
				Object object = input.readObject();
				if (isConnected() && listener != null) {  
					// connection is in place.
	  				listener.objectReceived(object, this);
				}
	  		} 
			catch (SocketException e) {
				if (! isShutDown() ) {
					Console.report("ClientServerConnection. Socket Exception. Shutting down.", e);
					shutDown();
				}
	  		} 
			catch (EOFException e) {
				Console.report("ClientServerConnection. Disconnection event. Shutting down.", e);
				shutDown();
			}
			catch (ClassNotFoundException e) {
				Console.report("ClientServerConnection. Object received not in path. Continuing.", e);
			}
			catch (Exception e) {
				Console.report("ClientServerConnection. General Exception on connection. Continuing.", e);
	  		}
		}
	}

	/** 
	 * Have we shut down the connection?
	 */
	public synchronized boolean isShutDown() {
		return shutDown;
	}

	/**
	 * Closes the connection on the connection on the socket.
	 * Calls ObjectListener.finalObjectReceived() to let listener know that
	 * the connection has been closed.
	 * @see ObjectListener
	 */
	public synchronized void shutDown() {
		if (shutDown) return; // already shutdown
		if (listener != null)
			listener.connectionClosing(this);

		try {
	  		if (output != null)
				output.close();
	  		if (input != null)
				input.close();
	  		if (socket != null)
				socket.close();
		} 
		catch (Exception e) {
	  		Console.error("ClientServerConnection. Error on shutdown.", null, e);
		}
		connected = false;
		shutDown = true;
		output = null;
		input = null;
		socket = null;
	}

	/**
	 * Gets the state of the connection.
	 * @return true if socket is successfully opened and has not been shut down.
	 */
	public synchronized boolean isConnected() {
		return connected;
	}

	/**
	 * Sends the given object over the wire to the peer.
	 * <p>
	 * Objects are sent one at a time: the output phase is synchronized.
	 * @param The object to send.
	 * @return True if object was successfully sent, false otherwise.
	 * @throws Exception The object could not be sent.
	 */
	public void sendObject(Object object) throws Exception {
		if (! connected || shutDown ) {
			throw new Exception("ClientServerConnection. Attempting to use a defunct connection!");
		}

		try {
			synchronized (output) {
				output.reset();
			  	output.writeObject(object);
			  	output.flush();
			}
		} 
		catch (Exception e) {
			throw new Exception("ClientServerConnection. Unable to send. Exception Handled=" + e);
		}
	}

	/** 
	 * Sets the object listener for the connection.
	 * @param The object implementing the listener interface.
	 * @see ObjectListener
	 */
	public void setObjectListener(ObjectListener listener) {
		this.listener = listener;
	}

	/**
	 * Sets the port of the connection.
	 * @param The connection port the server is listening or the port on the server the client connected to.
	 */
	public void setPort(int connectionPort) {
		port = connectionPort;
	}

	/**
	 * Returns the port of the connection.
	 * @return The port number.
	 */
	public int getPort() {
		return port;
	}

}




