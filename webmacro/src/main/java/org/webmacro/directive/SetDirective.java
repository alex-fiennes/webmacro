/*
 * Copyright (C) 1998-2000 Semiotek Inc. All Rights Reserved. Redistribution and use in source and
 * binary forms, with or without modification, are permitted under the terms of either of the
 * following Open Source licenses: The GNU General Public License, version 2, or any later version,
 * as published by the Free Software Foundation (http://www.fsf.org/copyleft/gpl.html); or The
 * Semiotek Public License (http://webmacro.org/LICENSE.) This software is provided "as is", with NO
 * WARRANTY, not even the implied warranties of fitness to purpose, or merchantability. You assume
 * all risks and liabilities associated with its use. See www.webmacro.org for more information on
 * the WebMacro project.
 */

package org.webmacro.directive;

import java.io.IOException;

import org.webmacro.Context;
import org.webmacro.FastWriter;
import org.webmacro.Macro;
import org.webmacro.PropertyException;
import org.webmacro.TemplateVisitor;
import org.webmacro.engine.BuildContext;
import org.webmacro.engine.BuildException;
import org.webmacro.engine.Variable;

/**
 * Implements the setting of a variable.
 */
public class SetDirective
  extends Directive
{

  private static final int SET_TARGET = 1;
  private static final int SET_RESULT = 2;

  private Variable target;
  private Object result;

  private static final ArgDescriptor[] myArgs =
      new ArgDescriptor[] { new LValueArg(SET_TARGET), new AssignmentArg(),
          new RValueArg(SET_RESULT) };

  private static final DirectiveDescriptor myDescr =
      new DirectiveDescriptor("set", null, myArgs, null);

  public static DirectiveDescriptor getDescriptor()
  {
    return myDescr;
  }

  public SetDirective()
  {
  }

  public SetDirective(Variable target,
                      Object result)
  {
    this.target = target;
    this.result = result;
  }

  @Override
  public Object build(DirectiveBuilder builder,
                      BuildContext bc)
      throws BuildException
  {
    try {
      target = (Variable) builder.getArg(SET_TARGET, bc);
    } catch (ClassCastException e) {
      throw new NotVariableBuildException(myDescr.name, e);
    }
    result = builder.getArg(SET_RESULT, bc);
    return this;
  }

  @Override
  public void write(FastWriter out,
                    Context context)
      throws PropertyException, IOException
  {

    try {
      if (result instanceof Macro)
        target.setValue(context, ((Macro) result).evaluate(context));
      else
        target.setValue(context, result);
    } catch (PropertyException e) {
      throw e;
    } catch (Exception e) {
      String errorText = "#set: Unable to set value: " + target;
      writeWarning(errorText, context, out, e);
    }
  }

  @Override
  public void accept(TemplateVisitor v)
  {
    v.beginDirective(myDescr.name);
    v.visitDirectiveArg("SetTarget", target);
    v.visitDirectiveArg("SetValue", result);
    v.endDirective();
  }

}
