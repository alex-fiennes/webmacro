/*
 * @(#)Connection.java	1.0 98/10/01
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
package org.opendoors.instant.lcis;


/**
 * Every LCIS connection on the client or the server implements
 * to this interface.
 * <p>
 * This interface supplies the common services for a
 * a connection. It is irrespective
 * of who initiated the connection (the client) or who
 * is listening (the server).
 * <p>
 * Once a connection is complete a common object is created
 * on both the client VM and the server VM. Each implements this interface.
 */
public interface Connection {

	/** The default LCIS Listening Port. */
	public static final int DEFAULT_LCIS_PORT = 1998;
    
	/** New connection created event ID. */
	public static final long CONNECTION_CREATED = 1;
	
	/** Existing connection closed event ID. */
	public static final long CONNECTION_CLOSED = 2;
 
	/**
	 * Sends asynchronously the given object over the wire to the peer connection.
	 * @param The object to send.
	 * @return True if object was successfully sent, false otherwise.
	 * @throws Exception The object could not be sent usually because the connection
	 * is now defunct.
	 */
	public void sendObject(Object object) throws Exception;

	/** 
	 * Sets the object listener for the connection.
	 * <p>
	 * Incoming objects will be 
	 * @param The object implementing the listener interface.
	 * @see ObjectListener
	 */
	public void setObjectListener(ObjectListener listener);

	/**
	 * Closes the connection.
	 */
	public void shutDown();

	/** 
	 * Have we shut down the connection?
	 */
	public boolean isShutDown();

	/**
	 * Gets the state of the connection.
	 * @return true The socket is open and available.
	 */
	public boolean isConnected();

	/**
	 * Gets the port the server is listening on.
	 * @return the port used for establishing connections
	 */
	public int getPort();

}

