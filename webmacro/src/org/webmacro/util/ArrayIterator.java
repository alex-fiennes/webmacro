
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
import org.webmacro.util.java2.*;

/**
  * This provides an iterator interface to an array
  */
final public class ArrayIterator implements com.sun.java.util.collections.Iterator
{
   final Object[] a;
   int pos;

   /**
     * Construct an iterator given an enumeration
     */
   public ArrayIterator(Object[] array) 
   {
      this.a = array;
      pos = 0;
   }

   /**
     * Return true if we have not yet reached the end of the enumeration
     */
   final public boolean hasNext() 
   {
      return (pos < a.length);
   }

   /**
     * Advance the iterator and return the next value. Return null if we
     * reach the end of the enumeration.
     */
   final public Object next() throws NoSuchElementException
   {
      if (pos < a.length) {
         return a[pos++];
      } else {
         throw new NoSuchElementException("Advanced beyond end of array");
      }
   }

   /**
     * Unsupported 
     */
   final public void remove() throws com.sun.java.util.collections.UnsupportedOperationException
   {
      throw new com.sun.java.util.collections.UnsupportedOperationException();
   }

   /**
     * Test harness
     */
   static public void main(String arg[]) {

      try {
         com.sun.java.util.collections.Iterator i = new ArrayIterator(arg);
         while (i.hasNext()) {
            System.out.println("item: " + i.next());
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}

