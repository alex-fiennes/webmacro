/*
 * Copyright (C) 1998-2001 Semiotek Inc.  All Rights Reserved.  
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted under the terms of either of the following
 * Open Source licenses:
 *
 * The GNU General Public License, version 2, or any later version, as
 * published by the Free Software Foundation
 * (http://www.fsf.org/copyleft/gpl.html);
 *
 *  or 
 *
 * The Semiotek Public License (http://webmacro.org/LICENSE.)  
 *
 * This software is provided "as is", with NO WARRANTY, not even the 
 * implied warranties of fitness to purpose, or merchantability. You
 * assume all risks and liabilities associated with its use.
 *
 * See www.webmacro.org for more information on the WebMacro project.  
 */


/**
 * SimpleCacheManager
 *
 * An implementation of CacheManager which implements the SoftReference-
 * based caching algorithm which used to live in CachingProvider.
 * @since 0.96
 */

package org.webmacro.resource;

import org.webmacro.*;
import org.webmacro.util.*;
import java.lang.ref.SoftReference;

public class SimpleCacheManager implements CacheManager {

   private ScalableMap _cache;
   private Object[] _writeLocks = new Object[101];
   private int _cacheDuration;
   private String _resourceType;
   private Settings _ourSettings;
   private boolean _reloadOnChange=true;

   private static final TimeLoop _tl;
   private Log _log;

   private static final long DURATION = 1000;
   private static int PERIODS = 600; 
   static {
      _tl = new TimeLoop(DURATION, PERIODS); // 10min max, 1sec intervals
      _tl.setDaemon(true);
      _tl.start();
   }

   private static class ScmCacheElement extends CacheElement {
      private SoftReference reference; 
      private CacheReloadContext reloadContext = null;

      public void setReloadContext(CacheReloadContext rc) { 
         this.reloadContext = rc;
      }
   }

   public SimpleCacheManager() { 
      for (int i=0; i<_writeLocks.length; i++) {
        _writeLocks[i] = new Object();
      }
   }
   
   public void init(Broker b, Settings config, String resourceType) 
   throws InitException {
      int cacheSize; 

      _log = b.getLog("resource", "Object loading and caching");
      _resourceType = resourceType;
      _ourSettings = new SubSettings(config, 
                                     "SimpleCacheManager." + _resourceType);
      cacheSize = _ourSettings.getIntegerSetting("CacheBuckets", 1001);
      _cache = new ScalableMap(cacheSize);
      _cacheDuration = _ourSettings.getIntegerSetting("ExpireTime", -1001);
      if (_cacheDuration == -1001) 
        _cacheDuration = config.getIntegerSetting("TemplateExpireTime", 0);
      _reloadOnChange = 
        (_ourSettings.containsKey("ReloadOnChange"))
        ? _ourSettings.getBooleanSetting("ReloadOnChange")
        : true;
      _log.info("SimpleCacheManager." + _resourceType + ": " 
                + "buckets=" + cacheSize + "; expireTime=" + _cacheDuration
                + "; reload=" + _reloadOnChange);
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
      // @@@ We should shut down TimeLoop too somehow
      _cache = null;
   }

   public boolean supportsReload() {
      return _reloadOnChange;
   }

   /**
     * Get the object associated with the specific query, first 
     * trying to look it up in a cache. If it's not there, then
     * call load(String) to load it into the cache.
     */
   public Object get(final Object query, ResourceLoader helper) 
   throws ResourceException {
      ScmCacheElement r;
      Object o = null;
      boolean reload = false;

      // bg; Reordered this logic to only call shouldReload if we have a 
      // candidate for reloading.  
      try {
         r = (ScmCacheElement) _cache.get(query);
         if (r != null) 
            o = r.reference.get();
      } catch (NullPointerException e) {
         throw new ResourceException(this + " is not initialized", e);
      }
      // should the template be reloaded, regardless of cached status?
      if (o != null && r.reloadContext != null && _reloadOnChange) 
         reload = r.reloadContext.shouldReload();

      if (o == null || reload) {

         // DOUBLE CHECKED LOCKING IS DANGEROUS IN JAVA:
         // this looks like double-checked locking but it isn't, we
         // synchronized on a less expensive lock inside _cache.get()
         // the following line lets us simultaneously load up to 
         // writeLocks.length resources.
         
         int lockIndex = query.hashCode() % _writeLocks.length;
         if (lockIndex < 0) lockIndex = -lockIndex;
         synchronized(_writeLocks[lockIndex])
         {
            r = (ScmCacheElement) _cache.get(query);
            if (r != null)
               o = r.reference.get();
            else 
               r = new ScmCacheElement();
            if (o == null || reload) {
               o = helper.load(query, r);
               if (o != null) {
                  r.reference = new SoftReference(o);
                  _cache.put(query, r);
               }
               try {
                  if (_log.loggingDebug())
                     _log.debug("cached: " + query + " for " + _cacheDuration);
                  // if timeout is < 0,
                  // then don't schedule a removal from cache
                  if (_cacheDuration >= 0) {   
                     _tl.scheduleTime( 
                        new Runnable() { 
                           public void run() { 
                              _cache.remove(query); 
                              if (_log.loggingDebug())
                                 _log.debug("cache expired: " + query);
                           } 
                        }, _cacheDuration);
                  }
               } catch (Exception e) {
                  _log.error("CachingProvider caught an exception", e);
               }
            }
         } 
      }
      return o;
   }

   public String toString() {
      return "SimpleCacheManager(type = " + _resourceType + ")";
   }
}
