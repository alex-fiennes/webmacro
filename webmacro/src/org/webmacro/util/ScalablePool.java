
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
