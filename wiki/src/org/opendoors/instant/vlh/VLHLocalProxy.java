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
import org.opendoors.intf.QueueListener;

/**
 * VLHLocalProxy is the general client-side factory for vlh services on localhost.
 * <p>
 * Applications may want to provide a wrapper for this class. This interface exploits
 * and sets up the file system so it can be used as a backing store. 
 * This proxy returns the same kind of objects as VLHProxy so that client
 * applications can implement crossloading: interchange between local and remote
 * vlh stores.
 * <p>
 * The VLHLocalProxy provides for establishing a LocalAgent efficiently and without complication. The only
 * requirement for obtaining a LocalAgent is that the client application be able to present its
 * partition key. The VLHProxy has a convenience method for creating/removing new keys.
 * <p>
 * Keys must map to file system syntax as to white space, length, and character type.
 * A partition key maps to a directory and a hash key maps to a filename inside the directory.
 * The content of the file is the content of the element plus some management values.
 * <p>
 * VLHLocalProxy is <b>not</b>connection oriented unlike VLHProxy.
 * @author Lane Sharman
 * @see VLHProxy
 * @version 1.0
 * @since 05-Sept-2000
 */
public interface VLHLocalProxy {

	/**
     * @see VLHProxy
     */
	public void setExceptionHandler(QueueListener exceptionHandler);

   /**
	* @see VLHProxy
    */
    public void setLogHandler(PrintStream log);

    /**
     * @see VLHProxy
     */
     public Store getAgentAsStore(String partitionKey,
    							 String immutableCertificate, String mutableCertificate) 
    	throws IllegalArgumentException, RemoteException, Exception;
 
	/**
     * @see VLHProxy
	 */
	public void createPartitionKey(String proposedKey, String owner, String immutableCertificate, String mutableCertificate) throws 
						IllegalArgumentException, RemoteException, ServerException, Exception;													

    /**
	 * @see VLHProxy
     */
	public void removePartitionKey(String existingKey, String owner, String immutableCertificate, String mutableCertificate) throws
    					IllegalArgumentException, RemoteException, ServerException, Exception;
	
    /**
     * Initializes the local connection to the vlh service.
     * <p>
     * This service opens up a thread allowing gets() and puts()
     * to be accomplished asynchronously to a local store
     * efficiently and with performance.
     * @param rootDirectory The directory which is a root partition directory. This directory
     * should be dedicated to containing vlh partitions and vlh elements.
     * Care must be taken when opening a multiple proxies to the same root directory.
     */
    public void initConnection(String rootDirectory) throws Exception;

	/**
	 * Close the connection to the File Service.
	 */
    public void close() throws java.lang.Exception;

}
