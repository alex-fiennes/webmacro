/*
* Copyright Open Doors Software and Acctiva, 1996-2001.
*
* Software is provided according to the MPL license.
* Open Doors Software and Acctiva provide this
* software on an as-is basis and make no representation as
* to fitness for a specific purpose.
*
* Direct all questions and comments to support@opendoors.com
*/
package org.opendoors.cache;
import java.util.Properties;
import java.util.Hashtable;

/**
 * The base interface for every cache regardless of
 * implementation.
 * <p>
 * The interface supports the basic semantics of
 * a cache inluding accessor, mutator, and invalidation.
 * <p>
 * Specific factories and cache managers will provide
 * this interface or contain the interface to provide
 * caching services to applications.
 * @see UpdateableCache
 * @author Lane Sharman
 */
public interface Cache {

	/**
	 * Invalidate an object in the cache according
	 * to the update strategy.
	 */
	public void invalidate(Object key);

	/**
	 * Invalidates all the objects in the cache in one shot.
	 * <p>
	 * The action is performed according to the update strategy
	 * of the implementation.
	 */
	public void invalidateAll();

	/**
	 * Put an object in the cache possibly
	 * updating and replacing an existing value.
	 * <p>
	 * Note: some implementations elect to defer
	 * this operation so the element may not
	 * be immediately present.
	 */
	public void put(Object key, Object value);

	/**
	 * Gets a value from the cache.
	 * <p>
	 * Returning null reports that the element
	 * cannot be found or regenerate with the key
	 * provided.
	 */
	public Object get(Object key);

	/**
	 * Returns all the values in the cache.
	 */
  public Object[] values();

  /**
   * Returns all the keys in the cache.
   */
  public Object[] keys();

}

