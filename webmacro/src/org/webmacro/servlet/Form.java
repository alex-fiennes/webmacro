

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
final public class Form implements ContextTool
{

   /**
     * This is the request object from the WebContext
     */
   final HttpServletRequest _request;

   /**
     * Read the form data from the supplied Request object
     */
   Form(final HttpServletRequest r) {
      _request = r;
   }

   /**
     * Get a form value
     */
   final public Object get(String field) {
      try {
         return _request.getParameterValues(field)[0];
      } catch (NullPointerException ne) {
         return null;
      }
   }

   /**
     * Unsupported
     */
   final public void set(String key, Object value) 
      throws UnsettableException
   {
      throw new UnsettableException("Cannot set a form property");
   }

}

