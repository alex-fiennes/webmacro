/*
 * @(#)LocalProxyImpl.java	1.0 2000
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
 * Usage: Class.forName(org.opendoors.instant.vlh.client.LocalProxyImpl).newInstance().
 * <p>
 * The implementation inherits parts of its implementation from AbstractAgent.
 * @see org.opendoors.instant.vlh.VLHProxy
 * 
 * @author Lane Sharman, Eric Ridge
 * 
 */
public class LocalProxyImpl implements VLHProxy  {
	

	private String rootDirectory;
	private QueueListener exceptionHandler;
	private LogService logService;
	private Object singleThreaded = new Object();

	/**
	 * The public constructor which is the only
	 * way to create the proxy object.
	 */
	public LocalProxyImpl () {}

	/** @see org.opendoors.instant.vlh.VLHLocalProxy */
	public Store getAgentAsStore(String  partitionKey,
								 String immutableCertificate, String mutableCertificate)
			throws IllegalArgumentException, RemoteException, Exception {
		LocalAgentImpl agent = new LocalAgentImpl(logService);
		agent.getConnected(rootDirectory, partitionKey, immutableCertificate, mutableCertificate);
		return agent;
	}
   
	/** @see org.opendoors.instant.vlh.VLHProxy */
	public Hashtable getAgentAsHashtable(String partitionKey,
								 String immutableCertificate, String mutableCertificate)
			throws IllegalArgumentException, RemoteException, Exception {
		LocalAgentImpl agent = new LocalAgentImpl(logService);
		agent.getConnected(rootDirectory, partitionKey, immutableCertificate, mutableCertificate);
		return agent;
	}   

	/** @see org.opendoors.instant.vlh.VLHLocalProxy */
	public void initConnection(String rootDirectory, int port)  throws Exception {
      this.rootDirectory = rootDirectory;
      if (port != -1) 
         this.rootDirectory += "-" + port;
	}

   
	/** @see org.opendoors.instant.vlh.VLHProxy */
	public void createPartitionKey(String proposedKey, String owner)
    		throws IllegalArgumentException, RemoteException, ServerException, Exception {
		createPartitionKey(proposedKey, owner, null, null);
   }
   
	/** @see org.opendoors.instant.vlh.VLHLocalProxy */
	public void createPartitionKey(String proposedKey, String owner, String immutableCertificate, String mutableCertificate) throws 
						IllegalArgumentException, RemoteException, ServerException, Exception {
		if (proposedKey == null || proposedKey.length() > VLHProxy.MAX_PARTITION_KEY_LENGTH) {
				throw new IllegalArgumentException(
					"Partition Keys may not be null or longer than " + VLHProxy.MAX_PARTITION_KEY_LENGTH);
		}

		// certificates
		if (immutableCertificate == null || mutableCertificate == null)
				throw new IllegalArgumentException(
					"Certificates must not be null.");
		
		if (immutableCertificate.length() > VLHProxy.MAX_CERTIFICATE_LENGTH)
			throw new IllegalArgumentException(
					"Immutable Certificate exceeds " + VLHProxy.MAX_CERTIFICATE_LENGTH);
		if (mutableCertificate.length() > VLHProxy.MAX_CERTIFICATE_LENGTH)
			throw new IllegalArgumentException(
					"mutable Certificate exceeds " + VLHProxy.MAX_CERTIFICATE_LENGTH);
		
		CreatePartition c = new CreatePartition();
		c.setValues(proposedKey, owner, immutableCertificate, mutableCertificate);
		synchronized (singleThreaded) {
			c.localExecution(rootDirectory);
			//System.out.println("Local Create complete=" + c);
		}
	}

	/** @see org.opendoors.instant.vlh.VLHProxy */
	public void removePartitionKey(String existingKey, String owner)
    		throws IllegalArgumentException, RemoteException, ServerException, Exception {
		removePartitionKey(existingKey, owner, null, null);
    }
   
	/** @see org.opendoors.instant.vlh.VLHLocalProxy */	
	public void removePartitionKey(String existingKey, String owner, String immutableCertificate, String mutableCertificate) throws
						IllegalArgumentException, RemoteException, ServerException, Exception {
		if (existingKey == null || existingKey.length() > VLHProxy.MAX_PARTITION_KEY_LENGTH) {
				throw new IllegalArgumentException(
					"Partition Keys may not be null or longer than " + VLHProxy.MAX_PARTITION_KEY_LENGTH);
		}
		RemovePartition c = new RemovePartition();
		c.setValues(existingKey, immutableCertificate, mutableCertificate);
		// make synchronous:
		synchronized (singleThreaded) {
			c.localExecution(rootDirectory);
			System.out.println("Local Remove complete=" + c);
		}
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

	/** @see org.opendoors.instant.vlh.VLHProxy */
	public void close() throws java.lang.Exception {
	}

   //
   // required but not implemented
   //
   
   /**
    * does nothing
    */
   public void destroyAgent (Object o) { }
  
   /**
    * always returns true
    */
   public boolean connected () { return true; }
   

}
