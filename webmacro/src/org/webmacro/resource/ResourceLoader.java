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


package org.webmacro.resource;

import org.webmacro.*;
import org.webmacro.util.*;

/**
 * Methods to be implemented by a resource loader, which will work with 
 * a cache manager.  
 * @see CachingProvider
 * @see CacheManager
 * @since 0.96
 * @author Brian Goetz
 */
public interface ResourceLoader {

   /**
     * Load an object from permanent storage (or construct it) on
     * demand.  */
   public Object load(String query, CacheElement ce)
      throws ResourceException; 

   /**
     * Load an object from permanent storage (or construct it) on
     * demand.  Classes which extend CachingProvider don't need to 
     * supply this, since providers will only be called with String
     * queries.  
     */
   public Object load(Object query, CacheElement ce)
      throws ResourceException; 

   /**
     * Initialize this provider based on the specified config.
     */
   public void init(Broker b, Settings config) throws InitException;
}
