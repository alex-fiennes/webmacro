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

/**
  * Share an object with minimal synchronization
  */
final public class SharedObject implements Cloneable 
{
   final private class SharedNode { Object o; }
   private final SharedNode shared = new SharedNode();
   private Object local;

   public SharedObject(Object o) {
      shared.o = local = o;
   }

   public boolean equals(Object o) {
      return ((this == o) || 
              ((o instanceof SharedObject) 
                && ((SharedObject) o).shared == shared));
   }

   public int hashCode() {
      Object o = get();
      return (o != null) ? o.hashCode() : 0;
   }

   public String toString() {
      Object o = get();
      return (o != null) ? o.toString() : null;
   }

   /**
     * This method must be called by only a single thread.
     * Get a copy of the Object.
     */
   public Object get() {
      if (local != shared.o) {
         synchronized(shared) {
            local = shared.o;
         }
      }
      return local;
   }

   /**
     * This method must be called by only a single thread.
     * Set the value of the Object.
     */
   public void set(Object o) {
      synchronized(shared) {
         local = o;
         shared.o = local; 
      }
   }

   /**
     * Make a copy for another thread
     */
   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException e) {
         e.printStackTrace();
         return null;
      }
   }

   public static void main(String arg[]) {

      final SharedObject so = new SharedObject("Hello!");

      Thread[] t = new Thread[20];
      for (int i = 0; i < t.length; i++) {
         final int num = i;
         Thread reader = new Thread() {
            SharedObject o = (SharedObject) so.clone();
            public void run() {
               Object l = o.get();
               while (true) {
                  Object lo = o.get();
                  if (l != lo) {
                     l = lo;
                  }
                  try {
                     sleep(num);
                  } catch (Exception e) {
                     e.printStackTrace();
                  }
                  System.out.println(l);
               }
            }
         };
         reader.setDaemon(true);
         reader.start();
      }

      Thread writer = new Thread() {
         SharedObject o = (SharedObject) so.clone();
         public void run() {
            for (int i = 0; i < 100000; i++) {
               try {
                  sleep(100);
               } catch (Exception e) {
                  e.printStackTrace();
               }
               o.set(new Integer(i));
            }
         }
      };
      writer.start();
   }
}
