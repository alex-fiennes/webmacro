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
 * As a subclass of Hashtable, implements the client-side
 * methods of the VLH Service.
 * <p>
 * This class is an agent. It provides,
 * through connectivity to the VLH Server, 
 * a robust set of functions nearly identical to the JDK 1.1
 * Hashtable. The only real exception is that
 * keys are strings not objects.
 * <p>
 * This class should be subclassed with care. Subclasses
 * can add things like local caching, atomicity, and text searching.
 * Consideration must be given to whether a function should be
 * implemented on the server or the client side.
 * @author Lane Sharman
 * @version 1.0
 */
public class AgentImpl extends Hashtable implements Store  {

	protected static int INITIAL = 1000;
	protected static float LOAD = (float) .5;
	protected String server;
	protected int port;
	protected ConnectionService connectionService;
	protected String partition;
	protected LogService logService;
	protected boolean immutableAuthorized;
	private boolean mutableAuthorized;
	

	/**
	 * The constructor does not establish a connection.
	 */
	protected AgentImpl(LogService logService) {
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
	protected void getConnected(String server, int port, String partition,
								String immutableCertificate, String mutableCertificate) throws Exception {
		connectionService = new ConnectionService();
		connectionService.init(connectionService, server, port);
		this.server = server;
		this.port = port;
		this.partition = partition;
		setAuthorization(immutableCertificate, mutableCertificate);
	}
	
	/**
	 * Disconnects and helps the GC.
	 */
	protected void destroy() {
		disconnect();
		connectionService = null;
		server = null;
		partition = null;
	}
		

	/**
	 * Disconnects the hashtable from the vlh server and disables it.
	 */
	protected void disconnect() {
		try {
			connectionService.shutDown();
		}
		catch (Exception e) {
			if (logService != null)
				logService.error("AgentImpl. Unable to disconnect", connectionService, e);
		}
	}

	/**
	 * Returns the number of elements in the vlh.
	 */
    public synchronized int size() {
		if (! immutableAuthorized )
			throw new IllegalStateException("Partition protected and immutable certificate not provided");
		try {
			connectionService.verifyConnectivity();
			Size k = new Size();
			k.setValues(partition);
			Size received = null;
			synchronized (connectionService.receivingMutex) {
				connectionService.waiting = true;
				connectionService.sendObject(k);
				connectionService.receivingMutex.wait();
				if (connectionService.received instanceof Exception)
					throw (Exception) connectionService.received;
				received = (Size) connectionService.received;
			}
			return received.count;
		}
		catch (Exception e) {
			throw new IllegalStateException("AgentImpl. Unable to perform size(): " + e.toString());
		}
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
		try {
			connectionService.verifyConnectivity();
			Keys k = new Keys();
			k.setValues(partition);
			Keys received = null;
			synchronized (connectionService.receivingMutex) {
				connectionService.waiting = true;
				connectionService.sendObject(k);
				connectionService.receivingMutex.wait();
				if (connectionService.received instanceof Exception)
					throw (Exception) connectionService.received;
				received = (Keys) connectionService.received;
			}
			return received.keys();
		}
		catch (Exception e) {
			throw new IllegalStateException("AgentImpl. Unable to perform keys(): " + e.toString());
		}
     }

	/**
	 * Returns an enumeration of all the values in the vlh for the current partition.
	 */
    public synchronized Enumeration elements() {
		if (! immutableAuthorized )
			throw new IllegalStateException("Partition protected and immutable certificate not provided");
		try {
			connectionService.verifyConnectivity();
			Elements k = new Elements();
			k.setValues(partition);
			synchronized (connectionService.receivingMutex) {
				connectionService.waiting = true;
				connectionService.sendObject(k);
				connectionService.receivingMutex.wait();
				if (connectionService.received instanceof Exception)
					throw (Exception) connectionService.received;
				k = (Elements) connectionService.received;
			}
			return k.elements();
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			throw new IllegalStateException("AgentImpl. Unable to perform elements(): " + e.toString());
		}
    }
	
	/**
	 * If true, there is at least one element in the vlh equal to this value.
	 * <p>
	 * <b>NOTE:</b> Two objects are equal if the serialized representation of the objects are equal.
	 */
	public synchronized boolean containsKey(String value) {
      return containsKey ((Object) value);
/**		if (value == null)
		    throw new NullPointerException();
		if (! immutableAuthorized )
			throw new IllegalStateException("Partition protected and immutable certificate not provided");
		try {
			connectionService.verifyConnectivity();
			SearchValue g = new SearchValue();
			g.setValues(partition, value);
			SearchValue received = null;
			synchronized (connectionService.receivingMutex) {
				connectionService.waiting = true;
				connectionService.sendObject(g);
				connectionService.receivingMutex.wait();
				if (connectionService.received instanceof Exception)
					throw (Exception) connectionService.received;
				received = (SearchValue) connectionService.received;
			}
			return received.containsValue();
		}
		catch (Exception e) {
			throw new IllegalStateException("AgentImpl. Unable to perform contains(): " + e.toString());
		}
**/
	}

	/**
	 * If true, the vlh contains this key which must be a String.
	 */
    public synchronized boolean containsKey(Object key) {
		if (key == null)
		    throw new NullPointerException();
		if ( !(key instanceof String) || (((String)key).length() > VLHProxy.MAX_HASH_KEY_LENGTH))
			throw new IllegalArgumentException("Key not string or longer than " + VLHProxy.MAX_HASH_KEY_LENGTH);
		if (! immutableAuthorized )
			throw new IllegalStateException("Partition protected and immutable certificate not provided");

		try {
			connectionService.verifyConnectivity();
			SearchKey g = new SearchKey();
			g.setValues(partition, (String) key);
			SearchKey received = null;
			synchronized (connectionService.receivingMutex) {
				connectionService.waiting = true;
				connectionService.sendObject(g);
				connectionService.receivingMutex.wait();
				if (connectionService.received instanceof Exception)
					throw (Exception) connectionService.received;
				received = (SearchKey) connectionService.received;
			}
			return received.containsKey();
		}
		catch (Exception e) {
			throw new IllegalStateException("AgentImpl. Unable to perform containsKey(): " + e.toString());
		}
	}
		
    /**
     * Returns the value associated with this key. If the key does not exist, null
     * is returned.
     * @param key The reference must point to a string. VLH uses String keys.
     * @return The value mapped by the key or null if no such mapping exists.
     */
	public synchronized Object get(Object key) {
		if (key == null)
		    throw new NullPointerException();
		if ( !(key instanceof String) || (((String)key).length() > VLHProxy.MAX_HASH_KEY_LENGTH))
			throw new IllegalArgumentException("Key not string or longer than " + VLHProxy.MAX_HASH_KEY_LENGTH);
		if (! immutableAuthorized )
			throw new IllegalStateException("Partition protected and immutable certificate not provided");
			
		try {
			connectionService.verifyConnectivity();
			GetValue g = new GetValue();
			g.setValues(partition, (String) key);
			GetValue received = null;
			synchronized (connectionService.receivingMutex) {
				connectionService.waiting = true;
				connectionService.sendObject(g);
				connectionService.receivingMutex.wait();
				if (connectionService.received instanceof Exception)
					throw (Exception) connectionService.received;
				received = (GetValue) connectionService.received;
			}
			return received.getValue();
		}
		catch (Exception e) {
			throw new IllegalStateException("AgentImpl. Unable to perform get(): " + e.toString());
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
		try {
			connectionService.verifyConnectivity();
			PutValue p = new PutValue();
			p.setValues(partition, (String) key, value);
			connectionService.sendObject(p);
			return null;
		}
		catch (Exception e) {
			throw new IllegalStateException("AgentImpl. Unable to perform put(): " + e.toString());
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
		try {
			connectionService.verifyConnectivity();
			RemoveValue p = new RemoveValue();
			p.setValues(partition, (String) key);
			connectionService.sendObject(p);
			return null;
		}
		catch (Exception e) {
			throw new IllegalStateException("AgentImpl. Unable to perform remove(): " + e.toString());
		}
    }

	/**
	 * Clears the entire vlh with the current partition.
	 */
    public void clear() {
		if (! mutableAuthorized )
			throw new IllegalStateException("Partition protected and mutable certificate not provided");
		try {
			connectionService.verifyConnectivity();
			RemoveAll p = new RemoveAll();
			p.setValues(partition);
			connectionService.sendObject(p);
		}
		catch (Exception e) {
			throw new IllegalStateException("AgentImpl. Unable to perform clear(): " + e.toString());
		}
    }

	/**
	 * Not supported and always throws an exception.
	 */
    public Object clone() {
		throw new IllegalStateException("clone() not supported.");
    }
    
    public String toString() {
		return "VLH Agent Mapping to Partition=" + partition;
    }
	
	// extensions to the hashtable.
	
	/**
	 * Operations which put values to the hash such as put() are asynchronously
	 * performed. This method flushes any operations which are queued but not
	 * completed. Operations like size() do not inspect the volatile queue. To get
	 * an accurate picture of the vlh, preceed it with a flush().
	 */
	protected synchronized void flush() {
		try {
			Flush f = new Flush();
			synchronized (connectionService.receivingMutex) {
				connectionService.waiting = true;
				connectionService.sendObject(f);
				connectionService.receivingMutex.wait();
			}
		}
		catch (Exception e) {
			throw new IllegalStateException("AgentImpl. Unable to perform flush(): " + e.toString());
		}
	}

	/**
	 * Sets the authorization level for use of the vlh.
	 */
	protected synchronized void setAuthorization(String immutableProvided, String mutableProvided) {
		try {
			Authorize a = new Authorize();
			a.setValues(partition, immutableProvided, mutableProvided);
			synchronized (connectionService.receivingMutex) {
				connectionService.waiting = true;
				connectionService.sendObject(a);
				connectionService.receivingMutex.wait();
				if (connectionService.received instanceof Exception)
					throw (Exception) connectionService.received;
				a = (Authorize) connectionService.received;
				immutableAuthorized = a.immutableAuthorized;
				mutableAuthorized = a.mutableAuthorized;
			}
		}
		catch (Exception e) {
			throw new IllegalStateException("AgentImpl. Unable to perform authorize(): " + e);
		}
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


	// the delegation class for incoming clients
	/**
	 * to do: solve potential race condition of object received before waiting resulting
	 * in infinite wait.
	 */
	protected class ConnectionService extends AbstractAgent implements ObjectListener {

		Object receivingMutex = new Object();
		Object received;
		boolean waiting = false;

		public void objectReceived(Object object, Connection connection) {
			// handle errors:
			if (object instanceof Throwable) {
				if (logService != null)
					logService.error("AgentImpl. Received error or exception from server.", this, (Throwable) object);
			}

			// application objects:
			synchronized (receivingMutex) {
				if (waiting) {
					waiting = false;
					received = object;
					receivingMutex.notify();
				}
			}
		}

		public void connectionClosing(Connection connection) {		}

		/**
		 * Throws an illegal state if not connected.
		 */
		void verifyConnectivity() {
			if (connected())
				return;
			else
				throw new IllegalStateException("AgentImpl. Not connected to server");
		}
	}

    
}
