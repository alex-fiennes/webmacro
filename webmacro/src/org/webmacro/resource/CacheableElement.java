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
  * This class is used by CacheManager and CachingProvider to supply
  * the cache manager with items that know something about where they came
  * from, so that they can efficiently be checked for validity.  
  * Implementations of CachingProviderMethods may want to subclass this 
  * class; see TemplateProvider and CacheableFileTemplate for an example.
  */

public class CacheableElement {
   /** These are for use by the CacheManager; don't touch these */
   public long _creationTime, _lastAccessTime, _expirationTime;

   /** CachingProvider implementations deposit the reference here; the
    * shouldReload method _should not_ reference it, though -- the cache
    * may substitute it with a SoftReference or something like that. 
    */
   public Object _resource;

   /** Convenience constructor which just sets up _resource */
   public CacheableElement(Object o) {
      _resource = o;
   }

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
