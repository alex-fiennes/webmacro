/*
 * Copyright (C) 1998-2000 Semiotek Inc.  All Rights Reserved.  
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

import java.lang.ref.SoftReference;

/**
  * This class is used by CachingProvider to manage object 
  * references with timeouts. A TimedReference can be 
  * garbage collected prior to the timeout, but will be 
  * released no later than the specified timeout.
  */
public class TimedReference extends SoftReference {
   public final long _timeout;
   
   /**
     * Construct a soft reference, specifying how long the 
     * reference is valid for. Beyond the specified timeout 
     * this object will return null.
     */
   public TimedReference(Object referent, long timeout) {
      super(referent);
      _timeout = timeout;
   }

  /**
   * The CachingProvider calls shouldReload to ask the reference whether
   * it has changed since we loaded it.  Subclasses should define an 
   * implementation if this if it makes sense to.  Otherwise, the default
   * returns false, which means the item is replaced when it expires from
   * the cache.
   */
   public boolean shouldReload() {
      return false;
   }
}
