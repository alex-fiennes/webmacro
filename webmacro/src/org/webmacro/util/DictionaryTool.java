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

import java.util.Dictionary;
import java.util.Enumeration;

/**
  * A HashTool wraps a java.util.Dictionary and provides additional features
  */
final public class DictionaryTool extends Dictionary 
{


   /**
     * Return a string which concatenates the keys, putting commas
     * between the keys
     */
   public final String keyString() {
      final Enumeration keys = dict.keys();
      String key = null;
      StringBuffer buf = new StringBuffer(10 * dict.size());
      while (keys.hasMoreElements()) {
         if (key != null) {
            buf.append(", ");
         } 
         key =(keys.nextElement()).toString();
         buf.append(key);
      }
      return buf.toString();
   }

   /**
     * The dictionary being wrapped
     */
   final private Dictionary dict;

   /**
     * Wrap the supplied dictionary. 
     */
   public DictionaryTool(final Dictionary dict) 
   {
      this.dict = dict;
   }

   /**
     * Forward call to Dictionary
     */
   final public Enumeration elements() {
      return dict.elements();
   }

   /**
     * Forward call to Dictionary
     */
   final public boolean isEmpty() {
      return dict.isEmpty();
   }

   /**
     * Forward call to Dictionary
     */
   final public Object get(final Object key)
   {
      return dict.get(key);
   }

   /**
     * Forward call to Dictionary
     */
   final public Enumeration keys()
   {
      return dict.keys();
   }

   /**
     * Forward call to Dictionary
     */
   final public Object put(final Object newKey, final Object newValue)
   {
      return dict.put(newKey,newValue);   
   }

   /**
     * Forward call to Dictionary
     */
   final public Object remove(final Object key)
   {
      return dict.remove(key);
   }

   /**
     * Forward call to Dictionary
     */
   final public int size()
   {
      return dict.size();
   }

   static public void main(String arg[]) {

      Dictionary d = new java.util.Hashtable();

      System.out.println("Adding arguments to hashtable.");
      for (int i = 0; i < arg.length; i++) {
         d.put(arg[i],"argument " + i);
      }

      System.out.println("Wrapping hashtable");
      DictionaryTool dt = new DictionaryTool(d);

      System.out.println("keyString: " + dt.keyString());

      System.out.println("Done.");
   }

}
