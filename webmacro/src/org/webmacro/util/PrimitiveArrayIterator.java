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
  * This provides an iterator interface to an array of primitives
  */
final public class PrimitiveArrayIterator implements Iterator
{
   private final Object a;
   private int _size;
   private int pos;

   /**
     * Construct an iterator given an enumeration
     */
   public PrimitiveArrayIterator(Object array) 
   {
      if (!array.getClass().isArray()) 
        throw new IllegalArgumentException(array.getClass().getName() 
          + " is not an array.");
      this.a = array;
      _size = java.lang.reflect.Array.getLength(a);
      pos = 0;
   }

   /**
     * Return true if we have not yet reached the end of the enumeration
     */
   final public boolean hasNext() 
   {
      return (pos < _size);
   }

   /**
     * Advance the iterator and return the next value. Return null if we
     * reach the end of the enumeration.
     */
   final public Object next() throws NoSuchElementException
   {
      if (pos < _size) {
         return java.lang.reflect.Array.get(a, pos++);
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
         Iterator i = new PrimitiveArrayIterator(arg);
         while (i.hasNext()) {
            System.out.println("item: " + i.next());
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}

