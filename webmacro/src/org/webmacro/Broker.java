
package org.webmacro;

import org.webmacro.util.LogManager;

import java.util.Hashtable;
import java.util.Properties;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.net.URL;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

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

   private static Properties getDefaultProperties() {
      Properties p = new Properties();
      p.setProperty("TemplatePath", "/:.");
      p.setProperty("TemplateExpireTime", "10");
      p.setProperty("LogLevel", "EXCEPTION");
      p.setProperty("LogTraceExceptions", "TRUE");
      p.setProperty("ErrorTemplate", "error.wm");
      p.setProperty("Providers", "org.webmacro.resource.TemplateProvider org.webmacro.resource.UrlProvider org.webmacro.resource.ConfigProvider org.webmacro.engine.DirectiveProvider org.webmacro.engine.ParserProvider");
      p.setProperty("Directives", "org.webmacro.engine.IncludeDirective org.webmacro.engine.ParseDirective org.webmacro.engine.SetDirective org.webmacro.engine.IfDirective org.webmacro.engine.ElseDirective org.webmacro.engine.UseDirective org.webmacro.engine.ParamDirective org.webmacro.engine.ForeachDirective org.webmacro.engine.LocalDirective org.webmacro.engine.PropertyDirective org.webmacro.engine.ToolDirective org.webmacro.engine.EscapeDirective org.webmacro.engine.EncodeDirective org.webmacro.engine.SilenceDirective");
      p.setProperty("ContextTools", "");
      p.setProperty("WebContextTools", "org.webmacro.servlet.CGITool org.webmacro.servlet.FormTool org.webmacro.servlet.FormListTool org.webmacro.servlet.CookieTool org.webmacro.servlet.ResponseTool org.webmacro.servlet.RequestTool org.webmacro.servlet.SessionTool");
      p.setProperty("Parsers", "org.webmacro.engine.WMParser org.webmacro.engine.NullParser org.webmacro.engine.TextParser");
      return p;
   }


   private Hashtable _providers = new Hashtable();
   private Properties _config;
   private String _name;
   private LogManager _lm;
   private Log _log;

   /**
     * Equivalent to Broker("WebMacro.properties")
     */
   public Broker() throws InitException
   {
      this("WebMacro.properties");
   }

   /**
     * Search the classpath for the properties file under 
     * the specified name.
     * @param fileName Use this name instead of "WebMacro.properties"
     */
   public Broker(String fileName) throws InitException
   {
      this(configSearch(fileName, Broker.class.getClassLoader()));
   }

   private static URL configSearch(String fileName, ClassLoader cl) 
      throws InitException
   {
      URL u = cl.getResource(fileName);
      if (u == null) {
         u = ClassLoader.getSystemResource(fileName);
      }
      if (u != null) {
         return u;
      }

      // report what happened

      StringBuffer error = new StringBuffer();
      error.append("WebMacro was unable to locate the configuration file: ");
      error.append(fileName);
      error.append("\n");
      error.append("This means that WebMacro could not be started. The \n");
      error.append("following list should be where I looked for it:\n");
      error.append("\n");
      error.append("   my classpath: ");
      try {
         buildPath(error, fileName, cl.getResources("."));
      } catch (Exception e) { }
      error.append("\n");
      error.append("   system classpath: ");
      try {
         buildPath(error, fileName, ClassLoader.getSystemResources("."));
      } catch (Exception e) { }
      error.append("\n\n");
      error.append("Alternately you can modify your servlet to request a\n");
      error.append("specific location or supply the properties yourself.\n");
      error.append("See the classes WebMacro, WM, and Broker in the\n");
      error.append("org.webmacro package.");
      throw new InitException(error.toString());
   }

   private static 
   void buildPath(StringBuffer b, String fileName, Enumeration e) 
   {
      while (e.hasMoreElements()) {
         b.append(e.nextElement().toString());
         b.append(fileName);
         b.append("");
      }         
   }

   /**
     * Load the properties file from the specified URL and 
     * initialize WebMacro.
     * @param url The URL WebMacro should read its properties from
     */
   public Broker(URL url) throws InitException
   {
      this(loadProperties(url), url.toString());
   }


   private static Properties loadProperties(URL url) throws InitException
   {
      try {
         InputStream in = new BufferedInputStream(url.openStream());
         Properties p = new Properties(getDefaultProperties());
         p.load(in);
         return p;
      } catch (Exception e) {
         throw new InitException("Failed to read WebMacro configuration "
            + "from location " + url.toString() + ": " + e);
      }
   }

   /**
     * Explicitly provide the properties that WebMacro should 
     * configure from. You also need to specify a name for this
     * set of properties so WebMacro can figure out whether 
     * two brokers point at the same properties information.
     * @param config WebMacro's configuration settings
     * @param name Two brokers are the "same" if they have the same name
     */
   public Broker(Properties config, String name)
      throws InitException
   {
      _lm = new LogManager(config);
      _log = _lm.getLog("wm");
      _log.notice("start: " + name);

      _name = name;

      if (config == null) {
         _log.error("no configuration: perhaps some config file is missing? ");
         throw new InitException("No configuration supplied: " +
              "perhaps some configuration file could not be located?");
      }
      _config = config;

      String providers = config.getProperty("Providers");
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
            _log.info("Loaded provider: " + className);
         } catch (Exception e) {
            _log.error("Provider (" + className + ") failed to load", e);
         }
      }
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
     * The name you supply will be associated with your log messages
     * in the log file.
     */
   public Log getLog(String name) {
      return _lm.getLog(name);
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
      _providers = null;
      while (e.hasMoreElements()) {
         Provider pr = (Provider) e.nextElement();
         _log.info("stopping: " + pr);
         pr.destroy();
      }
      _lm.flush();
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
