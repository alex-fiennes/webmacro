
package org.webmacro;

import org.webmacro.util.*;
import org.webmacro.profile.*;

import java.util.*;
import java.io.*;
import java.net.URL;

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
final public class Broker
{

   public static final String WEBMACRO_DEFAULTS = "WebMacro.defaults";
   public static final String WEBMACRO_PROPERTIES = "WebMacro.properties";


   private ClassLoader _classloader = null;
   final private Hashtable _providers = new Hashtable();
   final private Settings _config;
   final private String _name;
   final private LogSystem _ls;
   final private Log _log;
   final private ProfileCategory _prof;

   static private Settings initSettings(ClassLoader cl) throws InitException
   {
      Settings defaults = new Settings();
      try {
         defaults.load(WEBMACRO_DEFAULTS,cl);
      } catch (java.io.IOException e) {
         throw new InitException("IO Error reading " + WEBMACRO_DEFAULTS, e);
      }
      return new Settings(defaults);
   }

   static private Settings fileSettings(String name, ClassLoader cl) throws InitException
   {
      try {
         Settings s = initSettings(cl);
         s.load(name,cl);
         return s;
      } catch (IOException e) {
         throw new InitException("Error reading from " + name, e);
      }
   }

   static private Settings urlSettings(URL u, ClassLoader cl) throws InitException
   {
      try {
         Settings s = initSettings(cl);
         s.load(u);
         return s;
      } catch (IOException e) {
         throw new InitException("Error reading from " + u.toString(), e);
      }
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
     * Equivalent to Broker("WebMacro.properties")
     */
   public Broker() throws InitException
   {
      this(fileSettings(WEBMACRO_PROPERTIES,null), WEBMACRO_PROPERTIES, null);
   }
   
   /**
     * Initialize with a classloader
     */

   public Broker(ClassLoader cl) throws InitException
   {
      this(fileSettings(WEBMACRO_PROPERTIES,cl), WEBMACRO_PROPERTIES, cl);
   }

   /**
     * Search the classpath for the properties file under 
     * the specified name.
     * @param fileName Use this name instead of "WebMacro.properties"
     */
   public Broker(String fileName) throws InitException
   {
      this(fileSettings(fileName,null), fileName, null);
   }
   public Broker(String fileName, ClassLoader cl) throws InitException
   {
      this(fileSettings(fileName,cl), fileName, cl);
   }

   /**
     * Load the properties file from the specified URL and 
     * initialize WebMacro.
     * @param url The URL WebMacro should read its properties from
     */
   public Broker(URL url) throws InitException
   {
      this(urlSettings(url,null), url.toString(), null);
   }
   public Broker(URL url, ClassLoader cl) throws InitException
   {
      this(urlSettings(url,null), url.toString(), cl);
   }


   /**
     * Explicitly provide the properties that WebMacro should 
     * configure from. You also need to specify a name for this
     * set of properties so WebMacro can figure out whether 
     * two brokers point at the same properties information.
     * @param config WebMacro's configuration settings
     * @param name Two brokers are the "same" if they have the same name
     */
   public Broker(Settings settings, String name, ClassLoader cl)
      throws InitException
   {
      _classloader = cl;
      _config = settings;
      _name = name;
      _ls = LogSystem.getInstance(name);
      _log = _ls.getLog("broker", "general object loader and configuration");

      try {
         LogTarget lt = new LogFile(settings);
         _ls.addTarget(lt);
         _log.notice("start: " + name);
      } catch (IOException e) {
         _log.error("Failed to open logfile", e);
      }

      if (_config == null) {
         _log.error("no configuration: perhaps some config file is missing? ");
         throw new InitException("No configuration supplied: " +
              "perhaps some configuration file could not be located?");
      }

      // set up profiling

      ProfileSystem ps = ProfileSystem.getInstance();
      int pRate = _config.getIntegerSetting("Profile.rate",0);
      int pTime = _config.getIntegerSetting("Profile.time",60000);

      _log.debug("Profiling rate=" + pRate + " time=" + pTime);

      if ((pRate != 0) && (pTime != 0)) {
         _prof = ps.newProfileCategory(name, pRate, pTime);   
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

   /**
     * Return the classloader used by the broker.  This is not 
     * necessarily the classloader for the Broker class but that
     * passed in the optional consructor.
     * The driving classes (e.g., a servlet) may have a different
     * classloader from the webmacro classes.
     */

   public ClassLoader getClassLoader() {
      return (_classloader==null) 
        ? this.getClass().getClassLoader() 
        : _classloader;
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
