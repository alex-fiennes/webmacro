/*
 * @(#)ObjectListener.java	1.0 98/09/01
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
 * A client or server service must implement this interface to interact
 * with the LCIS kernel.
 * <p>
 * This interface allows the kernel to dispatch and delegate the incoming object
 * to the proper handler. Additionally, it provides a handle to the
 * the interface allowing the handler to respond immediately to the
 * peer on the other side of the wire.
 */
public interface ObjectListener {

	/** 
	 * This method provides the incoming object and the connection to respond to.
     * @param The incoming object.
     * @param The connection to respond to.
	 */
  	public void objectReceived(Object object, Connection connection);

	/** 
	 * Signals the delegate that the peer has requested to close the connection.
	 * <p>
     * Such an instance would be where a client has completed running an application.
     */
  	public void connectionClosing(Connection connection);
}


