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
 *
 *	@author	Marcel Huijkman
 *
 *	@version	15-07-2002
 *
 */


package org.webmacro.servlet;

import org.webmacro.Broker;
import org.webmacro.Context;
import org.webmacro.Log;
import org.webmacro.util.Bag;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * This is an implementation of the WebContext interface. It has the
 * ability to hook in commonly re-usable utilities via the configuraiton
 * file for the Broker. These commonly re-usable utilities are then made
 * available for use in the context. They include: Cookie, Form, FormList,
 * and several others--see the WebMacro.properties file for the actual
 * list, and a description of what's been loaded.
 * <p>
 * This class is made to be prototyped. You create
 * a prototypical instance of the WebContext containing all the desired
 * tools and a broker. You then use the newInstance(req,resp) method
 * to create an instance of the WebContext to use versus a particular
 * request.
 * <p>
 * IMPLEMENTATION NOTE: If you subclass this method you must provide a
 * sensible implementation of the clone() method. This class uses clone()
 * to create instances of the prototype in the newInstance method. You
 * should also be sure and implement the clear() method as well.
 * <p>
 */
public class WebContext extends Context
{

    /**
     * Log configuration errors, context errors, etc.
     */
    private final Log _log;

    /**
     * The request for this http connect
     */
    private HttpServletRequest _request = null;

    /**
     * The response for this http connect
     */
    private HttpServletResponse _response = null;

    // property interface fields that are lazily set, non-final, and private

    /**
     * Construct a new WebContext. The WebContext will have WebContextTools
     * in addition to the ordinary ContextTools loaded from config.
     */
    public WebContext (Broker broker, HttpServletRequest req, HttpServletResponse resp)
    {
        super(broker);
        _request = req;
        _response = resp;
        _log = broker.getLog("WebContext");
    }


    /**
     * Clear a WebContext of it's non-shared data
     */
    public void clear ()
    {
        _request = null;
        _response = null;
        super.clear();
    }

    /**
     * The HttpServletRequest object which contains information
     * provided by the HttpServlet superclass about the Request.
     * Much of this data is provided in other forms later on;
     * those interfaces get their data from this object.
     * In particular the form data has already been parsed.
     * <p>
     * @see HttpServletRequest
     */
    public final HttpServletRequest getRequest ()
    {
        return _request;
    }

    /**
     * The HttpServletResponse object which contains information
     * about the response we are going to send back. Many of these
     * services are provided through other interfaces here as well;
     * they are built on top of this object.
     * <p>
     * @see HttpServletResponse
     */
    public final HttpServletResponse getResponse ()
    {
        return _response;
    }

    // CONVENIENCE METHODS

    /**
     * Try to get the value of a form variable from the request
     */
    final public Object getPossibleForm (String strKey)
    {
        try
        {
            Form obForm = (Form) getProperty("Form");
            return obForm.getPossibleForm(strKey);
        }
        catch (Exception e)
        {
            _log.error("Could not load Form tool", e);
            return null;
        }
    }

    /**
     * Get the value of a form variable from the request
     */
    final public String getForm (String field)
    {
        try
        {
            Bag ct = (Bag) getProperty("Form");
            return (String) ct.get(field);
        }
        catch (Exception e)
        {
            _log.error("Could not load Form tool", e);
            return null;
        }
    }

    /**
     * Get the value of a form variable from the request as an array
     */
    final public String[] getFormList (String field)
    {
        try
        {
            Bag ct = (Bag) getProperty("FormList");
            return (String[]) ct.get(field);
        }
        catch (Exception e)
        {
            _log.error("Could not load FormList tool", e);
            return null;
        }
    }

    /**
     * Get the CGI Tool
     */
    final public CGI_Impersonator getCGI ()
    {
        try
        {
            return (CGI_Impersonator) getProperty("CGI");
        }
        catch (Exception e)
        {
            _log.error("Could not load CGI tool", e);
            return null;
        }
    }

    /**
     * get a cookie from the request
     */
    final public Cookie getCookie (String name)
    {
        try
        {
            CookieJar cj = (CookieJar) getProperty("Cookie");
            return (Cookie) cj.get(name);
        }
        catch (Exception e)
        {
            _log.error("Could not load Cookie tool", e);
            return null;
        }
    }

    /**
     * send a cookie in the response
     */
    final public void setCookie (String name, String value)
    {
        try
        {
            CookieJar cj = (CookieJar) getProperty("Cookie");
            cj.set(name, value);
        }
        catch (Exception e)
        {
            _log.error("Could not load Cookie tool", e);
        }
    }

    /**
     * get the session object
     */
    final public HttpSession getSession ()
    {
        try
        {
            return (HttpSession) getProperty("Session");
        }
        catch (Exception e)
        {
            _log.error("Could not load Session tool", e);
            return null;
        }
    }
}
