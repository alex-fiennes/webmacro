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


/**
 * A trivial implementation of CacheManager which does no caching at
 * all.
 * @since 0.96
 */
package org.webmacro.resource;

import org.webmacro.*;
import org.webmacro.util.Settings;


public class TrivialCacheManager implements CacheManager {

   public TrivialCacheManager() { 
   }
   
   public void init(Broker b, Settings config, String resourceType) 
   throws InitException {
   }

   public void flush() {
   }

   public void destroy() {
   }

   public Object get(final Object query, ResourceLoader helper) 
   throws ResourceException  {
      return helper.load(query, null);
   }

   public Object get(final Object query) {
      return null;
   }

   public void put(final Object query, Object resource) {
   }

   public boolean supportsReload() {
      return false;
   }

   public void invalidate(final Object query) {
   }

}
