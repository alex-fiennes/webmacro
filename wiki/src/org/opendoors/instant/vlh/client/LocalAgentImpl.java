/*
* Copyright Open Doors Software and Acctiva, 2000.
*
* Software is provided according to the ___ license.
* Open Doors Software and Acctiva provide this
* software on an as-is basis and make no representation as
* to fitness for a specific purpose.
*
* Direct all questions and comments to support@opendoors.com
*/
package org.opendoors.instant.vlh.client;

import java.util.Hashtable;
import java.util.Enumeration;
import org.opendoors.instant.lcis.*;
import org.opendoors.instant.vlh.server.*;
import org.opendoors.instant.vlh.*;

/**
 * A subclass of Hashtable, implements the client-side
 * methods of the VLH Service using local storage.
 * <p>
 * This class is an agent. It delegates
 * all execution to individual components executing local file system
 * methods.
 * @author Lane Sharman, Eric Ridge
 * @version 1.0
 */
public class LocalAgentImpl extends Hashtable implements Store  {

	protected static int INITIAL = 1000;
	protected static float LOAD = (float) .5;
	protected String partition;
	protected String rootDirectory;
	protected LogService logService;
	protected boolean immutableAuthorized;
	private boolean mutableAuthorized;
	private Object singleThreaded = new Object();
	

	/**
	 * The constructor does not establish a connection.
	 */
	protected LocalAgentImpl(LogService logService) {
		super(INITIAL, LOAD);
		this.logService = logService;
	}

	/**
	 * Connects the hashtable to the vlh server and a specific partition.
	 * <p>
	 * The proxy is responsible for getting this instance connected.
	 * Once connected, the instance will stay connected until disconnected.
	 * @param server The server to connect to.
	 * @param port The port to use.
	 * @param partition The partition the Hashtable maps to.
	 */
	protected void getConnected(String rootDirectory, String partition,
								String immutableCertificate, String mutableCertificate) throws Exception {
		this.rootDirectory = rootDirectory;
		this.partition = partition;
		setAuthorization(immutableCertificate, mutableCertificate);
	}
	
	/**
	 * Disconnects and helps the GC.
	 */
	protected void destroy() {
		rootDirectory = null;
		partition = null;
	}
		

	/**
	 * Returns the number of elements in the vlh.
	 */
    public synchronized int size() {
		if (! immutableAuthorized )
			throw new IllegalStateException("Partition protected and immutable certificate not provided");
		Size s = new Size();
		s.setValues(partition);
		s.localExecution(rootDirectory);
		return s.size();
   }

	/**
	 * Returns true if there are no elements in the vlh with the current partition.
	 */
    public boolean isEmpty() {
		return (size() == 0);
    }

	/**
	 * Returns an enumeration of all the keys in the vlh for the current partition.
	 */
    public synchronized Enumeration keys() {
		if (! immutableAuthorized )
			throw new IllegalStateException("Partition protected and immutable certificate not provided");
		Keys k = new Keys();
		k.setValues(partition);
		k.localExecution(rootDirectory);
		return k.keys();
	}

	/**
	 * Returns an enumeration of all the values in the vlh for the current partition.
	 */
    public synchronized Enumeration elements() {
		throw new IllegalStateException("Not implemented");
    }
	
	/**
	 * If true, there is at least one element in the vlh equal to this value.
	 * <p>
	 * <b>NOTE:</b> Two objects are equal if the serialized representation of the objects are equal.
	 */
	public synchronized boolean containsKey(String key) {
		return containsKey ((Object) key);
	}

	/**
	 * If true, the vlh contains this key which must be a String.
	 */
    public boolean containsKey(Object key) {
		if (key == null)
		    throw new NullPointerException();
		if ( !(key instanceof String) || (((String)key).length() > VLHProxy.MAX_HASH_KEY_LENGTH))
			throw new IllegalArgumentException("Key not string or longer than " + VLHProxy.MAX_HASH_KEY_LENGTH);
		if (! immutableAuthorized )
			throw new IllegalStateException("Partition protected and immutable certificate not provided");

		SearchKey g = new SearchKey();
		g.setValues(partition, (String) key);
		g.localExecution(rootDirectory);
		return g.containsKey();
	}
		
    /**
     * Returns the value associated with this key. If the key does not exist, null
     * is returned.
     * @param key The reference must point to a string. VLH uses String keys.
     * @return The value mapped by the key or null if no such mapping exists.
     */
	public Object get(Object key) {
		if (key == null)
		    throw new NullPointerException();
		if ( !(key instanceof String) || (((String)key).length() > VLHProxy.MAX_HASH_KEY_LENGTH))
			throw new IllegalArgumentException("Key not string or longer than " + VLHProxy.MAX_HASH_KEY_LENGTH);
		if (! immutableAuthorized )
			throw new IllegalStateException("Partition protected and immutable certificate not provided");
		try  {			
			GetValue g = new GetValue();
			g.setValues(partition, (String) key);
			g.localExecution(rootDirectory);
			return g.getValue();
    	}
    	catch (Exception e)  {
    		throw new IllegalStateException(e.toString());
		}
    }

    /**
     * Puts the value associated with this key into the vlh. 
     * @param key The reference must point to a string. VLH uses String keys.
     * @param value The object which must be serializable.
     * @return No value is ever returned even if this mapping replaces an existing mapping.
     */
    public Object put(Object key, Object value) {
		if (value == null || key == null)
		    throw new NullPointerException();
		if ( !(key instanceof String) || (((String)key).length() > VLHProxy.MAX_HASH_KEY_LENGTH))
			throw new IllegalArgumentException("Key not string or longer than " + VLHProxy.MAX_HASH_KEY_LENGTH);
		if (! mutableAuthorized )
			throw new IllegalStateException("Partition protected and mutable certificate not provided");

		try  {
        	PutValue p = new PutValue();
			p.setValues(partition, (String) key, value);
			p.localExecution(rootDirectory);
			return null;
   		}
    	catch (Exception e)  {
    		throw new IllegalStateException(e.toString());
		}
   	}

	/**
	 * Removes an element from the vlh where the element is mapped by the key provided.
	 */
    public Object remove(Object key) {
		if (key == null)
		    throw new NullPointerException();
		if ( !(key instanceof String) || (((String)key).length() > VLHProxy.MAX_HASH_KEY_LENGTH))
			throw new IllegalArgumentException("Key not string or longer than " + VLHProxy.MAX_HASH_KEY_LENGTH);
		if (! mutableAuthorized )
			throw new IllegalStateException("Partition protected and mutable certificate not provided");
		try  {
			RemoveValue p = new RemoveValue();
			p.setValues(partition, (String) key);
			p.localExecution(rootDirectory);
			return null;
   		}
    	catch (Exception e)  {
    		throw new IllegalStateException(e.toString());
		}
    }

	/**
	 * Clears the entire vlh with the current partition.
	 */
    public void clear() {
		throw new IllegalStateException("Not implemented");
    }

	/**
	 * Not supported and always throws an exception.
	 */
    public Object clone() {
		throw new IllegalStateException("clone() not supported.");
    }
    
    public String toString() {
		return "VLH Local Agent Mapping to Partition=" + partition;
    }
	
	// extensions to the hashtable.
	
	/**
	 * Operations which put values to the hash such as put() are asynchronously
	 * performed. This method flushes any operations which are queued but not
	 * completed. Operations like size() do not inspect the volatile queue. To get
	 * an accurate picture of the vlh, preceed it with a flush().
	 */
	protected synchronized void flush() {
		throw new IllegalStateException("Not implemented");
	}

	/**
	 * Sets the authorization level for use of the vlh.
	 */
	protected synchronized void setAuthorization(String immutableProvided, String mutableProvided) throws Exception {
		Authorize a = new Authorize();
		a.setValues(partition, immutableProvided, mutableProvided);
		a.localExecution(rootDirectory);
		immutableAuthorized = a.immutableAuthorized;
		mutableAuthorized = a.mutableAuthorized;
	}

	// the Store interface
	public Object put (String key, Object value) {
		return this.put((Object) key, value);
	}

	public Object get (String key) {
		return this.get((Object) key);
	}
	
	public Object remove (String key) {
		return this.remove((Object) key);
	}
   
	public boolean contains (String key) {
		return this.contains((Object) key);
	}

}
