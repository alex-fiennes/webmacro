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
  * <br>
  * This implementation uses SimpleHashMaps as its map implementation.
  */
final public class ScalableMap extends AbstractScalableMap {
    public final static int DEFAULT_SIZE=1001;

    public ScalableMap(final int factor,final int size) {
        super(factor,
              new SimpleMapFactory() {
                      public SimpleMap createSimpleMap() {
                          return new SimpleHashMap(size);
                      }
                  });
    }
    

    public ScalableMap(int size) {
        this(DEFAULT_FACTOR, size);
    }

    public ScalableMap() {
        this(DEFAULT_FACTOR, DEFAULT_SIZE);
    }
}
