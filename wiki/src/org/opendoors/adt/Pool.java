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
package org.opendoors.adt;



/**
 * A base, abstract class for managing a pool of arbitrary reuseable objects.
 * <p>
 * An implementation of this class need only be able to create
 * a fresh array of the underlying pooled object.
 * <p>
 * Typical usage would be NetworkConnection.getInstance.get() and
 * NetworkConnection.getInstance.free(Object)
 * <p>
 * This class does not keep track of objects dequeued and, thus, in use.
 * Performance is thus maximized. The get() and free() methods are completely
 * thread safe. Objects no longer in use must be freed using free().
 * <p>
 * Pools are dynamically increased when load exceeds supply, eg, when
 * there are no free objects.
 * <p>
 * Subclasses will implement, of course, variations on this theme.
 * @author  Lane Sharman
 */
public abstract class Pool {


	/** Needed to make thread safe. */
	private Object mutex = new Object();

	/** The pool container of free objects. */
	private Queue free = new Queue();

	/**
	 * Subclasses must implement pool creation and return this
	 * list as an array of objects, eg, an array of java.sql.Connection.
	 * <p>
	 * If the pool cannot be created, throw an exception.
	 */
	protected abstract Object[] createPool() throws Exception;

	/**
	 * Returns the current size of the pool.
	 */
	public int size() {
		return free.size();
	}

	/**
	 * Get an object that is free to use.
	 */
	public Object get() throws Exception {
		// make thread safe:
		synchronized (mutex) {
			if (free.size() == 0) {
				increasePool();
			}
			return getFree();
		}
	}

	/**
	 * Return an object to the pool so it can be reused.
	 */
	public void free(Object pooledObject) throws Exception {
		synchronized (mutex) {
			free.enqueue(pooledObject);
		}
	}

	/**
	 * Abandons an object which is broken.
	 */
	public void abandon(Object pooledObject) throws Exception {}
		// admin notification? other action?

	/**
	 * Removes an object from the free list and 
	 * adds it to the inUse list and returns it.
	 */
	private Object getFree() throws Exception {
		return free.dequeue();
	}

	/**
	 * Increases the free vector with a fresh supply of pooled objects.
	 */
	private void increaseFree(Object[] newPoolObjects) throws Exception {
		for (int index = 0; index < newPoolObjects.length; index++)
			free.enqueue(newPoolObjects[index]);
	}

	/**
	 * Called whenever the "load" exceeds the capacity of the
	 * free pool. Dynamically increases the free pool size.
	 */
	private void increasePool() throws Exception {
		// load exceeds capacity!
		increaseFree(createPool());
	}

}


