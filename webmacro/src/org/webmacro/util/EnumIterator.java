
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

import com.sun.java.util.collections.Iterator;
import com.sun.java.util.collections.UnsupportedOperationException;

/**
  * Allow a Java 1.1 enumeration to be used as a JDK 1.2 style Iterator
  */
final public class EnumIterator implements Iterator
{
   final Enumeration enum;
   private boolean hasNext;

   /**
     * Construct an iterator given an enumeration
     */
   public EnumIterator(Enumeration e) 
   {
      enum = e;
      hasNext = e.hasMoreElements();
   }

   /**
     * Return true if we have not yet reached the end of the enumeration
     */
   final public boolean hasNext() 
   {
      return hasNext;   
   }

   /**
     * Advance the iterator and return the next value. Return null if we
     * reach the end of the enumeration.
     */
   final public Object next() throws NoSuchElementException
   {
      if (!hasNext) {
         throw new NoSuchElementException("advanced past end of list");
      }
      Object o = enum.nextElement();
      hasNext = enum.hasMoreElements();
      return o;
   }

   /**
     * Unsupported 
     */
   final public void remove() throws UnsupportedOperationException
   {
      throw new UnsupportedOperationException();
   }

   /**
     * Test harness
     */
   static public void main(String arg[]) {
      java.util.Vector v = new java.util.Vector(arg.length);
      for (int i = 0; i < arg.length; i++) {
         v.addElement(arg[i]);
      }

      try {
         Iterator i = new EnumIterator(v.elements());
         while (i.hasNext()) {
            System.out.println("item: " + i.next());
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}

