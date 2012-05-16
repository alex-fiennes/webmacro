/*
 * Copyright (C) 1998-2000 Semiotek Inc. All Rights Reserved. Redistribution and use in source and
 * binary forms, with or without modification, are permitted under the terms of either of the
 * following Open Source licenses: The GNU General Public License, version 2, or any later version,
 * as published by the Free Software Foundation (http://www.fsf.org/copyleft/gpl.html); or The
 * Semiotek Public License (http://webmacro.org/LICENSE.) This software is provided "as is", with NO
 * WARRANTY, not even the implied warranties of fitness to purpose, or merchantability. You assume
 * all risks and liabilities associated with its use. See www.webmacro.org for more information on
 * the WebMacro project.
 */

package org.webmacro;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webmacro.engine.EvaluationExceptionHandler;
import org.webmacro.engine.FunctionCall;
import org.webmacro.engine.MethodWrapper;

/**
 * A Context contains state. The idea is to put all of the data you wish to render into the Context
 * and then merge it with a Template via the Template.write() or Template.evaluate() methods.
 * Actually you can render any Macro object by passing a Context to its write() or evaluate()
 * method, not just templates.
 * <p>
 * A Context is a per-thread data structure. It should not be shared between threads since it is not
 * thread safe. The idea is to put all of the state for a single request into the context and then
 * execute it, with each request having its own separate context. In this thread-per-request world
 * view there is no reason to synchronize the Context objects as they are not shared between
 * threads.
 * <p>
 * Ordinarily you acquire a Context object from the WebMacro interface, use it for awhile, and then
 * recycle() it. But you can implement your own Context objects and pass it to the evaluate() and
 * write() method of any Template or other Macro.
 * 
 * @author Marcel Huijkman
 * @version 23-07-2002
 */
public class Context
  implements Map<Object, Object>, Cloneable
{
  static Logger LOGGER = LoggerFactory.getLogger(Context.class);

  private final Broker __broker;
  private HashMap<String, MethodWrapper> _funcs = null; // lazy initialization

  private EvaluationExceptionHandler _eeHandler;

  private Map<Object, Object> _variables = null;

  private TemplateEvaluationContext _teContext = new TemplateEvaluationContext();

  private static final org.webmacro.engine.UndefinedMacro UNDEF =
      org.webmacro.engine.UndefinedMacro.getInstance();

  /**
   * Create a new Context relative to the default WM instance.
   */
  public Context() throws InitException
  {
    this(Broker.getBroker());
  }

  /**
   * Create a new Context relative to the supplied broker.
   */
  public Context(Broker broker)
  {
    this(broker, new HashMap<Object, Object>());
  }
  
  public Context(Broker broker, Map<Object,Object> variables)
  {
    __broker = broker;
    _variables = variables;
  }

  /** Holder for template place. */
  public final static class TemplateEvaluationContext
  {
    // public Block _curBlock;
    // public int _curIndex;
    public String _templateName;
    public int _lineNo;
    public int _columnNo;
  }

  /**
   * See cloneContext(). Subclasses should override cloneContext() rather than the clone() method.
   */
  @Override
  public final Object clone()
  {
    return cloneContext();
  }

  /**
   * Create a copy of this context. The underlying storage will be copied and the local variables
   * reset.
   */
  @SuppressWarnings("unchecked")
  public Context cloneContext()
  {
    Context c;
    try {
      c = (Context) super.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
      return null; // never going to happen
    }
    c._teContext = new TemplateEvaluationContext();
    if (_variables instanceof HashMap<?, ?>) {
      c._variables = (Map<Object, Object>) ((HashMap<Object, Object>) _variables).clone();
    } else {
      c._variables = new HashMap<Object, Object>(_variables);
    }
    return c;
  }

  /**
   * Clear the context so that it can be used for another request.
   * <p>
   * Subclasses may override the clear method and add functionality but they must call super.clear()
   * if they do so.
   */
  public void clear()
  {
    _variables.clear();
    _eeHandler = null;
  }

  /**
   * Get the instance of the Broker for this request.
   */
  public final Broker getBroker()
  {
    return __broker;
  }

  public final TemplateEvaluationContext getTemplateEvaluationContext()
  {
    return _teContext;
  }

  public final String getCurrentLocation()
  {
    StringBuilder loc = new StringBuilder();
    loc.append(_teContext._templateName == null ? "(unknown)" : _teContext._templateName);
    loc.append(":").append(_teContext._lineNo).append(".").append(_teContext._columnNo);
    return loc.toString();
  }

  /**
   * Get a log instance that can be used to write log messages into the log under the supplied log
   * type.
   */
  public final Logger getLog(String type,
                             String description)
  {
    return LOGGER;
  }

  /**
   * Get a log instance that can be used to write log messages into the log under the supplied log
   * type. The type will be used as the description.
   */
  public final Logger getLog(String type)
  {
    return LOGGER;
  }

  /**
   * Get the EvaluationExceptionHandler.
   */
  public EvaluationExceptionHandler getEvaluationExceptionHandler()
  {
    if (_eeHandler != null) {
      return _eeHandler;
    } else {
      return __broker.getEvaluationExceptionHandler();
    }
  }

  /**
   * Set a new EvaluationExceptionHandler.
   */
  public void setEvaluationExceptionHandler(EvaluationExceptionHandler eeh)
  {
    _eeHandler = eeh;
  }

  /**
   * Get the named object/property from the Context. If the Object does not exist and there is a
   * tool of the same name then the Object will be instantiated and managed by the tool. If there's
   * no such variable, it throws.
   */
  protected Object internalGet(Object name)
      throws PropertyException
  {
    Object ret = _variables.get(name);
    if (ret != null || _variables.containsKey(name))
      return ret;

    if (name instanceof String) {
      Object var = __broker.getAutoContextVariable((String) name, this);
      if (var != null) {
        put((String) name, var);
        return var;
      } else
        return UNDEF;
    } else if (name instanceof FunctionCall) {
      FunctionCall fc = (FunctionCall) name;
      String fname = fc.getName();
      MethodWrapper func = null;
      if (_funcs != null) {
        func = _funcs.get(fname);
      }
      if (func == null) {
        func = __broker.getFunction(fname);
      }
      if (func != null) {
        Object[] args = fc.getArguments(this);
        ret = func.invoke(args);
      } else {
        LOGGER.error("Function " + fname + " was not loaded!");
      }
      return ret;
    } else {
      // changed by Keats 30-Nov-01
      return UNDEF;
    }
  }

  /**
   * Get the named object/property from the Context; returns null if not found.
   */
  public final Object get(Object name)
  {
    try {
      Object o = internalGet(name);
      if (o == UNDEF) {
        return null;
      }
      return o;
    } catch (PropertyException e) {
      // NOTE: I don't think we get here anymore! -Keats
      return null;
    }
  }

  /**
   * Convenience method for putting static classes into the context, wraps the class instance in a
   * wrapper.
   */
  public final <T> Object put(String name,
                              Class<T> c)
  {
    if (c == null) {
      return _variables.put(name, null);
    } else {
      return _variables.put(name, new org.webmacro.engine.StaticClassWrapper<T>(c));
    }
  }

  // public final Object put(Object name, MethodWrapper mw){
  // System.out.println("Adding function " + name);
  // return _funcs.put(name, mw);
  // }

  public final void putFunction(String name,
                                Object instance,
                                String methodName)
  {
    MethodWrapper func = wrapMethod(instance, methodName);
    if (_funcs == null)
      _funcs = new HashMap<String, MethodWrapper>();
    _funcs.put(name, func);
  }

  public final void putGlobalFunction(String name,
                                      Object instance,
                                      String methodName)
  {
    MethodWrapper func = wrapMethod(instance, methodName);
    __broker.putFunction(name, func);
  }

  private final MethodWrapper wrapMethod(Object instance,
                                         String methodName)
  {
    MethodWrapper func = null;
    try {
      func = new MethodWrapper(instance, methodName);
    } catch (Exception e) {
      String className = null;
      if (instance instanceof Class<?>) {
        className = ((Class<?>) instance).getName();
      } else if (instance != null) {
        className = instance.getClass().getName();
      }
      LOGGER.error("Unable to construct function from method: " + methodName + " of class "
                 + className);
    }
    return func;
  }

  /**
   * Add an object to the context returning the object that was there previously under the same
   * name, if any.
   */
  public final Object put(Object name,
                          Object value)
  {
    return _variables.put(name, value);
  }

  /**
   * Get the named object from the Context. The name is a list of property names. The first name is
   * the name of an object in the context. The subsequent names are properties of that object which
   * will be searched using introspection.
   */
  protected Object internalGet(Object[] names)
      throws PropertyException
  {
    Object instance;
    try {
      instance = internalGet(names[0]);
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new PropertyException("Attempt to access property with a zero length name array");
    }
    if (names.length == 1) {
      return instance;
    } else if (instance == null) {
      throw new PropertyException.NullValueException(names[0].toString());
    } else {
      return __broker.__propertyOperators.getProperty(this, instance, names, 1);
    }
  }

  /**
   * Set the named property in the Context. The first name is the name of an object in the context.
   * The subsequent names are properties of that object which will be searched using introspection.
   * 
   * @return whether or not the set was successful
   */
  public final boolean set(Object[] names,
                           Object value)
      throws PropertyException
  {
    if (names.length == 1) {
      put((String) names[0], value);
      return true;
    } else {
      Object instance;
      try {
        instance = internalGet(names[0]);
      } catch (ArrayIndexOutOfBoundsException e) {
        return false;
      }
      return __broker.__propertyOperators.setProperty(this, instance, names, 1, value);
    }
  }

  /**
   * Same as get(name) but can be overridden by subclasses to do something different.
   */
  public Object getProperty(Object name)
      throws PropertyException
  {
    return internalGet(name);
  }

  /**
   * Same as put(name,value) but can be overridden by subclasses to do something different.
   */
  public boolean setProperty(Object name,
                             Object value)
      throws PropertyException
  {
    put(name, value);
    return true;
  }

  /**
   * Same as get(Object names[]) but can be overridden by subclasses to behave differently.
   */
  public Object getProperty(Object[] names)
      throws PropertyException
  {
    return internalGet(names);
  }

  /**
   * Same as set(Object names[], Object value) but can be overridden by subclasses to behave
   * differently.
   * 
   * @return whether or not the set was successful
   */
  public boolean setProperty(Object[] names,
                             Object value)
      throws PropertyException
  {
    return set(names, value);
  }

  /**
   * Set the underlying Map object. The supplied Map will subsequently be used to resolve local
   * variables.
   */
  public final void setMap(Map<Object, Object> m)
  {
    _variables = m;
  }

  /**
   * Get the underlying Map object.
   */
  public final Map<Object, Object> getMap()
  {
    return _variables;
  }

  /**
   * Method from Map interface, operates on underlying Map.
   */
  public boolean containsKey(Object key)
  {
    return _variables.containsKey(key);
  }

  /**
   * Method from Map interface, operates on underlying Map.
   */
  public final boolean containsValue(Object value)
  {
    return _variables.containsValue(value);
  }

  /**
   * Method from Map interface, operates on underlying Map.
   */
  public final Set<Map.Entry<Object, Object>> entrySet()
  {
    return _variables.entrySet();
  }

  /**
   * Method from Map interface, operates on underlying Map.
   */
  public final boolean isEmpty()
  {
    return _variables.isEmpty();
  }

  /**
   * Method from Map interface, operates on underlying Map.
   */
  public final Set<Object> keySet()
  {
    return _variables.keySet();
  }

  /**
   * Method from Map interface, operates on underlying Map.
   */
  @Override
  public void putAll(Map<? extends Object, ? extends Object> m)
  {
    _variables.putAll(m);
  }

  /**
   * Method from Map interface, operates on underlying Map.
   */
  public final Object remove(Object key)
  {
    return _variables.remove(key);
  }

  /**
   * Method from Map interface, operates on underlying Map.
   */
  public final int size()
  {
    return _variables.size();
  }

  /**
   * Method from Map interface, operates on underlying Map.
   */
  public final Collection<Object> values()
  {
    return _variables.values();
  }

  // ////////////////////////////////////////////////////////////

  /**
   * Dump the variables (and their values) contained in this Context. Output is similiar to
   * <code>java.util.HashMap.toString()</code>
   */
  @Override
  public String toString()
  {
    return _variables.toString();
  }
}
