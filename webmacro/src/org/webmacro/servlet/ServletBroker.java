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


/**
 * Base class for servlet brokers.
 * @see Servlet20Broker
 * @see Servlet22Broker
 * @see Broker
 * @author Brian Goetz
 * @since 0.96
 */
package org.webmacro.servlet;

import org.webmacro.*;
import org.webmacro.util.*;

import java.net.*;
import java.io.*;
import javax.servlet.*;

abstract public class ServletBroker extends Broker {
   protected static final ClassLoader 
      _systemClassLoader = ClassLoader.getSystemClassLoader();

   protected ServletContext _servletContext;

   protected ServletBroker(ServletContext sc) throws InitException {
      super((Broker) null, sc.toString());
      _servletContext = sc;
   }

   public static Broker getBroker(Servlet s) throws InitException {
      int minorVersion, majorVersion;

      ServletContext sc = s.getServletConfig().getServletContext();
      try {
         majorVersion = sc.getMajorVersion();
         minorVersion = sc.getMinorVersion();
      }
      catch (NoSuchMethodError e) {
         majorVersion = 2;
         minorVersion = 0;
      }

      if (majorVersion > 2 
          || (majorVersion == 2 && minorVersion >= 2))
         return Servlet22Broker.getBroker(s);
      else
         return Servlet20Broker.getBroker(s);
   }

   /** 
    * Get a resource (file), using either the Broker's class loader, or
    * the servlet's class loader, or the servlet context, whichever is
    * appropriate
    */
   abstract public URL getResource(String name);

   /**
    * Get a resource (file), using either the Broker's class loader, or
    * the servlet's class loader, or the servlet context, whichever is
    * appropriate
    */
   abstract public InputStream getResourceAsStream(String name);
   
}
