/*
 * @(#)ApplicationServer.java	1.0 98/10/01
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
package org.opendoors.instant.lcis.server;

import java.awt.event.ActionListener;

/** 
 * Application Servers implement this interface 
 * for the purpose of allowing a BaseServer to
 * start an ApplicationServer according to an interface contract.
 * <p>
 * This interface allows the App Server to obtain a reference 
 * to the base service managing the socket and
 * therefore the methods
 * to shut down the server entirely if required.
 * <p>
 * Application servers must also implement the action semantic because this
 * semantic is used to call back an Application Server with an interest in the class
 * type of an incoming object.
 * <p>
 * There is an example of using this protocol in the example directory.
 * @see org.opendoors.examples.lcis.taste
 */
public interface ApplicationServer extends ActionListener {

	/**
	 * All application servers must have a start method because
 	 * starting the ApplicationServer is controlled by the BaseServer.
 	 * The BaseServer starts first and then, for each ApplicationServer,
 	 * the ApplicationServer is started.
 	 * <p>
 	 * Implementing this call back allows an application server
 	 * to obtain a reference to the BaseServer managing all connections
 	 * on the server and, in extremis, the ApplicationServer can 
 	 * delegate to itself the role
 	 * of the server including shutting down
 	 * all clients and broadcasting to all clients.
 	 * @param baseServer The reference to the BaseServer starting this application server.*/
	public void start(DelegationServer server);

	/**
	 * Application servers inform the base server as to the classes it
	 * wishes to handle.
	 * @return {org.ibm.accounting.PurchaseRequest, org.ibm.accounting.ReqestForInvoice} are examples.
	 */
	public String[] getClassList();

	/**
	 * Application servers implement this interface for a graceful shutdown.
	 */
	public void shutdown();


}
