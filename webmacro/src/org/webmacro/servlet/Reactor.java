
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

import java.util.*;
import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;


import org.webmacro.NotFoundException;

/**
  * This is the core servlet class that you should register as the 
  * servlet to run in your servlet runner. You must register this 
  * class as the code to execute against each script name you intend
  * to support. When a request comes in, the Reactor will check the 
  * script name and then locate a handler with the same name and 
  * execute it. 
  * <p>
  * Your job is to write the Handler that the Reactor farms the work 
  * off to. Reactor's job is to set up a comfortable environment for 
  * you to work in, and match requests up with handlers.
  * <p>
  * This is the servlet which accepts incoming requests and farms them off
  * <p>
  * @see org.webmacro.Handler
  * @deprecated you probably ought to use the standalone org.webmacro.WM instead
  */
final public class Reactor extends WMServlet
{

   private static String HANDLER_TYPE = "handler";

   private static final Log _log = new Log("reactor","Reactor Servlet");

   // CLASS VARIABLES

   /**
     * The handler to use in event of an error
     */
   private String  _errorVariable;

   /**
     * This method is called by WMServlet on start up. 
     * @exception ServletException if it failed to start
     */
   final public void start() throws ServletException
   {
      try {
         _errorVariable = getConfig(ERROR_VARIABLE);
      } catch (Exception e) {
         _log.exception(e);
         _log.error("Could not grab handler or errorVariable from broker");
         throw new ServletException("Cannot initialize critical resources");
      }

   }

   // REACTOR

   /**
     * Retrieve a handler from the "handler" provider. Equivalent to 
     * getBroker().getValue(HandlerProvider.TYPE,key)
     * @exception NotFoundException if the handler was not found
     */
   final public Handler getHandler(String key)
      throws NotFoundException
   {
      try {
         return (Handler) getBroker().getValue(HANDLER_TYPE, key);
      } catch (InvalidTypeException e) {
         throw new NotFoundException("Type \"" + HANDLER_TYPE + "\"" 
               + " is not known to the Broker: " + e);
      }
   }


   /**
     * Private method to handle the incoming request no matter where 
     * it came from, by selecting the correct handler. This is the heart
     * of the selection routine, it drives the whole process.
     * <p>
     * @param c is the WebContext built up by GET/POST methods
     */
   final public Template handle(WebContext c)
      throws HandlerException
   {
      Handler hand = null;
      String scriptName = c.getRequest().getServletPath();

      try {
         hand = getHandler(scriptName);
      } catch (Exception e) {
         _log.exception(e);
         Template tmpl= error(c,
            "Reactor: Unable to create handler for script name: " 
            + scriptName
            + "... this may be because your handler is not in your CLASSPATH,"
            + " or that you have not registered WebMacro as the handler for "
            + " the name " + scriptName +" in servlet.properties (or equiv.).");
      }
      return hand.accept(c);
   }
  
}

