/*
 * @(#)VLHProxy.java	1.0 2000
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

package org.opendoors.instant.vlh;

import java.rmi.RemoteException;
import java.rmi.ServerException;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Properties;
import org.opendoors.intf.QueueListener;

/**
 * The VLHProxy is the general client-side factory for vlh services and partition management.
 * <p>
 * Applications may want to provide a wrapper for this class. The interface exposes some networking 
 * methods allowing the proxy to be flexibly configured at runtime.
 * <p>
 * The VLHProxy provides for establishing a VLHProxy efficiently and without complication. The only
 * requirement for obtaining a VLHProxy is that the client application be able to present its
 * partition key. The VLHProxy has a convenience method for creating a new key. The administrator of
 * the VLH Server can, for security and administrative reasons, disable this feature on the server. If
 * disabled, the request will return an exception. In creating a key do not use an obvious one like
 * "foobar". It is probably taken or is easily compromised. Partition keys are not password protected.
 * <p>
 * VLHProxy is connection oriented. If you do not need the connection-oriented and exception
 * reporting facilities, use VLHProvider, a singleton, for even easier access to a vlh server.
 * @author Lane Sharman, Eric Ridge
 * @see VLHProvider
 * @version 1.0
 * @see VLHProxy
 * @since 13-July-2000
 * @type Product Requirement 
 */
public interface VLHProxy {

	/** Partition Key Maximum Length */
	public final int  MAX_PARTITION_KEY_LENGTH = 128;

	/** Partition Key Maximum Length */
	public final int  MAX_HASH_KEY_LENGTH = 128;

	/** Certificate Length */
	public final int  MAX_CERTIFICATE_LENGTH = 128;

    /**
     * Client applications should dedicate a thread to monitor unusual exceptions and errors.
     * <p>
     * An exception might be the attempt to create a duplicate partition.
     * <p>
     * A more sever example is a buggy network or unstable server. The VLH server may fail
     * and each proxy obtains a notification.
     * This will happen even if you pull the ethernet cable because the proxy,
     * has its own heartbeat/ping monitor running at all times.
     * <p>
     * If an exception or crash occurs, the handler supplied will have its
     * QueueListener read method invoked and it can then analyze the kind of
     * object supplied. As soon as an exception is received, QueueListener.readQueue(Object)
     * is invoked. The protocol is simple: Object will either subclass Error or Exception. Errors
     * should be considered fatal. Exceptions will be identifiable by a toString() or type.
     * <p>
     * This is the proper way to notify fatal error conditions because the VLH is a highly asynch,
     * write-behind model on both the client and the server. Placing an exception in the put() method of
     * the VLHProxy is therefore meaningless.
     * <p>
     * To make a sequence of operations transactionally secure, issue a commit() on the VLHProxy. 
     * This will flush all pending operations on the server for that agent.
     * @param exceptionHandler The instance dedicated to monitoring if there has been a server failure. 
     */
    public void setExceptionHandler(QueueListener exceptionHandler);


    /**
     * Log records are written to a stream if requested. For debugging purposes or to track down problems,
     * use this method to set up a logging model.
     * <p>
     * <b>Note:</b>Logging comes at a severe price. As actions are taken on the server, they
     * are reported back to the Proxy for logging on behalf of an agent. This slows down the overall
     * performance. Not much but some. By default logging is turned off.
     * @param log The log stream to write to. If null, stops existing logging.
     */
    public void setLogHandler(PrintStream log);

    /**
      * This method provides the client application with a Store.
      * <p>
      * A Store is a simple interface for getting, putting and removing
      * objects to the vlh server.
      * @see Store
      * @exception IllegalArgument The partition key is invalid.
      * @exception RemoteException A connection cannot be established. 
      */
     public Store getAgentAsStore(String partitionKey,
    							 String immutableCertificate, String mutableCertificate) 
    	throws IllegalArgumentException, RemoteException, Exception;
  
     /**
      * This method provides the client application with the identical agent but as
      * a subclass of a hashtable. This agent implements all methods
      * of a Hashtable faithfully except clone().
      * <p>
      * See documentation below which documents how the returned
      * Hashtable, as a subclass, differs in behavior from a java.util.Hashtable.
      * @see org.opendoors.instant.vlh.client.AgentImpl
      * @exception IllegalArgument The partition key is invalid.
      * @exception RemoteException A connection cannot be established. 
      */
     public Hashtable getAgentAsHashtable(String partitionKey,
    	 								 String immutableCertificate, String mutableCertificate)
    		throws IllegalArgumentException, RemoteException, Exception ;
    		
	/**
	 * Destroys the agent and frees up resources including connection resources.
	 * @param agentInterface The agent interface supplied.
	 */
	public void destroyAgent(Object agentInterface);

    /**
     * A partition key divides the vlh space into arbitrary, named spaces.
     * <p>
     * This method allows you to create a public partition without security impositions.
     * @exception IllegalArgumentException proposedKey is a dupe or malformed or excessively long (sb 64 char or less).
     * @exception RemoteException The server is not responding.
     * @exception ServerException The owner is not registered to create a partition or some other
     * protocol violation of the specific implementation.
     * @param proposedKey The proposed partition key.
     * @param owner The registering owner of the new partition.*/
    public void createPartitionKey(String proposedKey, String owner) 
						throws IllegalArgumentException, RemoteException, ServerException, Exception;
	
	/**
	 * Partitions may be created with a vlh security protocol.
	 * <p>
	 * The rational is similar to a class loader. If you create a partition, only you can destroy it using the rw
	 * certificate provided. 
	 * <p>
	 * Further, you can protect others from changing the contents of the vlh by keeping secret your Read-Write
	 * certificate which you provide in this creation method. You can provide read access by publishing the 
	 * Read Certificate.
	 * <p>
	 * Both certificates are mandatory and may not be null to create
	 * a secured partition and its key. If a VLH
	 * partition is created under this protocol, 
	 * it must be accessed under this protocol using the certificates.
	 * Users can access the vlh with one, the other, or both. 
	 * Certificates are encrypted over the wire so SSL is not needed to protect from eavesdropping.
     * @param proposedKey The proposed partition key.
     * @param owner The registering owner of the new partition.
     * @param immutableCertificate Must be not null and will be saved as the certificate allowing Read-Write access.
     * @param mutableCertificate May be null and will be saved as the certificate allowing Read access.
     * @exception IllegalArgumentException proposedKey is a dupe or malformed or excessively long (sb 64 char or less).
     * @exception RemoteException The server is not responding.
     * @exception ServerException The owner is not registered to create a partition or some other
	 */
	public void createPartitionKey(String proposedKey, String owner, String immutableCertificate, String mutableCertificate) throws 
						IllegalArgumentException, RemoteException, ServerException, Exception;													

    /**
     * A partition key may be removed.
     * <p>
     * This method allows you to remove one which is public.
     * @exception IllegalArgumentException proposedKey is a dupe or malformed or excessively long (sb 64 char or less).
     * @exception RemoteException The server is not responding.
     * @exception ServerException The owner is not registered to create a partition or some other
     * protocol violation of the specific implementation.
     * @param existingKey The proposed partition key.
     * @param owner The registering owner of the new partition.*/
	public void removePartitionKey(String existingKey, String owner)
    		throws IllegalArgumentException, RemoteException, ServerException, Exception;
	
     /**
     * A partition key may be removed securely according to the vlh security protocol.
     * @param existingKey The existing partition key.
     * @param owner The registering owner of the existing partition.
     * @param immutableCertificate The certificate used at creation time.
     * @param mutableCertificate The certificate used at creation time.
     * @exception IllegalArgumentException existingKey or certificates do not match.
     * @exception RemoteException The server is not responding.
     * @exception ServerException The server has discovered that the arguments are illegal or the operation cannot be
     * completed due to a protocol violation of the specific implementation.
     */
	public void removePartitionKey(String existingKey, String owner, String immutableCertificate, String mutableCertificate) throws
    					IllegalArgumentException, RemoteException, ServerException, Exception;
	
   /**
     * Initializes the connection to the vlh server.
     * <p>
     * This connection may be closed using the close semantic
     * and, in some implementations, the connection
     * is durable: if the connection is lost it will
     * automatically be retried.
     * @param server The name of the server.
     * @param portNumber The port number to connect with.
     * @exception RemoteException The server is not available to make a connection with
     * the parameters supplied.
     */
    public void initConnection(String server, int port) throws RemoteException, Exception;

    /**
     * Closes the current connection and places the VLHProxy
     * into a disconnected state.
     * @exception Exception The server or the client was not able to perform the close.
     */
    public void close() throws Exception;

    /**
     * Tests if the proxy is currently connected to the server.
     */
    public boolean connected();
	
}
