
package org.webmacro;

import java.util.*;
import java.lang.reflect.Method;
import org.webmacro.*;
import org.webmacro.util.*;
import org.webmacro.profile.*;

/**
  * A Context contains all of the data you wish to display in a WebMacro
  * template. Ordinarily you just use the get() and set() methods to load
  * the Context with all of your data, as if it were a hashtable, and then
  * pass it to a Template for execution.
  * <p>
  * You should create a new Context for every request, usually by cloning
  * a prototype object. The Context is not thread-safe and expects to
  * operate in a thread-per-request environment. Because all of the
  * request-specific state is contained within the Context, Templates
  * and other objects can be safely shared between multiple threads.
  * <p>
  * A Context also contains other things that may be useful to the template
  * processing system: a copy of the Broker that is currently in effect,
  * which can be used to load other objects; a set of ContextTools which
  * add additional functionality to templates; and potentially an instance
  * of a Java Bean which should be used as the root of introspection. If
  * the bean is null then the Context itself is the root of introspection.
  * <p>
  * You may wish to make use of the ContextTool objects directly within
  * your application.
  * <p>
  * A Context is cloneable so that you can efficiently create a new
  * context from an existing one. Instantiating a brand new context object
  * using the constructor is expensive. The cloned context will not share
  * local variables with its sibling, but will share the same tools
  * (including tool instances), and the same broker.
  */
public class Context implements Cloneable {

   final private Broker _broker;

   private Object _bean; // root of property introspection

   final private Map _toolbox; // contains tool initializers
   final private Map _tools = new HashMap();

   private Map _globals = new HashMap();

   private Locale _locale = Locale.getDefault();

   private SimpleStack _contextCache = null;

   /**
     * This method expects to get a THREAD LOCAL pool in which to
     * deposit objects. The Stack is therefore an unsynchronized
     * pool. You must externally arrange that no two contexts try
     * and access the same pool at the same time.
     */
   final protected void setContextPool(SimpleStack pool) {
      _contextCache = pool;
   }

   /**
     * Log configuration errors, context errors, etc.
     */
   private final Log _log;

   private org.webmacro.profile.Profile _prof = null;


   // CONSTRUCTION, INITIALIZATION, AND LIFECYCLE

   /**
     * Create an empty context--no bean, just local variables
     * and a broker. Tools loaded from config "ContextTools".
     * <p>
     * Ordinarily you don't call this method: you create a prototype
     * Context and then use newInstance. Creating the initial Context
     * object is fairly expensive. Use the WebMacro.getContext()
     * method instead.
     */
   protected Context(final Broker broker) {
      _broker = broker;
      _log = _broker.getLog("context");
      _prof = _broker.newProfile();
      _bean = null;
      _toolbox = new HashMap();
      try {
         String tools = (String) broker.get("config","ContextTools");
         registerTools(tools);
      } catch (NotFoundException ne) {
         _log.warning("could not load ContextTools from config", ne);
      }
   }

   /**
     * Create a new context working from the specified broker with the
     * tools available in the supplied toolbox Map. If a bean
     * is specified (bean != null) then it will be used for property
     * introspection, otherwise property introspection will work with
     * the local variables stored in this context.
     */
   protected Context(final Broker broker, final Map toolbox, final Object bean)
   {
      _broker = broker;
      _log = _broker.getLog("context");
      _prof = _broker.newProfile();
      _bean = bean;
      _toolbox = toolbox;
   }



   /**
     * Create a new context based on this one, but using the specified
     * bean instead of this one. The clone will share tools and the broker
     * with its parent. It will have a null property bean.
     */
   protected Object clone() {
      Context c = null;
      try {
         c = (Context) super.clone();
      } catch (CloneNotSupportedException e) {
         // Object supports clone
      }
      c._globals = new HashMap();
      c._bean = null;
      c._locale = _locale;
      c._prof = _broker.newProfile();
      return c;
   }

   /**
     * This method is ordinarily called by the template processing system.
     * <p>
     * Clear the context of its non-shared data, preserving only the toolbox.
     */
   public void clear() {
      if (_prof != null) startTiming("Context.clear");
      Iterator i = _tools.entrySet().iterator();
      while (i.hasNext()) {
         Map.Entry m = (Map.Entry) i.next();
         ContextTool tool = (ContextTool) _toolbox.get(m.getKey());
         tool.destroy(m.getValue());
      }
      _tools.clear();
      _globals.clear();
      _bean = null;
      if (_prof != null) {
         stopTiming();
        _prof.destroy();
      }
   }

   /**
     * This method calls clear and then recycles the context back
     * to the pool it came from, if any.
     */
   public void recycle() {
      clear();
      if (_contextCache != null) {
         _contextCache.push(this);
      }
   }

   // INITIALIZATION: TOOL CONFIGURATION

   /**
     * This method is called when initializing a new context. You would
     * ordinarily then clone the configured context.
     * <p>
     * Subclasses can use this method to register new ContextTools
     * during construction or initialization of the Context.
     */
   final protected void registerTool(String name, ContextTool tool)
   {
      _toolbox.put(name,tool);
   }

   /**
     * Find the name of a tool given the name of a class
     */
   private String findToolName(String cname)
   {
      int start = cname.lastIndexOf('.') + 1;
      int end = (cname.endsWith("Tool")) ?
         (cname.length() - 4) : cname.length();
      String ret = cname.substring(start,end);
      return ret;
   }

   /**
     * Add the tools specified in the StringTokenized list of tools
     * passed as an argument. The list of tools passed should be a list
     * of class names which can be loaded and introspected. It is expected
     * this method will be used during construction or initialization.
     */
   final protected void registerTools(String tools) {
      Enumeration tenum = new StringTokenizer(tools);
      while (tenum.hasMoreElements()) {
         String toolName = (String) tenum.nextElement();
         try {
            Class toolType = Class.forName(toolName);
            String varName = findToolName(toolName);
            ContextTool tool = (ContextTool) toolType.newInstance();
            registerTool(varName,tool);
         } catch (ClassCastException cce) {
            _log.error("Tool class " + toolName
                  + " newInstance returns invalid type.", cce);
         } catch (ClassNotFoundException ce) {
            _log.error("Tool class " + toolName + " not found: ", ce);
         } catch (IllegalAccessException ia) {
            _log.error("Tool class and methods must be public for "
                  + toolName, ia);
         } catch (InstantiationException ie) {
            _log.error("Tool class " + toolName + " must have a public zero "
                  + "argument or default constructor", ie);
         }
      }
   }


   // ACCESS TO THE BROKER

   /**
     * Get the broker that it is in effect for this context
     */
   final public Broker getBroker() {
      return _broker;
   }

   /**
     * Convenience method equivalent to getBroker().getLog(name)
     */
   final public Log getLog(String name) {
      return _broker.getLog(name);
   }

   /**
     * Return true if the Context contains an active profiler, and
     * calls to startTiming/stopTiming will be counted.
     */
   public final boolean isTiming() {
      return (_prof != null);
   }

   /**
     * Mark the start of an event for profiling. Note that you MUST
     * call stop() or the results of profiling will be invalid.
     */
   final public void startTiming(String name) {
      if (_prof == null) return;
      _prof.startEvent(name);
   }

   /**
     * Same as startTiming(name1 + "(" + arg + ")") but the concatenation
     * of strings and the call to arg.toString() occurs only if profiling
     * is enabled.
     */
   final public void startTiming(String name1, Object arg) {
      if (_prof == null) return;
      _prof.startEvent(name1 + "(" + arg + ")");
   }

   /**
     * Same as startTiming(name1 + "(" + arg1 + "," + arg2 + ")") but the
     * concatenation of strings and the call to arg.toString() occurs only
     * if profiling * is enabled.
     */
   final public void startTiming(String name1, Object arg1, Object arg2) {
      if (_prof == null) return;
      _prof.startEvent(name1 + "(" + arg1 + ", " + arg2 + ")");
   }

    /**
     * Same as startTiming(name1 + "(" + arg + ")") but the
     * concatenation of strings and the call to toString() occurs only
     * if profiling is enabled.
     */
   final public void startTiming(String name, int arg) {
      if (_prof == null) return;
      _prof.startEvent(name + "(" + arg + ")");
   }

    /**
     * Same as startTiming(name1 + "(" + arg + ")") but the
     * concatenation of strings and the call to toString() occurs only
     * if profiling is enabled.
     */
   final public void startTiming(String name, boolean arg) {
      if (_prof == null) return;
      _prof.startEvent(name + "(" + arg + ")");
   }

   /**
     * Mark the end of an event for profiling. Note that you MUST
     * HAVE CALLED start() first or the results of profiling will
     * be invalid.
     */
   final public void stopTiming() {
      if (_prof == null) return;
      _prof.stopEvent();
   }


   // PROPERTY API

   /**
     * Get the local variables as a HashMap
     */
   final public Map getGlobalVariables() {
      return _globals;
   }


   /**
    * Set the local variables as a Map.<p>
    *
    * One should probably use this method like this:<br>
    * <code>context.setGlobalVariables ( (Map) _myDefaultMap.clone());</code>
    * @author Eric B. Ridge
    * @date Oct 16, 2000
    *
    * @param globalMap the HashMap to use as the global variabls for this Context
    */
   final public void setGlobalVariables (Map globalMap)
   {
      _globals = globalMap;
   }

   /**
     * Return the root of introspection, the top level bean for this
     * context which properties reference into. If this returns null,
     * then properties reference local variables.
     */
   final public Object getBean() {
      return _bean;
   }

   /**
     * Set the root of introspection
     */
   final public void setBean(Object bean) {
      _bean = bean;
   }

   /**
     * Get the named property via introspection. If there is no bean
     * in this context, then try accessing the value as a local variable.
     * If there is no bean, and no local variable, try it as a tool. This
     * fallback to local and tool is to make property variable access
     * backward compatible with older WebMacro implementations for the
     * top level template, where there is no bean.
     */
   public final Object getProperty(final Object[] names)
      throws PropertyException
   {
      Object root;
      Object name;

      try {
         name = names[0];
      } catch (ArrayIndexOutOfBoundsException e) {
         throw new PropertyException("Zero length property name",e);
      }

      if (_bean == null) {
         root = _globals.get(name);
         if (root == null) {
            root = getTool(name);
         }
         if (root == null) {
            throw new PropertyException("Could not access $" + names[0]
               + " since there is no such property in the Context"
               + " and no matching ContextTool has been registered.",null);
         }
         if (names.length == 1) {
            return root;
         } else {
            return PropertyOperator.getProperty(this,root,names,1);
         }
      } else {
         // bean: tool wins over bean property (avoid exception)
         root = getTool(name);
         if ((root != null) && (names.length == 1)) {
            return root;
         }
         return PropertyOperator.getProperty(this,
               ((root == null) ? _bean : root), names, 0);
      }
   }

   /**
     * Set the named property via introspection
     */
   final public boolean setProperty(final Object[] names, final Object value)
      throws PropertyException
   {
      if (_bean == null) {
         return setGlobal(names, value) || setTool(names, value);
      } else {
         return setTool(names,value) ||
               PropertyOperator.setProperty(this,_bean,names,value);
      }
   }


   // LOCAL VARIABLE API

   /**
     * Retrieve a local value from this Context.
     */
   final public Object get(Object name) {
      return _globals.get(name);
   }

   /**
     * Set a local value in this Context
     */
   final public void put(Object name, Object value) {
      _globals.put(name,value);
   }

   /**
     * Get the named local variable via introspection. This is
     * an advanced-use method.
     */
   public final Object getGlobal(final Object[] names)
      throws PropertyException
   {
      Object root;
      try {
         root = _globals.get(names[0]);
      } catch (ArrayIndexOutOfBoundsException e) {
         throw new PropertyException("Illegal property name: zero length",e);
      }
      if ((root == null) || (names.length == 1)) {
         return root;
      }
         return PropertyOperator.getProperty(this,root,names,1);
   }

   /**
     * Set the named local variable via introspection. This is
     * an advanced-use method.
     */
   final public boolean setGlobal(final Object[] names, final Object value)
      throws PropertyException
   {
      if (names.length == 1) {
         put(names[0], value);
         return true;
      } else {
         Object root;
         try {
            root = _globals.get(names[0]);
         } catch (ArrayIndexOutOfBoundsException e) {
            throw new PropertyException(
                     "Illegal property name: zero length",e);
         }
         if (root == null) {
            return false;
         }
         return PropertyOperator.setProperty(this,root,names,1,value);
      }
   }


   // TOOL API

   /**
     * Return the tool corresponding to the specified tool name, or
     * null if there isn't one. This is an advanced-use method.
     */
   final public Object getTool(Object name)
      throws PropertyException
   {
      Object ret = _tools.get(name);
      if (ret != null) return ret;
      try {
         ContextTool tool = (ContextTool) _toolbox.get(name);
         if (tool != null) {
            ret = tool.init(this);
            _tools.put(name,ret);
         }
         return ret;
      } catch (ClassCastException ce) {
         throw new PropertyException("Tool" + name
               + " does not implement the ContextTool interface!",null);
      }
   }

   /**
     * Get the named tool variable via introspection. This is an
     * advanced-use method.
     */
   public final Object getTool(final Object[] names)
      throws PropertyException
   {
      Object root;
      try {
         root = getTool(names[0]);
      } catch (ArrayIndexOutOfBoundsException e) {
         throw new PropertyException("Illegal tool name: zero length",e);
      }
      if (names.length == 1) {
         return root;
      }
      return PropertyOperator.getProperty(this,root,names,1);
   }

   /**
     * Set the named tool variable via introspection. This is an
     * advanced-use method.
     */
   final public boolean setTool(final Object[] names, final Object value)
      throws PropertyException
   {
      Object root;
      try {
        root = getTool(names[0]);
      }  catch (ArrayIndexOutOfBoundsException e) {
         throw new PropertyException("Illegal tool name: zero length",e);
      }
      if (names.length > 1) {
         return PropertyOperator.setProperty(this,root,names,1,value);
      }
      return false; // cannot reset the tool itself!
   }

   /**
     * Set the locale for this request
     */
   final public void setLocale(Locale l) {
      _locale = l;
   }

   /**
     * Get the locale for this request. This will return null if no
     * Locale has been set for the current request.
     */
   final public Locale getLocale() {
      return _locale;
   }


   private static final String propName(Object[] name) {
      StringBuffer buf = new StringBuffer();
      for (int i = 0; i < name.length; i++) {
         if (i != 0) buf.append(".");
         buf.append(name[i]);
      }
      return buf.toString();
   }

   public boolean containsKey(Object name) {
      return _globals.containsKey(name);
   }
}
