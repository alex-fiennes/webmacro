/*
 * Copyright (C) 1998-2000 Semiotek Inc. All Rights Reserved. Redistribution and use in source and
 * binary forms, with or without modification, are permitted under the terms of either of the
 * following Open Source licenses: The GNU General Public License, version 2, or any later version,
 * as published by the Free Software Foundation (http://www.fsf.org/copyleft/gpl.html); or The
 * Semiotek Public License (http://webmacro.org/LICENSE.) This software is provided "as is", with NO
 * WARRANTY, not even the implied warranties of fitness to purpose, or merchantability. You assume
 * all risks and liabilities associated with its use. See www.webmacro.org for more information on
 * the WebMacro project.
 */

/**
 * Base class for servlet brokers.
 * 
 * @see Servlet20Broker
 * @see Servlet22Broker
 * @see Broker
 * @author Brian Goetz
 * @since 0.96
 */
package org.webmacro.servlet;

import java.util.Properties;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webmacro.Broker;
import org.webmacro.InitException;

/**
 * The base broker for Servlets and web applications. getBroker will instantiate the correct
 * ServletXXBroker for the version of the servlet API that is available.
 * 
 * @see #getBroker
 * @since Dawn of time
 */
abstract public class ServletBroker
  extends Broker
{

  protected ServletContext _servletContext;
  protected Logger _log = LoggerFactory.getLogger(ServletBroker.class);

  protected ServletBroker(ServletContext sc) throws InitException
  {
    super((Broker) null,
          sc.toString());
    _servletContext = sc;
  }

  public static Broker getBroker(Servlet s,
                                 Properties additionalProperties)
      throws InitException
  {
    int minorVersion, majorVersion;

    ServletContext sc = s.getServletConfig().getServletContext();
    try {
      majorVersion = sc.getMajorVersion();
      minorVersion = sc.getMinorVersion();
    } catch (NoSuchMethodError e) {
      majorVersion = 2;
      minorVersion = 0;
    }

    Broker b;
    if (majorVersion > 2 || (majorVersion == 2 && minorVersion >= 2))
      b = Servlet22Broker.getBroker(s, additionalProperties);
    else
      b = Servlet20Broker.getBroker(s, additionalProperties);
    return b;
  }

  /**
   * Get a Broker according to Servlet API version, when no Servlet reference is available.
   * 
   * @param sc
   *          The ServletContext of the web application
   * @param cl
   *          A class loader, hopefully that of the web application, to use when loading resources
   *          or classes.
   * @return The broker for the servlet context.
   * @since 2.1 JSDK
   */
  public static Broker getBroker(ServletContext sc,
                                 ClassLoader cl,
                                 Properties additionalProperties)
      throws InitException
  {
    int minorVersion, majorVersion;

    try {
      majorVersion = sc.getMajorVersion();
      minorVersion = sc.getMinorVersion();
    } catch (NoSuchMethodError e) {
      majorVersion = 2;
      minorVersion = 0;
    }

    Broker b;
    if (majorVersion > 2 || (majorVersion == 2 && minorVersion >= 2))
      b = Servlet22Broker.getBroker(sc, cl, additionalProperties);
    else
      b = Servlet20Broker.getBroker(sc, cl, additionalProperties);
    return b;
  }

  public static Broker getBroker(Servlet s)
      throws InitException
  {
    return getBroker(s, null);
  }

  public ServletContext getServletContext()
  {
    return _servletContext;
  }

  protected static final class PropertiesPair
  {
    private final Object obj;
    private final Properties p;

    public PropertiesPair(Object s,
                          Properties p)
    {
      this.obj = s;
      this.p = p;
    }

    @Override
    public boolean equals(Object o)
    {
      if (this == o)
        return true;
      if (!(o instanceof PropertiesPair))
        return false;

      final PropertiesPair servletPropertiesPair = (PropertiesPair) o;

      if (!p.equals(servletPropertiesPair.p))
        return false;
      if (!obj.equals(servletPropertiesPair.obj))
        return false;

      return true;
    }

    @Override
    public int hashCode()
    {
      int result;
      result = obj.hashCode();
      result = 29 * result + p.hashCode();
      return result;
    }
  }
}
