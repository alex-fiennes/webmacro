
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
import org.webmacro.engine.StringTemplate;

/**
  * This handler gets called if a normal handler could not 
  * be constructed--it writes out an error message 
  * explaining what went wrong.
  */
final class ErrorHandler implements Handler
{
  private static final String DEFAULT_ERROR_TEXT = 
    "<HTML><HEAD><TITLE>Error</TITLE></HEAD>" 
    + "<BODY><H1>Error</H1>"
    + "<HR>$error</BODY></HTML>";
  
  private Template _errorTemplate = null;
   /**
     * The default error handler simply returns its template
     * @see TemplateStore
     * @exception HandlerException if you don't want to handle the connect
     * @return A Template which can be used to interpret the connection
     */
   public Template accept(WebContext c)
      throws HandlerException 
   {
     Broker broker = c.getBroker();
     String templateName;

     try {
       templateName = (String) broker.get("config", WMServlet.ERROR_TEMPLATE);
     } 
     catch (NotFoundException e) {
       templateName = WMServlet.ERROR_TEMPLATE_DEFAULT;
     }

     try {
       _errorTemplate = (Template) broker.get("template", templateName);
     }
     catch (NotFoundException e) {
       _errorTemplate = new StringTemplate(broker, DEFAULT_ERROR_TEXT);
     }
     
     return _errorTemplate;
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


