
package org.webmacro.engine;
import org.webmacro.*;
/**
  * Utility class
  */
final class EqualConditionBuilder implements Builder {
  
   private final Builder _l,_r;
   EqualConditionBuilder(Builder l, Builder r) { _l = l; _r = r; }

   /**
     * Returns Boolean.TRUE, Boolean.FALSE, or a Condition
     */
   public final Object build(BuildContext pc) throws BuildException
   {
      Object l, r;
      l = _l.build(pc);
      r = _r.build(pc);

      boolean lmacro = (l instanceof Macro);
      boolean rmacro = (r instanceof Macro);

      if (!lmacro && !rmacro) {
         if ((l == null) || (r == null)) {
            return (l == r) ? Boolean.TRUE : Boolean.FALSE;
         } else {
            return (l.equals(r)) ? Boolean.TRUE : Boolean.FALSE; 
         }
      }

      if (lmacro && rmacro) {
         return new EqualCondition((Macro) l, (Macro) r);
      }

      if (lmacro) {
         return new EqualConstantCondition((Macro) l, r);
      } else {
         return new EqualConstantCondition((Macro) r, l);
      }
   }
}


/**
  * Utility class
  */
final class EqualCondition extends Condition implements Macro {

   private final Macro _l,_r;
   EqualCondition(Macro l, Macro r) { _l = l; _r = r; }

   public final boolean test(Context context) {
      Object left, right;
      try { left = _l.evaluate(context); }
      catch (Exception e) { left = null; }

      try { right = _r.evaluate(context); }
      catch (Exception e) { right = null; }

      if ((left == null) || (right == null)) {
         return (left == right);
      }
      return left.equals(right);
   }
}

/**
  * Utility class
  */
final class EqualConstantCondition extends Condition implements Macro {

   private final Macro _l;
   private final Object _r;
   EqualConstantCondition(Macro l, Object r) {
      _l = l; _r = r; 
   }

   public final boolean test(Context context) {
      Object left;
      try { 
         left = _l.evaluate(context); 
      }
      catch (Exception e) { 
         left = null; 
      }

      if ((left == null) || (_r == null)) {
         return (left == _r);
      }
      return left.equals(_r);
   }
}
