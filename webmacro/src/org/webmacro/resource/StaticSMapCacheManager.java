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
 * StaticSMapCacheManager
 *
 * An implementation of CacheManager which uses ScalableMap and 
 * doesn't support invalidation, reloading, or expiration.  
 * @since 0.96
 * @author Brian Goetz
 */

package org.webmacro.resource;

import org.webmacro.*;
import org.webmacro.util.*;

public class StaticSMapCacheManager implements CacheManager {

   private static final String NAME = "StaticSMapCacheManager";
   private ScalableMap _cache;
   private String _resourceType;

   private Log _log;

   public StaticSMapCacheManager() { 
   }
   
   public void init(Broker b, Settings config, String resourceType) 
   throws InitException {
      int cacheSize, cacheFactor; 
      Settings ourSettings, defaultSettings;

      _log = b.getLog("resource", "Object loading and caching");
      _resourceType = resourceType;

      ourSettings = new SubSettings(config, 
                                    NAME + "." + _resourceType);
      defaultSettings = new SubSettings(config, NAME + ".*");

      cacheSize = ourSettings.getIntegerSetting("CacheBuckets", 
                    defaultSettings.getIntegerSetting("CacheBuckets", 
                      ScalableMap.DEFAULT_SIZE));
      cacheFactor = ourSettings.getIntegerSetting("CacheFactor", 
                      defaultSettings.getIntegerSetting("CacheFactor", 
                        ScalableMap.DEFAULT_FACTOR));
      _cache = new ScalableMap(cacheFactor, cacheSize);

      _log.info(NAME + "." + _resourceType + ": " 
                + "buckets=" + cacheSize 
                + "; factor=" + cacheFactor);
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
      _cache = null;
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
     * Put an object in the cache
     */
   public void put(final Object query, Object resource) {
      _cache.put(query, resource);
   }

   /**
    * This method throws an exception because this kind of a cache
    * does not support individual element invalidation.
    */
  public void invalidate(final Object query) {
    throw new IllegalStateException(
      "Cannot invalidate an element in a Static Cache");
  }

   

   public String toString() {
      return NAME+"(type = " + _resourceType + ")";
   }
}
