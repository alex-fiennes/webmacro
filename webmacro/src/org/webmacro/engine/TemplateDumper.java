package org.webmacro.engine;

import org.webmacro.*;
import org.webmacro.engine.*;

public class TemplateDumper extends TemplateVisitor {

  private boolean atBol = true;
    
  private void print(String s) {
    System.out.print(s);
    atBol = false;
  }
    
  private void println(String s) {
    System.out.println(s);
    atBol = true;
  }
    
  private void println() {
    System.out.println();
    atBol = true;
  }

  private void newLine() {
    if (!atBol) println();
  }


  public void visitString(String s) { 
    print(s); 
  };

  public void beginBlock() { 
    newLine(); 
    println("#begin"); 
  };

  public void endBlock() {
    newLine();
    println("#end"); 
  };

  public void beginDirective(String directiveName) {
    newLine();
    print("#" + directiveName + " "); 
  };

  public void visitDirectiveArg(String argName, Object o) {
    print(":" + argName + ":");
    if (o instanceof Macro) 
      visitMacro((Macro) o);
    else if (o == null) 
      print("<NULL>");
    else
      print(o.toString());
  };

  public void visitBinaryOperation(String opType, Object l, Object r) {
    print(opType + "(");
    if (l instanceof Macro) 
      visitMacro((Macro) l);
    else 
      print((l != null)? l.toString() : "<NULL>");
    print(", ");
    if (r instanceof Macro) 
      visitMacro((Macro) r);
    else 
      print((r != null)? r.toString() : "<NULL>");
    print(")");
  };

  public void visitUnaryOperation(String opType, Object o) {
    print(opType + "(" + ((o != null)? o.toString() : "<NULL>")
                       + ")");
  }

  public void visitVariable(Variable v, Object[] names) {
    print("$" + v.toString());
  }

  public void visitUnknownMacro(String macroType, Macro m) {
    print("[Unknown macro type " + macroType + "]");
  };
}
