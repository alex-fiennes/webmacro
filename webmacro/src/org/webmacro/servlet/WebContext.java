
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

import org.webmacro.util.*;
import org.webmacro.engine.*;
import org.webmacro.resource.*;

/**
  * A WebContext is the point of intersection between the back-end
  * Java code and the template script. Programmers are expected to
  * place objects needed by the template into the WebContext,
  * whereupon they become available in the template as a variable.
  * <p>
  * At the simplest level, this means you can create variables by
  * adding them as strings: put("Title","Product Listings") creates
  * a variable in the template called $Title which evaluates to 
  * the string "Product Listings".
  * <p>
  * You can also put complex, bean-like, objects into the context
  * and rely on WebMacro's property introspection to analyze your
  * object and determine what sub-variables it can extract from
  * the object you deposit.
  * <p>
  * For example, if you put a Customer object into the WebContext
  * under the key "Buyer", then that creates a $Buyer variable
  * in the template script. The template can then use methods
  * on the Customer class via the introspector. So, $Buyer.Name
  * might be translated into Customer.getName(). See the 
  * PropertyOperator class for an explanation of the kinds of
  * introspection that WebMacro will performed. 
  * <p>
  * Please note that if you put values into the map that have 
  * the same name as properties already declared here then your values 
  * will be hidden from the WebMacro script language--Property 
  * introspection will find the properties before checking the map. 
  * <p>
  * If you absolutely must use the same names as the WebContext
  * a workaround is to declare your own hashtable and 
  * put them into that. Then put your hashtable into the context 
  * like this: context.put("data", myHashtable)--then you can access
  * the values of your hashtable as $data.Somevalue
  * <p>
  * @see org.webmacro.servlet.Reactor
  * @see org.webmacro.util.Property
  * @see org.webmacro.util.Map
  */
public interface WebContext extends Cloneable, Map
{

   /**
     * Create a new WebContext like this one, only with new values
     * for request and response
     */
   public WebContext clone(
         final HttpServletRequest req, 
         final HttpServletResponse resp);

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
   public HttpServletRequest getRequest();

   /**
     * The HttpServletResponse object which contains information
     * about the response we are going to send back. Many of these
     * services are provided through other interfaces here as well;
     * they are built on top of this object.
     * <p>
     * @see HttpServletResponse
     * @see org.webmacro.util.Property
     */
   public HttpServletResponse getResponse();


   /**
     * The broker provides access to extended services and other 
     * resources, which live in the org.webmacro.resource package,
     * or that you define, or obtain from a third party.
     */
   public Broker getBroker();

   /**
     * A ContextTool is a simple object with get/set methods that provides
     * some sort of extended functionality. Tools can generally be used 
     * inside the context under their own names. This method helps the 
     * back end programmer access them as well, by returning the tool 
     * as an object with the ContextTool interface. If the named object
     * is a Macro, it will be evaluated. If the result is a ContextTool, 
     * it will be returned--otherwise an exception is raised.
     * @exception InvalidContextException named object is not a ContextTool
     */
   public ContextTool getTool(String key) throws InvalidContextException;

   /**
     * This is a get() method. If the result is a Macro, it is evaluated
     * first before returning it, so that the return of this method is 
     * never a Macro. This is useful for accessing objects that change
     * their behavior based on other objects in the context.
     * @return a dereferenced object, or null if the key does not exist
     * @exception InvalidContextException attempt to evaluate macro failed
     */
   public Object getMacro(String key) throws InvalidContextException;

   // LEGACY METHODS

   /**
     * Access a form value as a string. If there are multiple form 
     * values corresponding to this name the first one is returned.
     * Tool interface: implemented by loading a ContextTool
     */
   public String getForm(String field);


   /**
     * Access a form value as a list. If there is only one value 
     * corresponding to this name it will be returned as a one 
     * element array.
     * Tool interface: implemented by loading a ContextTool
     */
   public String[] getFormList(String field);


   /**
     * Access to properties with names familiar to CGI programmers. 
     * All of this data is available via the request and response 
     * interfaces, but ex-CGI programmers may find this more familiar.
     * Tool interface: implemented by loading a ContextTool
     */
   public CGI_Impersonator getCGI();


   /**
     * Load a cookie by name.
     * Tool interface: implemented by loading a ContextTool
     */
   public Cookie getCookie(String cookieName);
   

   /**
     * Set a cookie by name.
     * Tool interface: implemented by loading a ContextTool
     */
   public void setCookie(String name, String value);

   /**
     * Load the users session. 
     * Tool interface: implemented by loading a ContextTool
     */
   public HttpSession getSession();

}
