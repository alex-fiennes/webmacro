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

package org.webmacro.directive;

import java.io.*;
import org.webmacro.*;
import org.webmacro.engine.*;

/**
 * The #const directive sets a value at compile time
 * Invoking
 *   #const $a=expression
 * defines $a in the build context as (expression).  When the parser
 * encounters $a in the template, it will replace it with the value of
 * expression.  The right side can be a macro, in which case $a will
 * be an alias for that macro.
 * @author Brian Goetz
 * @since 0.98 */

public class ConstDirective extends Directive {

  private static final int CONST_TARGET = 1;
  private static final int CONST_RESULT = 2;

  private static final ArgDescriptor[] 
    myArgs = new ArgDescriptor[] {
      new LValueArg(CONST_TARGET), 
      new AssignmentArg(),
      new RValueArg(CONST_RESULT)
    };

  private static final DirectiveDescriptor 
    myDescr = new DirectiveDescriptor("const", null, myArgs, null);

  public static DirectiveDescriptor getDescriptor() {
    return myDescr;
  }

  public Object build(DirectiveBuilder builder, 
                      BuildContext bc) 
  throws BuildException {
    Variable target = null;
    Object result = null;
    try {
      target = (Variable) builder.getArg(CONST_TARGET, bc);
      result = builder.getArg(CONST_RESULT, bc);
      if (!target.isSimpleName()) 
        throw new NotSimpleVariableBuildException(myDescr.name);

      target.setValue(bc, result);
    }
    catch (ClassCastException e) {
      throw new NotVariableBuildException(myDescr.name, e);
    }
    catch (PropertyException e) {
      throw new BuildException("#param: Exception setting variable " 
                               + target.toString(), e);
    }
    return null;
  }

  public void write(FastWriter out, Context context) 
    throws PropertyException, IOException {
  } 

}

