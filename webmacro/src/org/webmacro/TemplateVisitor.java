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

package org.webmacro;

import org.webmacro.engine.*;

public abstract class TemplateVisitor {
  public void beginBlock() {}
  public void endBlock() {}

  public void beginDirective(String directiveName) {}
  public void visitDirectiveArg(String argName, Object o) {}
  public void endDirective() {}

  public void visitString(String s) {}

  public void visitBinaryOperation(String opType, Object l, Object r) {}
  public void visitUnaryOperation(String opType, Object o) {}

  public void visitVariable(Variable v, Object[] names) {}

  public void visitUnknownMacro(String macroClass, Macro m) {}

  public final void visitMacro(Macro m) {
    if (m instanceof Visitable) 
      ((Visitable) m).accept(this);
    else 
      visitUnknownMacro(m.getClass().getName(), m);
  }
}
