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

import java.lang.*;
import java.io.*;
import java.util.*;
import org.webmacro.util.*;
import org.webmacro.*;
import org.webmacro.servlet.*;


/**
  * Framework for handling template requests, requested by reactor. 
  * Anything which implements this interface can be used as a handler
  * to process web requests.
  * <p>
  * How to use:
  * <ol>
  * <li>. Define a name for your new Handler. It must be alphanumeric.
  * <li>. Set that to be a valid servlet name for WebMacro in your 
  *    servlet.properties file (if using servletrunner, otherwise 
  *    name your servlet this using the interface in your runner)
  * <li>. Set the script prefix to be the prefix added by servletrunner
  *    in the config file. eg: "ScriptName = /servlet/"
  * <li>. Register your Handler against the Reactor class by calling
  *    Reactor.register(handler) 
  * </ol>
  * <p>
  * When a request comes in, WebMacro loads the handler that matches
  * the script name of the request. It will search the ClassPath looking
  * for a script with the same fully qualified name as the script name,
  * which more or less means your handler should probably not be in any
  * particular package.
  * <p>
  * @see GenericHandler
  */
public interface Handler
{

   /**
     * This is the primary method you override to create a new handler. 
     * Incoming requests ultimately get passed to this method of your 
     * handler, at which point it is up to you to decide what to do. 
     * You must return a template--which will be used to format the 
     * data you have inserted into the supplied WebContext.
     * <p>
     * If you throw an Exception it will be used to provide an explanation
     * to the user of why the failure occurred. The HandlerException class
     * provides you with numerous options for reporting errors.
     * <p>
     * @param contextData contains information about this connection
     * @return A Template which can be used to interpret the connection
     * @exception HandlerException if something went wrong with the handler
     */
   public Template accept(WebContext contextData)
      throws HandlerException;

   /**
     * Use this method to run any startup initialization that you need
     * to perform. It will be called just before the first use of your
     * Handler.
     * @exception HandlerException if the handler failed to initialize
     */
   public void init() throws HandlerException;

   /**
     * You SHOULD override this method and provide a short name by 
     * which your handler is known. This will help you out in logging
     * and debugging messages if for some reason WebMacro needs to 
     * identify the handler in a log message.
     */
   public String toString();

   /**
     * You may use this method to save persistent state on exit.
     * It will be called whenever the servlet is shut down.
     */
   public void destroy();
   
}


