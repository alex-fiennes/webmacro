
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


package org.webmacro.resource;

import java.lang.*;
import java.io.*;
import java.util.*;
import org.webmacro.util.*;
import org.webmacro.engine.*;
import org.webmacro.broker.*;
import org.webmacro.servlet.*;
import org.webmacro.*;

/**
  * This is the canonical provider for mapping URLs to Handlers. The
  * reactor will request that the "handler" provider return a Handler
  * object when supplied with a URL.
  * <p>
  * You could implement your own version of this class to return 
  * handlers based on whatever criteria you wanted.
  */

final public class HandlerProvider implements ResourceProvider
{

   // STATIC DATA 

   /**
     * Constant that contains the Log and ResourceProvider Type 
     * served by this class.
     */
   final public static String TYPE = "handler";

   /**
     * Used to log events relating to handler init errors
     */
   final static Log _log = 
      new Log(TYPE,"handler provider mechanism");



   // CLASS STATE
   private Hashtable _handlers = new Hashtable();



   // RESOURCE PROVIDER API

   final static private String[] _types = { TYPE };

   /**
     * No concurrency--all in memory operation.
     */
   final public int resourceThreads() { return 0; }

   /**
     * We serve up "handler" type resources
     */
   final public String[] getTypes() { return _types; }

   /**
     * Cache created handlers for up to 20 minutes.
     */
   final public int resourceExpireTime() { return 1000 * 60 * 20; }

   /**
     * Request a handler, the supplied name is ScriptName
     */
   final public void resourceRequest(RequestResourceEvent evt) {

      Handler hand = null;
      String scriptName = evt.getName();
      
      try {
         hand = (Handler) _handlers.get(scriptName);
         if (hand == null) {
            // next line: handleName = scriptName if lastIndex == -1
            String handleName =
               scriptName.substring(scriptName.lastIndexOf('/') + 1);
            Class hc = Class.forName(handleName);
            hand = (Handler) hc.newInstance();
            hand.init();
            _handlers.put(scriptName,hand);
         }
         evt.set(hand);
      } catch (HandlerException he) {
         _log.exception(he);
         String error = 
            "Unable to initialize your handler due to exception: " + he;
         _log.warning(error);
      } catch (Exception e) {
         _log.exception(e);
         String error = 
            "Reactor: Unable to create handler for script name: "
            + scriptName
            + "... this may be because your handler is not in your CLASSPATH,"
            + " or that you have not registered WebMacro as the handler for "
            + " the name " + scriptName +" in servlet.properties (or equiv.).";
         _log.warning(error);
      }
   }

   /**
     * Stop the handler
     */
   final public boolean resourceSave(ResourceEvent evt) { 
      return false; 
   }

   /**
     * Unimplemented / does nothing
     */
   final public void destroy() { }


   /**
     * Resource broker will want to  init me
     */
   public void init(ResourceBroker broker)
   {
      // do nothing
   }

   /**
     * Unimplemented / does nothing
     */
   final public void resourceCreate(CreateResourceEvent evt) { }

   /**
     * Unimplemented / does nothing
     */
   final public boolean resourceDelete(ResourceEvent evt) { return false; }


}


