/*
 * @(#)AbstractAgent.java	1.0 1999
 * 
 * Copyright (c) 1999 Open Doors Software, Inc. All Rights Reserved.
 * 
 * Open Doors Software MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. OPEN DOORS SOFTWARE SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 */
package org.opendoors.instant.lcis;

import java.io.*;
import java.util.*;
import java.net.*;
import org.opendoors.util.*;
import org.opendoors.instant.lcis.kernel.ClientSideServerConnection;


/**
 * AbstractAgent is a base, abstract class for an LCIS client agent/proxy.
 * <p>
 * Agents perform transactions on behalf of client applications.
 * This class takes care of all the plumbing required to make a connection;
 * to monitor the connection; and to reestablish the connection if it fails.
 * <p>
 * It has an intrinsic method to send a shutdown command to the server which
 * in effect brings down the server.
 * <p>
 * AbstractAgent is a pure asynch agent.
 * All requests are pumped out to the server on an asycnh basis.
 * <p>
 * Specialized application agents subclass AbstractAgent
 * to provide a service to an application which is remotely performed
 * on the server.
 * <p>
 * If an application needs synchronous behavior, it must provide this if
 * the asynch model is too complex.
 */
public class AbstractAgent {

	/**
	 * The client connection.
	 */
	protected Connection connection = null;
	
	/**
	 * The host to connect with.
	 */
	protected String host = null;

	/**
	 * The port if specified in the constructor.
	 */
	protected int port = Connection.DEFAULT_LCIS_PORT;

	/** The connection has been shutdown normally. */
	protected boolean isShutdown = false;

	/** The listener for this connection. */
	ObjectListener clientHandler;
	 
	/** The wait interval for a network operation to complete. */
	protected static long WAIT_INTERVAL = 2000;


	/**
	 * The default constructor which does nothing.
	 */
	public AbstractAgent() {}

	/**
	 * Initializes the base agent and asks for a connection over the default port to a host.
	 * @param clientHandler Delegate to this instance the handler for incoming data.
	 * @param host The host to connect to.
	 * @throws Exception If it is not possible to make a connection to the host.
	 */
  	public void init(ObjectListener clientHandler, String host) throws Exception {
  		init(clientHandler, host, Connection.DEFAULT_LCIS_PORT);
	}

	/**
	 * Initializes the base agent and asks for a connection over a specified port to a host and starts the agent.
	 * @param clientHandler Delegate to this instance the handler for incoming data.
	 * @param host The host to connect to.
	 * @param port The port to connect to.
	 * @throws Exception If it is not possible to make a connection to the host.
	 */
  	public void init(ObjectListener clientHandler, String host, int port) throws Exception {
  		testConnectivity();
		// construct the listener on the client and tell it to post events to our inner class.
		this.host = host;
		this.port = port;
		ClientSideServerConnection connection = new ClientSideServerConnection(clientHandler, host, port);
		connection.start();
		this.connection = connection;
		this.clientHandler = clientHandler;
	}

	public boolean connected() {
		return (connection != null && connection.isConnected());
	}

	/**
	 * General network connectivity on the client: is the ethernet cable plugged in?
	 */
	protected void testConnectivity() throws Exception {
		// need to implement a ping monitor here
	}

	/**
	 * Sends an object to the server using the connection.
	 * <p>
	 * If there has been an intermittent network failure,
	 * this method will recover from it by testing for it and
	 * reestablishing the connection.
	 * @param The object to send.
	 */
	public void sendObject(Object object) throws Exception {
		if (connected())
			connection.sendObject(object);
		else if (! isShutdown) {
			// attempt a restart
			init(this.clientHandler, this.host, this.port);
			if (connected())
				connection.sendObject(object);
			else
				throw new Exception("Unable to restart after network failure.");
		}
	}

	/**
	 * Shuts down the server and the client in one command.
	 * <p>
	 * Do this only if you really want to shutdown the client
	 * <b>and</b> the server.
	 */
	public void shutDown() throws Exception {
		sendObject(new ShutdownCommand());
		synchronized (this) {
			wait(WAIT_INTERVAL);
			connection.shutDown();
			connection = null;
			isShutdown = true;
		}
	}

	/**
	 * Closes the agent and the connection gracefully.
	 */
	public synchronized void close() throws Exception {
		connection.shutDown();
		connection = null;
		isShutdown = true;
	}

	
}
