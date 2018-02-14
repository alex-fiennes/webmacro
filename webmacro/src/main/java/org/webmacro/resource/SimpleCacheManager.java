package org.webmacro.resource;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.webmacro.Broker;
import org.webmacro.InitException;
import org.webmacro.ResourceException;
import org.webmacro.util.Settings;

import java.util.concurrent.ConcurrentHashMap;

/**
 * SimpleCacheManager -- a basic cache manager backed by ConcurrentHashMap which does not support
 * reloading, or expiration.
 * 
 * @author Brian Goetz
 */
public class SimpleCacheManager
  implements CacheManager
{

  static Logger _log = LoggerFactory.getLogger(SimpleCacheManager.class);

  private static final String NAME = "SimpleCacheManager";
  private final Map<Object, Object> _cache = new ConcurrentHashMap<Object, Object>();

  private String _resourceType;

  public SimpleCacheManager()
  {
  }

  @Override
  public void init(Broker b,
                   Settings config,
                   String resourceType)
      throws InitException
  {

    _resourceType = resourceType;

    _log.info(NAME + "." + _resourceType);
  }

  /**
   * Clear the cache.
   */
  @Override
  public void flush()
  {
    _cache.clear();
  }

  /**
   * Close down the provider.
   */
  @Override
  public void destroy()
  {
    _cache.clear();
  }

  @Override
  public boolean supportsReload()
  {
    return false;
  }

  /**
   * Get the object associated with the specific query, first trying to look it up in a cache. If
   * it's not there, then call load(String) to load it into the cache.
   */
  @Override
  public Object get(final Object query,
                    ResourceLoader helper)
      throws ResourceException
  {
    Object o = null;

    o = _cache.get(query);
    if (o == null) {
      o = helper.load(query, null);
      _cache.put(query, o);
    }
    return o;
  }

  /**
   * Get the object associated with the specific query, trying to look it up in a cache. If it's not
   * there, return null.
   */
  @Override
  public Object get(final Object query)
  {
    return _cache.get(query);
  }

  /**
   * Put an object in the cache.
   */
  @Override
  public void put(final Object query,
                  Object resource)
  {
    _cache.put(query, resource);
  }

  /**
   * Remove an element.
   */
  @Override
  public void invalidate(final Object query)
  {
    _cache.remove(query);
  }

  @Override
  public String toString()
  {
    return NAME + "(type = " + _resourceType + ")";
  }
}
