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

/**
  * This is an unsynchronized object pool. It does synchronize on the 
  * objects added to and removed from the pool but it never synchronizes
  * on its own data structures. As a result a null may be returned from
  * the pool when an object actually exists (rare case), but an object is
  * still guaranteed to be returned to only one thread.
  */
final public class UPool implements Pool
{

   // IMPLEMENTATION NOTES:

   // Assertion: An object is in a consistent state when it is returned
   // Proof: The adding and removing threads both synch on it

   // Assertion: An object cannot be returned twice from the pool 
   // Proof: Returning thread synchs on it, then checks whether it
   //        is the object in _pool[i] and returns it only if it is
   //        atomically setting _pool[i] to null from the perspective
   //        of any other thread trying to return the same object

   // Assertion: Thread collision on the same object will be rare 
   // Proof: Threads iterate through the _pool from different offsets

   // NOTE: The count1 and count2 variables are used to evenly distribute
   // the hash value across the pool. If we used only one then getters 
   // and setters might get into some kind of feedback loop where the
   // result is gets/sets commonly happening near one another, causing
   // more overwrites, etc. The _hash is simply anded with the counts 
   // to select a starting location in the pool.

   private final int _hash;
   private final int _size;
   private final Object[] _pool;
   private int _count1 = 0;
   private int _count2 = 1001;

   /**
     * Create a new Pool. Size is the logarithm of the number of 
     * slots in the pool. A size 4 pool has 16 spaces, a size 
     * 5 pool has 32, a size 6 pool has 64, and so on.
     */
   public UPool(int size) {
      _size = 1 << size;
      _hash = _size - 1; // all bits set to one!
      _pool = new Object[_size];
   }

   /**
     * Add an item to the pool for later re-use
     */
   public void put(final Object o) {
      int pos = ++_count1 & _hash;
      for (int i = 0; i < 7; i++) {
         if (_pool[pos] == null) {
            synchronized(o) {
               // who knows if it is really null, may overrwrite!
               _pool[pos] = o; 
            }
            return;
         }
         pos = (pos + 1) & _hash;
      }
   }

   /**
     * Get an item from the pool
     */
   public Object get() {
      int pos = ++_count2 & _hash;
      for (int i = 0; i < 3; i++) {
         Object o = _pool[pos]; 
         if (o != null) {
            synchronized(o) {
               if (_pool[pos] == o) {
                  // _pool[pos] could "actually" be anything, but
                  // this synchs us with anyone else who is 
                  // trying to return o, so it's good enough
                  _pool[pos] = null;
                  return o;
               }
            }
         }
         pos = (pos + 1) & _hash;
      }

      // there may have been something there, but oh well
      return null;  
   }
}
