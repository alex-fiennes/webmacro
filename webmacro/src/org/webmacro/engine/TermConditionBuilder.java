
package org.webmacro.engine;
import org.webmacro.util.*;
import org.webmacro.*;


final class TermConditionBuilder implements Builder {
   
   final private Object _term;

   public TermConditionBuilder(Object term) {
      _term = term;
   }

   public Object build(BuildContext bc) throws BuildException
   {
      Object term = (_term instanceof Builder) ? 
         ((Builder) _term).build(bc) : _term;
      return (term instanceof Macro) ?
         new TermCondition((Macro) term) : term;
   }
}


/**
  * Utility class
  */
final class TermCondition extends Condition implements Macro {

   final private Macro _term;

   TermCondition(Macro term) {
      _term = term;
   }

   final public Object evaluate(Object context) 
      throws InvalidContextException
   {
      return _term.evaluate(context);
   }

   final public boolean test(Object context) {
      try {
         return isTrue(_term.evaluate(context));
      } catch (Exception e) {
         return false;
      }
   }
}

