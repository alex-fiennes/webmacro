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

abstract public class CachingProvider implements Provider, 
                                                 CachingProviderMethods
{
   private CacheManager _cache; 
   private Log _log;

   public CachingProvider() { 
   }
   
   /**
     * If you override this method be sure and call super.init(...)
     */
   public void init(Broker b, Settings config) throws InitException
   {
      _log = b.getLog("resource", "Object loading and caching");
      String cacheManager = b.getSetting("CachingProvider." + getType() 
                                         + ".CacheManager");
      if (cacheManager == null || cacheManager.trim().length() == 0) {
         _log.info("No cache manager specified for " + getType() 
                   + ", using TrivialCacheManager");
         _cache = new TrivialCacheManager();
      }
      else {
         try {
            Class c = Class.forName(cacheManager);
            _cache = (CacheManager) c.newInstance();
         }
         catch (Exception e) {
            _log.warning("Unable to load cache manager " + cacheManager 
                         + " for resource type " + getType()
                         + ", using TrivialCacheManager.  Reason:\n" + e);
            _cache = new TrivialCacheManager();
         }
      }
      _cache.init(b, config, getType());
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
   public Object get(final String query) throws ResourceException {
      return _cache.get(query, this);
   }

   public String toString() {
      return "CachingProvider(type = " + getType() + ")";
   }
}
