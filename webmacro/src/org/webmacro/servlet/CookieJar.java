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


package org.webmacro.servlet;

import java.util.*;
import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.webmacro.util.*;
import org.webmacro.*;


/**
  * Provide access to form variables
  */
final public class CookieJar
{

   private Cookie[] jar;
   private HttpServletResponse res;

   /**
     * Read the form data from the supplied Request object
     */
   CookieJar(final HttpServletRequest rq, final HttpServletResponse rs) {
      jar = rq.getCookies();
      res = rs;
   }

   /**
     * Get a form value
     */
   final public Object get(String cookieName) {
      if (jar == null) {
         return null;
      }
      for(int i = 0; i < jar.length; i++) {
         if ((jar[i] != null) && (jar[i].getName().equals(cookieName))) 
         {
            return jar[i];
         }
      }
      return null;
   }

   /**
     * Create a new cookie with the supplied name and value and set it 
     * in the response header.
     */
   public final void put(final String name, final String value) {
      Cookie c = new Cookie(name,value);
      res.addCookie(c);
   }

   /**
     * Calls put 
     */
   public final void set(final String name, final Object value) 
      throws UnsettableException
   {
      try {
         put(name, (String) value);
      } catch (ClassCastException ce) {
         throw new UnsettableException("Value must be a String");
      }
   }

}

