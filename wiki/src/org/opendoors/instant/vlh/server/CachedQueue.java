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
package org.opendoors.instant.vlh.server;
import java.util.Hashtable;
import org.opendoors.adt.*;
import org.opendoors.util.*;


/**
 * The CachedQueue is a specialized queue
 * optimized for the PutValue when caching and queuing are both enabled.
 * <p>
 * With caching enabled, this class provides the following implementation:
 * <p>
 * <ul>
 * <li>Only a PutValue is currently cached.</li>
 * <li>Before a PutValue is placed on the queue, if the same
 * key and partition is already on the queue, its value is replaced
 * with the new value. This operation is synchronized.</li>
 * <li>When a put value is taken off the queue, it is no longer cached.<li>
 * @author Lane Sharman
 */
class CachedQueue extends Queue {

	public int totalReads = 0;
	public int totalWrites = 0;
	public int totalCacheHits = 0;
	public int totalCacheReadHits = 0;
	public int totalCacheReadValueHits = 0;

	private Object mutex = new Object();
	private Hashtable cache = new Hashtable(VLHServiceEnv.initialCacheWriteSize, (float) .5);
	private Hashtable cacheValue = new Hashtable(VLHServiceEnv.initialCacheWriteSize, (float) .5);
	
	public void clear() {
		synchronized (mutex) {
			super.clear();
			cache.clear();
			cacheValue.clear();
		}
	}
			

	/**
	 * Finds the cached value if it exists. Only PutValues are cached.
	 */
	public byte[] readCachedValue(String partition, String key) {
		String cacheKey = partition + key;
		synchronized (mutex) {
			PutValue existingP = (PutValue) cache.get(cacheKey);
			if (existingP != null) {
				totalCacheReadHits++;
				return existingP.value;
			}
		}
		return null;
	}
	
	/**
	 * Uses the local cache to search by value and returns
	 * true if the byte array is contained in the cache to be written.
	 */
	public boolean containsValue(String partition, byte[] value) {
		String valueKey = partition + new String(value);
		synchronized (mutex) {
			PutValue existingP = (PutValue) cacheValue.get(valueKey);
			if (existingP != null) {
				totalCacheReadValueHits++;
				return true;
			}
		}
		return false;
	}
	/**
	 * Appends the object to the queue. If it is a PutValue,
	 * the cache is tested.
	 */
	public void enqueue(Object o) {
		ThreadedQueueItem i = (ThreadedQueueItem) o; // uses the ThreadedQueue
		if (i.object instanceof QueuableVLHMethod) {
			if (i.object instanceof PutValue) {
				totalWrites++;
				PutValue p = (PutValue) i.object;
				String cacheKey = p.partition + p.key;
				Object valueKey = p.partition + new String(p.value);
				synchronized (mutex) {
					PutValue existingP = (PutValue) cache.get(cacheKey);
					if (existingP != null) {
						// cached once already
						totalCacheHits++;
						// get rid of old cached value:
						cacheValue.remove(p.partition + new String(p.value));
						//replace value:
						existingP.value = p.value; // replaces the value in the object
					}
					else {
						cache.put(cacheKey, p); // place the new put value object in the cache
						super.enqueue(o); // place the item in the queue
					}
					// add the cachedValue:
					cacheValue.put(valueKey, p);
				}
			}
			else {
				super.enqueue(o);
			}
		}
		else {
			super.enqueue(o);
		}
	}

	/**
	 * Dequeues the object from the the queue.
	 */
	public Object dequeue() {
		Object o = super.dequeue();
		ThreadedQueueItem i = (ThreadedQueueItem) o; // uses the ThreadedQueue
		if (i.object instanceof PutValue) {
			totalReads++;
			PutValue p = (PutValue) i.object;
			synchronized (mutex) {
				cache.remove(p.partition + p.key); // take it out of the queue
				cacheValue.remove(p.partition + new String(p.value));
			}
		}
		return o;
	}
}

