/*
 * @(#)ExecServer.java	1.0 98/09/01
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

import org.opendoors.instant.lcis.Connection;
import org.opendoors.instant.lcis.RemotelyExecutable;
import org.opendoors.util.Console;

/**
 *  Provides for execution of objects implementing the RemotelyExecutable interface.
 *   <P>
 *  Essentially, this server expects all incoming objects
 *  to implement the RemotelyExecutable interface which has a single
 *  method call.
 */
public class ExecServer extends AbstractServer {

	/** The number of objects received to date. */
	public long objectsReceivedToDate;

	/**
	 * The default constructor uses the default settings: name & port.
	 */
	public ExecServer() {
		super();
	}

	/**
	 * Allows the server to be instantiated with a name and a port.
	 */
	public ExecServer(String serverName, int port) {
		super(serverName, port);
	}

	/**
	 * Dispatches the object as a RemotelyExecutable object. 
	 */
  	public void objectReceived(Object object, Connection connection) {
  		objectsReceivedToDate++;
  		try {
			//System.out.println("ExecServer=" + object + " Connection=" + connection);
			RemotelyExecutable service = (RemotelyExecutable) object;
			service.execute(connection);
		}
		catch (Exception e) {
			Console.report(serverName + ". Unable to execute object. Error=" + e, object);
			try {
				connection.sendObject(e);
			}
			catch (Exception sendEx) {
				Console.error(serverName + ". Error sending notice to client.", this, sendEx);
			}
		}
  		notifyObjectListeners(object, connection);
	}

	/**
	 * Exec servers interested in tracking resets and client connection
	 * loss should implement this adapter method.
	 */ 
	public void connectionClosing(Connection connection) {}

	/**
	 * If the server is shut down, this adapter method is called just prior to
	 * a complete shutdown.
	 */
	protected void dispose() {}
}
