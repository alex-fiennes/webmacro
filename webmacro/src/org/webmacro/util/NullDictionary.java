
/*
 * Copyright (c) 1998, 1999 Semiotek Inc. All Rights Reserved.
 *
 * This software is the confidential intellectual property of
 * of Semiotek Inc.; it is copyrighted and licensed, not sold.
 * You may use it under the terms of the GNU General Public License,
 * version 2, as published by the Free Software Foundation. If you 
 * do not want to use the GPL, you may still use the software after
 * purchasing a proprietary developers license from Semiotek Inc.
 *
 * This software is provided "as is", with NO WARRANTY, not even the 
 * implied warranties of fitness to purpose, or merchantability. You
 * assume all risks and liabilities associated with its use.
 *
 * See the attached License.html file for details, or contact us
 * by e-mail at info@semiotek.com to get a copy.
 */


package org.webmacro.util;
import java.util.*;

/**
  * This exists so I can avoid allocating a new Hashtable 
  * every time I want an empty one--this one stays empty.
  */
final public class NullDictionary extends Dictionary
{
   private final Enumeration myNullEnumeration = 
      new Enumeration() {
         final public boolean hasMoreElements() { return false; }
         final public Object nextElement() { return null; }
      };
   final public int size() { return 0; }
   final public boolean isEmpty() { return true; }
   final public Enumeration keys() { return myNullEnumeration; }
   final public Enumeration elements() { return myNullEnumeration; }
   final public Object get(Object key) { return null; }
   final public Object put(Object key, Object value) { 
      Util.log.error("Reactor: Attempt to put stuff in NULL DICTIONARY");
      return null;
   }
   final public Object remove(Object key) { return null; } 
}
