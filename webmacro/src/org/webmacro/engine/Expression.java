package org.webmacro.engine;

import java.io.*;
import org.webmacro.*;

public abstract class Expression { 

  public abstract static class ExpressionBase implements Macro {
    protected ExpressionBase() {}

    // Macro methods 
    final public void write(FastWriter out, Context context) 
      throws ContextException, IOException {
      out.write(evaluate(context).toString());
    }
  }

  final private static Boolean TRUE  = Boolean.TRUE;
  final private static Boolean FALSE = Boolean.FALSE;

  public static boolean isTrue(Object o) {
    if (o == null) 
      return false;
    else if (o instanceof Boolean)
      return ((Boolean) o).booleanValue();
    else if (isNumber(o))
      return (numberValue(o) != 0);
    else 
      return (o != null);
  }

  public static boolean isNumber(Object o) {
    return ((o instanceof Integer) 
            || o instanceof Long
            || o instanceof Short
            || o instanceof Byte);
  }

  public static long numberValue(Object o) {
    return ((Number) o).longValue();
  }

  public abstract static class BinaryOperation extends ExpressionBase {

    private Object _l, _r;
    
    BinaryOperation(Object l, Object r) { _l = l; _r = r; }

    public abstract Object operate(Object l, Object r) throws ContextException;

    public Object evaluate(Context context) 
      throws ContextException  {
      Object l, r;

      try { 
        l = (_l instanceof Macro) ? ((Macro) _l).evaluate(context) : _l;
      } catch (Exception e) { l = null; }
      try { 
        r = (_r instanceof Macro) ? ((Macro) _r).evaluate(context) : _r;
      } catch (Exception e) { r = null; }

      return operate(l, r);
    }
  }

  public abstract static class UnaryOperation extends ExpressionBase {

    private Object _o;
    
    UnaryOperation(Object o) { _o = o; }

    public abstract Object operate(Object o);

    public Object evaluate(Context context) 
      throws ContextException {
      Object o = (_o instanceof Macro) ? ((Macro) _o).evaluate(context) : _o;

      return operate(o);
    }
  }

  public static class AndOperation extends BinaryOperation {
    public AndOperation(Object l, Object r) { super(l, r); }

    public Object operate(Object l, Object r) {
      return (isTrue(l) && isTrue(r)) ? TRUE : FALSE;
    }
  }

  public static class OrOperation extends BinaryOperation {
    public OrOperation(Object l, Object r) { super(l, r); }

    public Object operate(Object l, Object r) {
      return (Expression.isTrue(l) || Expression.isTrue(r)) ? TRUE : FALSE;
    }
  }

  public static class NotOperation extends UnaryOperation {
    public NotOperation(Object o) { super(o); }

    public Object operate(Object o) {
      return (!Expression.isTrue(o)) ? TRUE : FALSE;
    }
  }


  public static class AddOperation extends BinaryOperation {
    public AddOperation(Object l, Object r) { super(l, r); }

    public Object operate(Object l, Object r) throws ContextException {
      if (!isNumber(l) || !isNumber(r))
        throw new ContextException("Add requires numeric operands");
      else
        return new Long(numberValue(l) + numberValue(r));
    }
  }

  public static class SubtractOperation extends BinaryOperation {
    public SubtractOperation(Object l, Object r) { super(l, r); }

    public Object operate(Object l, Object r) throws ContextException {
      if (!isNumber(l) || !isNumber(r))
        throw new ContextException("Subtract requires numeric operands");
      else
        return new Long(numberValue(l) - numberValue(r));
    }
  }

  public static class MultiplyOperation extends BinaryOperation {
    public MultiplyOperation(Object l, Object r) { super(l, r); }

    public Object operate(Object l, Object r) throws ContextException {
      if (!isNumber(l) || !isNumber(r))
        throw new ContextException("Multiply requires numeric operands");
      else
        return new Long(numberValue(l) * numberValue(r));
    }
  }

  public static class DivideOperation extends BinaryOperation {
    public DivideOperation(Object l, Object r) { super(l, r); }

    public Object operate(Object l, Object r) throws ContextException {
      if (!isNumber(l) || !isNumber(r))
        throw new ContextException("Divide requires numeric operands");
      else {
        long denom = numberValue(r);
        if (denom == 0)
          throw new ContextException("Divide by zero");
        else 
          return new Long(numberValue(l) / denom);
      }
    }
  }


  public abstract static class Compare extends BinaryOperation {
    public Compare(Object l, Object r) { super(l, r); }
    
    public abstract Boolean compare(String l, String r);
    public abstract Boolean compare(long l, long r);
    public Boolean compare(Object l, Object r) {
      return null;
    }
    public Boolean compareNull(Object o) {
      return null;
    }

    public Object operate(Object l, Object r) throws ContextException {
      Boolean b=null;

      if ((l instanceof String) && (r instanceof String)) 
        b = compare((String) l, (String) r);
      else if (isNumber(l) && isNumber(r))
        b = compare(numberValue(l), numberValue(r));
      else if (l instanceof String && isNumber(r)) 
        b = compare((String) l, Long.toString(numberValue(r)));
      else if (isNumber(l) && r instanceof String) 
        b = compare(Long.toString(numberValue(l)), (String) r);
      else if (l == null) 
        b = compareNull(r);
      else if (r == null) 
        b = compareNull(l);
      else 
        b = compare(l, r);

      if (b == null)
        throw new ContextException("Objects not comparable");
      else 
        return b;
    }
  }

  public static class CompareEq extends Compare {
    public CompareEq(Object l, Object r) { super(l, r); }
    
    public Boolean compare(Object l, Object r) {
      return (l.equals(r)) ? TRUE : FALSE;
    }

    public Boolean compare(String l, String r) {
      return (l.equals(r)) ? TRUE : FALSE;
    }

    public Boolean compare(long l, long r) {
      return (l == r) ? TRUE : FALSE;
    }

    public Boolean compareNull(Object o) {
      return (o == null) ? TRUE : FALSE;
    }
  }

  public static class CompareNe extends Compare {
    public CompareNe(Object l, Object r) { super(l, r); }
    
    public Boolean compare(Object l, Object r) {
      return (l.equals(r)) ? TRUE : FALSE;
    }

    public Boolean compare(String l, String r) {
      return (!l.equals(r)) ? TRUE : FALSE;
    }

    public Boolean compare(long l, long r) {
      return (l != r) ? TRUE : FALSE;
    }

    public Boolean compareNull(Object o) {
      return (o != null) ? TRUE : FALSE;
    }
  }

  public static class CompareLe extends Compare {
    public CompareLe(Object l, Object r) { super(l, r); }
    
    public Boolean compare(String l, String r) {
      return (l.compareTo(r) <= 0) ? TRUE : FALSE;
    }

    public Boolean compare(long l, long r) {
      return (l <= r) ? TRUE : FALSE;
    }
  }

  public static class CompareLt extends Compare {
    public CompareLt(Object l, Object r) { super(l, r); }
    
    public Boolean compare(String l, String r) {
      return (l.compareTo(r) < 0) ? TRUE : FALSE;
    }

    public Boolean compare(long l, long r) {
      return (l < r) ? TRUE : FALSE;
    }
  }

  public static class CompareGe extends Compare {
    public CompareGe(Object l, Object r) { super(l, r); }
    
    public Boolean compare(String l, String r) {
      return (l.compareTo(r) >= 0) ? TRUE : FALSE;
    }

    public Boolean compare(long l, long r) {
      return (l >= r) ? TRUE : FALSE;
    }
  }

  public static class CompareGt extends Compare {
    public CompareGt(Object l, Object r) { super(l, r); }
    
    public Boolean compare(String l, String r) {
      return (l.compareTo(r) > 0) ? TRUE : FALSE;
    }

    public Boolean compare(long l, long r) {
      return (l > r) ? TRUE : FALSE;
    }
  }



  public abstract static class BinaryOperationBuilder implements Builder {

    private Object _l, _r;

    public BinaryOperationBuilder(Object l, Object r) { _l = l; _r = r; }

    public abstract Object build(Object l, Object r) throws BuildException;

    public Object build(BuildContext pc) 
      throws BuildException {
      Object l, r;
      l = (_l instanceof Builder) ? ((Builder) _l).build(pc) : _l;
      r = (_r instanceof Builder) ? ((Builder) _r).build(pc) : _r;
      
      return build(l, r);
    }
  } 

  public abstract static class UnaryOperationBuilder implements Builder {

    private Object _o;;

    public UnaryOperationBuilder(Object o) { _o = o; }

    public abstract Object build(Object o) throws BuildException;

    public Object build(BuildContext pc) 
      throws BuildException {
      Object o;
      o = (_o instanceof Builder) ? ((Builder) _o).build(pc) : _o;
      
      return build(o);
    }
  } 


  public static class AndBuilder extends BinaryOperationBuilder {
    public AndBuilder(Object l, Object r) { super(l,r); }

    public Object build(Object l, Object r) {
      if (l instanceof Macro) {
        if (r instanceof Macro) 
          return new AndOperation(l, r);
        else 
          return isTrue(r) ? l : FALSE;
      }
      else {
        if (r instanceof Macro)
          return isTrue(l) ? r : FALSE;
        else 
          return (isTrue(l) && isTrue(r)) ? TRUE : FALSE;
      }
    }
  }

  public static class OrBuilder extends BinaryOperationBuilder {
    public OrBuilder(Object l, Object r) { super(l,r); }

    public Object build(Object l, Object r) {
      if (l instanceof Macro) {
        if (r instanceof Macro) 
          return new OrOperation(l, r);
        else 
          return isTrue(r) ? TRUE : l;
      }
      else {
        if (r instanceof Macro)
          return isTrue(l) ? TRUE : r;
        else 
          return (isTrue(l) || isTrue(r)) ? TRUE : FALSE;
      }
    }
  }

  public static class NotBuilder extends UnaryOperationBuilder {
    public NotBuilder(Object o) { super(o); }

    public Object build(Object o) {
      if (o instanceof Macro) 
        return new NotOperation(o);
      else 
        return !isTrue(o) ? TRUE : FALSE;
    }
  }



  public static class AddBuilder extends BinaryOperationBuilder {
    public AddBuilder(Object l, Object r) { super(l,r); }

    public Object build(Object l, Object r) throws BuildException {
      if (!(l instanceof Macro) && !(r instanceof Macro)) {
        if (isNumber(l) && isNumber(r))
          return new Long(numberValue(l) + numberValue(r));
        else
          throw new BuildException("Add requires numeric operands");
      }
      else
        return new AddOperation(l, r);
    }
  }

  public static class SubtractBuilder extends BinaryOperationBuilder {
    public SubtractBuilder(Object l, Object r) { super(l,r); }

    public Object build(Object l, Object r) throws BuildException {
      if (!(l instanceof Macro) && !(r instanceof Macro)) {
        if (isNumber(l) && isNumber(r))
          return new Long(numberValue(l) - numberValue(r));
        else
          throw new BuildException("Subtract requires numeric operands");
      }
      else
        return new SubtractOperation(l, r);
    }
  }

  public static class MultiplyBuilder extends BinaryOperationBuilder {
    public MultiplyBuilder(Object l, Object r) { super(l,r); }

    public Object build(Object l, Object r) throws BuildException {
      if (!(l instanceof Macro) && !(r instanceof Macro)) {
        if (isNumber(l) && isNumber(r))
          return new Long(numberValue(l) * numberValue(r));
        else
          throw new BuildException("Multiply requires numeric operands");
      }
      else
        return new MultiplyOperation(l, r);
    }
  }

  public static class DivideBuilder extends BinaryOperationBuilder {
    public DivideBuilder(Object l, Object r) { super(l,r); }

    public Object build(Object l, Object r) throws BuildException {
      if (!(l instanceof Macro) && !(r instanceof Macro)) {
        if (isNumber(l) && isNumber(r)) {
          long denom = numberValue(r);
          if (denom == 0)
            throw new BuildException("Divide by zero");
          else 
            return new Long(numberValue(l) / denom);
        }
        else
          throw new BuildException("Divide requires numeric operands");
      }
      else
        return new DivideOperation(l, r);
    }
  }

  public static class CompareEqBuilder extends BinaryOperationBuilder {
    public CompareEqBuilder(Object l, Object r) { super(l,r); }

    public Object build(Object l, Object r) throws BuildException {
      CompareEq c = new CompareEq(l, r);

      if ((l instanceof Macro) || (r instanceof Macro))
        return c;
      else 
        try {
          return c.operate(l, r);
        }
        catch (ContextException e) { throw new BuildException(e.toString()); }
    }
  }

  public static class CompareNeBuilder extends BinaryOperationBuilder {
    public CompareNeBuilder(Object l, Object r) { super(l,r); }

    public Object build(Object l, Object r) throws BuildException {
      CompareNe c = new CompareNe(l, r);

      if ((l instanceof Macro) || (r instanceof Macro))
        return c;
      else 
        try {
          return c.operate(l, r);
        }
        catch (ContextException e) { throw new BuildException(e.toString()); }
    }
  }

  public static class CompareLeBuilder extends BinaryOperationBuilder {
    public CompareLeBuilder(Object l, Object r) { super(l,r); }

    public Object build(Object l, Object r) throws BuildException {
      CompareLe c = new CompareLe(l, r);

      if ((l instanceof Macro) || (r instanceof Macro))
        return c;
      else 
        try {
          return c.operate(l, r);
        }
        catch (ContextException e) { throw new BuildException(e.toString()); }
    }
  }

  public static class CompareLtBuilder extends BinaryOperationBuilder {
    public CompareLtBuilder(Object l, Object r) { super(l,r); }

    public Object build(Object l, Object r) throws BuildException {
      CompareLt c = new CompareLt(l, r);

      if ((l instanceof Macro) || (r instanceof Macro))
        return c;
      else 
        try {
          return c.operate(l, r);
        }
        catch (ContextException e) { throw new BuildException(e.toString()); }
    }
  }

  public static class CompareGeBuilder extends BinaryOperationBuilder {
    public CompareGeBuilder(Object l, Object r) { super(l,r); }

    public Object build(Object l, Object r) throws BuildException {
      CompareGe c = new CompareGe(l, r);

      if ((l instanceof Macro) || (r instanceof Macro))
        return c;
      else 
        try {
          return c.operate(l, r);
        }
        catch (ContextException e) { throw new BuildException(e.toString()); }
    }
  }

  public static class CompareGtBuilder extends BinaryOperationBuilder {
    public CompareGtBuilder(Object l, Object r) { super(l,r); }

    public Object build(Object l, Object r) throws BuildException {
      CompareGt c = new CompareGt(l, r);

      if ((l instanceof Macro) || (r instanceof Macro))
        return c;
      else 
        try {
          return c.operate(l, r);
        }
        catch (ContextException e) { throw new BuildException(e.toString()); }
    }
  }


}
