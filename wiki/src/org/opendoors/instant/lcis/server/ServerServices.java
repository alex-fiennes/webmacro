/*
 * @(#)ServerServices.java	1.0 98/10/01
 * Copyright (c) 1998 Open Doors Software, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Open Doors Software
 * Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Open Doors Software.
 * 
 * Open Doors Software MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. OPEN DOORS SOFTWARE SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1
 * 
 */
package org.opendoors.instant.lcis.server;

import java.util.Enumeration;

/**
 * This interface is the contract for the implementation
 * of the LCIS Connection kernel.
 * <p>
 * This interface guarantees a set of services
 * which the kernel connector on the server implements.
 */
public interface ServerServices {

	/**
	 * Request the server to stop listening on the port and effectively shut down the server.
	 */
	public void stopListening();

	/**
	 * Informs if the server is handling incoming requests.
	 * @return True if the server is listening at this time.
	 */
	public boolean isListening();

	/** 
	 * Broadcast to all currently connected clients the supplied object.
	 * @param The object to send.
 	 */
	public void sendToAllClients(Object object) throws Exception;

	/** 
	 * Disconnects all clients from server.
	 * <p>
	 * Use this call to permit a normal server shutdown of all connected clients.
	 * Each individual connection is given a chance to shut itself
	 * down normally.
	 */
	public void disconnectAllClients();

	/**
	 * Gets a count of the connected clients.
	 * @return The number of clients currently connected.
	 */
	public int getClientConnectionCount();

	/**
	 * Gets the port the server is listening on.
	 * @return the port used for establishing connections
	 */
	public int getPort();

	/**
	 * Returns an enumeration of every individual ServerConnection object
	 * where each object stands for an existing client connection to the server.
	 * @return An enumeration of all the ServerConnection objects active
	 * for this server.
	 * @see ServerConnection
	 */
	public Enumeration getClients();

	/**
	 * Starts the server.
	 */
	public void start();


}

