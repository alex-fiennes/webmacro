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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.webmacro.Broker;
import org.webmacro.InitException;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

/**
 * An implementation of Broker tailored for Servlet 2.2
 * environments.  Loads templates and other resources from the servlet
 * context (WAR file), writes log messages to the servlet log, and loads
 * properties from the WAR file context parameters.
 * 
 * @author Brian Goetz
 * @author Marc Palmer (wj5@wangjammers.org)
 * @since 0.96
 */

public class Servlet22Broker extends ServletBroker
{

    static Logger _log =  LoggerFactory.getLogger(Servlet22Broker.class);
    protected final ClassLoader _servletClassLoader;
    protected String _templatePrefix;

    /**
     * Creates the broker looking in WEB-INF first
     * for WebMacro.properties before looking
     * in the application root.
     */
    protected Servlet22Broker (ServletContext sc,
                             ClassLoader cl,
                             Properties additionalProperties) throws InitException
    {
        super(sc);
        _servletClassLoader = cl;
        String propertySource = WEBMACRO_DEFAULTS;
        loadDefaultSettings();
        boolean loaded = loadSettings("WEB-INF/" + WEBMACRO_PROPERTIES, true);
        if (loaded)
            propertySource += ", " + "WEB-INF/" + WEBMACRO_PROPERTIES;
        else
        {
            loadSettings(WEBMACRO_PROPERTIES, true);
            propertySource += ", " + WEBMACRO_PROPERTIES;
        }
        propertySource += ", (WAR file)";
        loadServletSettings(Broker.SETTINGS_PREFIX);
        if (additionalProperties != null && additionalProperties.keySet().size() > 0)
        {
            propertySource += ", (additional Properties)";
            loadSettings(additionalProperties);
        }
        propertySource += ", (System Properties)";
        loadSystemSettings();

        _log.info("Loaded settings from " + propertySource);
        init();
    }

    protected void loadServletSettings (String prefix)
            throws InitException
    {
        Properties p = new Properties();
        Enumeration e = _servletContext.getInitParameterNames();
        if (e != null)
        {
            String dotPrefix = (prefix == null) ? "" : prefix + ".";
            while (e.hasMoreElements())
            {
                String key = (String) e.nextElement();
                if (prefix == null)
                    p.setProperty(key, _servletContext.getInitParameter(key));
                else if (key.startsWith(dotPrefix))
                    p.setProperty(key, _servletContext.getInitParameter(key)
                            .substring(dotPrefix.length()));
            }
        }
        _config.load(p, prefix);
    }

    @Override
    protected void init () throws InitException
    {
        super.init();
        String s = getSetting("Servlet22Broker.TemplateLocation");
        if (s == null || s.trim().equals(""))
            _templatePrefix = null;
        else
            _templatePrefix = (s.endsWith("/")) ? s : s + "/";
    }


    public static Broker getBroker (Servlet s, Properties additionalProperties) throws InitException
    {
        ServletContext sc = s.getServletConfig().getServletContext();
        ClassLoader cl = s.getClass().getClassLoader();
        return _getBroker(sc, cl, additionalProperties, true,
            s.getClass().getName());
    }

    /**
     * Get a Servlet API 2.2 compatible broker for the ServletContext specified.
     * 
     * @param sc The Servlet context
     * @param cl A ClassLoader to use, presumably the webapp classloader
     * @param additionalProperties
     * @return The broker for the servlet context.
     * @throws InitException
     * @since 2.1 JSDK
     */
    public static Broker getBroker(ServletContext sc, ClassLoader cl,
        Properties additionalProperties) throws InitException
    {
        return _getBroker(sc, cl, additionalProperties, false,
            sc.toString());
    }

    /**
     * Get an existing instance of the Servlet 2.2 broker or create a new one.
     * 
     * Templates will be retrieved relative to the ServletContext root
     * and classes loaded from the ClassLoader passed in. 
     * 
     * NOTE: Templates will <b>not</b> be loaded from the classpath.
     * 
     * @param sc The ServletContext to template access
     * @param cl The ClassLoader for class loading, typically servlet or
     *           JSP page's class loader
     * @param additionalProperties
     * @param fromServlet true if it is actually an initialization derived from
     *                    a Servlet instance passed in - just for nicer logging output
     * @param servletOrContextName Name of the servlet or context originating this broker,
     *                             for nicer logging
     *                             
     * @throws org.webmacro.InitException
     * @since 2.1
     */
    private static Broker _getBroker(ServletContext sc, ClassLoader cl,
        Properties additionalProperties, boolean fromServlet,
        String servletOrContextName) throws InitException
    {
        Broker result;
        try
        {
            Object key = sc;
            if (additionalProperties != null && additionalProperties.keySet().size() > 0)
                key = new PropertiesPair(sc, additionalProperties);

            Broker b = findBroker(key);
            if (b == null)
            {
                b = new Servlet22Broker(sc, cl, additionalProperties);
                register(key, b);
            }
            else
                _log.info(
                    (fromServlet ? "Servlet " : "ServletContext ")
                    + servletOrContextName
                    + " joining Broker" + " " + b.getName());
            result = b;
        }
        catch (InitException e)
        {
            _log.error("Failed to initialized WebMacro from "+
                    (fromServlet ? "Servlet " : "ServletContext ")
                    + servletOrContextName);
            throw e;
        }
        return result;
    }

    /**
     * Get a resource (file) from the the Broker's class loader.
     */
    @Override
    public URL getResource (String name)
    {
        try
        {
            // NOTE: Tomcat4 needs a leading '/'.
            // However, when loading resources from the class-loader, they
            // may _not_ start with a leading '/'. So we have to build
            // up two different names. Ugly!
            String contextName = name;
            if (name.startsWith("/"))
            {
                name = name.substring(1);
            }
            else
            {
                StringBuffer b = new StringBuffer(name.length() + 1);
                b.append("/");
                b.append(name);
                contextName = b.toString();
            }
            URL u = _servletContext.getResource(contextName);
            if (u != null && u.getProtocol().equals("file"))
            {
                File f = new File(u.getFile());
                if (!f.exists())
                    u = null;
            }
            if (u == null)
            {
                u = _servletClassLoader.getResource(name);
            }
            if (u == null)
                u = super.getResource(name);
            return u;
        }
        catch (MalformedURLException e)
        {
            _log.warn("MalformedURLException caught in " +
                    "ServletBroker.getResource for " + name);
            return null;
        }
    }

    /**
     * Get a resource (file) from the Broker's class loader.
     */
    @Override
    public InputStream getResourceAsStream (String name)
    {
        InputStream is = _servletContext.getResourceAsStream(name);
        if (is == null)
            is = _servletClassLoader.getResourceAsStream(name);
        if (is == null)
            is = super.getResourceAsStream(name);
        return is;
    }

    /**
     * Get a template; kind of like getting a resource, but might come
     * from a different place.
     */
    @Override
    public URL getTemplate (String name)
    {
        if (_templatePrefix == null)
            return getResource(name);
        else
        {
            URL u = getResource(_templatePrefix + name);
            return (u != null) ? u : getResource(name);
        }
    }

    /**
     * Loads a class by name. Uses the servlet classloader to load the
     * class. If the class is not found uses the Broker classForName
     * implementation.  
     */
    @Override
    public Class classForName (String name) throws ClassNotFoundException
    {
        Class cls = null;
        try
        {
            cls = _servletClassLoader.loadClass(name);
        }
        catch (ClassNotFoundException e)
        {
        }

        if (cls == null)
            cls = super.classForName(name);

        return cls;
    }

}
