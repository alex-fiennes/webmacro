
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

      boolean lcond = (l instanceof Condition);
      boolean rcond = (r instanceof Condition);

      if (!lcond && !rcond) {
         if ((l == null) || (r == null)) {
            return (l == r) ? Boolean.TRUE : Boolean.FALSE;
         } else {
            return (l.equals(r)) ? Boolean.TRUE : Boolean.FALSE; 
         }
      }

      if (lcond && rcond) {
         return new EqualCondition((Condition) l, (Condition) r);
      }

      if (lcond) {
         return new EqualConstantCondition((Condition) l, r);
      } else {
         return new EqualConstantCondition((Condition) r, l);
      }
   }
}


/**
  * Utility class
  */
final class EqualCondition extends Condition implements Macro {

   private final Condition _l,_r;
   EqualCondition(Condition l, Condition r) { _l = l; _r = r; }

   public final boolean test(Object context) {
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
final class EqualConstantCondition extends Condition {

   private final Condition _l;
   private final Object _r;
   EqualConstantCondition(Condition l, Object r) { _l = l; _r = r; }

   public final boolean test(Object context) {
      Object left;
      try { left = _l.evaluate(context); }
      catch (Exception e) { left = null; }

      if ((left == null) || (_r == null)) {
         return (left == _r);
      }
      return left.equals(_r);
   }
}
