
package org.webmacro.engine;

import org.webmacro.*;

/**
  * A predicate is a term passed to a Directive. It consists of a string 
  * word representing the action to beformed, and a target object on which 
  * to perform the action. The target object can be any kind of WebMacro
  * term. 
  */
final class Predicate
{

   final private String _verb;
   final private Object _object;

   /**
     * Create a new predicate
     */
   public Predicate(String verb, Object object) {
      _verb = verb;
      _object = object;
   }

   /**
     * Return the action code for this predicate
     */
   public String getVerb() { return _verb; }

   /**
     * Return the object on which this predicate operates
     */
   public Object getObject() { return _object; }

}
