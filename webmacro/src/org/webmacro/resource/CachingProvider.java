
package org.webmacro.resource;

import org.webmacro.*;
import org.webmacro.util.*;
import java.util.Properties;
import org.webmacro.util.ScalableMap;
import java.lang.ref.Reference;
import org.webmacro.util.TimeLoop;
import org.webmacro.Log;
import org.webmacro.Flags;

abstract public class CachingProvider implements Provider
{

   private ScalableMap _cache;
   private Object[] _writeLocks = new Object[101];

   private static final TimeLoop _tl;
   private Log _log;
   private Profiler _prof; 

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
     * You must implement this, loading an object from permanent
     * storage (or constructing it) on demand. 
     */
   abstract public TimedReference load(String query)
      throws NotFoundException; 


	/**
	  * should object be loaded again?  or is the cache value valid?<p>
	  *
	  * regardless of return value, CachingProvider will still reload
	  * the object if CachingProvider's cache is invalid
	  */
	abstract public boolean shouldReload(String query);

   /**
     * If you over-ride this method be sure and call super.init(...)
     */
   public void init(Broker b, Properties config) throws InitException
   {
      _prof = b.getProfiler("provider:" + getType());
      _log = b.getLog("resource");
      _cache = new ScalableMap(1001);
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
      Object prof = null;
      if (Flags.PROFILE && (_prof != null)) prof = _prof.start(query);
      try {
			boolean reload = shouldReload (query);	// should the template be reloaded, regardless of cached status?
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
         if (o == null || reload) {
            // DOUBLE CHECKED LOCKING IS DANGEROUS IN JAVA:
            // this looks like double-checked locking but it isn't, we
            // synchronized on a less expensive lock inside _cache.get()
            // the following line lets us simultaneously load up to 
            // writeLocks.length resources.
            
            int lockIndex = Math.abs(query.hashCode()) % _writeLocks.length;
            synchronized(_writeLocks[lockIndex])
            {
               r = (TimedReference) _cache.get(query);
               if (r != null){ 
                 o = r.get();
               }
               if (o == null || reload) {
                  r = load(query);
                  if (r != null) {
                     _cache.put(query,r);
                  }
                  o = r.get();
                  try {
                     _log.debug("cached: " + query + " for " + r.getTimeout());
							 if (r.getTimeout() >= 0) {	// if timeout of TimedReference is < 0,then don't schedule a removal from cache
	                     _tl.scheduleTime( 
	                        new Runnable() { 
	                           public void run() { 
	                              _cache.remove(query); 
	                              _log.debug("cache expired: " + query);
	                           } 
	                        }, r.getTimeout());
							 }
                  } catch (Exception e) {
                     _log.error("CachingProvider caught an exception", e);
                  }
               }
            } 
         }
         return o;
      } finally {
         if (Flags.PROFILE && (_prof != null)) _prof.stop(prof);
      }
   }

   public String toString() {
      return "CachingProvider(type = " + getType() + ")";
   }
}
