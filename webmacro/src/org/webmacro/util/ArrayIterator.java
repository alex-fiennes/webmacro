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
import java.util.*;


/**
  * This provides an iterator interface to an array
  */
final public class ArrayIterator implements Iterator
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
   final public void remove() throws UnsupportedOperationException
   {
      throw new UnsupportedOperationException();
   }

   /**
     * Test harness
     */
   static public void main(String arg[]) {

      try {
         Iterator i = new ArrayIterator(arg);
         while (i.hasNext()) {
            System.out.println("item: " + i.next());
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}

