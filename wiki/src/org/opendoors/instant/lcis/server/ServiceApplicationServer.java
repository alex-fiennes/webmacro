/*
 * @(#)ServiceApplicationServer.java	1.0 1999
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
package org.opendoors.instant.lcis.server;

import java.awt.event.*;
import java.util.*;
import org.opendoors.util.*;
import org.opendoors.instant.lcis.ObjectListener;
import org.opendoors.instant.lcis.Connection;
import org.opendoors.instant.lcis.ConnectionDataEvent;

/**
 * ServiceApplicationServer is an abstract base class for
 * building an application service handler.
 * <p>
 * The pattern for an application server is analagous to a servlet: command, action, response.
 * <p>
 * Application serice handlers (like a servlet) are optimally implemented as subclasses
 * to this class.
 */
public abstract class ServiceApplicationServer extends Thread implements ApplicationServer {

	//-------public members-----
	protected long monitorInterval = 30000;
    protected DelegationServer dispatcher;

    /** When true, instance uses processMessage(Object, Connection) */
	protected boolean provideConnection = true;

	private boolean shutdown = false;
	

	public ServiceApplicationServer() {
		super();
	}

	/**
	 * Application servers implement initialization code if any here.
	 */
	protected void init() throws Exception {}
	
	/**
	 * An application server must describe the java objects it handles.
	 * <p>
	 * This list is in the form of fully qualified class names, eg, {org.opendoors.model.Approval}.
	 */  
	public abstract String[] getClassList();
	
	/**
	 *  The call back to start the application server.
	 * <p>
	 * This method is called by the base server when it has started the server.
	 */
	public void start(DelegationServer dispatcher) {
		try {
			setDaemon(true); // when all else is shut down, we may be too.
			init();
            this.dispatcher = dispatcher;
			super.start();
		}
		catch (Exception e) {
			Console.error("ServiceApplicationServer. Unable to start", this, e);
		}
	}
	
	/**
	 * Runs the application in a separate thread to provide a periodic monitor callback.
	 */
	public void run() {
		while (! shutdown) {
			try {
				synchronized (this) {
					wait(monitorInterval);
				}
			}
			catch (Exception e) {}
			try {
				monitor();
			}
			catch (Exception e) {
				Console.error("Error in application monitor.", null, e);
			}
		}
	}

	/**
	 * Handles incoming commands on behalf of the application
	 * service from a client.
	 * <p>
	 * Replies within the current connection thread to the requestor.
	 */
  	public void actionPerformed(ActionEvent event) {
		Connection originator = null;
 		try {
			originator = (Connection)((ConnectionDataEvent) event.getSource()).connectionHandler;
			Object message = ((ConnectionDataEvent) event.getSource()).receivedObject;
			Object response = null;
			if (provideConnection)
				response = processMessage(message, originator);
			else
				response = processMessage(message);
			// we are replying to the sender; not some arbitrary connection!!
			if (response != null)
				originator.sendObject(response);
		}
		catch (Exception e) {
			Console.error("Service Application Error. Report Details:", this, e);
			//return error to sender
			if (originator != null ) {
				// return the error as the data!
				try {
					originator.sendObject(e);
				}
				catch (Exception sendEx) {
					Console.error("Unable to send error to client.", this, sendEx);
				}
			}
			else {
				Console.error("Unable to dispatch a connection event.", event, e);
			}
		}
	}

	/**
	 * Delegates to processShutdown() the command to shut down this server.
	 */
	public void shutdown() {
		try {
			synchronized (this) {
				shutdown = true;
				notifyAll();
			}
			Thread.sleep(1000);
			Console.report("Shutting down application service.", this);
			processShutdown();
		}
		catch (Exception e) {
			Console.error("Unexpected error during application shutdown.", this, e);
		}
	}

	/**
	 * Application services process commands and usually return a non-null object.
	 * @param message The incoming message from the client.
	 */
	protected abstract Object processMessage(Object message) throws Exception;

	/**
	 * Application services process commands and usually return a non-null object.
	 * @param message The incoming message from the client.
	 * @param connection The connection for making a direct reply.
	 */
	protected abstract Object processMessage(Object message, Connection connection) throws Exception;

	/**
	 * Application services receive a call back on shutdown.
	 */
	protected abstract void processShutdown() throws Exception;
	
	/**
	 * Application servers receive a monitor call on a periodic basis. The interval
	 * is determined by the protected instance member, monitorInterval.
	 */
	protected abstract void monitor() throws Exception;
}
