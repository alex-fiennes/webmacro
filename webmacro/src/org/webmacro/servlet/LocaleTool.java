
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


package org.webmacro.servlet;

import org.webmacro.*;
import org.webmacro.util.*;
import java.io.*;
import javax.servlet.http.*;
import javax.servlet.*;
import java.util.Locale;
import java.lang.reflect.Field;

/**
  * Provide Template with access to Locales.  Also gives access to the static
  * fields e.g., Locale.US
  */
public class LocaleTool implements ContextTool, Bag
{
   public static final String RCS = "$Id$";
   public Object init(Context context) 
      throws PropertyException
   {
      return this;
   }

   /* 
    * return the default locale for this JVM
    */
   
   final public Locale getDefault() {
      return Locale.getDefault();
   }

   /**
    * wrappers around the 3 constructors for Locale
    * 
    * XXXX: should these be cached?
    */

   final public Locale getLocale(String country) {
      return new Locale(country, "", "");
   }
   final public Locale getLocale(String country, String language) {
      return new Locale(country, language, "");
   }
   final public Locale getLocale(String country, String language, String variant) {
      return new Locale(country, language, variant);
   }

   /**
    * access to the static members such as Locale.US, etc
    */

   
   final public Object get(String field) {
      try {
          Field f = Locale.class.getField(field);
          return f.get(null);
      } catch (Exception ne) {
         return null;
      }
   }


   /**
     * Unsupported
     */
   final public void put(String key, Object value) 
      throws UnsettableException
   {
      throw new UnsettableException("Cannot set a form property");
   }


   /**
     * Unsupported
     */
   final public void remove(String key) 
      throws UnsettableException
   {
      throw new UnsettableException("Cannot unset a form property");
   }


   public void destroy(Object o) { }
}
