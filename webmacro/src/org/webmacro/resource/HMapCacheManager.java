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
 * HMapCacheManager
 *
 * An implementation of CacheManager which uses HashMap and 
 * doesn't support invalidation, reloading, or expiration.  
 * @since 0.96
 * @author Brian Goetz
 */

package org.webmacro.resource;

import java.util.*;

import org.webmacro.*;
import org.webmacro.util.*;

public class HMapCacheManager implements CacheManager {

   private static final String NAME = "HMapCacheManager";
   private HashMap _cache;
   private String _resourceType;

   private Log _log;

   public HMapCacheManager() { 
   }
   
   public void init(Broker b, Settings config, String resourceType) 
   throws InitException {

      _log = b.getLog("resource", "Object loading and caching");
      _resourceType = resourceType;

      _cache = new HashMap();

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

      synchronized(_cache) {
        o = _cache.get(query);
      }
      if (o == null) {
        o = helper.load(query, null); 
        synchronized(_cache) {
          _cache.put(query, o);
        }
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
      synchronized (_cache) {
         _cache.put(query, resource);
      }
   }

  /**
   * Remove an element
   */
  public void invalidate(final Object query) {
      synchronized (_cache) {
         _cache.remove(query);
      }
  }

   public String toString() {
      return NAME+"(type = " + _resourceType + ")";
   }
}
