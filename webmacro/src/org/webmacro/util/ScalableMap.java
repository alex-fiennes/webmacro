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


package org.webmacro.util;

/**
  * Reduce locking overhead for a map with few writers and many 
  * readers. Writes are five times more expensive than a SimpleMap,
  * reads cost only slightly more. However, five readers can access
  * the same value simultaneously, without blocking.
  */
final public class ScalableMap {

   public final static int DEFAULT_FACTOR = 5, DEFAULT_SIZE=1001;

   private final int factor;
   private final SimpleMap[] _maps;
   private int pos = 0;

   public ScalableMap(int factor, int size) {
      this.factor = factor;
      _maps = new SimpleMap[factor];
      for (int i = 0; i < factor; i++) {
         _maps[i] = new SimpleMap(size);
      }
   }

   public ScalableMap(int size) {
     this(DEFAULT_FACTOR, size);
   }

   public ScalableMap() {
      this(DEFAULT_FACTOR, DEFAULT_SIZE);
   }

   public void put(final Object key, final Object value) {
      for (int i = 0; i < factor; i++) {
         _maps[i].put(key,value); 
      }
   }

   public Object get(final Object key) {
      pos = (pos + 1) % factor;
      return _maps[pos].get(key);
   }

   public void remove(final Object key) {
      for (int i = 0; i < _maps.length; i++) {
         _maps[i].remove(key);
      }
   }

   public void clear() {
      for (int i = 0; i < _maps.length; i++) {
         _maps[i].clear();
      }
   }
}
