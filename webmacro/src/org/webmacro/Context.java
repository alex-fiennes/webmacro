
package org.webmacro;


import java.util.*;
import org.webmacro.*;
import org.webmacro.util.*;
import org.webmacro.util.java2.*;

/**
  * A Context is the execution context of a WebMacro request. It represents
  * the data associated with a specific user of a Macro. The Context class 
  * is not threaded--the expectation is that each use of a Macro will occur
  * in its own thread. You can have multiple threads, each with its own 
  * Context, accessing the same Macro. A Context contains a reference to 
  * the broker in use for this request, a collection of Context tools, 
  * local variables, and a bean object representing the Context's data.
  * <p>
  * Historical Note: In previous versions of WebMacro a Context was an 
  * object of arbitrary type. That corresponds to Context.getBean()
  * in the current implementation. 
  * <p>
  * A Context is cloneable so that you can efficiently create a new 
  * context from an existing one. The cloned context will not share 
  * local variables with its sibling, but will share the same tools 
  * (including tool instances), and the same broker.
  */
public class Context implements Cloneable {

   final private Broker _broker;

   private Object _bean; // root of property introspection

   private HashMap _toolbox; // contains tool initializers
   private HashMap _tools = null;   // contains in-use tools

   private HashMap _locals = null; // local variables

   private Object[] _beanState = null; // managed by push/pop
   private HashMap[] _localState = null; // managed by push/pop
   private int _state = 0; // managed by push/pop

   /**
     * Log configuration errors, context errors, etc.
     */
   private final static Log _log = new Log("context","Context Messages");



   // CONSTRUCTION, INITIALIZATION, AND LIFECYCLE

   /**
     * Create an empty context--no bean, no tools, just local variables
     * and a broker.
     */
   public Context(final Broker broker) {
      _broker = broker; 
      _bean = null;
      _toolbox = null;
   }

   /**
     * Create a new context working from the specified broker with the 
     * tools available in the supplied toolbox HashMap. If a bean 
     * is specified (bean != null) then it will be used for property
     * introspection, otherwise property introspection will work with 
     * the local variables stored in this context.
     */
   public Context(final Broker broker, final HashMap toolbox, 
         final Object bean)
   {
      _broker = broker;
      _bean = bean;
      _toolbox = toolbox;
   }


   /**
     * Create a new context based on this one, but using the specified 
     * bean instead of this one. The clone will be in an initial state, 
     * no pushes performed on the parent will be visible in it. The 
     * clone will share tools and the broker with its parent. It will
     * have a null property bean.
     */
   public Object clone() {
      Context c = null;
      try {
         c = (Context) super.clone();
      } catch (CloneNotSupportedException e) {
         // Object supports clone
      }

      c._localState = null;
      c._beanState = null;
      c._state = 0;

      c._locals = null;
      c._bean = null;

      return c;
   }

   /**
     * Push the supplied bean onto the context, creating a sub-context 
     * which is equivalent to the current one, only with fresh local vars
     * and using this bean for property evaluation. A subsequent pop will
     * restore the context to its present state. The broker and tools 
     * will be unaffected.
     */
   public void push(Object bean) 
      throws InvalidContextException
   {
      if (_state > 256) {
         throw new InvalidContextException("Infinite recursion detected: "
               + " context recursion cutoff a stack depth of 256.");
      }
   
      if (_localState == null) {
         _localState = new HashMap[7];
      }

      if (_beanState == null) {
         _beanState = new Object[7];
      }

      if (_state == _localState.length) {
         HashMap[] tmp = new HashMap[ _localState.length * 2 + 1 ];
         System.arraycopy(_localState,0,tmp,0,_localState.length);
         _localState = tmp;
      }

      if (_state == _beanState.length) {
         Object[] tmp = new Object[ _beanState.length * 2 + 1 ];
         System.arraycopy(_beanState,0,tmp,0,_beanState.length);
         _beanState = tmp;
      }

      _localState[_state] = _locals;
      _beanState[_state] = _bean;
      _state++;
      _bean = bean;
      _locals = null;
   }

   /**
     * Restore the context to the state prior to the last push(bean), 
     * recovering the old local variables and the old bean. If you pop
     * more times than you push, this has no effect.
     */
   public void pop() {
      if (_state == 0) {
         return;
      }
      if (_locals != null) {
         _locals.clear();
      }
      _state--;

      _bean = _beanState[_state];
      _beanState[_state] = null;

      _locals = _localState[_state];
      _localState[_state] = null;
   }


   /**
     * Clear the context of its non-shared data, preserving only the toolbox.
     */
   public void clear() {
      while (_state > 0) {
         pop();
      }

      if (_tools != null) {
         _tools.clear();
         _tools = null;
      }

      if (_locals != null) {
         _locals.clear();
         _locals = null;
      }

      _bean = null;
   }


   // INITIALIZATION: TOOL CONFIGURATION

   /**
     * Subclasses can use this method to register new ContextTools
     * during construction or initialization of the Context. 
     */
   final protected void registerTool(String name, ContextTool tool) 
      throws InvalidContextException
   {
      if (_toolbox == null) {
         _toolbox = new HashMap();
      }
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
            _log.exception(cce);
            _log.error("Tool class " + toolName 
                  + " newInstance returns invalid type.");
         } catch (ClassNotFoundException ce) {
            _log.exception(ce);
            _log.error("Tool class " + toolName + " not found: " + ce);
         } catch (IllegalAccessException ia) {
            _log.exception(ia);
            _log.error("Tool class and methods must be public for "
                  + toolName + ": " + ia);
         } catch (InvalidContextException e) {
            _log.exception(e);
            _log.error("InvalidContextException thrown while registering "
                  + "Tool: " + toolName);
         } catch (InstantiationException ie) {
            _log.exception(ie);
            _log.error("Tool class " + toolName + " must have a public zero "
                  + "argument or default constructor: " + ie);
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


   // PROPERTY API

   /**
     * Get the local variables as a HashMap
     */
   final public HashMap getLocalVariables() {
      if (_locals == null) {
         _locals = new HashMap();
      }
      return _locals;
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
      throws PropertyException, InvalidContextException
   {
      if (names.length == 0) {
         return null;
      } else if (_bean == null) {
         Object ret = getLocal(names);
         if (ret == null) {
            ret = getTool(names);
         }
         return ret;
      } else {
         return PropertyOperator.getProperty(this,_bean,names);
      }
   }

   /**
     * Set the named property via introspection 
     */
   final public boolean setProperty(final Object[] names, final Object value) 
      throws PropertyException, InvalidContextException
   {
      if (names.length == 0) {
         return false;
      } else if (_bean == null) {
         return setLocal(names, value);
      } else {
         return PropertyOperator.setProperty(this,_bean,names,value);      
      }
   }


   // LOCAL VARIABLE API

   /**
     * Retrieve a local value from this Context
     */
   final public Object get(Object name) {
      return (_locals != null) ? _locals.get(name) : null;
   }

   /**
     * Set a local value in this Context
     */
   final public void put(Object name, Object value) {
      if (_locals == null) {
         _locals = new HashMap();
      }
      _locals.put(name,value);
   }

   /**
     * Get the named local variable via introspection 
     */
   public final Object getLocal(final Object[] names) 
      throws PropertyException, InvalidContextException
   {
      if ((names.length == 0) || (_locals == null)) {
         return null;
      } else {
         Object res = get(names[0]);
         if (names.length == 1) {
            return res;
         } 
         return PropertyOperator.getProperty(this,get(names[0]),names,1);
      } 
   }

   /**
     * Set the named local variable via introspection 
     */
   final public boolean setLocal(final Object[] names, final Object value) 
      throws PropertyException, InvalidContextException
   {
      if (names.length == 0) {
         return false;
      } 
      if (_locals == null) {
         _locals = new HashMap();
      }
      if (names.length == 1) {
         put(names[0], value);
         return true;
      } else {
         return PropertyOperator.setProperty(this,get(names[0]),names,1,value);
      } 
   }


   // TOOL API

   /**
     * Return the tool corresponding to the specified tool name, or 
     * null if there isn't one
     */
   final public Object getTool(Object name) 
      throws InvalidContextException
   {
      try {
         if (_toolbox == null) {
            return null;
         }
         Object ret = (_tools != null) ? _tools.get(name) : null;
         if (ret == null) {
            ContextTool tool = (ContextTool) _toolbox.get(name);
            if (tool != null) {
               if (_tools == null) {
                  _tools = new HashMap();
               }
               ret = tool.init(this);
               _tools.put(name,ret);
            }
         }
         return ret;
      } catch (ClassCastException ce) {
         throw new InvalidContextException("Tool" + name  
               + " does not implement the ContextTool interface!");
      }
   }
   /**
     * Get the named tool variable via introspection 
     */
   public final Object getTool(final Object[] names) 
      throws PropertyException, InvalidContextException
   {
      if ((names.length == 0) || (_toolbox == null)) {
         return null;
      } else {
         Object res = getTool(names[0]);
         if (names.length == 1) {
            return res;
         } 
         return PropertyOperator.getProperty(this,getTool(names[0]),names,1);
      } 
   }

   /**
     * Set the named tool variable via introspection 
     */
   final public boolean setTool(final Object[] names, final Object value) 
      throws PropertyException, InvalidContextException
   {
      if (names.length == 0) {
         return false;
      } 
      if (names.length == 1) {
         throw new InvalidContextException("Cannot reset tool in a running context. Tools can only be registered via the registerTool method.");
      } else {
         return PropertyOperator.setProperty(this,getTool(names[0]),names,1,value);
      } 
   }
}
