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

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;

/**
  * Share an object with minimal synchronization
  */
final public class SharedReference implements Cloneable 
{
   final private class SharedNode { Reference r; }
   private final SharedNode shared = new SharedNode();
   private Reference local;

   /**
     * Create a new SoftReference to o and wrap it in a SharedReference
     */
   public SharedReference(Object o) {
      shared.r = local = new SoftReference(o);
   }

   public boolean equals(Object o) {
      return ((this == o) || 
              ((o instanceof SharedReference) 
                && ((SharedReference) o).shared == shared));
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
      if (local != shared.r) {
         synchronized(shared) {
            local = shared.r;
         }
      }
      return local.get();
   }

   /**
     * This method must be called by only a single thread.
     * Set the value of the Object.
     */
   public void set(Object o) {
      Reference r = new SoftReference(o);
      local = r;
      synchronized(shared) {
         shared.r = local; 
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

      final SharedReference so = new SharedReference("Hello!");

      Thread[] t = new Thread[20];
      for (int i = 0; i < t.length; i++) {
         final int num = i;
         Thread reader = new Thread() {
            SharedReference o = (SharedReference) so.clone();
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
         SharedReference o = (SharedReference) so.clone();
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
