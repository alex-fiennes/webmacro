/*
 * @(#)ClientSideServerConnection.java	1.0 98/10/01
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
import org.opendoors.instant.lcis.ObjectListener;
import org.opendoors.util.Console;

/**
 * This class provides the glue for an application to connect to an
 * LCIS Server. Applications may reside on <b>any</b> platform and
 * need only successfully construct this class and test for the connection
 * using isConnected().
 * <p>
 * This class derives from ClientServer which contains all the basic plumbing.
 * <p>
 * This class primarily serves as an adapter in providing a method
 * to get an object to the server, sendObject(), and, to link your client
 * object as a listener to objects received.
 * <p>
 * @see ObjectListener
 * @see ClientServer
 */
public class ClientSideServerConnection extends ClientServerConnection  {

	/** 
	 * Constructs a connection to a specific host, eg, sf1.enterprise.com, over
	 * a specific port.
	 * @param client The client listener for incoming objects.
	 * @param host URL to server
	 * @param port Port the server is listening on.
	 */
	public ClientSideServerConnection(ObjectListener client, 
				    String host, int port) {
		initialize(client, host, port);
	}

	/**
	 * Initializes the connection for the client.
	 */
	private void initialize(ObjectListener client, String host, int port) {
		Socket socket = null;
		this.port = port;
		try {
		  	//System.out.println("ClientSideConnection: trying " + host +":"+ port);
		  	socket = new Socket(host, port);
			setPort(port);
		  	//System.out.println("ClientSideConnection: Setting socket values. Socket=" + socket);
			initSocket(socket);
		  	//System.out.println("ClientSideConnection: Socket set. Setting listener...");
			setObjectListener(client);
		} 
		catch (Exception e) {
		  	Console.report("ClientSideConnection. Cannot make connection to host=" + host + " on port=" + port, e);
		}
	}

}

