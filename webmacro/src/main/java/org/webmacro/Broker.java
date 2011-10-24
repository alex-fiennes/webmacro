/*
 * Copyright (c) 1998, 1999 Semiotek Inc. All Rights Reserved. This software is the confidential
 * intellectual property of of Semiotek Inc.; it is copyrighted and licensed, not sold. You may use
 * it under the terms of the GNU General Public License, version 2, as published by the Free
 * Software Foundation. If you do not want to use the GPL, you may still use the software after
 * purchasing a proprietary developers license from Semiotek Inc. This software is provided "as is",
 * with NO WARRANTY, not even the implied warranties of fitness to purpose, or merchantability. You
 * assume all risks and liabilities associated with its use. See the attached License.html file for
 * details, or contact us by e-mail at info@semiotek.com to get a copy.
 */

package org.webmacro;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webmacro.broker.ContextAutoLoader;
import org.webmacro.engine.DefaultEvaluationExceptionHandler;
import org.webmacro.engine.EvaluationExceptionHandler;
import org.webmacro.engine.IntrospectionUtils;
import org.webmacro.engine.MacroDefinition;
import org.webmacro.engine.MethodWrapper;
import org.webmacro.engine.PropertyOperatorCache;
import org.webmacro.util.Settings;
import org.webmacro.util.SubSettings;

/**
 * The Broker is responsible for loading and initializing almost everything in WebMacro. It reads a
 * set of Properties and uses them to determine which components of WebMacro should be loaded. It is
 * also responsible for loading in things like Templates, URLs, and so forth.
 * <p>
 * By default the Broker reads a file called WebMacro.properties, searching your CLASSPATH and
 * system CLASSPATH for it. There are constructors to allow you to specify a different location, a
 * URL, or even just supply a properties object directly.
 * <p>
 * The most common WebMacro installation problems revolve around the Broker. Without a properly
 * configured Broker WebMacro is unable to load templates, parse templates, fetch URLs, or perform
 * most of its other basic functions.
 * 
 * @author Marcel Huijkman
 * @version 23-07-2002
 */
public class Broker
{

  public static final String WEBMACRO_DEFAULTS = "WebMacro.defaults";
  public static final String WEBMACRO_PROPERTIES = "WebMacro.properties";
  public static final String SETTINGS_PREFIX = "org.webmacro";

  public static final WeakHashMap<Object, WeakReference<Broker>> BROKERS =
      new WeakHashMap<Object, WeakReference<Broker>>();

  private static Settings _defaultSettings;
  protected static ClassLoader _myClassLoader = Broker.class.getClassLoader();
  protected static ClassLoader _systemClassLoader = ClassLoader.getSystemClassLoader();

  final protected Map<String, Provider> _providers = new Hashtable<String, Provider>();
  final protected Settings _config = new Settings();
  final protected String _name;
  final public PropertyOperatorCache _propertyOperators = new PropertyOperatorCache();

  protected Logger _log = LoggerFactory.getLogger(Broker.class);

  private EvaluationExceptionHandler _eeHandler;

  /** a local map for one to dump stuff into, specific to this Broker */
  private Map<Object, Object> _brokerLocal = new ConcurrentHashMap<Object, Object>();

  /** a local map for "global functions" */
  private Map<String, MethodWrapper> _functionMap = new ConcurrentHashMap<String, MethodWrapper>();

  /** a local map for context tools and other automatic context goodies */
  private Map<String, ContextAutoLoader> _toolLoader =
      new ConcurrentHashMap<String, ContextAutoLoader>();

  /** map of global macros */
  private Map<String, MacroDefinition> _macros = new ConcurrentHashMap<String, MacroDefinition>();

  /*
   * Constructors. Callers shouldn't use them; they should use the factory methods (getBroker).
   * Broker construction is kind of confusing. There's a common constructor, which initializes the
   * log and a few other private fields but doesn't do much else. There's a bit of a chicken-and-egg
   * problem with loading the properties; we want to be able to post log messages to indicate
   * success or failure of finding the configuration files, but we can't set a log target until we
   * do so. The log system writes to standard error until we set a target, so we let the constructor
   * set up the default target, then try and load properties, and then continue with the setup by
   * calling the various init() routines.
   */

  /**
   * Equivalent to Broker("WebMacro.properties"), except that it doesn't complain if
   * WebMacro.properties can't be found.
   */
  protected Broker() throws InitException
  {
    this((Broker) null,
         WEBMACRO_PROPERTIES);
    String propertySource =
        WEBMACRO_DEFAULTS + ", " + WEBMACRO_PROPERTIES + ", " + "(System Properties)";
    loadDefaultSettings();
    loadSettings(WEBMACRO_PROPERTIES, true);
    loadSystemSettings();
    _log.info("Loaded settings from " + propertySource);
    init();
  }

  /**
   * Equivalent to Broker("WebMacro.properties"), except that it doesn't complain if
   * WebMacro.properties can't be found, and it loads properties from a specified Properties.
   */
  protected Broker(Properties props) throws InitException
  {
    this((Broker) null,
         "Ad-hoc Properties " + props.hashCode());
    String propertySource =
        WEBMACRO_DEFAULTS + ", " + WEBMACRO_PROPERTIES
            + ", (caller-supplied Properties), (System Properties)";
    loadDefaultSettings();
    loadSettings(WEBMACRO_PROPERTIES, true);
    loadSettings(props);
    loadSystemSettings();
    _log.info("Loaded settings from " + propertySource);
    init();
  }

  /**
   * Search the classpath for the properties file under the specified name.
   * 
   * @param fileName
   *          Use this name instead of "WebMacro.properties"
   */
  protected Broker(String fileName) throws InitException
  {
    this((Broker) null,
         fileName);
    String propertySource = WEBMACRO_DEFAULTS + ", " + fileName;
    loadDefaultSettings();
    boolean loaded = loadSettings(fileName, false);
    if (!loaded) {
      propertySource += "(not found)";
    }
    loadSystemSettings();
    propertySource += ", " + "(System Properties)";
    _log.info("Loaded settings from " + propertySource);
    init();
  }

  /**
   * Explicitly provide the properties that WebMacro should configure from. You also need to specify
   * a name for this set of properties so WebMacro can figure out whether two brokers point at the
   * same properties information.
   * 
   * @param dummy
   *          a Broker instance that is never used
   * @param name
   *          Two brokers are the "same" if they have the same name
   */
  protected Broker(Broker dummy,
                   String name) throws InitException
  {
    _name = name;
  }

  /**
   * Provide access to the broker's log.
   */
  public Logger getBrokerLog()
  {
    return _log;
  }

  private class ProviderSettingHandler
    extends Settings.ListSettingHandler
  {
    @Override
    public void processSetting(String settingKey,
                               String settingValue)
    {
      try {
        Class<?> pClass = classForName(settingValue);
        Provider instance = (Provider) pClass.newInstance();
        addProvider(instance, settingKey);
      } catch (Exception e) {
        _log.error("Provider (" + settingValue + ") failed to load", e);
      }
    }
  }

  private class AutoLoaderSettingHandler
    extends Settings.ListSettingHandler
  {
    @Override
    public void processSetting(String settingKey,
                               String settingValue)
    {
      try {
        Class<?> pClass = classForName(settingValue);
        ContextAutoLoader instance = (ContextAutoLoader) pClass.newInstance();
        instance.init(Broker.this, settingKey);
      } catch (Exception e) {
        _log.error("ContextAutoLoader (" + settingValue + ") failed to load", e);
      }
    }
  }

  /**
   * Constructors should call this after they've set up the properties to set up common things like
   * profiling, providers, etc.
   */
  protected void init()
      throws InitException
  {
    String eehClass;

    // Initialize the property operator cache
    _propertyOperators.init(this, _config);

    // Write out our properties as debug records
    if (_log.isDebugEnabled()) {
      String[] properties = _config.getKeys();
      Arrays.sort(properties);
      for (int i = 0; i < properties.length; i++) {
        _log.debug("Property " + properties[i] + ": " + _config.getSetting(properties[i]));
      }
    }

    // set up providers
    _config.processListSetting("Providers", new ProviderSettingHandler());
    if (_providers.size() == 0) {
      _log.error("No Providers specified");
      throw new InitException("No Providers specified in configuration");
    }

    _config.processListSetting("ContextAutoLoaders", new AutoLoaderSettingHandler());
    // @@@ load autoloaders

    eehClass = _config.getSetting("ExceptionHandler");
    if (eehClass != null && !eehClass.equals("")) {
      try {
        _eeHandler = (EvaluationExceptionHandler) classForName(eehClass).newInstance();
      } catch (Exception e) {
        _log.warn("Unable to instantiate exception handler of class " + eehClass + "; " + e);
      }
    }
    if (_eeHandler == null) {
      _eeHandler = new DefaultEvaluationExceptionHandler();
    }

    _eeHandler.init(this, _config);

    // Initialize function map
    SubSettings fnSettings = new SubSettings(_config, "Functions");
    String[] fns = fnSettings.getKeys();
    for (int i = 0; fns != null && i < fns.length; i++) {
      String fn = fns[i];
      String fnSetting = fnSettings.getSetting(fn);
      int lastDot = fnSetting.lastIndexOf('.');
      if (lastDot == -1) {
        throw new IllegalArgumentException("Bad function declaration for "
                                           + fn
                                           + ": "
                                           + fnSetting
                                           + ".  This setting must include full class name followed by a '.' and method name");
      }
      String fnClassName = fnSetting.substring(0, lastDot);
      String fnMethName = fnSetting.substring(lastDot + 1);
      // function type may be static, instance, or factory. Default is static
      String fnType = _config.getSetting("Function." + fn + ".type", "static");
      Object[] args = null;
      if ("factory".equals(fnType)) {
        // TODO: implement this!!!
        // get function from a factory method
        // declared class/method is the factory class/instance method
        // get the factory class method name
        // String factoryMeth = _config.getSetting("Function." + fn + ".factory.method");
      }
      if (!"static".equals(fnType)) {
        // get arg string
        String argString = _config.getSetting("Function." + fn + ".args");
        if (argString != null) {
          if (!argString.startsWith("[")) {
            argString = "[" + argString + "]";
          }
          org.webmacro.engine.StringTemplate tmpl =
              new org.webmacro.engine.StringTemplate(this, "#set $args=" + argString);
          Context argContext = new Context(this);
          try {
            tmpl.evaluateAsString(argContext);
          } catch (Exception e) {
            _log.error("Unable to evaluate arguments to function " + fn
                       + ".  The specified string was " + argString + ".", e);
          }
          args = (Object[]) argContext.get("args");
          _log.debug("Args for function " + fn + ": " + Arrays.asList(args));
        }
      }

      Class<?> c = null;
      try {
        c = Class.forName(fnClassName);
      } catch (Exception e) {
        _log.error("Unable to load class " + fnClassName + " for function " + fn, e);
      }

      Object o = c;
      try {
        if (c != null) {
          if ("instance".equals(fnType)) {
            // instantiate the class
            o = IntrospectionUtils.instantiate(c, args);
          }
        }
        MethodWrapper mw = new MethodWrapper(o, fnMethName);
        _functionMap.put(fn, mw);
      } catch (Exception e) {
        _log.error("Unable to instantiate the function " + fn
                   + " using the supplied configuration.", e);
      }
    }

    MacroIncludeSettingHandler macroHandler = new MacroIncludeSettingHandler();

    // parse all macro libraries
    _config.processListSetting("Macros.Include", macroHandler);

    // handle exceptions if any
    if (macroHandler.e != null) {
      throw new InitException("Error loading one or more macro libraries", macroHandler.e);
    }

  }

  /**
   * This class is necessary as we cannot throw an exception directly from
   * processSetting(String,String)
   */
  private class MacroIncludeSettingHandler
    extends Settings.ListSettingHandler
  {
    Exception e;

    @Override
    public void processSetting(String settingKey,
                               String settingValue)
    {
      try {
        Template t = (Template) getProvider("template").get(settingValue);
        _macros.putAll(t.getMacros());
      } catch (ResourceException e) {
        _log.error("Error loading macro library '" + settingValue + "', ignoring it", e);
        // store exception
        if (this.e == null)
          this.e = e;
      }
    }

  }

  /* Factory methods -- the supported way of getting a Broker */

  public static Broker getBroker()
      throws InitException
  {
    try {
      Broker b = findBroker(WEBMACRO_PROPERTIES);
      if (b == null) {
        b = new Broker();
        register(WEBMACRO_PROPERTIES, b);
      }
      return b;
    } catch (InitException e) {
      throw new InitException("Failed to initialize WebMacro with default config", e);
    }
  }

  public static Broker getBroker(Properties p)
      throws InitException
  {
    Broker b = findBroker(p);
    if (b == null) {
      b = new Broker(p);
      register(p, b);
    }
    return b;
  }

  public static Broker getBroker(String settingsFile)
      throws InitException
  {
    Broker b = findBroker(settingsFile);
    if (b == null) {
      b = new Broker(settingsFile);
      register(settingsFile, b);
    }

    return b;
  }

  /* Static (internal) methods used for loading settings */

  protected synchronized void loadDefaultSettings()
      throws InitException
  {
    if (_defaultSettings == null) {
      try {
        _defaultSettings = new Settings(WEBMACRO_DEFAULTS);
      } catch (IOException e) {
        throw new InitException("IO Error reading " + WEBMACRO_DEFAULTS, e);
      }
    }
    // _log.info("Loading properties file " + WEBMACRO_DEFAULTS);
    _config.load(_defaultSettings);
  }

  protected boolean loadSettings(String name,
                                 boolean optional)
      throws InitException
  {
    URL u = getResource(name);
    if (u != null) {
      try {
        _config.load(u);
        return true;
      } catch (IOException e) {
        if (optional) {
          _log.info("Cannot find properties file " + name + ", continuing");
        }
        e.printStackTrace();
        if (!optional) {
          throw new InitException("Error reading settings from " + name, e);
        }
      }
    } else {
      if (!optional) {
        throw new InitException("Error reading settings from " + name);
      }
    }
    return false;
  }

  protected void loadSettings(Properties p)
  {
    _config.load(p);
  }

  protected void loadSystemSettings()
  {
    // _log.info("Loading properties from system properties");
    _config.load(System.getProperties(), SETTINGS_PREFIX);
  }

  /**
   * Used to maintain a weak map mapping the partition _key to the Broker. Registers a broker for a
   * given partition _key.
   */
  protected static void register(Object key,
                                 Broker broker)
  {
    BROKERS.put(key, new WeakReference<Broker>(broker));
  }

  /**
   * Find the broker for the specified partition _key, if one is registered. Used by factory methods
   * to ensure that there is only one broker per WM partition
   */
  protected static Broker findBroker(Object key)
  {
    WeakReference<Broker> ref = BROKERS.get(key);
    if (ref != null) {
      Broker broker = (Broker) ref.get();
      if (broker == null)
        BROKERS.remove(key);
      return broker;
    } else {
      return null;
    }
  }

  /**
   * Access to the settings in WebMacro.properties
   */
  public Settings getSettings()
  {
    return _config;
  }

  /**
   * Access to the settings in WebMacro.properties
   */
  public String getSetting(String key)
  {
    return _config.getSetting(key);
  }

  /**
   * Access to the settings in WebMacro.properties
   */
  public boolean getBooleanSetting(String key)
  {
    return _config.getBooleanSetting(key);
  }

  /**
   * Access to the settings in WebMacro.properties
   */
  public int getIntegerSetting(String key)
  {
    return _config.getIntegerSetting(key);
  }

  /**
   * Access to the settings in WebMacro.properties
   */
  public int getIntegerSetting(String key,
                               int defaultValue)
  {
    return _config.getIntegerSetting(key, defaultValue);
  }

  /**
   * Register a new provider, calling its getType() method to find out what type of requests it
   * wants to serve.
   */
  public void addProvider(Provider p,
                          String pType)
      throws InitException
  {
    if (pType == null || pType.equals("")) {
      pType = p.getType();
    }
    p.init(this, _config);
    _providers.put(pType, p);
    _log.info("Loaded provider " + p);
    if (!pType.equals(p.getType())) {
      _log.info("Provider name remapped from " + p.getType() + " to " + pType);
    }
  }

  /**
   * Get a provider
   */
  public Provider getProvider(String type)
      throws NotFoundException
  {
    Provider p = (Provider) _providers.get(type);
    if (p == null) {
      throw new NotFoundException("No provider for type " + type
                                  + ": perhaps WebMacro couldn't load its configuration?");
    }
    return p;
  }

  /**
   * Get an Iterator of String names that the Provider items in this Broker have been registered
   * under. This can then be used with getProvider(type) to retrieve the registered Provider.
   * 
   * @return Iterator of String
   **/
  public Iterator<String> getProviderTypes()
  {
    return _providers.keySet().iterator();
  }

  /**
   * Close down this Broker. This will call destroy() on all registered Providers with the Broker.
   * 
   * @see Provider#destroy()
   **/
  public void destroy()
  {
    Iterator<Provider> providers = _providers.values().iterator();
    while (providers.hasNext()) {
      providers.next().destroy();
    }
  }

  /**
   * Get the EvaluationExceptionHandler
   */
  public EvaluationExceptionHandler getEvaluationExceptionHandler()
  {
    return _eeHandler;
  }

  /**
   * Set a new EvaluationExceptionHandler
   */
  public void setEvaluationExceptionHandler(EvaluationExceptionHandler eeh)
  {
    _eeHandler = eeh;
  }

  /**
   * Return a map of all global macros. These macros will be included into all templates. As the Map
   * implementation is thread-safe, you can even add macros to the map, if you want to.
   * 
   * @return map of global macros.
   */
  public Map<String, MacroDefinition> getMacros()
  {
    return _macros;
  }
  /**
   * Get a resource (file) from the the Broker's class loader. We look first with the Broker's class
   * loader, then with the system class loader, and then for a file.
   */
  public URL getResource(String name)
  {
    URL u = _myClassLoader.getResource(name);
    if (u == null) {
      u = _systemClassLoader.getResource(name);
    }
    if (u == null) {
      try {
        u = new URL("file", null, -1, name);
        File f = new File(u.getFile());
        if (!f.exists()) {
          u = null;
        }
      } catch (MalformedURLException ignored) {
        _log.error("MalformedURL", ignored);
      }
    }
    return u;
  }

  /**
   * Get a resource (file) from the Broker's class loader
   */
  public InputStream getResourceAsStream(String name)
  {
    InputStream is = _myClassLoader.getResourceAsStream(name);
    if (is == null) {
      is = _systemClassLoader.getResourceAsStream(name);
    }
    if (is == null) {
      try {
        is = new FileInputStream(name);
      } catch (FileNotFoundException ignored) {
        _log.error("FileNotFound", ignored);
      }
    }
    return is;
  }

  /**
   * Get a template from the Broker. By default, this just calls getResource, but allows brokers to
   * substitute their own idea of where templates should come from.
   */
  public URL getTemplate(String name)
  {
    return getResource(name);
  }

  /**
   * Load a class through the broker's class loader. Subclasses can redefine or chain if they know
   * of other ways to load a class.
   */
  public Class<?> classForName(String name)
      throws ClassNotFoundException
  {
    return Class.forName(name);
  }

  /**
   * Look up query against a provider using its integer type handle.
   */
  public Object get(String type,
                    final String query)
      throws ResourceException
  {
    return getProvider(type).get(query);
  }

  /**
   * Store a _key/value in this Broker. This is a utility feature for one to save data that is
   * specific to this instance of WebMacro.
   * <p>
   * Please remember that you probably aren't the only one storing keys in here, so be specific with
   * your _key names. Don't use names like <code>String</code> or <code>Foo</code>. Instead, use
   * <code>IncludeDirective.String</code> and <code>IncludeDirective.Foo</code>.
   */
  public void setBrokerLocal(Object key,
                             Object value)
  {
    _brokerLocal.put(key, value);
  }

  /**
   * Get a value that was previously stored in this Broker.
   * 
   * @see #setBrokerLocal
   */
  public Object getBrokerLocal(Object key)
  {
    return _brokerLocal.get(key);
  }

  /** fetch a "global function" */
  public MethodWrapper getFunction(String fnName)
  {
    return (MethodWrapper) _functionMap.get(fnName);
  }

  /** store a "global function" */
  public void putFunction(String fnName,
                          MethodWrapper mw)
  {
    _functionMap.put(fnName, mw);
  }

  /** store a "global function" by name */
  public void putFunction(String fnName,
                          Object instance,
                          String methodName)
  {
    MethodWrapper func = new MethodWrapper(instance, methodName);
    putFunction(fnName, func);
  }

  /** Fetch a tool */
  public Object getAutoContextVariable(String variableName,
                                       Context context)
  {
    ContextAutoLoader loader = (ContextAutoLoader) _toolLoader.get(variableName);
    try {
      if (loader == null)
        return null;
      else
        return loader.get(variableName, context);
    } catch (PropertyException e) {
      return null;
    }
  }

  public void registerAutoContextVariable(String variableName,
                                          ContextAutoLoader loader)
  {
    _toolLoader.put(variableName, loader);
  }

  /**
   * Explain myself
   */
  @Override
  public String toString()
  {
    StringBuffer buf = new StringBuffer();
    buf.append("Broker:");
    buf.append(_name);
    buf.append("(");
    for (Iterator<Provider> i = _providers.values().iterator(); i.hasNext(); ) {
      buf.append(i.next());
      if (i.hasNext()) {
        buf.append(", ");
      }
    }
    buf.append(")");
    return buf.toString();
  }

  public String getName()
  {
    return _name;
  }

  public ClassLoader getClassLoader()
  {
    return _myClassLoader;
  }

  /**
   * Test the broker or a provider. Reads from stdin: TYPE NAME
   */
  public static void main(String[] arg)
  {
    try {
      if (arg.length != 1) {
        System.out.println("Arg required: config file URL");
        System.out.println("Then input is: TYPE NAME lines on stdin");
        System.exit(1);
      }
      Broker broker = new Broker(arg[0]);

      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

      String line;
      while ((line = in.readLine()) != null) {

        int space = line.indexOf(' ');
        String type = line.substring(0, space);
        String name = line.substring(space + 1);
        System.out.println("broker.get(\"" + type + "\", \"" + name + "\"):");
        Object o = broker.get(type, name);
        System.out.println("RESULT:");
        System.out.println(o.toString());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
