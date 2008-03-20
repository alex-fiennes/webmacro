package org.webmacro.resource;

import java.util.Map;

import org.webmacro.Broker;
import org.webmacro.InitException;
import org.webmacro.Log;
import org.webmacro.ResourceException;
import org.webmacro.util.Settings;

import EDU.oswego.cs.dl.util.concurrent.ConcurrentHashMap;

/**
 * SimpleCacheManager -- a basic cache manager backed by ConcurrentHashMap 
 * which does not support reloading, or expiration.
 *
 * @author Brian Goetz
 */
public class SimpleCacheManager implements CacheManager {

    private static final String NAME = "SimpleCacheManager";
    private final Map _cache = new ConcurrentHashMap();

    private String _resourceType;
    private Log _log;

    public SimpleCacheManager() {
    }

    public void init(Broker b, Settings config, String resourceType)
            throws InitException {

        _log = b.getLog("resource", "Object loading and caching");
        _resourceType = resourceType;

        _log.info(NAME + "." + _resourceType);
    }

    /**
     * Clear the cache.
     */
    public void flush() {
        _cache.clear();
    }

    /**
     * Close down the provider.
     */
    public void destroy() {
        _cache.clear();
    }

    public boolean supportsReload() {
        return false;
    }

    /**
     * Get the object associated with the specific query, first
     * trying to look it up in a cache. If it's not there, then
     * call load(String) to load it into the cache.
     */
    public Object get(final Object query, ResourceLoader helper)
            throws ResourceException {
        Object o = null;

        o = _cache.get(query);
        if (o == null) {
            o = helper.load(query, null);
            _cache.put(query, o);
        }
        return o;
    }

    /**
     * Get the object associated with the specific query,
     * trying to look it up in a cache. If it's not there, return null.
     */
    public Object get(final Object query) {
        return _cache.get(query);
    }

    /**
     * Put an object in the cache.
     */
    public void put(final Object query, Object resource) {
        _cache.put(query, resource);
    }

    /**
     * Remove an element.
     */
    public void invalidate(final Object query) {
        _cache.remove(query);
    }

    public String toString() {
        return NAME + "(type = " + _resourceType + ")";
    }
}
