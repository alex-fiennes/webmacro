
package org.webmacro;

import java.util.*;
import org.webmacro.*;
import org.webmacro.util.*;

public class Context implements Cloneable {

   final private Broker _broker;
   final private Hashtable _toolbox;
   final private Hashtable _tools;

   private Hashtable _locals;
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
      _locals = new Hashtable();
   }

   /**
     * Create a new context working from the specified broker with the 
     * tools available in the supplied toolbox Hashtable. If a bean 
     * is specified (bean != null) then it will be used for property
     * introspection, otherwise property introspection will work with 
     * the local variables stored in this context.
     */
   public Context(final Broker broker, final Hashtable toolbox, 
         final Object bean)
   {
      _broker = broker;
      _bean = bean;
      _toolbox = toolbox;
      _tools = new Hashtable();
      _locals = new Hashtable();
   }


   /**
     * All subclasses must provide sensible clone implementations
     */
   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException cnse) { 
         return null;
      }
   }

   /**
     * Create a new context base don this one, but using the specified 
     * bean instead of this one
     */
   public Context clone(Object bean) {
      Context c = (Context) clone();
      c._locals = new Hashtable();;
      c._bean = bean;
      return c;
   }

   /**
     * Clear the context of its non-shared data
     */
   public void clear() {
      _tools.clear();
      _locals.clear();
      _bean = null;
      _locals = null;
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
      return _locals.get(name);
   }

   /**
     * Set a local value in this Context
     */
   final public void put(Object name, Object value) {
      _locals.put(name,value);
   }

   /**
     * Get the local variables as a Hashtable
     */
   final public Hashtable getLocalVariables() {
      return _locals;
   }


   /**
     * Subclasses can use this method to register new ContextTools
     * into a prototypical context.
     */
   final protected void addTool(String name, ContextTool tool) 
      throws InvalidContextException
   {
      if (_toolbox == null) {
         throw new InvalidContextException("No tools in this context.");
      }
      _tools.put(name,tool);
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
     * of class names which can be loaded and introspected. 
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
      if (_toolbox == null) {
         throw new InvalidContextException("No tools in this context.");
      }
      try {
         Object ret = _tools.get(name);
         if (ret == null) {
            ContextTool tool = (ContextTool) _toolbox.get(name);
            if (tool != null) {
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

