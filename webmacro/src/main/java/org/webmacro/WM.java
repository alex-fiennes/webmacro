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


package org.webmacro;

import java.util.Properties;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webmacro.servlet.ServletBroker;
import org.webmacro.servlet.WebContext;


/**
 * This class implements the WebMacro Manager interface. You can instantiate
 * this yourself if you want to use WebMacro in standalone mode, rather than
 * subclassing from org.webmacro.servlet.WMServlet. This is actually the
 * same class used by the servlet framework to manage access to the broker
 * there, so you really don't lose much of anything by choosing to go
 * standalone by using this object. All you have to do is come up with
 * your own context objects.
 */
public class WM implements WebMacro
{
    static Logger _log =  LoggerFactory.getLogger(WM.class);

    // INIT METHODS--MANAGE ACCESS TO THE BROKER

    final private Broker _broker;      // cache for rapid access

    final private Provider _tmplProvider;
    final private Provider _urlProvider;


    /**
     * Constructs a WM which gets its properties (optionally) from the
     * file WebMacro.properties, as found on the class path.  No servlet
     * integration.  Templates will be loaded from the class path or from
     * TemplatePath.   Most users will want to use the WM(Servlet) constructor.
     */
    public WM () throws InitException
    {
        this(Broker.getBroker());
    }

    /**
     * Constructs a WM which gets its properties (optionally) from the
     * file WebMacro.properties, as found on the class path, and from properties
     * specified in a provided Properties.  No servlet
     * integration.  Templates will be loaded from the class path or from
     * TemplatePath.   Most users will want to use the WM(Servlet) constructor.
     */
    public WM (Properties p) throws InitException
    {
        this(Broker.getBroker(p));
    }

    /**
     * Constructs a WM which gets its properties from the file specified,
     * which must exist on the class path or be an absolute path.  No servlet
     * integration.  Templates will be loaded from the class path or from
     * TemplatePath.  Most users will want to use the WM(Servlet) constructor.
     */
    public WM (String config) throws InitException
    {
        this(Broker.getBroker(config));
    }

    /**
     * Constructs a WM tied to a Servlet broker.  Depending on the
     * servlet container's level of servlet support, property fetching,
     * logging, and template fetching will be managed by the servlet broker.
     */
    public WM (Servlet s) throws InitException
    {
        this(ServletBroker.getBroker(s));
    }

    /**
     * Constructs a WM is tied to a Servlet broker, with an additional set of
     * properties passed in from the caller.
     */
    public WM (Servlet s, Properties additionalProperties) throws InitException
    {
        this(ServletBroker.getBroker(s, additionalProperties));
    }

    /**
     * Constructs a WM is tied to a Servlet broker,
     * with an additional set of
     * properties passed in from the caller, using the ServletContext
     * and ClassLoader provided.
     * @since 2.1
     */
    public WM (ServletContext sc, ClassLoader cl,
               Properties additionalProperties) throws InitException
    {
        this(ServletBroker.getBroker(sc, cl, additionalProperties));
    }

    /**
     * Constructs a WM from an arbitrary Broker.  Don't use this unless
     * you have very specific needs and know what you are doing; constructing
     * a properly functioning broker is not obvious.
     */
    public WM (Broker broker) throws InitException
    {
        if (broker == null)
            throw new InitException("No Broker passed to WM()");

        _broker = broker;
        _log.info("new " + this + " v" + WebMacro.VERSION);

        try
        {
            _tmplProvider = _broker.getProvider("template");
            _urlProvider = _broker.getProvider("url");
        }
        catch (NotFoundException nfe)
        {
            _log.error("Could not load configuration", nfe);
            throw new InitException("Could not locate provider; "
                    + "This implies that WebMacro is badly misconfigured, you\n"
                    + "should double check that all configuration files and\n"
                    + "options are set up correctly. In a default install of\n"
                    + "WebMacro this likely means your WebMacro.properties file\n"
                    + "was not found on your CLASSPATH.", nfe);
        }
    }

    @Override
    public String toString ()
    {
        return "WebMacro(" + _broker.getName() + ")";
    }


    /**
     * This object is used to access components that have been plugged
     * into WebMacro; it is shared between all instances of this class and
     * its subclasses. It is created when the first instance is initialized,
     * and deleted when the last instance is shut down. If you attempt to
     * access it after the last servlet has been shutdown, it will either
     * be in a shutdown state or else null.
     */
    final public Broker getBroker ()
    {
        // this method can be unsynch. because the broker manages its own
        // state, plus the only time the _broker will be shutdown or null
        // is after the last servlet has shutdown--so why would anyone be
        // accessing us then? if they do the _broker will throw exceptions
        // complaining that it has been shut down, or they'll get a null here.
        return _broker;
    }

    /**
     * Instantiate a new context.
     */
    final public Context getContext ()
    {
        return new Context(_broker);
    }

    /**
     * Instantiate a new webcontext.
     */
    final public WebContext getWebContext (HttpServletRequest req,
                                           HttpServletResponse resp)
    {
        return new WebContext(_broker, req, resp);
    }


    /**
     * Retrieve a template from the "template" provider.
     * @exception NotFoundException  if the template could not be found
     * @exception ResourceException  if the template could not be loaded
     */
    final public Template getTemplate (String key)
            throws ResourceException
    {
        return (Template) _tmplProvider.get(key);
    }

    /**
     * Retrieve a URL from the "url" provider. Equivalent to
     * getBroker().getValue("url",url)
     * @exception NotFoundException  if the template could not be found
     * @exception ResourceException  if the template could not be loaded
     */
    final public String getURL (String url)
            throws ResourceException
    {
        return (String) _urlProvider.get(url);
    }

    /**
     * Retrieve configuration information from the "config" provider.
     * Equivalent to getBroker().get("config",key)
     * @exception NotFoundException could not locate requested information
     */
    final public String getConfig (String key)
            throws NotFoundException
    {
        try
        {
            return (String) _broker.get("config", key);
        }
        catch (NotFoundException e)
        {
            throw e;
        }
        catch (ResourceException e)
        {
            throw new NotFoundException(e.toString(), e);
        }
    }

    /**
     * Get a log to write information to. Logger type names should be lower
     * case and short. They may be printed on every line of the log
     * file. The description is a longer explanation of the type of
     * log messages you intend to produce with this Logger object.
     */
    final public Logger getLog (String type, String description)
    {
        return _log;
    }

    /**
     * Get a log using the type as the description
     */
    final public Logger getLog (String type)
    {
        return _log;
    }

    /**
     * Convenience method for writing a template to an OutputStream.
     * This method takes care of all the typical work involved
     * in writing a template.<p>
     *
     * This method uses the default <code>TemplateOutputEncoding</code> specified in
     * WebMacro.defaults or your custom WebMacro.properties.
     *
     * @param templateName name of Template to write.  Must be accessible
     *                     via TemplatePath
     * @param out          where the output of the template should go
     * @param context      The Context (can be a WebContext too) used
     *                     during the template evaluation phase
     * @throws java.io.IOException if the template cannot be written to the
     *                             specified output stream
     * @throws ResourceException if the template name specified cannot be found
     * @throws PropertyException if a fatal error occured during the Template
     *                           evaluation phase
     */
    final public void writeTemplate (String templateName, java.io.OutputStream out,
                                     Context context)
            throws java.io.IOException, ResourceException, PropertyException
    {

        writeTemplate(templateName, out,
                getConfig(WMConstants.TEMPLATE_OUTPUT_ENCODING), context);
    }

    /**
     * Convienence method for writing a template to an OutputStream.
     * This method takes care of all the typical work involved
     * in writing a template.
     *
     * @param templateName name of Template to write.  Must be accessible
     *                     via TemplatePath
     * @param out          where the output of the template should go
     * @param encoding     character encoding to use when writing the template
     *                     if the encoding is <code>null</code>, the default
     *                     <code>TemplateOutputEncoding</code> is used
     * @param context      The Context (can be a WebContext too) used
     *                     during the template evaluation phase
     * @throws java.io.IOException if the template cannot be written to the
     *                             specified output stream
     * @throws ResourceException if the template name specified cannot be found
     * @throws PropertyException if a fatal error occured during the Template
     *                           evaluation phase
     */
    final public void writeTemplate (String templateName, java.io.OutputStream out,
                                     String encoding, Context context)
            throws java.io.IOException, ResourceException, PropertyException
    {

        if (encoding == null)
            encoding = getConfig(WMConstants.TEMPLATE_OUTPUT_ENCODING);

        Template tmpl = getTemplate(templateName);
        tmpl.write(out, encoding, context);
    }

    /**
     * Print the version, and quit
     */
    public static void main (String[] args)
    {
        System.out.println("WebMacro v" + WebMacro.VERSION + ".  Built " + WebMacro.BUILD_DATE);
    }

    /**
     * Close down this WM.  This will invoke destroy() on the Broker.
     * @see Broker#destroy()
     **/
    public void destroy() 
    {
        getBroker().destroy();
    }
}
