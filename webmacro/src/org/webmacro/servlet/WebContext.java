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

import java.util.*;
import java.lang.reflect.*;
import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.webmacro.*;
import org.webmacro.util.*;

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
  * @see org.webmacro.util.Property
  * @see org.webmacro.util.Map
  */
public class WebContext extends Context 
{

   /**
     * Log configuration errors, context errors, etc.
     */
  private Log _log;

   /**
     * The request for this http connect
     */
   HttpServletRequest _request = null;

   /**
     * The response for this http connect 
     */
   HttpServletResponse _response = null;

   // property interface fields that are lazily set, non-final, and private
              
   /**
     * Construct a new WebContext. The WebContext will have WebContextTools
     * in addition to the ordinary ContextTools loaded from config.
     */
   public WebContext(final Broker broker) 
   {
      super(broker);
      loadTools("WebContextTools");
   }


   /**
     * Create a new WebContext like this one, only with new values
     * for request and response
     */
   final public WebContext newInstance(
         final HttpServletRequest req, 
         final HttpServletResponse resp) 
   {
      try {

         // want: new local vars, both existing tools tables, no bean,
         // plus store req and resp somewhere, plus existing broker

         WebContext wc = (WebContext) clone();
         wc._request = req;
         wc._response = resp;
         return wc;
      } catch (Exception e) {
         _log.error("Clone not supported on WebContext!");
         return null;
      }
   }

   /**
     * Clear a WebContext of it's non-shared data
     */
   public void clear() {
      _request = null;
      _response = null;
      super.clear();
   }

   /**
     * Reinitalized a WebContext for a new request
     */
   public void reinitialize(HttpServletRequest req, HttpServletResponse resp) {
     clear();
     _request = req;
     _response = resp;
   }

   /**
     * The HttpServletRequest object which contains information 
     * provided by the HttpServlet superclass about the Request.
     * Much of this data is provided in other forms later on;
     * those interfaces get their data from this object. 
     * In particular the form data has already been parsed.
     * <p>
     * @see HttpServletRequest
     * @see org.webmacro.util.Property
     */
   public final HttpServletRequest getRequest() { 
      return _request; 
   }

   /**
     * The HttpServletResponse object which contains information
     * about the response we are going to send back. Many of these
     * services are provided through other interfaces here as well;
     * they are built on top of this object.
     * <p>
     * @see HttpServletResponse
     * @see org.webmacro.util.Property
     */
   public final HttpServletResponse getResponse() { 
      return _response; 
   }

   // LEGACY METHODS

   /**
     * @deprecated: use getProperty() and access the tool in a 
     * the conventional way, as a property of the context.
     */
   final public String getForm(String field) {
      try {
         Bag ct = (Bag) getProperty("Form");
         return (String) ct.get(field);
      } catch (Exception e) {
         _log.error("Could not load Form tool",e);
         return null;
      }
   }

   /**
     * @deprecated: use getProperty() and access the tool in a 
     * the conventional way, as a property of the context.
     */
   final public String[] getFormList(String field) {
      try {
          Bag ct = (Bag) getProperty("FormList");
         return (String[]) ct.get(field);
      } catch (Exception e) {
         _log.error("Could not load FormList tool",e);
         return null;
      }
   }

   /**
     * @deprecated: use getProperty() and access the tool in a 
     * the conventional way, as a property of the context.
     */
   final public CGI_Impersonator getCGI() {
      try {
         return (CGI_Impersonator) getProperty("CGI");
      } catch (Exception e) {
         _log.error("Could not load CGI tool",e);
         return null;
      }
   }

   /**
     * @deprecated: use getProperty() and access the tool in a 
     * the conventional way, as a property of the context.
     */
   final public Cookie getCookie(String name) {
      try {
         CookieJar cj = (CookieJar) getProperty("Cookie");
         return (Cookie) cj.get(name);
      } catch (Exception e) {
         _log.error("Could not load Cookie tool",e);
         return null;
      }
   }

   /**
     * @deprecated: use getProperty() and access the tool in a 
     * the conventional way, as a property of the context.
     */
   final public void setCookie(String name, String value) {
      try {
         CookieJar cj = (CookieJar) getProperty("Cookie");
         cj.set(name, value);
      } catch (Exception e) {
         _log.error("Could not load Cookie tool",e);
      }
   }

   /**
     * @deprecated: use getProperty() and access the tool in a 
     * the conventional way, as a property of the context.
     */
   final public HttpSession getSession() {
      try {
         return (HttpSession) getProperty("Session");
      } catch (Exception e) {
         _log.error("Could not load Session tool",e);
         return null;
      }
   }
}
