/*
* Copyright Open Doors Software and Acctiva, 1996-2001.
* All rights reserved.
*
* Software is provided according to the MPL license.
* Open Doors Software and Acctiva provide this
* software on an as-is basis and make no representation as
* to fitness for a specific purpose.
*
* Direct all questions and comments to support@opendoors.com
*/

package org.opendoors.cache.immutable;
import java.util.HashMap;
import java.util.Map;
import org.opendoors.cache.UpdateableCache;

/**
 * Implements the updateable cache interface according
 * to the immutable / mutable
 * cache strategy of VFC.
 */
class CacheImpl implements UpdateableCache  {

  /**
   * The immutable cache with a volatile reference
   * so that its reference is cached in local memory.
   */
  volatile HashMap immutable;

  /** The mutable cache used for updates. */
  volatile HashMap mutable;

  /** The change control agent. */
  ChangeControl changeControl;

  /** The change control mutex. */
  Object changeControlMutex;

  /**
   * Constructs and preloads the cache
   * and if there is a subclass of ChangeControl
   * it is to be used instead of the default
   * base class.
   * @param preload A map to place into the cache and it
   * may be null.
   * @param changeControlClass The class which implements change
   * control and this may be null in which case VFC provides
   * the change control mechanics.
   * @param refreshRate the period of time between generational
   * updates provided the cache is idle during the refresh period.
   */
  CacheImpl(Map preload, Class changeControlClass, int refreshRate) {

    int load = 500;
    if (preload != null) load = preload.size() * 2;
    immutable = new HashMap(load, (float)0.5);
    mutable = new HashMap(load, (float)0.5);
    if (preload != null) {
      immutable.putAll(preload);
      mutable.putAll(preload);
    }

    if (changeControlClass == null) {
      changeControl = new ChangeControl();
    }
    else {
      try {
        changeControl = (ChangeControl) changeControlClass.newInstance();
      }
      catch (Exception e) {
        e.printStackTrace(System.err);
        changeControl = new ChangeControl();
      }
    }
    changeControlMutex = changeControl.setCacheImpl(this, refreshRate);
  }

  // the interface implementations
 	/**
	 * Gets a value from the immutable cache.
	 * <p>
	 * Note: this implementation will also
	 * search the mutable cache if the element
	 * is not found in the mutable cache.
	 * @param argument The key to the element in the cache.
	 */
	public Object get(Object argument) {
    HashMap h;
    synchronized (immutable) { h = immutable; }
    Object o = h.get(argument);
    if (o == null) {
      synchronized (changeControlMutex) {
        o = mutable.get(argument);
      }
    }
    return o;
	}

  /** Returns the elements in the cache as an array using the current image. */
  public Object[] values() {
    Object[] values = null;
    synchronized (changeControlMutex) {
      values = mutable.values().toArray();
    }
    return values;
  }

  /** Returns the keys of the cache as an array. */
  public Object[] keys() {
    Object[] keys = null;
    synchronized (changeControlMutex) {
      keys = mutable.keySet().toArray();
    }
    return keys;
  }
    

	/**
   * Invalidates an object in the cache by removing
   * it from the mutable cache. On the next
   * regeneration, this element will no longer be in the
   * cache.
   * @param argument The key to the element in the cache.
   */
  public void invalidate(Object argument) {
    changeControl.invalidate(argument);
  }

  /**
   * Invalidates the entire cache.
   */
  public void invalidateAll() {
    changeControl.invalidateAll();
  }

	/**
	 * Puts an object into the cache by
	 * enqueueing it on the immutable cache using
	 * the change control delegate.
	 * @param argument The key to the element in the cache.
	 * @param value The element to place into the cache.
	 */
	public void put(Object argument, Object value)  {
	  changeControl.put(argument, value);
	}

  // management methods

	/** Allows for the cache to be explicitly updated. */
	public void update() {
	  changeControl.update(null, null);
	}

  /** Destroys this instance allowing for speedy GC action. */
  void destroy() {
    changeControl.destroy();
    immutable = null;
    mutable = null;
    changeControl = null;
    changeControlMutex = null;
  }
    

}
