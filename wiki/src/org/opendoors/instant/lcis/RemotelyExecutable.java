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
 * An object which implements this interface expresses its ability to
 * executed remotely by the LCIS ExecServer or one of its derivatives.
 * <p>
 * Objects normally instantiate themselves on the <b>client</b>
 * and then send themselves to the server for execution.
 * <p>
 * Once on the server, the object's execute() is called. Within
 * that method, you may send one or more objects back to the client.
 * <p>
 * Objects which implement this method must be serializable and this
 * interface enforces that constraint.
 */
public interface RemotelyExecutable extends java.io.Serializable {

	/** 
	 * Objects implement this method for execution on the server.
	 * <p>
	 * Note: a pushlet would never return from this call.
     * @param connection The lcis interface providing for socket
     * connectivity to the client application.
	 */
  	public void execute(Connection connection) throws Exception;

}


