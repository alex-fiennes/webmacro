package org.webmacro.resource;

import org.webmacro.*;
import org.webmacro.util.*;

public interface CachingProviderMethods {

   /**
     * Load an object from permanent storage (or construct it) on
     * demand.  */
   public TimedReference load(String query)
      throws ResourceException; 

   /**
     * Initialize this provider based on the specified config.
     */
   public void init(Broker b, Settings config) throws InitException;
}
