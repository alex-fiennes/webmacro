package org.webmacro.servlet;

import org.webmacro.*;
import org.webmacro.util.*;

import java.net.*;
import java.io.*;
import java.util.*;
import javax.servlet.*;

public class Servlet22Broker extends ServletBroker {

   protected Servlet22Broker(ServletContext sc) throws InitException {
      super(sc);
      loadDefaultSettings();
      loadSettings(WEBMACRO_PROPERTIES, true);
      loadSystemSettings();
      if (_config.getBooleanSetting("LoadSystemProperties")) 
         loadServletSettings(Broker.SETTINGS_PREFIX);

      if (_config.getBooleanSetting("LogUsingServletLog"))
        _ls.addTarget(new ServletLog(_servletContext, _config));
      else
        initLog();
      init();
   }

   protected void loadServletSettings(String prefix) 
      throws InitException {
      _log.notice("Loading properties from servlet context ");
      Properties p = new Properties();
      Enumeration e = _servletContext.getInitParameterNames();
      String dotPrefix = (prefix == null) ? "" : prefix + ".";
      while (e.hasMoreElements()) {
         String key = (String) e.nextElement();
         if (prefix == null)
            p.setProperty(key, _servletContext.getInitParameter(key));
         else if (key.startsWith(dotPrefix)) 
            p.setProperty(key, _servletContext.getInitParameter(key)
                                      .substring(dotPrefix.length()));
      }
      _config.load(p, prefix);
      
   }

   public static Broker getBroker(Servlet s) throws InitException {
      ServletContext sc = s.getServletConfig().getServletContext();
      try {
         Broker b = findBroker(sc);
         if (b == null) {
            b = new Servlet22Broker(sc); 
            register(sc, b);
         }
         else 
           b.getLog("broker").notice("Servlet " 
                                     + s.getServletConfig().getServletName()
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
      try {
         URL u = _servletContext.getResource(name);
         if (u == null) 
            u = _systemClassLoader.getResource(name);
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
         is = _systemClassLoader.getResourceAsStream(name);
      return is;
   }
   
}
