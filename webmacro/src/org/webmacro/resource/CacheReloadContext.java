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


/**
 * CacheReloadContext provides support for providers which support
 * cache invalidation on a per-element basis, for example if an underlying
 * resource has changed.  The reload context allows the cache manager to ask
 * the provider if the resource should be reloaded.
 * @since 0.96
 */

public abstract class CacheReloadContext {

   /**
    * The CacheManager calls shouldReload to ask the reference whether
    * it has changed since we loaded it.  Subclasses should define an
    * implementation if this if it makes sense to.  Otherwise, the default
    * returns false, which means the item is replaced when it expires from
    * the cache.
    */
   public boolean shouldReload() {
      return false;
   }
}
