
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

package org.webmacro;

import org.webmacro.engine.*;
import org.webmacro.resource.*;
import org.webmacro.util.*;
import java.util.*;

import org.webmacro.servlet.WebContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


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

   final private static Map _brokers = new HashMap();
   final private static BrokerOwner _default = new BrokerOwner();
   private Context _context = null;
   private WebContext _webContext = null;

   // INIT METHODS--MANAGE ACCESS TO THE BROKER

   /*final*/ private Broker _broker;      // cache for rapid access
   /*final*/ private BrokerOwner _owner; // mgr that loads/unloads broker

   private boolean _alive = false;   // so we don't unload twice

   private Provider _tmplProvider;
   private Provider _urlProvider;
   private Provider _configProvider;

   public WM() throws InitException
   {
      this(null);
   }

   public WM(String config) throws InitException
   {
      BrokerOwner owner = null;
      Broker broker = null;
      try {
         if (config == null) {
            owner = _default;
         } else {
            synchronized(_brokers) {
               owner = (BrokerOwner) _brokers.get(config);
               if (owner == null) {
                  owner = new BrokerOwner(config);
                  _brokers.put(config,owner);
               }
            }
         }
         broker = owner.init();
      } finally {
         _owner = owner;
         _broker = broker;
         _alive = true;
      }
   
      try {
         _tmplProvider = _broker.getProvider("template");
         _urlProvider = _broker.getProvider("url");
      } catch (NotFoundException nfe) {
         throw new InitException("Could not locate provider:\n  " + nfe 
            + "\nThis implies that WebMacro is badly misconfigured, you\n"
            + "should double check that all configuration files and\n"
            + "options are set up correctly. In a default install of\n"
            + "WebMacro this likely means your WebMacro.properties file\n"
            + "was not found on your CLASSPATH.");
      }
   }


   /**
     * Call this method when you are finished with WebMacro. If you don't call this 
     * method, the Broker and all of WebMacro's caches may not be properly 
     * shut down, potentially resulting in loss of data, and wasted memory. This 
     * method is called in the finalizer, but it is best to call it as soon as you
     * know you are done with WebMacro.
     * <p>
     * After a call to destroy() attempts to use this object may yield unpredicatble
     * results.
     */
   final public void destroy() {
      if (_alive) {
         _alive = false;
         _owner.done();
      }
   }


   /**
     * This message returns false until you destroy() this object, subsequently it 
     * returns true. Do not attempt to use this object after it has been destroyed.
     */
   final public boolean isDestroyed() {
      return !_alive;
   }


   /**
     * You should never call this method, on any object. Leave it up to the garbage
     * collector. If you want to shut this object down, call destroy() instead. If 
     * you subclass this message, be sure to call super.finalize() since this is one 
     * of the cases where it matters. 
     */
   protected void finalize() {
      destroy();
   }


   /**
     * This object is used to access components that have been plugged
     * into WebMacro; it is shared between all instances of this class and
     * its subclasses. It is created when the first instance is initialized,
     * and deleted when the last instance is shut down. If you attempt to 
     * access it after the last servlet has been shutdown, it will either 
     * be in a shutdown state or else null.
     */
   final public Broker getBroker() {
      // this method can be unsynch. because the broker manages its own
      // state, plus the only time the _broker will be shutdown or null 
      // is after the last servlet has shutdown--so why would anyone be 
      // accessing us then? if they do the _broker will throw exceptions
      // complaining that it has been shut down, or they'll get a null here.
      return _broker;
   }

   final public Context getContext() {
      if (_context == null) {
         _context = new Context(getBroker());
      }
      return (Context) _context.clone();
   }

   final public WebContext getWebContext(HttpServletRequest req, HttpServletResponse resp) {
      if (_webContext == null) {
         _webContext = new WebContext(getBroker());
      }
      return _webContext.newInstance(req,resp);
   }


   /**
     * Retrieve a template from the "template" provider.
     * @exception NotFoundException if the template was not found
     */
   final public Template getTemplate(String key) 
      throws NotFoundException
   {
      return (Template) _tmplProvider.get(key); 
   }

   /**
     * Retrieve a URL from the "url" provider. Equivalent to 
     * getBroker().getValue("url",url)
     * @exception NotFoundException if the template was not found
     */
   final public String getURL(String url) 
      throws NotFoundException
   {
      return (String) _urlProvider.get(url);
   }

   /**
     * Retrieve configuration information from the "config" provider.
     * Equivalent to getBroker().get("config",key)
     * @exception NotFoundException could not locate requested information
     */
   final public String getConfig(String key) 
      throws NotFoundException
   {
      return (String) _broker.get("config", key);
   }

   /**
     * Get a log to write information to. Log names should be lower
     * case and short. The name you supply corresponds to the name
     * associated with the messages in the resulting log file.
     */
   final public Log getLog(String name) {
      return _broker.getLog(name);
   }
}


final class BrokerOwner {

   /*final*/ String _config;
   private Broker _broker;
   private static int _brokerUsers = 0;

   BrokerOwner() {
      this(null);
   }

   BrokerOwner(String config) {
      _config = config;
      _broker = null;
      _brokerUsers = 0;
   }

   synchronized Broker init() throws InitException 
   {
      _brokerUsers++;
      if (_broker == null) {
         try {
            _broker = (_config == null) ?  new Broker() : new Broker(_config);
         } catch (InitException e) {
            _broker = null;
            _brokerUsers = 0; 
            throw e; // rethrow
         } catch (Throwable t) {
            _broker = null;
            _brokerUsers = 0;
            throw new InitException(
"An unexpected exception was raised during initialization. This is bad,\n" +
"there is either a bug in WebMacro, or your configuration is messed up:\n" +
    t +
"\nHere are some clues: if you got a ClassNotFound exception, either\n" +
"something is missing from your classpath (odd since this code ran)\n" +
"or your JVM is failing some important classfile due to bytecode problems\n" +
"and then claiming that, it does not exist... you might also see some kind\n" +
"of VerifyException or error in that case. If you suspect something like\n" +
"this is happening, recompile the WebMacro base classes with your own Java\n" +
"compiler and libraries--usually that helps. It could also be that your\n" +
"JVM is not new enough. Again you could try recompiling, but if it is\n" +
"older than Java 1.1.7 you might be out of luck. If none of this helps,\n" +
"please join the WebMacro mailing list and let us know about the problem,\n" +
"because yet another possibility is WebMacro has a bug--and anyway, we \n" +
"might know a workaround, or at least like to hear about the bug.\n");

         }
      }
      return _broker;
   }

   synchronized void done() {
         _brokerUsers--;
         if ((_brokerUsers == 0) && (_broker != null)) {
            _broker.shutdown(); 
            _broker = null;
         }
   }
}
