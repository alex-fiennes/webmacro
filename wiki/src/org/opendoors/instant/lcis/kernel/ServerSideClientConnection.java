/*
 * @(#)ServerSideClientConnection.java	1.0 98/10/01
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

import org.opendoors.instant.lcis.ObjectListener;

/**
 * This is a connection object instantiated on the server for each connection
 * created by a client.
 * <p>
 * It provides some minor extensions to the base class, ClientServerConnection,
 * and is a replaceable class, if required.
 */
public class ServerSideClientConnection extends ClientServerConnection {

	/**
	 * A reference to the server connector initiating this connection.
	 */
	private ServerSideConnector server;

	/** 
	 * Constructor called by the server to create this connection.
	 * @param connector The server-side connector for all clients.
	 * @param server When objects are received from the client on the server,
	 * dispatch them to this client. This will be the listener on the server.
	 * @param socket The socket for this connection.
	 * @see ServerSideConnector
	 */
	public ServerSideClientConnection(ServerSideConnector server, 
				    ObjectListener listener, Socket socket) {
		initSocket(socket);
		setObjectListener(listener);
		this.server = server;
	}

	/**
	 * Overrides the shutdown to assure that the server for this connection
	 * removes this connection from the pool.
	 */
	public void shutDown() {
		super.shutDown();
		server.removeClient(this);
	}

}





