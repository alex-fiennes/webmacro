/*
 * @(#)DelegationServer.java	1.0 98/09/01
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

import java.awt.*;
import java.awt.AWTEvent.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;


import org.opendoors.instant.lcis.ObjectListener;
import org.opendoors.instant.lcis.ConnectionDataEvent;
import org.opendoors.instant.lcis.Connection;
import org.opendoors.instant.lcis.ShutdownCommand;
import org.opendoors.instant.lcis.kernel.ServerSideConnector;
import org.opendoors.util.*;

/**
 * This is a base server which provides the delegation model on a server.
 *   <P>
 *  Essentially, this class implements all core methods supporting the
 *  LCIS framework on the server. It is plug and play. However, one may
 *  easily subclass this server to provide alternate or specific behavior for an application.
 *   <p>
 *  This server is a delegator
 *  and it can multiplex multiple kinds of client
 *  applications and delegations using multicasting.
 *  <p>
 *  Handlers for the incoming requests. In other words, it is a dispatcher. When
 *  creating an instance of it or some subclass, it is the responsibility of the creating
 *  class to also specify the handlers and the associated objects they will handle. If an incoming
 *  object does not have a handler, the Server will dump the object in the bit bucket and return
 *  an appropriate response to the client application as an exception.
 */
public class DelegationServer extends AbstractServer {

	/**
	 * The action listeners by object class, each element is an ActionListener.
	 * Each key is a class name, eg, "org.opendoors.instant.lcis.commands.MessageCommand"
	 */
	protected transient Hashtable applicationListeners;

	/**
	 * The default Action Listener for an incoming event.
	 */
	protected ActionListener defaultListener;

	/**
	 * The application servers that are runnning.
	 */
	protected ApplicationServer[] runningApplicationServer;

	/**
	 * The default constructor uses the default settings: name & port.
	 */
	public DelegationServer() {
		super();
	}
	
	/**
	 * Allows the server to be instantiated with a name and a port.
	 */
	public DelegationServer(String serverName, int port) {
		super(serverName, port);
	}

	/**
	 * Registers the application servers/handlers with the base server.
	 * <p>
	 * Each application server provides a call back, getServiceList().
	 * This is a list of classes which the handler wants to react to and handle.
	 * @param applicationServers is the array of application servers this base server will dispatch to.
	 */
	public void initApplicationServers(ApplicationServer[] applicationServers) {
	 	if (applicationServers == null) return;
		runningApplicationServer = applicationServers;
		applicationListeners = new Hashtable();
		int serverCount = applicationServers.length;
		for (int index = 0; index < serverCount; index++) {
			String[] classList = runningApplicationServer[index].getClassList();
			for (int classIndex = 0; classIndex < classList.length; classIndex++) {
				// get the listener if it exists for this class:
				ActionListener listener = (ActionListener) applicationListeners.get(classList[classIndex]);
				listener = AWTEventMulticaster.add(listener, runningApplicationServer[index]);
				applicationListeners.put(classList[classIndex], listener);
			}
		}
	}

	/**
	 * Starts the delegation server.
	 * <p>
	 * Functionally, in starting the server, initApplicationServers() should have been called
	 * but this is not absolute. It can actually be called and reset at any time.
	 * @throws Exception The server or an application server could not be started.
	 */
	public void startServer() throws Exception {
		super.startServer(); // starts the server

		// now start the application handlers
		int serverCount = runningApplicationServer.length;
		for (int index = 0; index < serverCount; index++) {
			ApplicationServer s = runningApplicationServer[index];
			s.start(this);
		}
	}

	/** Returns the application servers which are running. */
	public ApplicationServer[] getrunningApplicationServer() {
	 	return runningApplicationServer;
	}

	/**
	 * Efficiently dispatches events to servers. 
	 * This class does not need to be reimplemented or subclassed as a general
	 * matter as long as application servers have been initialized.
	 */
  	public void objectReceived(Object object, Connection connection) {
		if ( acceptObject(object) ) {
			String className = object.getClass().getName();
			ActionListener listeners = (ActionListener) applicationListeners.get(className);
			ActionEvent action = new ActionEvent(new ConnectionDataEvent(object, connection), ActionEvent.ACTION_PERFORMED, null);
			if (listeners != null) {
				listeners.actionPerformed(action);
			}
			else if (defaultListener != null) {
				defaultListener.actionPerformed(action);
			}
			else {
				Exception e = new Exception("No listeners for object=" + object);
				Console.report(serverName + ". No event listener", e);
				try {
					connection.sendObject(e);
				}
				catch (Exception sendEx) {
					Console.error(serverName + ". Error sending notice to client.", this, sendEx);
				}
			}
		}
	}

	/**
	 * This method looks to see if a client has sent
	 * the shutdown command and, if so, shuts down the server!
	 */
	public boolean acceptObject(Object object) {
		if (object instanceof ShutdownCommand) {
			shutdown = true;
			synchronized (this) {
				try {
					notifyAll();
				}
				catch (Exception e) {}
			}
			return false;
		}
		else
			return true;
	}

	/**
	 * Override this method to handle disconnection by a client application.
	 */ 
	public void connectionClosing(Connection connection) {
    	Console.report("Received disconnection event. Closing connection.", connection);
  	}


	/**
	 * Override this method to handle the connection by a client application.
	 */
	public void addClient(Connection client) {
		Console.report("New Connection! Welcome client=" + client, null);
	}

	/** 
	 * Call back for notification of the voluntary termination
	 * of a connection by the client
   	 */
  	public void removeClient(Connection client) {
		Console.report("Client connection closed! Client=" + client, null);
	}

	/** Called on shutdown. Disposes application services. */
	protected void dispose() {
		try {
			// shutdown all application servers in reverse order
			int serverCount = runningApplicationServer.length;
			for (int index = serverCount; index >= 0; index--) {
				ApplicationServer s = (ApplicationServer) runningApplicationServer[index];
				s.shutdown();
				Thread.sleep(1000);
			}
			server.disconnectAllClients();
			applicationListeners = null;
			defaultListener = null;
			Console.report(serverName + " reports closing all services without error.", this);
		}
		catch (Exception e) {
			Console.error("Unable to close all server resources", null, e);
		}
	}
}
