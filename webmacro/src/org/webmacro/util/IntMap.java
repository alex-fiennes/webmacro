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
import java.util.HashMap;

public class IntMap {

   Object[] _values;

   public IntMap(int size) {
      _values = new Object[size];
   }

   public void put(int key, Object value) {
      if (key > _values.length) {
         int size = key * 2 + 1;
         Object newV[] = new Object[size];
         System.arraycopy(_values,0,newV,0,_values.length);
         _values = newV;
      }
      _values[key] = value;
   }

   public Object get(int key) {
      try {
         return _values[key];
      } catch (ArrayIndexOutOfBoundsException e) {
         return null;
      }
   }

   public void remove(int key) {
      try {
         _values[key] = null;
      } catch (ArrayIndexOutOfBoundsException e) {
         // do nothing
      }
   }

   public Iterator iterator() {
      return new SparseArrayIterator(_values);      
   }

   public static void main(String args[]) {

      HashMap hm = new HashMap();
      HashMap ihm = new HashMap();
      IntMap im = new IntMap(83);

      Integer[] ia = new Integer[args.length];

      for (int i = 0; i < args.length; i++) {
         hm.put(args[i], args[i]);
         im.put(i,args[i]);
         ia[i] = new Integer(i);
         ihm.put(ia[i],args[i]);
      }

      int MAX = 10000000;
      long start = System.currentTimeMillis();
      for (int i = 0; i < MAX; i++) {
         for (int j = 0; j < args.length; j++) {
            String s1 = args[j];
            Object o = hm.get(s1);
         }
      }
      long time = System.currentTimeMillis() - start;
      System.out.println("hashmap: " + time);

      start = System.currentTimeMillis();
      for (int i = 0; i < MAX; i++) {
         for (int j = 0; j < args.length; j++) {
            Integer i1 = ia[j];
            Object o = ihm.get(i1);
         }
      }
      time = System.currentTimeMillis() - start;
      System.out.println("i-hashmap: " + time);

      start = System.currentTimeMillis();
      for (int i = 0; i < MAX; i++) {
         for (int j = 0; j < args.length; j++) {
            String s1 = args[j];
            Object o = im.get(j);
         }
      }
      time = System.currentTimeMillis() - start;
      System.out.println("intmap: " + time);
   }
}
