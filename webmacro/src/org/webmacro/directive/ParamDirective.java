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
 * The #param directive is provided for backward compatibility.  
 * Invoking
 *   #param $a="stuff"
 * is equivalent to
 *   #attribute $a="stuff"
 *   #set $a="stuff"
 */

public class ParamDirective extends Directive {

  private static final int PARAM_TARGET = 1;
  private static final int PARAM_RESULT = 2;

  private static final ArgDescriptor[] 
    myArgs = new ArgDescriptor[] {
      new LValueArg(PARAM_TARGET), 
      new AssignmentArg(),
      new RValueArg(PARAM_RESULT)
    };

  private static final DirectiveDescriptor 
    myDescr = new DirectiveDescriptor("param", null, myArgs, null);

  public static DirectiveDescriptor getDescriptor() {
    return myDescr;
  }

  public Object build(DirectiveBuilder builder, 
                      BuildContext bc) 
  throws BuildException {
    Variable target = null;
    Object result = null;
    try {
      target = (Variable) builder.getArg(PARAM_TARGET, bc);
      result = builder.getArg(PARAM_RESULT, bc);
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
    return new SetDirective(target, result);
  }

  public void write(FastWriter out, Context context) 
    throws PropertyException, IOException {
  } 

}

