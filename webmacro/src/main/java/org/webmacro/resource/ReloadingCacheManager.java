package org.webmacro.resource;

import java.lang.ref.SoftReference;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;

import org.webmacro.Broker;
import org.webmacro.InitException;
import org.webmacro.Log;
import org.webmacro.ResourceException;
import org.webmacro.util.Settings;
import org.webmacro.util.SubSettings;

/**
 * ReloadingCacheManager -- a cache manager which supports reloading and expiration, backed
 * by a ConcurrentHashMap.  Derived from the old SMapCacheManager, but simplified.
 *
 * @author Brian Goetz
 */
public class ReloadingCacheManager implements CacheManager {

    private static final String NAME = "ReloadingCacheManager";

    private final ConcurrentHashMap _cache = new ConcurrentHashMap();
    private int _cacheDuration;
    private String _resourceType;
    private boolean _reloadOnChange = true, _useSoftReferences = true;
    private boolean _delayReloadChecks = false;
    private long _checkForReloadDelay;

    // Use ClockDaemon instead of the original TimeLoop -- more efficient priority-queue based implementation
    private Timer _clockDaemon;

    private Log _log;

    private abstract class MyCacheElement extends CacheElement implements Cloneable
    {

        private CacheReloadContext reloadContext = null;

        public void setReloadContext (CacheReloadContext rc)
        {
            if (_delayReloadChecks && rc != null)
                this.reloadContext = new TimedReloadContext(rc, _checkForReloadDelay);
            else
                this.reloadContext = rc;
        }

        public abstract Object getObject ();

        public abstract void setObject (Object o);
    }

    private final class SoftScmCacheElement extends MyCacheElement
    {

        private SoftReference reference;

        public Object getObject ()
        {
            return reference.get();
        }

        public void setObject (Object o)
        {
            reference = new SoftReference(o);
        }
    }

    private final class DirectScmCacheElement extends MyCacheElement
    {

        private Object object;

        public Object getObject ()
        {
            return object;
        }

        public void setObject (Object o)
        {
            object = o;
        }
    }

    public ReloadingCacheManager()
    {
    }

    public void init (Broker b, Settings config, String resourceType)
            throws InitException
    {
        Settings ourSettings, defaultSettings;

        _clockDaemon = new Timer();
        _clockDaemon.setThreadFactory(new ThreadFactory() {
            public Thread newThread(Runnable runnable) {
                _log.info("Creating new ClockDaemon thread");
                Thread t = new Thread(runnable);
                t.setDaemon(true);
                return t;
            }
        });

        _log = b.getLog("resource", "Object loading and caching");
        _resourceType = resourceType;

        ourSettings = new SubSettings(config, NAME + "." + _resourceType);
        defaultSettings = new SubSettings(config, NAME + ".*");

        _cacheDuration =
                ourSettings.getIntegerSetting("ExpireTime",
                        defaultSettings.getIntegerSetting("ExpireTime",
                                config.getIntegerSetting("TemplateExpireTime", 0)));
        _useSoftReferences =
                (ourSettings.containsKey("UseSoftReferences"))
                ? ourSettings.getBooleanSetting("UseSoftReferences")
                : ((defaultSettings.containsKey("UseSoftReferences"))
                ? defaultSettings.getBooleanSetting("UseSoftReferences") : true);
        _reloadOnChange =
                (ourSettings.containsKey("ReloadOnChange"))
                ? ourSettings.getBooleanSetting("ReloadOnChange")
                : ((defaultSettings.containsKey("ReloadOnChange"))
                ? defaultSettings.getBooleanSetting("ReloadOnChange") : true);

        _checkForReloadDelay =
                (ourSettings.getIntegerSetting("CheckForReloadDelay",
                        defaultSettings.getIntegerSetting("CheckForReloadDelay", -1)));
        _delayReloadChecks = _checkForReloadDelay > 0;

        _log.info(NAME + "." + _resourceType + ": "
                + "; expireTime=" + _cacheDuration
                + "; reload=" + _reloadOnChange
                + "; softReference=" + _useSoftReferences
                + "; checkForReloadDelay=" + _checkForReloadDelay);
    }

    private MyCacheElement newCacheElement() {
        if (_useSoftReferences)
            return new SoftScmCacheElement();
        else
            return new DirectScmCacheElement();
    }

    /**
     * Clear the cache.
     */
    public void flush ()
    {
        _cache.clear();
    }

    /**
     * Close down the provider.
     */
    public void destroy ()
    {
        _cache.clear();
        _clockDaemon.shutDown();
    }

    public boolean supportsReload ()
    {
        return _reloadOnChange;
    }

    private final void scheduleRemoval(final Object key) {
        _clockDaemon.executeAfterDelay(_cacheDuration,
                new Runnable()
                {
                    public void run ()
                    {
                        _cache.remove(key);
                        if (_log.loggingDebug())
                            _log.debug("cache expired: " + key);
                    }
                });
    }

    /**
     * Get the object associated with the specific query, first
     * trying to look it up in a cache. If it's not there, then
     * call load(String) to load it into the cache.
     */
    public Object get (final Object query, ResourceLoader helper)
            throws ResourceException
    {
        MyCacheElement r;
        Object o = null;
        boolean reload = false;

        r = (MyCacheElement) _cache.get(query);
        if (r != null) {
            o = r.getObject();

            // should the template be reloaded, regardless of cached status?
            if (o != null && r.reloadContext != null && _reloadOnChange)
                reload = r.reloadContext.shouldReload();
        }
        if (o == null || reload)
        {
            r = newCacheElement();
            o = helper.load(query, r);
            if (o != null)
            {
                r.setObject(o);
                _cache.put(query, r);
                if (_log.loggingDebug())
                    _log.debug("cached: " + query + " for " + _cacheDuration);
                try
                {
                    // if timeout is < 0,
                    // then don't schedule a removal from cache
                    if (_cacheDuration >= 0)
                        scheduleRemoval(query);
                }
                catch (Exception e)
                {
                    _log.error("CachingProvider caught an exception", e);
                }
            }
        }
        return o;
    }

    /**
     * Get the object associated with the specific query,
     * trying to look it up in a cache. If it's not there, return null.
     */
    public Object get (final Object query)
    {
        MyCacheElement r = (MyCacheElement) _cache.get(query);
        if (r != null)
            return r.getObject();
        else
            return null;
    }

    /**
     * Put an object in the cache
     */
    public void put (final Object query, Object resource)
    {
        MyCacheElement r = newCacheElement();
        r.setObject(resource);

        _cache.put(query, r);
        if (_cacheDuration >= 0)
        {
            if (_log.loggingDebug())
                _log.debug("cached: " + query + " for " + _cacheDuration);
            scheduleRemoval(query);
        }
    }

    /** Removes a specific entry from the cache. */
    public void invalidate (final Object query)
    {
        _cache.remove(query);
    }

    public String toString ()
    {
        return NAME + "(type = " + _resourceType + ")";
    }
}
