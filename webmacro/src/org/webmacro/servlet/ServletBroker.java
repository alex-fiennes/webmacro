package org.webmacro.servlet;

import org.webmacro.*;
import org.webmacro.util.*;

import java.net.*;
import java.io.*;
import javax.servlet.*;

public class ServletBroker extends Broker {
   private static final ClassLoader 
      systemClassLoader = ClassLoader.getSystemClassLoader();

   private ServletContext _servletContext;

   protected ServletBroker(ServletContext sc) throws InitException {
      super((Broker) null, sc.toString());
      _servletContext = sc;
      loadDefaultSettings();
      loadSettings(WEBMACRO_PROPERTIES, true);
      loadServletSettings("org.webmacro");

      LogTarget lt = new ServletLog(_servletContext, _config);
      _ls.addTarget(lt);
      _log.notice("start: " + _name);

      init();
   }

   protected void loadServletSettings(String prefix) 
      throws InitException {
      _log.notice("Loading properties from servlet context ");
      // @@@
   }

   public static Broker getBroker(ServletContext sc) throws InitException {
      try {
         Broker b = findBroker(sc);
         if (b == null) {
            b = new ServletBroker(sc); 
            register(sc, b);
         }
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
      try {
         URL u = _servletContext.getResource(name);
         if (u == null) 
            u = systemClassLoader.getResource(name);
         return u;
      }
      catch (MalformedURLException e) {
         _log.warning("MalformedURLException caught in " + 
                      "ServletBroker.getResource for " + name);
         return null;
      }
   }

   /**
    * Get a resource (file) from the Broker's class loader 
    */
   public InputStream getResourceAsStream(String name) {
      InputStream is = _servletContext.getResourceAsStream(name);
      if (is == null) 
         is = systemClassLoader.getResourceAsStream(name);
      return is;
   }
   
}
