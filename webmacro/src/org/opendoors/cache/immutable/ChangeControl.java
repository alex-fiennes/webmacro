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
import java.util.*;
import org.opendoors.util.PostponeObservable;

/**
 * Default behavior for managing
 * the mutable and immutable cache as implemented
 * by CacheImpl.
 * <p>
 * Advanced implementations of the Immutable/Mutable
 * strategy may elect to subclass this class
 * and provide fine grained 
 * change control for a specific type of cache.
 * <p>
 * For example a cluster of file caches would
 * possibly require a change control manager which
 * would sweep from time to time a disk volume and
 * matching files in the cache which were out
 * of date would be replaced in the cache. This
 * kind of functionality can be performed as well
 * by a cache manager wrapping itself around the
 * cache interface.
 * <p>
 * This class delegates observability of the cache to
 * an instance of PostponeObservable.
 * In this manner, updates to the immutable
 * cache are postponed until a sufficient period
 * of idle activity has elapsed before changes are
 * propagate to the immutable image.
 * @see org.opendoors.util.PostponeObservable
 * @see org.opendoors.cache.Cache
 * @see org.opendoors.cache.UpdateableCache
 */
public class ChangeControl implements Observer {

  /** The actual cache under management. */
  protected CacheImpl cache;

  /**
   * The thread delegate for managing changes.
   * By default, 5 seconds of inactivity must occur
   * before pending updates are propagated
   * and the immutable cache is upgraded.
   */
  protected PostponeObservable observable;

  /** The synch mutex. */
  protected Object actionMutex = new Object();

  /**
   * The default constructor
   * which does nothing.
   */
  public ChangeControl () {  }

  /**
   * Sets up the cache for change control: the cache, the refresh
   * rate and returns the mutex.
   * @param cache The cache to manage.
   * @param refreshRate How often in millis to refresh the cache.
   * @return The mutex synchronizing updates.
   */
  protected Object setCacheImpl(CacheImpl cache, int refreshRate) {
    this.cache = cache;
    observable = new PostponeObservable(refreshRate, true);
    observable.addObserver(this);
    return actionMutex;
  }

  /**
   * Schedules a removal of an element from the cache.
   */
  void invalidate(Object argument) {
    synchronized (actionMutex) {
      cache.mutable.remove(argument);
      observable.propertyChange(null);
    }
  }

  /**
   * Schedules a removal of everything.
   */
  void invalidateAll()  {
    synchronized (actionMutex) {
      cache.mutable.clear();
      observable.setChanged();
    }
  }


  /**
   * Schedules a put to the queue.
   */
  void put(Object argument, Object value) {
    synchronized (actionMutex) {
      cache.mutable.put(argument, value);
      observable.setChanged();
    }
  }

  /**
   * Call back for changes pending to
   * be implemented.
   * <p>
   * Cache has been idle for at least n seconds.
   * <p>
   * This routine implements the generational update
   * such that the immutable becomes identical now
   * to the most current image in the mutable instance.
   */
  public void update(Observable o, Object arg) {
    synchronized (actionMutex) {
      HashMap tmp = new HashMap(cache.mutable.size()*2, (float) 0.5);
      tmp.putAll(cache.mutable);
      cache.immutable = tmp;
      //System.out.println("Cache Updated. Mutable Size=" + cache.mutable.size());
      //System.out.println("Cache Updated. Size=" + cache.immutable.size());
    }
  }

  /**
   * Destroys this instance making it inoperable.
   */
  void destroy() {
    synchronized (actionMutex) {
      cache.mutable.clear();
      cache.immutable.clear();
      cache = null;
    }
    observable.destroy();
    observable = null;
    actionMutex = null;
  }
    
  
}

