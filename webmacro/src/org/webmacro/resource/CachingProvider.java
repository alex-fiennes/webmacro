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

   private ScalableMap _cache;
   private Object[] _writeLocks = new Object[101];

   private static final TimeLoop _tl;
   private Log _log;

   private static final long DURATION = 1000;
   private static int PERIODS = 600; 
   static {
      _tl = new TimeLoop(DURATION, PERIODS); // 10min max, 1sec intervals
      _tl.setDaemon(true);
      _tl.start();
   }

   public CachingProvider() { 
      for (int i=0; i<_writeLocks.length; i++) {
        _writeLocks[i] = new Object();
      }
   }
   
   /**
     * Load an object from permanent storage (or construct it) on
     * demand.  */
   abstract public TimedReference load(String query)
      throws NotFoundException; 

   /**
     * If you override this method be sure and call super.init(...)
     */
   public void init(Broker b, Settings config) throws InitException
   {
      _log = b.getLog("resource", "Object loading and caching");
      _cache = new ScalableMap(1001);
   }

   /**
     * Clear the cache. If you override this method be sure 
     * and call super.flush().
     */
   public void flush() {
      _cache.clear();
   }

   /**
     * Close down the provider. If you override this method be
     * sure and call super.destroy().
     */
   public void destroy() {
      _cache = null;
   }

   /**
     * Get the object associated with the specific query, first 
     * trying to look it up in a cache. If it's not there, then
     * call load(String) to load it into the cache.
     */
   public Object get(final String query) throws NotFoundException
   {
      TimedReference r;
      Object o = null;
      boolean reload = true;

      // bg; Reordered this logic to only call shouldReload if we have a 
      // candidate for reloading.  
      try {
         r = (TimedReference) _cache.get(query);
         if (r != null) 
            o = r.get();
      } catch (NullPointerException e) {
         throw new NotFoundException(this + " is not initialized", e);
      }
      // should the template be reloaded, regardless of cached status?
      if (o != null) 
         reload = r.shouldReload();

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
            r = (TimedReference) _cache.get(query);
            if (r != null)
              o = r.get();
            if (o == null || reload) {
               r = load(query);
               if (r != null) {
                  _cache.put(query,r);
                  o = r.get();
               }
               try {
                  _log.debug("cached: " + query + " for " + r._timeout);
                  // if timeout of TimedReference is < 0,
                  // then don't schedule a removal from cache
                  if (r._timeout >= 0) {   
                     _tl.scheduleTime( 
                        new Runnable() { 
                           public void run() { 
                              _cache.remove(query); 
                              _log.debug("cache expired: " + query);
                           } 
                        }, r._timeout);
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
      return "CachingProvider(type = " + getType() + ")";
   }
}
