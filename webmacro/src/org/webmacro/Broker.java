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

import org.webmacro.util.*;
import org.webmacro.profile.*;

import java.util.*;
import java.io.*;
import java.net.URL;
import java.lang.ref.WeakReference;

/**
  * The Broker is responsible for loading and initializing almost everything
  * in WebMacro. It reads a set of Properties and uses them to determine 
  * which components of WebMacro should be loaded. It is also responsible 
  * for loading in things like Templates, URLs, and so forth. 
  * <p>
  * By default the Broker reads a file called WebMacro.properties, searching
  * your CLASSPATH and system CLASSPATH for it. There are constructors to 
  * allow you to specify a different location, a URL, or even just supply
  * a properties object directly. 
  * <p>
  * The most common WebMacro installation problems revolve around the 
  * Broker. Without a properly configured Broker WebMacro is unable to 
  * load templates, parse templates, fetch URLs, or perform most of its
  * other basic functions.
  */
public class Broker
{

   public static final String WEBMACRO_DEFAULTS = "WebMacro.defaults";
   public static final String WEBMACRO_PROPERTIES = "WebMacro.properties";
   public static final String SETTINGS_PREFIX = "org.webmacro";

   public static final WeakHashMap brokers = new WeakHashMap();
   private static Settings _defaultSettings;
   private static ClassLoader myClassLoader = Broker.class.getClassLoader(), 
      systemClassLoader = ClassLoader.getSystemClassLoader();

   final protected Hashtable _providers = new Hashtable();
   final protected Settings _config = new Settings();
   final protected String _name;
   final protected LogSystem _ls;
   final protected Log _log;
   protected ProfileCategory _prof;


   /*
    * Constructors.  Callers shouldn't use them; they should use the
    * factory methods (getBroker). 
    *
    * Broker construction is kind of confusing.  There's a common
    * constructor, which initializes the log and a few other private
    * fields but doesn't do much else.  There's a bit of a
    * chicken-and-egg problem with loading the properties; we want to
    * be able to post log messages to indicate success or failure of
    * finding the configuration files, but we can't set a log target
    * until we do so.  The log system writes to standard error until
    * we set a target, so we let the constructor set up the default
    * target, then try and load properties, and then continue with the
    * setup by calling the various init() routines.
    */

   /**
    * Equivalent to Broker("WebMacro.properties"), except that it doesn't
    * complain if WebMacro.properties can't be found.  
    */
   protected Broker() throws InitException
   {
      this((Broker) null, WEBMACRO_PROPERTIES);
      loadDefaultSettings();
      loadSettings(WEBMACRO_PROPERTIES, true);
      if (_config.getBooleanSetting("LoadSystemProperties")) 
         loadSystemSettings();
      initLog();
      init();
   }

   /**
     * Search the classpath for the properties file under 
     * the specified name.
     * @param fileName Use this name instead of "WebMacro.properties"
     */
   protected Broker(String fileName) throws InitException
   {
      this((Broker) null, fileName);
      loadDefaultSettings();
      loadSettings(WEBMACRO_PROPERTIES, false);
      if (_config.getBooleanSetting("LoadSystemProperties")) 
         loadSystemSettings();
      initLog();
      init();
   }

   /**
     * Explicitly provide the properties that WebMacro should 
     * configure from. You also need to specify a name for this
     * set of properties so WebMacro can figure out whether 
     * two brokers point at the same properties information.
     * @param config WebMacro's configuration settings
     * @param name Two brokers are the "same" if they have the same name
     */
   protected Broker(Broker dummy, String name)
      throws InitException
   {
      _name = name;
      _ls = LogSystem.getInstance(_name);
      _log = _ls.getLog("broker", "general object loader and configuration");
   }

   /** 
    * Constructors should call this after they've set up the properties
    * to set up the log target.  If subclasses are going to set up logging
    * themselves, then they don't have to call it.
    */
   protected void initLog() {
      try {
         LogTarget lt = new LogFile(_config);
         _ls.addTarget(lt);
         _log.notice("starting " + this.getClass().getName() 
                     + ": " + _name);
      } catch (IOException e) {
         _log.error("Failed to open logfile", e);
      }
   }

   /** 
    * Constructors should call this after they've set up the properties
    * to set up common things like profiling, providers, etc. 
    */
   protected void init() throws InitException {

      // set up profiling
      ProfileSystem ps = ProfileSystem.getInstance();
      int pRate = _config.getIntegerSetting("Profile.rate",0);
      int pTime = _config.getIntegerSetting("Profile.time",60000);

      _log.debug("Profiling rate=" + pRate + " time=" + pTime);

      if ((pRate != 0) && (pTime != 0)) {
         _prof = ps.newProfileCategory(_name, pRate, pTime);   
         _log.debug("ProfileSystem.newProfileCategory: " + _prof);
      } else {
         _prof = null;
      }
      if (_prof != null) {
         _log.notice("Profiling started: " + _prof);
      } else {
         _log.info("Profiling not started.");
      }

      // set up providers
      String providers = _config.getSetting("Providers");
      if (providers == null) {
         _log.error("configuration exists but has no Providers listed");
         throw new InitException("No providers in configuration");
      }

      Enumeration pen = new StringTokenizer(providers);
      Class pClass;
      Provider instance;

      while (pen.hasMoreElements()) {
         String className = (String) pen.nextElement();
         try {
            pClass = Class.forName(className);
            instance = (Provider) pClass.newInstance();
            addProvider(instance);
         } catch (Exception e) {
            _log.error("Provider (" + className + ") failed to load", e);
         }
      }
   }

   /* Factory methods -- the supported way of getting a Broker */

   public static Broker getBroker() throws InitException {
      try {
         Broker b = findBroker(WEBMACRO_PROPERTIES);
         if (b == null) {
            b = new Broker();
            register(WEBMACRO_PROPERTIES, b);
         }
         return b;
      }
      catch (InitException e) {
         Log log = LogSystem.getSystemLog("wm");
         log.error("Failed to initialize WebMacro with default config");
         throw e;
      }
   }

   public static Broker getBroker(String settingsFile) throws InitException {
      try {
         Broker b = findBroker(settingsFile);
         if (b == null) {
            b = new Broker(settingsFile);
            register(settingsFile, b);
         }
         return b;
      }
      catch (InitException e) {
         Log log = LogSystem.getSystemLog("wm");
         log.error("Failed to initialize WebMacro from " + settingsFile);
         throw e;
      }
   }

   /* Static (internal) methods used for loading settings */

   protected synchronized void loadDefaultSettings() 
   throws InitException {
      if (_defaultSettings == null) {
         try {
            _defaultSettings = new Settings(WEBMACRO_DEFAULTS);
         }
         catch (IOException e) {
            throw new InitException("IO Error reading " + WEBMACRO_DEFAULTS, 
                                    e);
         }
      }
      _log.notice("Loading properties file " + WEBMACRO_DEFAULTS);
      _config.load(_defaultSettings);
   }

   protected void loadSettings(String name, 
                               boolean optional) throws InitException
   {
      URL u = getResource(name);
      if (u != null) {
         _log.notice("Loading properties file " + name);
         try {
            _config.load(u);
         } catch (IOException e) {
            if (!optional)
               throw new InitException("Error reading settings from " + name, 
                                       e);
         }
      }
      else {
         _log.notice("Unable to load settings from file " + name);
         if (!optional)
            throw new InitException("Error reading settings from " + name);
      }
   }

   protected void loadSystemSettings() {
      _log.notice("Loading properties from system properties");
      _config.load(System.getProperties(), SETTINGS_PREFIX);
   }

   /**
    * Used to maintain a weak map mapping the partition key to the
    * Broker.  Registers a broker for a given partition key. 
    */
   protected static void register(Object key, Broker broker) {
      brokers.put(key, new WeakReference(broker));
   }

   /**
    * Find the broker for the specified partition key, if one is
    * registered.  Used by factory methods to ensure that there is
    * only one broker per WM partition 
    */
   protected static Broker findBroker(Object key) {
      WeakReference ref = (WeakReference) brokers.get(key);
      if (ref != null) 
         return (Broker) ref.get();
      else 
         return null;
   }

   /**
     * Access to the settings in WebMacro.properties
     */
   public Settings getSettings() { return _config; }

   /**
     * Access to the settings in WebMacro.properties
     */
   public String getSetting(String key) { return _config.getSetting(key); }

   /**
     * Access to the settings in WebMacro.properties
     */
   public boolean getBooleanSetting(String key) {
      return _config.getBooleanSetting(key);
   }

   /**
     * Access to the settings in WebMacro.properties
     */
   public int getIntegerSetting(String key) {
      return _config.getIntegerSetting(key);
   }

   /**
     * Access to the settings in WebMacro.properties
     */
   public int getIntegerSetting(String key, int defaultValue) {
      return _config.getIntegerSetting(key, defaultValue);
   }


   /**
     * Register a new provider, calling its getType() method to find
     * out what type of requests it wants to serve.
     */
   public void addProvider(Provider p) throws InitException
   {
      p.init(this,_config);
      _providers.put(p.getType(),p);
      _log.info("Loaded provider " + p);
   }

   /**
     * Get a provider
     */
   public Provider getProvider(String type) throws NotFoundException
   {
      Provider p = (Provider) _providers.get(type);
      if (p == null) {
         throw new NotFoundException("No provider for type " + type
            + ": perhaps WebMacro couldn't load its configuration?");
      }
      return p;
   }

   /**
     * Get a log: the behavior of this log depends on the configuration
     * of the broker. If your system loads from a WebMacro.properties 
     * file then look in there for details about setting up and 
     * controlling the Log. 
     * <p>
     * You should try and hang on to the Log you get back from this
     * method since creating new Log objects can be expensive. You
     * also likely pay for IO when you use a log object.
     * <p>
     * The type you supply will be associated with your log messages
     * in the log file.
     */
   public Log getLog(String type, String description) {
      return _ls.getLog(type);
   }

   /**
     * Shortcut: create a new log using the type as the description
     */
   public Log getLog(String type) {
      return _ls.getLog(type,type);
   }

   /** 
    * Get a resource (file) from the the Broker's class loader
    */
   public URL getResource(String name) {
      URL u = myClassLoader.getResource(name);
      if (u == null) 
         u = systemClassLoader.getResource(name);
      return u;
   }

   /**
    * Get a resource (file) from the Broker's class loader 
    */
   public InputStream getResourceAsStream(String name) {
      InputStream is = myClassLoader.getResourceAsStream(name);
      if (is == null) 
         is = systemClassLoader.getResourceAsStream(name);
      return is;
   }


   /**
     * Get a profile instance that can be used to instrument code. 
     * This instance must not be shared between threads. If profiling
     * is currently disabled this method will return null.
     */
   public Profile newProfile() {
      return (_prof == null) ? null : _prof.newProfile();
   }


   /**
     * Look up query against a provider using its integer type handle.
     */
   public Object get(String type, final String query) 
      throws NotFoundException
   {
      return getProvider(type).get(query);
   }

   /**
     * Backwards compatible, calls get(String,String)
     * @deprecated call get(String,String) instead
     */
   final public Object getValue(String type, String query) 
      throws NotFoundException
   {
      return get(type,query);
   }

   /**
     * Shut down the broker
     */
   synchronized public void shutdown() {
      _log.notice("shutting down");
      Enumeration e = _providers.elements();
      _providers.clear();
      while (e.hasMoreElements()) {
         Provider pr = (Provider) e.nextElement();
         _log.info("stopping: " + pr);
         pr.destroy();
      }
      _ls.flush();
   }

   /**
     * Explain myself
     */ 
   public String toString() {
      StringBuffer buf = new StringBuffer();
      buf.append("Broker:");
      buf.append(_name);
      buf.append("(");
      Enumeration e = _providers.elements();
      while (e.hasMoreElements()) {
         Provider pr = (Provider) e.nextElement();
         buf.append(pr);
         if (e.hasMoreElements()) {
            buf.append(", ");
         }
      }
      return buf.toString();
   }

   public String getName() {
      return _name;
   }

   /**
     * Test the broker or a provider. Reads from stdin: TYPE NAME
     */
   public static void main(String arg[]) {
      try {
         if (arg.length != 1) {
            System.out.println("Arg required: config file URL");
            System.out.println("Then input is: TYPE NAME lines on stdin");
            System.exit(1);
         }
         Broker broker = new Broker(arg[0]);

         BufferedReader in = 
            new BufferedReader(new InputStreamReader(System.in));

         String line;
         while ( (line = in.readLine()) != null) 
         {

            int space = line.indexOf(' ');
            String type = line.substring(0, space);
            String name = line.substring(space + 1);
            System.out.println("broker.get(\"" + type + "\", \"" 
                           +  name + "\"):");
            Object o = broker.get(type,name);
            System.out.println("RESULT:");
            System.out.println(o.toString());
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
