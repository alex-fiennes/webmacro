/*
 * @(#)ProxyImpl.java	1.0 2000
 * 
 * Copyright (c) 1999 Open Doors Software, Inc. All Rights Reserved.
 * 
 * Open Doors Software MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. OPEN DOORS SOFTWARE SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 */

package org.opendoors.instant.vlh.client;

import java.util.*;
import java.io.PrintStream;
import java.util.Hashtable;
import java.rmi.RemoteException;
import java.rmi.ServerException;
import org.opendoors.util.Console;
import org.opendoors.intf.QueueListener;
import org.opendoors.instant.lcis.*;
import org.opendoors.instant.vlh.*;
import org.opendoors.instant.vlh.server.*;


/**
 * Implements the org.opendoors.instant.vlh.VLHProxy interface.
 * <p>
 * Usage: Class.forName(org.opendoors.instant.vlh.client.ProxyImpl).newInstance().
 * <p>
 * The implementation inherits parts of its implementation from AbstractAgent.
 * @see org.opendoors.instant.vlh.VLHProxy
 * 
 * @author Lane Sharman, Eric Ridge
 *  
 */
public class ProxyImpl extends AbstractAgent implements VLHProxy  {
	

	private String server;
	private int port;
	private QueueListener exceptionHandler;
	private LogService logService;
	private Object receivingMutex = new Object();
	private Object singleThreaded = new Object();
	private boolean waiting = false;
	private Object received;

	/**
	 * The public constructor which is the only
	 * way to create the proxy object.
	 * <p>
	 * The implementation is connection failure resilient.
	 */
	public ProxyImpl () {}

	/** @see org.opendoors.instant.vlh.VLHProxy */
	public Store getAgentAsStore(String  partitionKey,
								 String immutableCertificate, String mutableCertificate)
			throws IllegalArgumentException, RemoteException, Exception {
		AgentImpl agent = new AgentImpl(logService);
		agent.getConnected(server, port, partitionKey, immutableCertificate, mutableCertificate);
		return agent;
	}

	/** @see org.opendoors.instant.vlh.VLHProxy */
	public Hashtable getAgentAsHashtable(String partitionKey,
								 String immutableCertificate, String mutableCertificate)
			throws IllegalArgumentException, RemoteException, Exception {
		AgentImpl agent = new AgentImpl(logService);
		agent.getConnected(server, port, partitionKey, immutableCertificate, mutableCertificate);
		return agent;
	}
   
	/** @see org.opendoors.instant.vlh.VLHProxy */
	public void initConnection(String server, int port)  throws RemoteException, Exception {

		// The anon class implementing the listener interface for incoming objects from the server peer
		ListenerAdapter listener = (new ListenerAdapter() {
			public void objectReceived(Object object, Connection connection) {
				dispatchServerObject(object, connection);
			}

			public void connectionClosing(Connection connection) {
				if (exceptionHandler != null)
					exceptionHandler.readQueue(new RemoteException("Connection closing."));
			}
		});
			
		init(listener, server, port); // connects to the server for our own connection
		this.server = server;
		this.port = port;
	}

	/** @see org.opendoors.instant.vlh.VLHProxy */
	public void createPartitionKey(String proposedKey, String owner)
    		throws IllegalArgumentException, RemoteException, ServerException, Exception {
		createPartitionKey(proposedKey, owner, null, null);
    }

	public void createPartitionKey(String proposedKey, String owner, String immutableCertificate, String mutableCertificate) throws 
						IllegalArgumentException, RemoteException, ServerException, Exception {
		if (proposedKey == null || proposedKey.length() > MAX_PARTITION_KEY_LENGTH) {
				throw new IllegalArgumentException(
					"Partition Keys may not be null or longer than " + MAX_PARTITION_KEY_LENGTH);
		}

		// certificates
		if ( (immutableCertificate == null && mutableCertificate != null)
			 || (immutableCertificate != null && mutableCertificate == null) )
				throw new IllegalArgumentException(
					"Certificates must be both null or both not null.");
		
		if (immutableCertificate != null && immutableCertificate.length() > MAX_CERTIFICATE_LENGTH)
			throw new IllegalArgumentException(
					"Immutable Certificate exceeds " + MAX_CERTIFICATE_LENGTH);
		if (mutableCertificate != null && mutableCertificate.length() > MAX_CERTIFICATE_LENGTH)
			throw new IllegalArgumentException(
					"mutable Certificate exceeds " + MAX_CERTIFICATE_LENGTH);
		
			
		CreatePartition c = new CreatePartition();
		c.setValues(proposedKey, owner, immutableCertificate, mutableCertificate);
		// make synchronous:
		synchronized (singleThreaded) {
			waiting = true;
			received = null;
			sendObject(c);
			System.out.println("Sent=" + c );
			synchronized (receivingMutex) {
				if (waiting) {
					System.out.println("waiting...");
					receivingMutex.wait(); //wait until receieved
				}
			}
			if (received instanceof Exception)
				throw (Exception) received;
			c = (CreatePartition) received;
		}
		if (c.returnValue instanceof Exception)
				throw (Exception) c.returnValue;
		//System.out.println("Create complete=" + c);
	}

	
	
	/** @see org.opendoors.instant.vlh.VLHProxy */
	public void removePartitionKey(String existingKey, String owner)
    		throws IllegalArgumentException, RemoteException, ServerException, Exception {
		removePartitionKey(existingKey, owner, null, null);
    }

	public void removePartitionKey(String existingKey, String owner, String immutableCertificate, String mutableCertificate) throws
						IllegalArgumentException, RemoteException, ServerException, Exception {
		if (existingKey == null || existingKey.length() > MAX_PARTITION_KEY_LENGTH) {
				throw new IllegalArgumentException(
					"Partition Keys may not be null or longer than " + MAX_PARTITION_KEY_LENGTH);
		}
		RemovePartition c = new RemovePartition();
		c.setValues(existingKey, immutableCertificate, mutableCertificate);
		// make synchronous:
		synchronized (singleThreaded) {
			waiting = true;
			received = null;
			sendObject(c);
			System.out.println("Sent=" + c );
			synchronized (receivingMutex) {
				if (waiting) {
					System.out.println("waiting...");
					receivingMutex.wait(); //wait until receieved
				}
			}
			if (received instanceof Exception)
				throw (Exception) received;
			c = (RemovePartition) received;
		}
		if (c.returnValue instanceof Exception)
			throw (Exception) c.returnValue;
	}

	
	/** @see org.opendoors.instant.vlh.VLHProxy */
	public void destroyAgent(Object agentInterface) {
		if (agentInterface instanceof AgentImpl)
			((AgentImpl) agentInterface).destroy();
	}


	/** @see org.opendoors.instant.vlh.VLHProxy */
    public void setExceptionHandler(QueueListener exceptionHandler) {
    	this.exceptionHandler = exceptionHandler;
    }

	/** @see org.opendoors.instant.vlh.VLHProxy */
	public void setLogHandler(PrintStream log) {
		if (log == null)
			logService = null;
		else
			logService = new LogService(log);
	}

	/**
	 * Handles objects from the server.
	 */
	protected void dispatchServerObject(Object object, Connection connection) {
		System.out.println("Dispatching. Waiting=" + waiting);
		synchronized (receivingMutex) {
			received = object;
			if (waiting) {
				waiting = false;
				System.out.println("Notifying.");
				receivingMutex.notify();
			}
		}
		if (received instanceof Exception && exceptionHandler != null)
			exceptionHandler.readQueue(object);
		System.out.println("Dispatch Complete.");
	}
}
