/*
 * TempletDirective.java Created on May 12, 2003, 2:25 PM Copyright (C) 1998-2003 Semiotek Inc. All
 * Rights Reserved. Redistribution and use in source and binary forms, with or without modification,
 * are permitted under the terms of either of the following Open Source licenses: The GNU General
 * Public License, version 2, or any later version, as published by the Free Software Foundation
 * (http://www.fsf.org/copyleft/gpl.html); or The Semiotek Public License
 * (http://webmacro.org/LICENSE.) This software is provided "as is", with NO WARRANTY, not even the
 * implied warranties of fitness to purpose, or merchantability. You assume all risks and
 * liabilities associated with its use. See www.webmacro.org for more information on the WebMacro
 * project.
 */

package org.webmacro.directive;

import java.io.IOException;

import org.webmacro.PropertyException;
import org.webmacro.TemplateVisitor;
import org.webmacro.engine.BuildContext;
import org.webmacro.engine.BuildException;
import org.webmacro.engine.Variable;

/**
 * This directive allows a block within a template to be reused as a "templet" that can be invoked
 * using the #eval directive.
 * 
 * @author Keats Kirsch
 * @since June 2003
 * @see org.webmacro.directive.EvalDirective
 */

public class TempletDirective
  extends org.webmacro.directive.Directive
{

  private static final int TEMPLET_TARGET = 1;
  private static final int TEMPLET_RESULT = 2;

  private Variable _target;
  private org.webmacro.engine.Block _result;
  // static private org.webmacro.servlet.TemplateTool _templateTool;

  private static final ArgDescriptor[] myArgs =
      new ArgDescriptor[] { new LValueArg(TEMPLET_TARGET), new BlockArg(TEMPLET_RESULT) };

  private static final DirectiveDescriptor myDescr =
      new DirectiveDescriptor("templet", null, myArgs, null);

  /**
   * Returns the descriptor for this directive.
   * 
   * @return the directive descriptor
   */
  public static DirectiveDescriptor getDescriptor()
  {
    return myDescr;
  }

  /** Creates a new instance of TempletDirective. */
  public TempletDirective()
  {
  }

  /**
   * Build the directive. Parses the block and saves it.
   * 
   * @param builder
   *          The Builder
   * @param bc
   *          The BuildContext
   * @throws BuildException
   *           when directive cannot build its arguments, e.g., when the first argument is not a
   *           valid lval.
   * @return the built directive
   */
  @Override
  public Object build(DirectiveBuilder builder,
                      BuildContext bc)
      throws BuildException
  {
    try {
      _target = (Variable) builder.getArg(TEMPLET_TARGET, bc);
    } catch (ClassCastException e) {
      throw new NotVariableBuildException(myDescr.name, e);
    }
    _result = (org.webmacro.engine.Block) builder.getArg(TEMPLET_RESULT, bc);
    // store the variable name in the block for debugging
    _result.setTemplateName(_target.getVariableName());
    return this;
  }

  /**
   * Do nothing. This directive is completely evaluated at build time.
   * 
   * @param out
   *          the FastWriter
   * @param context
   *          the Context
   * @throws PropertyException
   *           N/A
   * @throws IOException
   *           N/A
   */
  public void write(org.webmacro.FastWriter out,
                    org.webmacro.Context context)
      throws org.webmacro.PropertyException, IOException
  {
    try {
      _target.setValue(context, _result);
    } catch (PropertyException e) {
      throw e;
    } catch (Exception e) {
      String errorText = "#templet: Unable to set " + _target;
      if (!(e instanceof PropertyException)) {
        throw new PropertyException(errorText, e);
      } else
        throw (PropertyException) e;
      // writeWarning(errorText, context, out);
    }
  }

  /**
   * Used by template visitors.
   * 
   * @param v
   *          a template vistor
   */
  @Override
  public void accept(TemplateVisitor v)
  {
    v.beginDirective(myDescr.name);
    v.visitDirectiveArg("TempletTarget", _target);
    v.visitDirectiveArg("TempletValue", _result);
    v.endDirective();
  }

}
