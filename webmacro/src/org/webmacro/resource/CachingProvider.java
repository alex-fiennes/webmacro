/*
 * Copyright (C) 1998-2000 Semiotek Inc.  All Rights Reserved.  
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


package org.webmacro.resource;

import org.webmacro.*;
import org.webmacro.util.*;
import java.lang.ref.Reference;

/**
 * CacheManager is an abstract base class for providers which wish to 
 * implement caching functionality.  By extending CachingProvider and
 * implementing the methods in ResourceLoader, a provider can
 * automatically support caching using any CacheManager.  CachingProvider
 * looks in the properties file to find the desired cache manager. 
 * @since 0.96
 */

abstract public class CachingProvider implements Provider, 
                                                 ResourceLoader
{
   private CacheManager _cache; 
   private Log _log;
   protected boolean _cacheSupportsReload;

   public CachingProvider() { 
   }
   
   /**
     * If you override this method be sure and call super.init(...)
     */
   public void init(Broker b, Settings config) throws InitException
   {
      String cacheManager;

      _log = b.getLog("resource", "Object loading and caching");

      cacheManager = b.getSetting("CachingProvider." + getType() 
                                  + ".CacheManager");
      if (cacheManager == null) 
        cacheManager = b.getSetting("CachingProvider.*.CacheManager");
      if (cacheManager == null || cacheManager.equals("")) {
         _log.info("CachingProvider: No cache manager specified for " 
                   + getType() + ", using TrivialCacheManager");
         _cache = new TrivialCacheManager();
      }
      else {
         try {
            _cache = (CacheManager) b.classForName(cacheManager).newInstance();
         }
         catch (Exception e) {
            _log.warning("Unable to load cache manager " + cacheManager 
                         + " for resource type " + getType()
                         + ", using TrivialCacheManager.  Reason:\n" + e);
            _cache = new TrivialCacheManager();
         }
      }
      _cache.init(b, config, getType());
      _cacheSupportsReload = _cache.supportsReload();
   }

   /**
     * Clear the cache. If you override this method be sure 
     * and call super.flush().
     */
   public void flush() {
      _cache.flush();
   }

   /**
     * Close down the provider. If you override this method be
     * sure and call super.destroy().
     */
   public void destroy() {
      _cache.destroy();
   }

   /**
     * Get the object associated with the specific query, using the
     * specified cache manager. 
     */
   public Object get(String query) throws ResourceException {
      return _cache.get(query, this);
   }

   /* 
    * The cache manager will call this version; the providers implement
    * the other version; so dispatch 
    */
   public Object load(Object query, CacheElement ce)
     throws ResourceException {
     return load((String) query, ce);
   }

   public String toString() {
      return "CachingProvider(type = " + getType() + ")";
   }

   
}
