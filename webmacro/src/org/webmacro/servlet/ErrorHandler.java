
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

import java.lang.*;
import java.io.*;
import java.util.*;
import org.webmacro.util.*;
import org.webmacro.*;
import org.webmacro.resource.*;

/**
  * This handler gets called if a normal handler could not 
  * be constructed--it writes out an error message 
  * explaining what went wrong.
  */
final class ErrorHandler implements Handler
{

   private Template _errorTmpl = null;

   /**
     * The default error handler simply returns its template
     * @see TemplateStore
     * @exception HandlerException if you don't want to handle the connect
     * @return A Template which can be used to interpret the connection
     */
   public Template accept(WebContext c)
      throws HandlerException 
   {

      if (_errorTmpl == null) {
         try {
            String name = (String) c.getBroker().get(
                  "config", WMServlet.ERROR_TEMPLATE);
            _errorTmpl = (Template) c.getBroker().get(
                  "template", name);
         } catch (Exception e) { } 
         finally {
            if (_errorTmpl == null) {
               try {
                  _errorTmpl = (Template) c.getBroker().get(
                     "template", WMServlet.ERROR_TEMPLATE_DEFAULT);
               } catch (Exception e) {
                  throw new HandlerException("Could not load error handler");
               }
            }
         }
      }
      return _errorTmpl;
   }

   /**
     * Does nothing
     */
   public void destroy() { }

   /**
     * Does nothing
     */
   public void init() { }


   /**
     * Return the name of this handler
     */
   final public String toString()
   {
      return "WebMacro ErrorHandler";
   }
}


