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
 * The CacheManager sits between the template provider and WM.  This
 * interface specifies all the functions of the cache manager, allowing
 * a complete separation of the fetching of resources from how they are
 * cached.  CachingProvider looks in the properties file to figure out
 * what cache manager to load.
 * @author Brian Goetz
 * @since 0.96
 */

public interface CacheManager {
   /**
     * Same as defined in Provider, except with an additional type
     * parameter so it knows what type of resource it is caching
     */
   public void init(Broker b, Settings config, String resourceType) 
      throws InitException;

   /**
     * Same as defined in Provider
     */
   public void flush();

   /**
     * Same as defined in Provider
     */
   public void destroy();

   /**
     * Called to get a resource from the cache.  The helper object is used
     * to load the resource if the resource was not in the cache.  
     */
   public Object get(final Object query, ResourceLoader helper) 
     throws ResourceException;

   /**
     * Called to get a resource from the cache.  Returns null if not present.
     */
   public Object get(final Object query);

   /**
     * Called to put a resource into the cache.
     */
   public void put(final Object query, Object resource);

   /**
     * Invalidates an entry in the cache. Depending on the
     * the implementation, the actual removal from the cache
     * may or may not be immediate.
     */
   public void invalidate(final Object query);

   /**
     * Does this cache manager support reloading of resources if the
     * underlying resource has changed? 
     */
   public boolean supportsReload();

}
