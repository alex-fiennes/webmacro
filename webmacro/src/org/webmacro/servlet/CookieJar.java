

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

