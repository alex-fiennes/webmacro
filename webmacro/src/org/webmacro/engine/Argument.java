
package org.webmacro.engine;

import org.webmacro.*;

/**
  * A predicate is a term passed to a Directive. It consists of a string 
  * word representing the action to beformed, and a target object on which 
  * to perform the action. The target object can be any kind of WebMacro
  * term. 
  */
final class Argument
{

   final private String _name;
   final private Object _value;

   /**
     * Create a new predicate
     */
   public Argument(String name, Object value) {
      _name = name;
      _value = value;
   }

   /**
     * Return the action code for this predicate
     */
   public String getName() { return _name; }

   /**
     * Return the object on which this predicate operates
     */
   public Object getValue() { return _value; }

}
