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
  * This is a scalable pool. It reduces locking contention by multiplexing
  * across a number of internal pools. Each of those pools is a stack 
  * which can grow to be as large as necessary to maintain a constant
  * supply of the pooled objects.
  */
final public class ScalablePool implements Pool
{

   private static final int FAST_HASH=7;
   final private Object[][] _stack = new Object[FAST_HASH + 1][];
   final private Object[] _lock = new Object[FAST_HASH + 1];
   final private int _count[] = new int[FAST_HASH + 1];
   private int _hash1 = 0;
   private int _hash2 = 101;

   /**
     * Create a new Pool.
     */
   public ScalablePool() {
      for (int i = 0; i <= FAST_HASH; i++) {
         _stack[i] = new Object[10];
         _lock[i] = new Object();
         _count[i] = 0;
      }
   }

   /**
     * Add an item to the pool for later re-use
     */
   public void put(final Object o) {
      if (o == null) return;
      int hash = (_hash1++ & FAST_HASH);
      Object[] stack = _stack[hash];

      synchronized(_lock[hash]) {
         int count = _count[hash];
         try {
            stack[count] = o;
         } catch (ArrayIndexOutOfBoundsException e) {
            int size = stack.length;
            stack = new Object[size * 2];
            System.arraycopy(_stack[hash],0,stack,0,size);
            _stack[hash] = stack;

            stack[count] = o;
         }
         _count[hash] = count + 1;
      }
   }

   /**
     * Get an item from the pool
     */
   public Object get() {
      Object ret = null;
  
      for (int i = 0; i < FAST_HASH; i++) {
         int hash = _hash2++ & FAST_HASH; 
         synchronized(_lock[hash]) {
            int count = _count[hash];
            Object[] stack = _stack[hash];
            if (count == 0) {
               continue;
            }
            count--;
            ret = stack[count];
            stack[count] = null;
            _count[hash] = count; 
         }
         return ret;
      }
      return null;
   }
}
