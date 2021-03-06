package org.webmacro.resource;

import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.webmacro.Broker;
import org.webmacro.InitException;
import org.webmacro.ResourceException;
import org.webmacro.util.Settings;
import org.webmacro.util.SubSettings;

/**
 * ReloadingCacheManager -- a cache manager which supports reloading and expiration, backed by a
 * ConcurrentHashMap. Derived from the old SMapCacheManager, but simplified.
 * 
 * @author Brian Goetz
 */
public class ReloadingCacheManager
  implements CacheManager
{

  static Logger _log = LoggerFactory.getLogger(ReloadDelayDecorator.class);

  private static final String NAME = "ReloadingCacheManager";

  private final ConcurrentHashMap<Object, MyCacheElement> _cache =
      new ConcurrentHashMap<Object, MyCacheElement>();
  private int _cacheDurationMilliseconds;
  private String _resourceType;
  private boolean _reloadOnChange = true, _useSoftReferences = true;
  private boolean _delayReloadChecks = false;
  private long _checkForReloadDelay;

  // Used ClockDaemon instead of the original TimeLoop -- more efficient priority-queue based
  // implementation
  // Then renamed in move to java.util.concurrent
  private ScheduledExecutorService _clockDaemon;

  private abstract class MyCacheElement
    extends CacheElement
    implements Cloneable
  {

    private CacheReloadContext reloadContext = null;

    @Override
    public void setReloadContext(CacheReloadContext rc)
    {
      if (_delayReloadChecks && rc != null)
        this.reloadContext = new TimedReloadContext(rc, _checkForReloadDelay);
      else
        this.reloadContext = rc;
    }

    public abstract Object getObject();

    public abstract void setObject(Object o);
  }

  private final class SoftScmCacheElement
    extends MyCacheElement
  {

    private SoftReference<Object> reference;

    @Override
    public Object getObject()
    {
      return reference.get();
    }

    @Override
    public void setObject(Object o)
    {
      reference = new SoftReference<Object>(o);
    }
  }

  private final class DirectScmCacheElement
    extends MyCacheElement
  {

    private Object object;

    @Override
    public Object getObject()
    {
      return object;
    }

    @Override
    public void setObject(Object o)
    {
      object = o;
    }
  }

  public ReloadingCacheManager()
  {
  }

  @Override
  public void init(Broker b,
                   Settings config,
                   String resourceType)
      throws InitException
  {
    Settings ourSettings, defaultSettings;

    _clockDaemon = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
      @Override
      public Thread newThread(Runnable runnable)
      {
        _log.info("Creating new ClockDaemon thread");
        Thread t = new Thread(runnable);
        t.setDaemon(true);
        return t;
      }
    });

    _resourceType = resourceType;

    ourSettings = new SubSettings(config, NAME + "." + _resourceType);
    defaultSettings = new SubSettings(config, NAME + ".*");

    _cacheDurationMilliseconds =
        ourSettings.getIntegerSetting("ExpireTime",
                                      defaultSettings.getIntegerSetting("ExpireTime",
                                                                        config.getIntegerSetting("TemplateExpireTime",
                                                                                                 0)));
    _useSoftReferences =
        (ourSettings.containsKey("UseSoftReferences"))
            ? ourSettings.getBooleanSetting("UseSoftReferences")
            : ((defaultSettings.containsKey("UseSoftReferences"))
                ? defaultSettings.getBooleanSetting("UseSoftReferences")
                : true);
    _reloadOnChange =
        (ourSettings.containsKey("ReloadOnChange"))
            ? ourSettings.getBooleanSetting("ReloadOnChange")
            : ((defaultSettings.containsKey("ReloadOnChange"))
                ? defaultSettings.getBooleanSetting("ReloadOnChange")
                : true);

    _checkForReloadDelay =
        (ourSettings.getIntegerSetting("CheckForReloadDelay",
                                       defaultSettings.getIntegerSetting("CheckForReloadDelay", -1)));
    _delayReloadChecks = _checkForReloadDelay > 0;

    _log.info(NAME + "." + _resourceType + ": " + "; expireTime=" + _cacheDurationMilliseconds
              + "; reload=" + _reloadOnChange + "; softReference=" + _useSoftReferences
              + "; checkForReloadDelay=" + _checkForReloadDelay);
  }

  private MyCacheElement newCacheElement()
  {
    if (_useSoftReferences)
      return new SoftScmCacheElement();
    else
      return new DirectScmCacheElement();
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
    _clockDaemon.shutdown();
  }

  @Override
  public boolean supportsReload()
  {
    return _reloadOnChange;
  }

  private final void scheduleRemoval(final Object key)
  {
    _clockDaemon.schedule(new Runnable() {
      @Override
      public void run()
      {
        _cache.remove(key);
        _log.debug("cache expired: " + key);
      }
    }, _cacheDurationMilliseconds, TimeUnit.MILLISECONDS);
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
    MyCacheElement r;
    Object o = null;
    boolean reload = false;

    r = _cache.get(query);
    if (r != null) {
      o = r.getObject();

      // should the template be reloaded, regardless of cached status?
      if (o != null && r.reloadContext != null && _reloadOnChange)
        reload = r.reloadContext.shouldReload();
    }
    if (o == null || reload) {
      r = newCacheElement();
      o = helper.load(query, r);
      if (o != null) {
        r.setObject(o);
        _cache.put(query, r);
        _log.debug("cached: " + query + " for " + _cacheDurationMilliseconds);
        try {
          // if timeout is < 0,
          // then don't schedule a removal from cache
          if (_cacheDurationMilliseconds >= 0)
            scheduleRemoval(query);
        } catch (Exception e) {
          _log.error("CachingProvider caught an exception", e);
        }
      }
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
    MyCacheElement r = _cache.get(query);
    if (r != null)
      return r.getObject();
    else
      return null;
  }

  /**
   * Put an object in the cache
   */
  @Override
  public void put(final Object query,
                  Object resource)
  {
    MyCacheElement r = newCacheElement();
    r.setObject(resource);

    _cache.put(query, r);
    if (_cacheDurationMilliseconds >= 0) {
      _log.debug("cached: " + query + " for " + _cacheDurationMilliseconds);
      scheduleRemoval(query);
    }
  }

  /** Removes a specific entry from the cache. */
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
