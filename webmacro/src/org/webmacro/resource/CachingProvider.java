
package org.webmacro.resource;

import org.webmacro.*;
import org.webmacro.util.*;
import java.util.Properties;
import java.util.Hashtable;
import java.lang.ref.Reference;
import org.webmacro.util.TimeLoop;
import org.webmacro.Log;

abstract public class CachingProvider implements Provider
{

   private Hashtable _cache;
   private static final TimeLoop _tl;
   private Log _log;

   private static final long DURATION = 1000;
   private static int PERIODS = 600; 
   static {
      _tl = new TimeLoop(DURATION, PERIODS); // 10min max, 1sec intervals
      _tl.setDaemon(true);
      _tl.start();
   }

   /**
     * You must implement this, loading an object from permanent
     * storage (or constructing it) on demand. 
     */
   abstract public TimedReference load(String query)
      throws NotFoundException; 

   /**
     * If you over-ride this method be sure and call super.init(...)
     */
   public void init(Broker b, Properties config) throws InitException
   {
      _log = b.getLog("resource");
      _cache = new Hashtable();
   }

   /**
     * Clear the cache. If you over-ride this method be sure 
     * and call super.flush().
     */
   public void flush() {
      _cache.clear();
   }

   /**
     * Close down the provider. If you over-ride this method be
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
      try {
         r = (TimedReference) _cache.get(query);
      } catch (NullPointerException e) {
         throw new NotFoundException(this + " is not initialized", e);
      }
      Object o = null;
      if (r != null) {
         o = r.get();
      }
      if (o == null) {
         r = load(query);
         if (r != null) {
            _cache.put(query,r);
            o = r.get();
            try {
               _log.debug("cached: " + query + " for " + r.getTimeout());
               _tl.scheduleTime( 
                  new Runnable() { 
                     public void run() { 
                        _cache.remove(query); 
                        _log.debug("cache expired: " + query);
                     } 
                  }, r.getTimeout());
            } catch (Exception e) {
               _log.error("CachingProvider caught an exception", e);
            }
         } 
      }
      return o;
   }

   public String toString() {
      return "CachingProvider(type = " + getType() + ")";
   }
}

