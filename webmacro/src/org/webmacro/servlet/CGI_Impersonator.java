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
import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.webmacro.util.*;
import org.webmacro.*;


/**
  * Provided to mimic the CGI environment within the WebMacro script
  * language via introspection. The data in this class duplicates 
  * information already available in request, but makes it available
  * in a familiar form.
  * <p>
  * From the WebMacro script language you can refer to the properties
  * contained in this class with names that exactly duplicate the names
  * familiar to CGI programmers. eg: REQUEST_METHOD, PATH_INFO, etc.
  */
final public class CGI_Impersonator
{

   /**
     * This is the request object from the WebContext
     */
   final HttpServletRequest requst_;

   /**
     * Use the supplied HttpServletRequest to produce the results 
     * below. Really this class just forwards methods to this sub 
     * object in order to provide a familiar interface to CGI programmers.
     */
   CGI_Impersonator(final HttpServletRequest r) {
      requst_ = r;
   }

   /**
     * Return the name of the server
     */
   final public String getSERVER_NAME() 
      { return requst_.getServerName(); }

   /*
    * XXX: This is the only CGI variable we can't do, because we 
    * don't have the servlet context available here.
    *
    *final public String getSERVER_SOFTWARE() 
    *  { getServletContext().getServerInfo(); }
    */

   /**
     * Return the server protocol
     */
   final public String getSERVER_PROTOCOL() 
      { return requst_.getProtocol(); }

   /**
     * Return the server port
     */
   final public Integer getSERVER_PORT()
      { return new Integer(requst_.getServerPort()); }

   /**
     * Return what type of REQUEST this was: GET, POST, etc.
     */
   final public String getREQUEST_METHOD() 
      { return requst_.getMethod(); }

   /**
     * What portion of the URL appeared as additional path beyond 
     * the SCRIPT_NAME portion? Return that as a string.
     */
   final public String getPATH_INFO() 
      { return requst_.getPathInfo(); }

   /**
     * Same as PATH_INFO but translated to a real path
     */
   final public String getPATH_TRANSLATED() 
      { return requst_.getPathTranslated(); }

   /**
     * What portion of the URL represented the servlet being run? 
     * Return that as a string.
     */
   final public String getSCRIPT_NAME() 
      { return requst_.getServletPath(); }

   /**
     * What is the root of documents served by this servlet
     * 
     * WARNING: the method called (getRealPath) is deprecated in Servlet 2.2
     *
     */
   final public String getDOCUMENT_ROOT()
      { return requst_.getRealPath("/"); }

   /**
     * In a GET request, return the query string that was submitted, if any
     */
   final public String getQUERY_STRING() 
      { return requst_.getQueryString(); }

   /**
     * Return the remote host connected to this request
     */
   final public String getREMOTE_HOST()
      { return requst_.getRemoteHost(); }

   /**
     * Return the remove address connected to this servlet
     */
   final public String getREMOTE_ADDR()
      { return requst_.getRemoteAddr(); }

   /**
     * Type of authorization for this request
     */
   final public String getAUTH_TYPE()
      { return requst_.getAuthType(); }
  
   /**
     * Name of the remote user if it was supplied with the HTTP request
     */
   final public String getREMOTE_USER() 
      { return requst_.getRemoteUser(); }

   /**
     * Get the content type submitted to this request
     */
   final public String getCONTENT_TYPE()
      { return requst_.getContentType(); }

   /**
     * Get the content length submitted to this request
     */
   final public Integer getCONTENT_LENGTH()
      { return new Integer(requst_.getContentLength()); }

   /**
     * What type of data is accepted by the client
     */
   final public String getHTTP_ACCEPT()
      { return requst_.getHeader("Accept"); }

   /**
     * Get the user agent (browser) connected to this request
     */
   final public String getHTTP_USER_AGENT()
     { return requst_.getHeader("User-Agent"); }

   /**
     * Get the URL that the request claims to have visited prior to this one
     */
   final public String getHTTP_REFERER()
      { return requst_.getHeader("Referer"); }

}

