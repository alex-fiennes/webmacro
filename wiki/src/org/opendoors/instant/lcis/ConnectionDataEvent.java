/*
 * @(#)ConnectionDataEvent.java	1.0 98/09/01
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

package org.opendoors.instant.lcis;

/**
 * Encapsulates incoming data and the connection
 * for responding to the sender of the data.
 */
public class ConnectionDataEvent {

	/**
	 * The data received.
	 */
	public Object receivedObject;

	/**
	 * The Connection handling the incoming object.
	 */
	public Connection connectionHandler;

	public ConnectionDataEvent(Object receivedObject, Connection connectionHandler) {
		this.receivedObject = receivedObject;
		this.connectionHandler = connectionHandler;
	}
}
