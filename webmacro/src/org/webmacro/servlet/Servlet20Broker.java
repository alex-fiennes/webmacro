package org.webmacro.servlet;

import org.webmacro.*;
import org.webmacro.util.*;

import java.net.*;
import java.io.*;
import javax.servlet.*;

public class Servlet20Broker extends ServletBroker {
   protected ClassLoader _servletClassLoader;

   protected Servlet20Broker(ServletContext sc, 
                             ClassLoader cl) throws InitException {
      super(sc);
      _servletClassLoader = cl;

      loadDefaultSettings();
      loadSettings(WEBMACRO_PROPERTIES, true);
      if (_config.getBooleanSetting("LoadSystemProperties")) 
         loadSystemSettings();

      if (_config.getBooleanSetting("LogUsingServletLog"))
        _ls.addTarget(new ServletLog(_servletContext, _config));
      else
        initLog();
      init();
   }

   public static Broker getBroker(Servlet s) throws InitException {
      ServletContext sc = s.getServletConfig().getServletContext();
      ClassLoader cl = s.getClass().getClassLoader();
      try {
         Broker b = findBroker(cl);
         if (b == null) {
            b = new Servlet20Broker(sc, cl); 
            register(cl, b);
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
    * Get a resource (file) from the the Broker's class loader
    */
   public URL getResource(String name) {
      URL u = _servletClassLoader.getResource(name);
      if (u == null) 
         u = _systemClassLoader.getResource(name);
      return u;
   }

   /**
    * Get a resource (file) from the Broker's class loader 
    */
   public InputStream getResourceAsStream(String name) {
      InputStream is = _servletClassLoader.getResourceAsStream(name);
      if (is == null) 
         is = _systemClassLoader.getResourceAsStream(name);
      return is;
   }
   
}
