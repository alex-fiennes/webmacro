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

import java.util.NoSuchElementException;

/**
  * This is a simple unsynchronized stack for the primitive type int
  */
final public class IntStack
{

   private int[] _stack;
   private int _count = 0;

   public void push(final int i) {
      ensureCapacity(_count + 1);
      _stack[_count] = i;
      _count++;
   }

   public int pop() 
      throws NoSuchElementException
   {
      if (_count == 0) {
         throw new NoSuchElementException("More pop()s than push()es");   
      }
      _count--;
      return _stack[_count];
   }

   public int size() {
      return _count;
   }

   public boolean isEmpty() {
      return (_count == 0);
   }

   public void clear() {
      _count = 0;
   }

   public void ensureCapacity(int numElems) {
      if (_stack == null) {
         _stack = new int[numElems];
      } else if (numElems > _stack.length) {
         int[] newStack = new int[numElems];
         System.arraycopy(_stack,0,newStack,0,_stack.length);
         _stack = newStack;
      }
   }

}
