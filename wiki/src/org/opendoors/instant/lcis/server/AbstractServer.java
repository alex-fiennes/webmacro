/*
 * @(#)AbstractServer.java	1.0 98/09/01
 * Copyright (c) 2000 Open Doors Software, Inc. All Rights Reserved.
 * 
 * This software is published as open source under the ___ license.
 * 
 * Open Doors Software MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. OPEN DOORS SOFTWARE SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 */

package org.opendoors.instant.lcis.server;

import java.util.*;

import org.opendoors.instant.lcis.*;
import org.opendoors.instant.lcis.kernel.ServerSideConnector;
import org.opendoors.util.*;


/**
 * All LCIS servers derive from this class.
 * <P>
 * This class contains the core elements for writing
 * a server that provides peer-to-peer connection services whereby
 * this class defines the role of the "server".
 * <p>
 * This class supports event notification on the server-side for call backs
 * to all clients connected for broadcasts. There are two classes of events.
 * <ul>
 * <li>The connection created or closed event allowing for building a working connection pool
 * which is active. This class performs the actual notification.</li>
 * <li>The object received event. Calling from this class is too-fined grained. A 100 objects
 * might compromise a logical "update". Therefore, subclasses of this abstract class are directed
 * to make the call to notifyObjectListeners() for a more logical notification scheme.
 * </ul>
 * With this event notification, it is possible to perform complete bidirectional services and
 * broadcasting. A server-side class can track changes to a "server-side" model and notify all clients
 * of that a model has been modified in a particular manner. Connection is provided to event handlers
 * which implements the socket methods required for sending objects back to the client.
 * @see Connection
 * @see org.opendoors.instant.lcis.server.ExecServer
 */
abstract public class AbstractServer extends Thread implements LCIServer, ObjectListener {

	/**
	 * Evaluates and outputs system statistics. 30 second default.
	 */
	protected long sampleWaitTime = 30000;

	/**
	 * The connection for the AbstractServer with the port it is listening on.
	 */
	protected ServerServices server = null;

	/**
	 * The name for this AbstractServer. Override this name!
	 */
	protected String serverName = "LCIS Server";

	/**
	 * Is the Server started?
	 */
	protected boolean started = false;

	/**
	 * The port number to listen on.
	 */
	protected int portNumber = Connection.DEFAULT_LCIS_PORT;

	/**
	 * Shutdown notifier to the LCIS Server?
	 */
	protected boolean shutdown = false;

	/**
	 * The connection event listeners.
	 */
	private Vector connectionEventListener;

	/**
	 * The object event listeners.
	 */
	private Vector objectEventListener;

	/**
	 *  Constructs the AbstractServer but provides no functionality.
	 */
  	public AbstractServer() {}
	
	/**
	 * A convenience constructor to set the name.
	 */
	public AbstractServer(String serverName) {
	 	this.serverName = serverName;
	}

	/**
	 * Constructs an instance a name and a port number to listen on.
	 */
	public AbstractServer(String serverName, int port) {
	 	this.serverName = serverName;
	 	this.portNumber = port;
	}

	/**
	 * Sets the port on which this server will listen.
	 * <p>
	 * The default port is on which the LCIS Server listens on is 2975.
	 */
	protected void initPort(int portNumber) {
		this.portNumber = portNumber;
	}

    protected ServerServices getServerServices() {
    	return server;
    }

	/**
	 * Starts the server.
	 *  <ul>
	 *  <li>Creates the Server thread
	 *  on the defined port.</li>
	 *  <li>Registers that we want notification of client disconnects and connects.</li>
	 *  <li>Registers that we listen for all incoming objects.</li>
	 *  <li>Throws an exception if the server cannot be started.</li>
	 *  </ul>
	 * @throws Exception The server could not be started.
	 */
	public void startServer() throws Exception {
		server = new ServerSideConnector(this, portNumber, this);
		server.start(); // starts the listener
		super.start(); // starts the thread
		started = true;
		System.out.println(serverName + " started.");
	}

	/**
	 * Adds a connection event listener for client connections and disconnections.
	 */
	public void addConnectionListener(ConnectionEventListener connectionListener) {
		if (connectionEventListener == null)
			connectionEventListener = new Vector();
		connectionEventListener.addElement(connectionListener);
	}

	/**
	 * Removes a connection event listener for client connections and disconnections.
	 */
	public void removeConnectionListener(ConnectionEventListener connectionListener) {
		if (connectionEventListener == null)
			return;
		connectionEventListener.removeElement(connectionListener);
	}

	/**
	 * Adds an object event listener for objects received from a peer.
	 */
	public void addObjectListener(ObjectListener objectListener) {
		if (objectEventListener == null)
			objectEventListener = new Vector();
		objectEventListener.addElement(objectListener);
	}

	/**
	 * Removes a connection event listener for client connections and disconnections.
	 */
	public void removeObjectListener(ObjectListener objectListener) {
		if (objectEventListener == null)
			return;
		objectEventListener.removeElement(objectListener);
	}

	/**
	 * A convenience method for notifying listeners of incoming objects received.
	 */
	public void notifyObjectListeners(Object object, Connection connection) {
		if (objectEventListener == null)
			return;
		Enumeration e = connectionEventListener.elements();
		while (e.hasMoreElements()) {
			ObjectListener l = (ObjectListener) e.nextElement();
			l.objectReceived(object, connection);
		}
	}
		
	

	/**
	 * When the server is shut down, this method
	 * is always invoked before the thread terminates.
	 */
	abstract protected void dispose();

	/**
	 * Implementations of this class must implement this method
	 * signature.
	 */
  	abstract public void objectReceived(Object object, Connection connection);

	/**
	 * Implementations must supply their own
	 * handler for when a client socket resets
	 * and disconnects from the connection.
	 */ 
	abstract public void connectionClosing(Connection connection);

	/**
	 * Override this method to handle the connection by a client application.
	 */
	public void addClient(Connection client) {
		Console.report("New Connection! Welcome client=" + client, null);
		if (connectionEventListener == null)
			return;
		Enumeration e = connectionEventListener.elements();
		while (e.hasMoreElements()) {
			ConnectionEventListener l = (ConnectionEventListener) e.nextElement();
			l.connectionOpened(client);
		}
	}

	/** 
	 * Call back for notification of the voluntary termination
	 * of a connection by the client
   	 */
  	public void removeClient(Connection client) {
		Console.report("Client connection closed! Client=" + client, null);
		if (connectionEventListener == null)
			return;
		Enumeration e = connectionEventListener.elements();
		while (e.hasMoreElements()) {
			ConnectionEventListener l = (ConnectionEventListener) e.nextElement();
			l.connectionClosed(client);
		}
	}

	/**
	 * Runs the server in a thread until it receives a shutdown notice.
	 */
	public void run() {
		try {
			while (! shutdown) {
				synchronized (this) {
					wait(sampleWaitTime);
				}
				statistics();
			}
		}
		catch (Exception e) {
			Console.error("Error running server. Shutting down.", null, e);
			shutdown = true;
		}
		finally {
			dispose();
			server.stopListening(); // close the server connector.
		}
	}

	protected void statistics() {
		System.out.println(serverName + " reporting statistics:");
		System.out.println("Current client connection count=" + server.getClientConnectionCount() + " at=" + (new java.util.Date()) );
		Runtime runtime = Runtime.getRuntime();
		long total = runtime.totalMemory();
		long free = runtime.freeMemory();
		int load = (int) ( ((total-free)*100) / total);
		System.out.println("Total Memory=" + total + " Free=" + free + " Memory Load=" + load + "%");
	}

	/**
	 * Stops the server gracefully so it can be restarted.
	 * <ul>
	 * <li> disconnects connected clients.</li>
	 * <li> saves the server state.</li>
	 * <li> closes down the running thread.</li>
	 * </ul>
	 */
	public void shutdown() {
		try {
			System.out.println("Shutting down server...");
			synchronized (this) {
				shutdown = true;
				notifyAll(); // stop the server thread
			}
		}
		catch (Exception e) {}
		if (connectionEventListener == null)
			return;
		Enumeration e = connectionEventListener.elements();
		while (e.hasMoreElements()) {
			ConnectionEventListener l = (ConnectionEventListener) e.nextElement();
			l.serverShutdown();
		}
	}

}
