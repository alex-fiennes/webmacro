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

import java.lang.reflect.*;
import java.util.*;

/**
  * This wraps a string array such that it can be used as the index of a 
  * hashtable, etc. It is immutable. You can directly access the 
  * final array object (a) that it contains. 
  */

final public class StringArray
{

   /**
     * The value of the string array is available as 'a'.
     */
   public final String[] a;

   /**
     * Create a new StringArray
     */
   public StringArray(String[] array) {
      a = array;
      intern();
   }

   /**
     * Create an empty StringArray
     */
   public StringArray(int size) {
      a = new String[size];
   }

   /**
     * Create a StringArray from a Vector, if the elements of the vector
     * are not strings you will get a ClassCastException
     */
   public StringArray(Vector stringVector) {
      a = new String[ stringVector.size() ];
      stringVector.copyInto(a);
      intern();
   }


   /**
     * Intern all the strings in the array (ensuring that they are 
     * unique values, and allowng the use of == for comparing them.)
     */
   public final void intern() {
      for (int i = 0; i < a.length; i++) {
         try {
            a[i] = a[i].intern();
         } catch (NullPointerException e) {
            // ignore
         }
      }
   }

   /**
     * Return a string representation listing all of the children
     */
   public final String toString()
   {
      StringBuffer buf = new StringBuffer();
      buf.append("{");
      for (int i = 0; i < a.length; i++) {
         if (i != 0) {
            buf.append(", ");
         }
         buf.append("\"");
         buf.append(a[i]);
         buf.append("\"");
      }
      buf.append("}");
      return buf.toString();
   }
  
   /**
     * Two arrays are equal if they have the same elements and are 
     * the same size.
     */
   public final boolean equals(Object o) {
      if (! (o instanceof StringArray)) {
         return false;
      }
      StringArray oa = (StringArray) o;

      if (a.length != oa.a.length) {
         return false;
      }

      for (int i = 0; i < a.length; i++) {
         if (a[i] != oa.a[i]) {
           return false;
         } 
      }  
      return true;
   }
   
   /**
     * Two arrays  that are equal have the same hashcode
     */
   public int hashCode() {
      boolean o = true;
      int code = 0;
      for (int i = 0; i < a.length; i++) {
         o = !o;
         if (o) { 
            code += a[i].hashCode() * (i + 1);
         } else {
            code -= a[i].hashCode() * (i + 1);
         }
      }
      return code;
   }

   /**
     * Test harness
     */
   public static void main(String arg[])  {

      String a[] = { "hello","there","world" };

      Vector v = new Vector();
      v.addElement("hello");
      v.addElement("there");
      v.addElement("world");

      StringArray sa1 = new StringArray(arg);
      StringArray sa2 = new StringArray(a);
      StringArray sv = new StringArray(v);

      if (sv.equals(sa2)) {
         System.out.println("Vector and array compatible: OK");
      } else {
         System.out.println("Vector and array compatible: FAILED");
      }

      if (sa1.equals(sa2)) {
         System.out.println("args equal my array (hello,there,world)");
      } else {
         System.out.println("args NOT equal my array (hello,there,world)");
      }

      if (sa1.hashCode() == sa2.hashCode()) {
         System.out.println("Same hashcode:" + sa1.hashCode());
      } else {
         System.out.println("Different hashcode.");
      }
   }
}
