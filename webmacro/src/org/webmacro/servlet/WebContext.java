
/*
 * Copyright (c) 1998, 1999 Semiotek Inc. All Rights Reserved.
 *
 * This software is the confidential intellectual property of
 * of Semiotek Inc.; it is copyrighted and licensed, not sold.
 * You may use it under the terms of the GNU General Public License,
 * version 2, as published by the Free Software Foundation. If you 
 * do not want to use the GPL, you may still use the software after
 * purchasing a proprietary developers license from Semiotek Inc.
 *
 * This software is provided "as is", with NO WARRANTY, not even the 
 * implied warranties of fitness to purpose, or merchantability. You
 * assume all risks and liabilities associated with its use.
 *
 * See the attached License.html file for details, or contact us
 * by e-mail at info@semiotek.com to get a copy.
 */


package org.webmacro.servlet;

import org.webmacro.util.java2.*;
import org.webmacro.*;
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
  * @see org.webmacro.servlet.Reactor
  * @see org.webmacro.util.Property
  * @see org.webmacro.util.Map
  */
final public class WebContext extends Context 
{

   // raw data fields that are always set, final, and available to the package

   /**
     * Log configuration errors, context errors, etc.
     */
   private final static Log _log = new Log("webcon","WebContext Messages");

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
     * Construct a new WebContext. This class will be loaded with a set
     * of tools and other things, which can be configured. Actual 
     * runtime instances which are used on a per-request basis 
     * will be obtained from this prototype using the newInstance 
     * factory method.
     */
   public WebContext(final Broker broker) 
   {
      super(broker);
      try {
         String tools = (String) broker.getValue("config","TemplateTools");
         addTools(tools);
      } catch (InvalidTypeException it) {
         _log.exception(it);
         _log.error("config type not registered with broker!");
      } catch (NotFoundException ne) {
         _log.exception(ne);
         _log.warning("could not load tools from config: " + ne);
      }
   }


   /**
     * Create a new WebContext like this one, only with new values
     * for request and response
     */
   final public WebContext clone(
         final HttpServletRequest req, 
         final HttpServletResponse resp) 
   {
      try {

         // want: new local vars, both existing tools tables, no bean,
         // plus store req and resp somewhere, plus existing broker

         WebContext wc = (WebContext) clone(null);
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
      super.clear();
      _request = null;
      _response = null;
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


   public String getForm(String field) {
      try {
         Bag ct = (Bag) getTool("Form");
         return (String) ct.get(field);
      } catch (Exception e) {
         _log.exception(e);
         _log.error("Could not load Form tool");
         return null;
      }
   }

   public String[] getFormList(String field) {
      try {
          Bag ct = (Bag) getTool("FormList");
         return (String[]) ct.get(field);
      } catch (Exception e) {
         _log.exception(e);
         _log.error("Could not load FormList tool");
         return null;
      }
   }

   public CGI_Impersonator getCGI() {
      try {
         return (CGI_Impersonator) getTool("CGI");
      } catch (Exception e) {
         _log.exception(e);
         _log.error("Could not load CGI tool");
         return null;
      }
   }

   public Cookie getCookie(String name) {
      try {
         CookieJar cj = (CookieJar) getTool("Cookie");
         return (Cookie) cj.get(name);
      } catch (Exception e) {
         _log.exception(e);
         _log.error("Could not load Cookie tool");
         return null;
      }
   }

   public void setCookie(String name, String value) {
      try {
         CookieJar cj = (CookieJar) getTool("Cookie");
         cj.set(name, value);
      } catch (Exception e) {
         _log.exception(e);
         _log.error("Could not load Cookie tool");
      }
   }

   public HttpSession getSession() {
      try {
         return (HttpSession) getTool("Session");
      } catch (Exception e) {
         _log.exception(e);
         _log.error("Could not load Session tool");
         return null;
      }
   }
}
