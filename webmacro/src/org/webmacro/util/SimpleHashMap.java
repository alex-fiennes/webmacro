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

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.LinkedList;


/**
  * This map has a fixed number of buckets. Each bucket is an LRU 
  * cache. The map never increases the number of buckets once it has
  * been created. The advantage is that it synchronizes on the bucket
  * so multiple threads can access the map simultaneously without 
  * blocking, providing they access different buckets.
  */
final public class SimpleHashMap implements SimpleMap
{
   private MapNode[] _map;
   private Object[] _locks;

    private static class MapNode {
        final Object key;
        Object value;
        MapNode next;
        
        MapNode(Object key) {
            this.key = key;
        }
    }

   /**
     * Create a new SimpleMap with 1001 LRU buckets
     */
   public SimpleHashMap() {
      this(1001);
   }

   /**
     * Create a new SimpleMap with 'size' LRU buckets
     */
   public SimpleHashMap(int size) {
      _map = new MapNode[size];      
      _locks = new Object[size];
      for (int i = 0; i < size; i++) {
         _locks[i] = new Object();
      }
   }

   /**
     * Add a key to the SimpleMap. 
     */
   public void put(Object key, Object value) {
      if (key == null) {
         return;
      }
      if (value == null) {
         remove(key);
         return;
      }
      int hash = key.hashCode() % _map.length;
      if (hash < 0) { hash *= -1; }
      synchronized(_locks[hash]) {
         MapNode m = _map[hash];
         while (m != null) {
            if ((m.key == key) || (m.key.equals(key))) {
               m.value = value;
               return;
            }
            m = m.next;
         }
         m = new MapNode(key);
         m.value = value;
         m.next = _map[hash];
         _map[hash] = m;
      }
   }

   /**
     * Get the value of 'key' back. Returns null if no such key.
     */
   public Object get(Object key) {
      int hash = key.hashCode() % _map.length;
      if (hash < 0) { hash *= -1; }
      MapNode last = null;
      synchronized(_locks[hash]) {
         MapNode m = _map[hash];
         if (m == null) return null;
         while (m != null) {
            if ((m.key == key) || (m.key.equals(key))) {
               if (last != null) {
                  last.next = m.next;
                  m.next = _map[hash];
                  _map[hash] = m;
               }
               return m.value;
            }
            last = m;
            m = m.next;
         } 
      }
      return null;
   }

   /**
     * Ensure that the key does not appear in the map
     */
   public Object remove(Object key) {
      int hash = key.hashCode() % _map.length;
      if (hash < 0) { hash *= -1; }
      synchronized(_locks[hash]) {
         MapNode m = _map[hash];
         if (m == null) return null;
         MapNode last = null;
         while (m != null) {
            if ((m.key == key) || (m.key.equals(key))) {
               if (last == null) {
                  _map[hash] = m.next; // may be to null :-)
               } else {
                  last.next = m.next;
               }
               return m;
            }
            last = m;
            m = m.next;
         }
         return null;
      }
   }

   public void clear() {
      for (int i = 0; i < _map.length; i++) {
         synchronized(_locks[i]) {
            _map[i] = null;
         }
      }
   }
   

   /**
     * Returns an iterator that will walk along a snapshot of the keys of 
     * this SimpleMap. If the Map changes during the creation of this 
     * iterator some values may be missed or included twice, but otherwise
     * it will work. The remove() method on this iterator is well defined.
     */
   public Iterator iterator() {
      LinkedList ll = new LinkedList();
      for (int i = 0; i < _map.length; i++) {
         synchronized(_locks[i]) {
            MapNode m = _map[i];
            while (m != null) {
               ll.add(m.key);
               m = m.next;
            }
         }
      }
      final Iterator lli = ll.iterator();
      return new Iterator() {
         Object last = null;

         public boolean hasNext() {
            return lli.hasNext();
         }

         public Object next() {
            last = lli.next();
            return last;
         }

         public void remove() {
             SimpleHashMap.this.remove(last);
         }
      };
   }

   public static void main(String arg[]) {

      SimpleHashMap sm = new SimpleHashMap(3);

      for (int i = 0; i < arg.length; i++) {
         Integer oi = new Integer(i);
         System.out.println("*** Adding " + arg[i] + " = " + oi);
         sm.put(arg[i], oi);
         for (int j = 0; j < arg.length; j++) {
            System.out.println(arg[j] + " ==> " + sm.get(arg[j]));      
         }
         System.out.println("==========\n");
      }

      System.out.println("\nACCESSORS:\n");
      Iterator it1 = sm.iterator();
      while (it1.hasNext()) {
         String key = (String) it1.next();
         System.out.println("*** Removing " + key);
         it1.remove();

         Iterator it2 = sm.iterator();
         while (it2.hasNext()) {
            String key2 = (String) it2.next();
            System.out.println(key2 + " => " + sm.get(key2));
         }
         System.out.println("==========\n");
     }
   }
}
