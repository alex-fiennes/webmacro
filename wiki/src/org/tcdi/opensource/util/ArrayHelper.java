/** 
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 * 
 * The Initial Developer of this code is Technology Concepts 
 * and Design, Inc. 
 * Copyright (C) 2000 Technology Concepts and Design, Inc.  All
 * Rights Reserved.
 * 
 * Contributor(s):  Eric B. Ridge (Technology Concepts and Design, Inc.)
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the GNU General Public License Version 2 or later (the
 * "GPL"), in which case the provisions of the GPL are applicable 
 * instead of those above.  If you wish to allow use of your 
 * version of this file only under the terms of the GPL and not to
 * allow others to use your version of this file under the MPL,
 * indicate your decision by deleting the provisions above and
 * replace them with the notice and other provisions required by
 * the GPL.  If you do not delete the provisions above, a recipient
 * may use your version of this file under either the MPL or the
 * GPL.
 * 
 */
package org.tcdi.opensource.util;

/**
 * Singleton to help us manage object arrays
 * 
 * @author Eric B. Ridge
 */
public class ArrayHelper
{
   private static ArrayHelper instance = null;
   
   /**
    * @return the only instance of this object in the JVM
    */
   public static ArrayHelper getInstance ()
   {
      if (instance == null) instance = new ArrayHelper ();
      
      return instance;
   }
   
   /**
    * Append one object to an array<p>
    * 
    * Same as: <code>appendToArray(toArray, new Object[] {toAppend})</code>
    * 
    * @param array the array to append to
    * @param toAppend the object to append
    *
    * @return a new array containing all objects.  Castable into type of array[0]
    */
   public static Object[] appendToArray (Object[] array, Object toAppend)
   {
      return appendToArray (array, new Object[] {toAppend});  
   }
   
   /**
    * Append one array to another<p>
    * 
    * @param array the array to append to
    * @param toAppend the array to append
    * 
    * @return a new array containing all objects.  Castable into type of array[0]
    */
   public static Object[] appendToArray (Object[] array, Object[] toAppend)
   {
      Class c;
      if (array.length > 0) c = array[0].getClass ();
      else c = toAppend[0].getClass ();
      
      Object[] tmp = (Object[]) java.lang.reflect.Array.newInstance (c, array.length+toAppend.length);  
      System.arraycopy (array, 0, tmp, 0, array.length);
      System.arraycopy (toAppend, 0, tmp, array.length, toAppend.length);
      
      return tmp;
   }
   
   /**
    * Remove an element from an array.
    * 
    * @param array the array to remove an element from
    * @param idx the index of the element to remove
    * 
    * @return a new array containing all objects, less object at position idx.  Castable into type of array[0]
    */
   public static Object removeIndex (Object[] array, int idx)
   {
      if (idx < 0 || idx >= array.length)
         throw new IndexOutOfBoundsException ("" + idx + " not within [0, " + array.length + "]");
      if (array.length == 0)
         throw new RuntimeException ("Array length is 0");
      
      Object[] tmp = (Object[]) java.lang.reflect.Array.newInstance (array[0].getClass(), array.length-1);  

      System.arraycopy (array, 0, tmp, 0, idx);
      System.arraycopy (array, idx+1, tmp, idx, array.length-(idx+1));

      return tmp;
   }
   
   
   /**
    * quick test routine
    */
   public static void main (String[] args) throws Exception
   {
      String[] a = new String[] {"zero", "one", "two", "three", "four", "five"};
      String[] b = new String[] {"Killians", "Guiness", "Bud", "New Castle"};
      
      a = (String[]) ArrayHelper.removeIndex (a, 3); // remove three
      a = (String[]) ArrayHelper.removeIndex (a, 0); // remove zero
      a = (String[]) ArrayHelper.removeIndex (a, a.length-1); // remove last: five

      a = (String[]) ArrayHelper.appendToArray (a, b);   // append beers
      
      for (int i=0; i<a.length; i++)
      {
         System.out.println (a[i]);  
      }

      while (System.in.read() == -1);  // wait on 'Enter' to be pressed
   }
}
