
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

   private HashMap _toolbox; // contains tool initializers
   private HashMap _tools;   // contains in-use tools

   private HashMap _locals;
   private Object _bean;

   /**
     * Log configuration errors, context errors, etc.
     */
   private final static Log _log = new Log("context","Context Messages");

   /**
     * Create an empty context--no bean, no tools, just local variables
     * and a broker.
     */
   public Context(final Broker broker) {
      _broker = broker; 
      _bean = null;
      _tools = null;
      _toolbox = null;
      _locals = null;
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
      _tools = null;
      _locals = null;
   }


   /**
     * All subclasses must provide sensible clone implementations
     */
   protected Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException cnse) { 
         return null;
      }
   }

   /**
     * Create a new context based on this one, but using the specified 
     * bean instead of this one
     */
   public Context clone(Object bean) {
      Context c = (Context) clone();
      c._locals = null;
      c._bean = bean;
      return c;
   }

   /**
     * Clear the context of its non-shared data
     */
   public void clear() {
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

   /**
     * Return the root of introspection, the top level bean for this 
     * context which properties reference into. If this returns null, 
     * then properties reference local variables.
     */
   final public Object getBean() {
      return _bean;
   }

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
     * Get the local variables as a HashMap
     */
   final public HashMap getLocalVariables() {
      if (_locals == null) {
         _locals = new HashMap();
      }
      return _locals;
   }


   /**
     * Subclasses can use this method to register new ContextTools
     * during construction or initialization of the Context. 
     */
   final protected void addTool(String name, ContextTool tool) 
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
   private String getToolName(String cname)
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
   final protected void addTools(String tools) {
      Enumeration tenum = new StringTokenizer(tools);
      while (tenum.hasMoreElements()) {
         String toolName = (String) tenum.nextElement();
         try {
            Class toolType = Class.forName(toolName);
            String varName = getToolName(toolName);
            ContextTool tool = (ContextTool) toolType.newInstance(); 
            addTool(varName,tool);
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


   /**
     * Return the tool corresponding to the specified tool name, or 
     * null if there isn't one
     */
   final public Object getTool(String name) 
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
     * Get the broker that it is in effect for this context
     */
   final public Broker getBroker() {
      return _broker;
   }

   /**
     * Get the named property via introspection 
     */
   public final Object getProperty(final Object[] names) 
      throws PropertyException, InvalidContextException
   {
      if (names.length == 0) {
         return null;
      } else if (_bean == null) {
         Object res = get(names[0]);
         if (names.length == 1) {
            return res;
         } 
         return PropertyOperator.getProperty(this,get(names[0]),names,1);
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
         if (names.length == 1) {
            put(names[0], value);
            return true;
         }
         return PropertyOperator.setProperty(this,get(names[0]),names,1,value);
      } else {
         return PropertyOperator.setProperty(this,_bean,names,value);      
      }
   }

}

