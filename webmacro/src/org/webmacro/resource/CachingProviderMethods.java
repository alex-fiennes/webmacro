package org.webmacro.resource;

import org.webmacro.*;
import org.webmacro.util.*;

public interface CachingProviderMethods {

   /**
     * Load an object from permanent storage (or construct it) on
     * demand.  */
   public TimedReference load(String query)
      throws NotFoundException; 


   /**
     * Should this object be loaded again, or is the cache value
     * valid?  It provides you with the cached value, so you can cache
     * things relevant to the search for this resource.  The ref may
     * be null.
     *
     * You should implement one version of shouldReload.  
     * Regardless of return value, CachingProvider will still reload
     * the object if CachingProvider's cache is invalid */
   public boolean shouldReload(String query, TimedReference ref);

  /**
    * Initialize this provider based on the specified config.
    */
  public void init(Broker b, Settings config) throws InitException;
}
