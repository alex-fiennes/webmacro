/*
 * Copyright (C) 1998-2000 Semiotek Inc.  All Rights Reserved.  
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted under the terms of either of the following
 * Open Source licenses:
 *
 * The GNU General Public License, version 2, or any later version, as
 * published by the Free Software Foundation
 * (http://www.fsf.org/copyleft/gpl.html);
 *
 *  or 
 *
 * The Semiotek Public License (http://webmacro.org/LICENSE.)  
 *
 * This software is provided "as is", with NO WARRANTY, not even the 
 * implied warranties of fitness to purpose, or merchantability. You
 * assume all risks and liabilities associated with its use.
 *
 * See www.webmacro.org for more information on the WebMacro project.  
 */

package org.webmacro.engine;

import java.io.*;

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
  }

  public void beginBlock() { 
    newLine(); 
    println("#begin"); 
  }

  public void endBlock() {
    newLine();
    println("#end"); 
  }

  public void beginDirective(String directiveName) {
    newLine();
    print("#" + directiveName + " "); 
  }

  public void visitDirectiveArg(String argName, Object o) {
    print(":" + argName + ":");
    if (o instanceof Macro) 
      visitMacro((Macro) o);
    else if (o == null) 
      print("<NULL>");
    else
      print(o.toString());
  }

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
  }

  public void visitUnaryOperation(String opType, Object o) {
    print(opType + "(" + ((o != null)? o.toString() : "<NULL>")
                       + ")");
  }

  public void visitVariable(Variable v, Object[] names) {
    print("$" + v.toString());
  }

  public void visitUnknownMacro(String macroType, Macro m) {
    print("[Unknown macro type " + macroType + "]");
  }

  public static void main(String args[]) throws Exception { 
    WM wm = new WM();
    Context context = wm.getContext();
    WMTemplate t = new StreamTemplate(wm.getBroker(), 
                                      new InputStreamReader(System.in));
    t.parse();
    System.out.println("--------");
    TemplateDumper td = new TemplateDumper();
    t.accept(td);
    System.out.println("--------");
  }
}
