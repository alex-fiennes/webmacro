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

import java.io.*;
import java.net.URL;
import java.util.*;
import javax.servlet.*;

import org.webmacro.Broker;
import org.webmacro.InitException;
import org.webmacro.Log;
import org.webmacro.util.LogSystem;

/**
 * An implementation of Broker tailored for Servlet 2.0/2.1
 * environments.  Loads templates and other resources from the class
 * path, writes log messages to the servlet log.
 * @author Brian Goetz
 * @since 0.96
 */

public class Servlet20Broker extends ServletBroker {

   protected ClassLoader _servletClassLoader;

   private Servlet20Broker(ServletContext sc,
                           ClassLoader cl,
                           Properties additionalProperties) throws InitException {
      super(sc);
      _servletClassLoader = cl;

      String propertySource = WEBMACRO_DEFAULTS + ", " + WEBMACRO_PROPERTIES;
      loadDefaultSettings();
      loadSettings(WEBMACRO_PROPERTIES, true);
      if (additionalProperties != null && additionalProperties.keySet().size() > 0) {
         propertySource += ", (additional Properties)";
         loadSettings(additionalProperties);
      }
      propertySource += ", (System Properties)";
      loadSystemSettings();
      initLog(_config);

      _log.notice("Loaded settings from " + propertySource);
      init();
   }

   public static Broker getBroker(Servlet s, Properties additionalProperties) throws InitException {
      ServletContext sc = s.getServletConfig().getServletContext();
      ClassLoader cl = s.getClass().getClassLoader();
      try {
         Object key = cl;
         if (additionalProperties != null && additionalProperties.keySet().size() > 0)
            key = new PropertiesPair(cl, additionalProperties);

         Broker b = findBroker(key);
         if (b == null) {
            b = new Servlet20Broker(sc, cl, additionalProperties);
            register(key, b);
         }
         else
            b.getLog("broker").notice("Servlet " + s.getClass().getName()
                                      + " joining Broker " + b.getName());
         return b;
      }
      catch (InitException e) {
         Log log = LogSystem.getSystemLog("wm");
         log.error("Failed to initialized WebMacro from servlet context"
                   + sc.toString());
         throw e;
      }
   }

   /**
    * Get a resource (file) from the the Broker's class loader */
   public URL getResource(String name) {
      URL u = _servletClassLoader.getResource(name);
      if (u == null)
         u = super.getResource(name);
      return u;
   }

   /**
    * Get a resource (file) from the Broker's class loader
    */
   public InputStream getResourceAsStream(String name) {
      InputStream is = _servletClassLoader.getResourceAsStream(name);
      if (is == null)
         is = super.getResourceAsStream(name);
      return is;
   }

   /**
    * Loads a class by name. Uses the servlet classloader to load the
    * class. If the class is not found uses the Broker classForName
    * implementation.  */

   public Class classForName(String name) throws ClassNotFoundException {
      Class cls = null;
      try {
         cls = _servletClassLoader.loadClass(name);
      }
      catch (ClassNotFoundException e) {
      }

      if (cls == null)
         cls = super.classForName(name);

      return cls;
   }

}
