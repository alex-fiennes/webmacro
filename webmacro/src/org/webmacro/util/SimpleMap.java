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
 * Interface for a simple map, that does not have
 * all the features of java.util.Map.
 * <br>
 * AbstractScalableMap uses an array of instances
 * of this interface.
 * @author skanthak@muehlheim.de
 */
public interface SimpleMap {

   public void put(Object key, Object value);

   /**
    * Get the value of 'key' back. Returns null if no such key.
    */
   public Object get(Object key);

   /**
    * Ensure that the key does not appear in the map
    */
   public Object remove(Object key);

   public void clear();

}
