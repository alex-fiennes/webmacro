
package org.webmacro.engine;

import org.webmacro.*;

final class NotConditionBuilder implements Builder {

   private Builder _cond;

   NotConditionBuilder(Builder condition) {
      _cond = condition;
   }

   public Object build(BuildContext bc) throws BuildException
   {
      Object c = _cond.build(bc);
      return (c instanceof Condition) ?
         (Object) new NotCondition((Condition) c) 
         :
         (Object) (Condition.isTrue(c) ? Boolean.FALSE : Boolean.TRUE);
   }
}

/**
  * Utility class
  */
final class NotCondition extends Condition implements Macro {

   private final Condition _cond;

   NotCondition(Condition c) {
      _cond = c;
   }

   final public boolean test(Object context) {
      return (! _cond.test(context) );
   }
}
