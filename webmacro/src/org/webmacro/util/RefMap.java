
package org.webmacro.util;

import java.util.HashMap;
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
  * RefMap is a very specialized data structure that is probably
  * not suitable for most uses. It is a faster version of a 
  * HashMap type structure which is broken in the following 
  * ways, for efficiency: (1) it does not use object.equals()
  * to compare keys, so you must use the identical instance
  * as the key with get() as with put().  (2) it never exands
  * itself, but instead throws an exception when it fills up.
  * you can use the copy() method to make a bigger one in 
  * this case. (3) you cannot remove data from the map. 
  * (4) it is not synchronized on reads, but it does guarantee 
  * that the value read did at one point belong to the key 
  * supplied (and not to some other key).
  * <p>
  * RefMap was implemented to optimize a particular section
  * of PropertyOperator where the above characteristics are
  * acceptable and the speed increase desirable. Do not use 
  * it in any other context unless you are really sure you 
  * understand just how inconvenient the above limitations 
  * can be. 
  */

final public class RefMap {

   final Object _key[];
   final Object _value[];
   int _size;

   public RefMap() {
      this(1001);
   }

   public RefMap(int size) {
      _key = new Object[size];
      _value = new Object[size];
      _size = 0;
   }

   synchronized public void put(Object key, Object value) 
      throws java.lang.IndexOutOfBoundsException
   {
      if (key == null) {
         return;
      }
      int loc = System.identityHashCode(key) % _key.length;
      if (loc < 0) { loc *= -1; }
      if ((_key[loc] != null) || (_key[loc] == key)) {
         int i = loc + 1;
         while (i != loc) {
            if (i == _key.length) {
               i = 0;
            } else if ((_key[i] == null) || (_key[loc] == key)) {
               break;
            } else {
               i++;
            }
         }
         loc = i;
      }
      if ((_key[loc] == null) || (_key[loc] == key)) {
         _size++;
         _value[loc] = value;
         _key[loc] = key;
      } else {
         throw new java.lang.IndexOutOfBoundsException("RefMap is Full, use realloc");
      }
   }

   /**
     * WARNING: this method is unsynchronized and it might 
     * return invalid data. This could happen if one thread
     * attempted to write at the same moment as another 
     * thread attempted a read.
     */
   public Object get(Object key) {
      if (key == null) {
         return null;
      }
      int loc = System.identityHashCode(key) % _key.length;
      if (loc < 0) { loc *= -1; }
      if (_key[loc] == key) {
         return _value[loc];   
      } else {
         int i = loc + 1;
         while (i != loc) {
            if (i == _key.length) {
               i = 0;
            } else if (_key[i] == key) {
               return _value[i];
            } else {
               i++;
            }
         }
         return null;
      }
   }

   public Enumeration elements() {
      return new Enumeration() {
         int loc = -1;
         boolean _consumed = true;

         // consumed: do we use the item or just move to it?
         private void advance() {
            if (_consumed) {
               try {
                  while (_key[++loc] == null) { }
               } catch (ArrayIndexOutOfBoundsException e) {
                  // do nothing
               }
               _consumed = false;
            }
         }

         public boolean hasMoreElements() {
            advance();
            return (loc < _key.length);
         }

         public Object nextElement() throws NoSuchElementException 
         {
            if (! hasMoreElements()) {
               throw new NoSuchElementException("Past end of Enumeration");
            }
            _consumed = true;
            return _key[loc];
         }

      };
   }

   public RefMap copy(int size) 
      throws IndexOutOfBoundsException
   {
      if (_size > size) {
         throw new IndexOutOfBoundsException("New map must be bigger than the old map");
      }
      RefMap nm = new RefMap(size);
      for (int i = 0; i < _key.length; i++) {
         if (_key[i] != null) {
            nm.put(_key[i],_value[i]);
         }
      }
      return nm;
   }

   public void clear() {
      for (int i = 0; i < _key.length; i++) {
         _key[i] = null;
         synchronized(_key) { } // control update order
         _value[i] = null;
      }
   }

   public int size() {
      return _size;
   }

   public int capacity() {
      return _key.length;
   }


   public static void main(String arg[]) {

      RefMap rm = new RefMap(11);
      HashMap hm = new HashMap();
      Integer[] vals = new Integer[arg.length];

      for(int i = 0; i < arg.length; i++) {
         arg[i] = new String(arg[i]);
      }

      for(int i = 0; i < arg.length; i++) { 
         vals[i] = new Integer(i);
      }

      for(int i = 0; i < arg.length; i++) {
         rm.put(arg[i],vals[i]);
         hm.put(arg[i],vals[i]);
      }

      for(int i = 0; i < arg.length; i++) {
         System.out.println(arg[i] + " :rm: " + rm.get(arg[i]));
      }
      System.out.println();
      for(int i = 0; i < arg.length; i++) {
         System.out.println(arg[i] + " :hm: " + hm.get(arg[i]));
      }

      long time;
      int size = 10;
      for(int q = 0; q < 200; q++) {
         time = System.currentTimeMillis();
         for(int i = 1; i < size; i++) {
            for(int j = 1; j < arg.length; j++) {
               Object o = rm.get(arg[j]);
            }
         }
         System.out.println("refmap : " + (System.currentTimeMillis() - time));
    
         time = System.currentTimeMillis();
         for(int i = 1; i < size; i++) {
            for(int j = 1; j < arg.length; j++) {
               Object o = rm.get(arg[j]);
            }
         }
         System.out.println("hashmap: " + (System.currentTimeMillis() - time));

      }

      System.out.println();
      System.out.println("refmap enum: ");
      Enumeration en = rm.elements();
      while (en.hasMoreElements()) {
         System.out.println("elem: " + en.nextElement());
      }
   }
}


