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
  * Provide access to form variables
  */
final public class Form implements Bag
{

   /**
     * This is the request object from the WebContext
     */
   final HttpServletRequest _request;

   /**
     * Read the form data from the supplied Request object
     */
   Form(final HttpServletRequest r) {
      _request = r;
   }

   /**
     * Get a form value
     */
   final public Object get(String field) {
      try {
         return _request.getParameterValues(field)[0];
      } catch (NullPointerException ne) {
         return null;
      }
   }


   /**
     * Get a form value as an array
     */
   final public Object[] getList(String field) {
      try {
         return _request.getParameterValues(field);
      } catch (NullPointerException ne) {
         return null;
      }
   }

   /**
     * Unsupported
     */
   final public void put(String key, Object value) 
      throws UnsettableException
   {
      throw new UnsettableException("Cannot set a form property");
   }


   /**
     * Unsupported
     */
   final public void remove(String key) 
      throws UnsettableException
   {
      throw new UnsettableException("Cannot unset a form property");
   }

}

