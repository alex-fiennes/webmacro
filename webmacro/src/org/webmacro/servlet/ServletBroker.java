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
