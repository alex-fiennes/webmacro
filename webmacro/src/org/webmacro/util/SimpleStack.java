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

import java.io.*;

/**
  * This is a simple stack. I would have liked to use java.util.Stack, but 
  * I need a stack that returns "null" when the Stack is empty, rather than
  * throwing an exception.
  */
final public class SimpleStack
{

   private Object[] _stack;
   private int _count = 0;

   public void push(final Object o) {
      if (o == null) {
         return;
      }
      ensureCapacity(_count + 1);
      _stack[_count] = o;
      _count++;
   }

   public Object pop() {
      if (_count == 0) {
         return null;
      }
      _count--;
      Object ret = _stack[_count];
      _stack[_count] = null;
      return ret;
   }

   public int size() {
      return _count;
   }

   public boolean isEmpty() {
      return (_count == 0);
   }

   public void clear() {
      for (int i = 0; i < _stack.length; i++) {
         _stack[i] = null;
      }
      _count = 0;
   }

   public void ensureCapacity(int numElems) {
      if (_stack == null) {
         _stack = new Object[numElems];
      } else if (numElems > _stack.length) {
         Object[] newStack = new Object[numElems];
         System.arraycopy(_stack,0,newStack,0,_stack.length);
         _stack = newStack;
      }
   }

   static public void main(String arg[]) {
      SimpleStack ss = new SimpleStack();
      for (int i = 0; i < arg.length; i++) {
         System.out.println("Pushing: " + arg[i]);
         ss.push(arg[i]);
      }
      System.out.println("Number of elements on stack: " + ss.size());
      Object o;
      while ( (o = ss.pop()) != null) {
         System.out.println("pop: " + o);
      }
      System.out.println("Number of elements on stack: " + ss.size());
   }
}
