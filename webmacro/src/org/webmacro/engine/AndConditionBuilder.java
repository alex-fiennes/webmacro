
package org.webmacro.engine;

import org.webmacro.*;
import org.webmacro.util.*;

/**
  * Utility class
  */
final class AndConditionBuilder implements Builder {
  
   private final Builder _l,_r;
   AndConditionBuilder(Builder l, Builder r) { _l = l; _r = r; }

   /**
     * Returns Boolean.TRUE, Boolean.FALSE, or a Condition
     */
   public final Object build(BuildContext pc) 
      throws BuildException
   {
      Object l, r;
      l = _l.build(pc);
      r = _r.build(pc);

      
      return ((l instanceof Condition) && (r instanceof Condition)) ?
         new AndCondition((Condition) l, (Condition) r)
         :
         (l instanceof Condition) ?
            Condition.isTrue(r) ? l : Boolean.FALSE
            :
            Condition.isTrue(l) ? r : Boolean.FALSE;
   }
}

/**
  * Utility class
  */
final class AndCondition extends Condition implements Macro {
  
   private final Condition _l,_r;
   AndCondition(Condition l, Condition r) { _l = l; _r = r; }

   public final boolean test(Object context) {
      return (_l.test(context) && _r.test(context));
   }
}

