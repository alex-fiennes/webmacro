package org.webmacro;

import org.webmacro.engine.*;

public abstract class TemplateVisitor {
  public void beginBlock() {};
  public void endBlock() {};

  public void beginDirective(String directiveName) {};
  public void visitDirectiveArg(String argName, Object o) {};
  public void endDirective() {};

  public void visitString(String s) {};

  public void visitBinaryOperation(String opType, Object l, Object r) {};
  public void visitUnaryOperation(String opType, Object o) {};

  public void visitVariable(Variable v, Object[] names) {};

  public void visitUnknownMacro(String macroClass, Macro m) {};

  public final void visitMacro(Macro m) {
    if (m instanceof Visitable) 
      ((Visitable) m).accept(this);
    else 
      visitUnknownMacro(m.getClass().getName(), m);
  }
}
