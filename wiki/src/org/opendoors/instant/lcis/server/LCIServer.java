/*
 * @(#)LCIServer.java	1.0 98/10/01
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

import org.opendoors.instant.lcis.Connection;


/** 
 * A class behaving as a Lightweight Server implements
 * this interface. The implementing server is then notified through
 * the method call backs of the events specific to a server, eg,
 * a new client connecting to the server.
 * <p>
 * By implementing server services as an interface, the implementing
 * class is free to belong to any class inheritance outside of this
 * framework.
 */
public interface LCIServer {

	/** 
	 * Call back when a client connects and is added to the pool of
	 * of connected clients on the server.
	 */
	public void addClient(Connection client);

	/** 
	 * Call back for notification of the voluntary termination
	 * of a connection by the client
   	 */
  	public void removeClient(Connection client);

	/**
	 * Servers must be able to shutdown the LCIS server service
	 * and gracefully notify all client connections.
	 */
	public void shutdown();

}



