
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


package org.webmacro.util.java2;

import java.util.*;

/**
  * This really should be of type java.util.Map, but Java 1.2 is not 
  * out yet, so we can't subclass that. However all the operations on 
  * this object have been chosen so that it will be easy to convert to
  * the JDK/ODMG collection API once it is available. Only a subset of
  * that API is provided here, but eventually all of it will be available
  * as this will eventually be a subclass of Map.
  *
  */
public class HashMap implements Map, Cloneable
{
   private Hashtable myMap;

   /** create a Map Object */
   public HashMap() {
      myMap = new Hashtable();
   }

   /** remove all values from this Map */
   final public void clear() {
      myMap.clear();
   }

   /** clone me */
   public Object clone() {
      try {
         HashMap clone = (HashMap) super.clone();
         clone.myMap = (Hashtable) myMap.clone();
         return clone;
      } catch (CloneNotSupportedException cnse) {
         throw new InternalError(cnse.toString());
      }
   }

   /** return true if this context contains the supplied key 
     * @return whether the key is contained
     */
   final public boolean containsKey(Object key) {
      return (myMap.containsKey(key));
   }

   /** 
     * This performs a normal hashtable lookup, and returns the 
     * value for the supplied key if it exists, or null if it does not..
     */
   final public Object get(Object key) {
      return myMap.get(key);
   }

   /**
     * return true if the map contains no key-value mappings
     * @return boolean 
     */
   final public boolean isEmpty() {
      return (myMap.isEmpty());
   }

   /**
     * Put the supplied key and value into the Map. Both the key 
     * and value must obey the classtypes set by myKeyType and myValueType 
     * other wise a ClassCastException is thrown. Neither keys nor values  
     * may be null if they are null put does nothing and returns null.
     * <p>
     * @return the object that was under this key previously, or null
     */
   final public Object put(Object key, Object value) 
      throws ClassCastException 
   {

      // nothing will be added to the Map object if either key or value is null
      if ((key == null) || (value == null)) {
         return null;
      }
      return (myMap.put(key, value)); 
   }

   /**
     * Remove the supplied key from the map. 
     * @retrun removed key's value object
     */
   final public Object remove(Object key) 
   {
      return myMap.remove(key);
   }
}

