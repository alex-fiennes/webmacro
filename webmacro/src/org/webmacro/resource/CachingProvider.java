
package org.webmacro.resource;

import org.webmacro.*;
import org.webmacro.util.*;
import java.util.Properties;
import java.util.Hashtable;
import java.lang.ref.Reference;
import java.util.TimerTask;
import java.util.Timer;

abstract public class CachingProvider implements Provider
{

   private Hashtable _cache;
   private static final Timer _timer = new Timer(true); // i am daemon

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
               _timer.schedule( 
                  new TimerTask() { 
                     public void run() { _cache.remove(query); } 
                  }, r.getTimeout());
            } catch (Exception e) {
               // not much we can do, ignore it
            }
         } 
      }
      return o;
   }

   public String toString() {
      return "CachingProvider(type = " + getType() + ")";
   }
}

