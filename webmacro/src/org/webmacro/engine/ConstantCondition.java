

package org.webmacro.engine;
import org.webmacro.*;

/**
  * This condition is set with a term value which is already 
  * resolved (ie: not a Macro). It resolves to false if this value
  * is null or Boolean.FALSE, and true otherwise. No macro resolution
  * is performed--this is a utility which is convenient in the 
  * implementation of the DirectiveBuilder and other classes, which
  * helps with static resolution.
  *
  * Note that this class DOES NOT implement Macro
  */
final class ConstantCondition extends Condition {

   private final boolean _value;

   ConstantCondition(Object cond) { 
      _value = isTrue(cond);
   }

   final public boolean test(Context context) {
      return _value;
   }

}
