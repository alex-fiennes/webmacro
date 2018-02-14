/*
 * EvalDirective.java Created on May 12, 2003, 2:25 PM Copyright (C) 1998-2003 Semiotek Inc. All
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

import java.util.Map;

import org.webmacro.Context;
import org.webmacro.Macro;
import org.webmacro.PropertyException;
import org.webmacro.TemplateVisitor;
import org.webmacro.engine.BuildContext;
import org.webmacro.engine.BuildException;
import org.webmacro.engine.StringMacro;
import org.webmacro.engine.Variable;

/**
 * Evaluate an instance of a webmacro target.
 * 
 * <pre>
 *   #eval $macroVar
 * or
 *   #eval $macroVar using $mapVar
 * </pre>
 * 
 * @author kkirsch
 */

public class EvalDirective
  extends org.webmacro.directive.Directive
{

  private static final int EVAL_EXPR = 1;
  private static final int EVAL_USING = 2;
  private static final int EVAL_MAP = 3;
  private static final int MAX_RECURSION_DEPTH = 100;

  // private Macro _evalMacro;
  private Object _evalTarget;
  private Object _mapExpr = null;

  private static final ArgDescriptor[] myArgs =
      new ArgDescriptor[] { new RValueArg(EVAL_EXPR), new OptionalGroup(2),
          new KeywordArg(EVAL_USING, "using"), new RValueArg(EVAL_MAP) };

  private static final DirectiveDescriptor myDescr =
      new DirectiveDescriptor("eval", null, myArgs, null);

  public static DirectiveDescriptor getDescriptor()
  {
    return myDescr;
  }

  /** Creates a new instance of EvalDirective. */
  public EvalDirective()
  {
  }

  @Override
  public Object build(DirectiveBuilder builder,
                      BuildContext bc)
      throws BuildException
  {
    _evalTarget = builder.getArg(EVAL_EXPR, bc);
    if (builder.getArg(EVAL_USING) != null) {
      // "using" keyword specified, get map expression
      _mapExpr = builder.getArg(EVAL_MAP, bc);
    }
    // _result = (org.webmacro.engine.Block)builder.getArg(TEMPLET_RESULT, bc);
    return this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void write(org.webmacro.FastWriter out,
                    org.webmacro.Context context)
      throws org.webmacro.PropertyException, java.io.IOException
  {
    try {
      Macro macro = null;

      // The target may be:
      // 1) a variable referencing a Macro
      // 2) a String to be evaluated as a template
      // 3) an expression that evaluates into one of the above?
      Object targ = _evalTarget;
      if (targ instanceof Variable) {
        // if variable, get it's value
        targ = ((Variable) _evalTarget).getValue(context);
      }
      if (targ instanceof String) {
        // convert string to template
        targ = new StringMacro((String) targ);
      }
      if (targ instanceof Macro) {
        macro = (Macro) targ;
      } else {
        // throw and exception ... we don't know what to do with this!
        if (targ == null) {
          throw new PropertyException("null is an invalid argument to #eval directive.");
        }
        throw new PropertyException(String.format("Invalid argument to #eval directive.  \"%s\" is a %s and must be a Macro or a String.",
                                                  targ,
                                                  targ.getClass()));
      }
      if (_mapExpr == null) {
        // no map specified, use current context
        macro.write(out, context);
      } else {
        Object argMapObj = _mapExpr;
        if (argMapObj instanceof Macro) {
          argMapObj = ((Macro) argMapObj).evaluate(context);
        }
        if (!(argMapObj instanceof java.util.Map<?, ?>)) {
          throw new PropertyException("The supplied expression did not evaluate to a java.util.Map instance.");
        }
        // check for max recursion
        int recursionDepth = 0;
        if (context.containsKey("EvalDepth")) { // check the value
          try {
            recursionDepth = ((Integer) context.get("EvalDepth")).intValue();
            recursionDepth++;
          } catch (Exception e) {
            // something bad happend, leave depth at default
          }
          if (recursionDepth > MAX_RECURSION_DEPTH) {
            throw new PropertyException("ERROR: A recursive call to #eval exceeded the maximum depth of "
                                        + MAX_RECURSION_DEPTH);
          }
        }
        java.util.Map<?, ?> outerVars = null;
        if (context.containsKey("OuterVars")) { // check the value
          try {
            outerVars = (java.util.Map<?, ?>) context.get("OuterVars");
          } catch (Exception e) {
            // something bad happened, use vars from calling context
          }
        }
        if (outerVars == null)
          outerVars = context.getMap();
        Context c = new Context(context.getBroker(), (Map<Object, Object>) argMapObj);
        // put current depth into the new context
        c.put("EvalDepth", recursionDepth);
        // add a reference to parent context variables
        c.put("OuterVars", outerVars);
        // add a reference to this macro
        c.put("Self", macro);
        macro.write(out, c);
      }
    } catch (Exception e) {
      if (e instanceof PropertyException)
        throw (PropertyException) e;
      throw new PropertyException("#eval: Unable to evaluate macro.", e);
    }
  }

  @Override
  public void accept(TemplateVisitor v)
  {
    v.beginDirective(myDescr.name);
    v.visitDirectiveArg("EvalTarget", _evalTarget);
    if (_mapExpr != null) {
      v.visitDirectiveArg("EvalKeyword", "using");
      v.visitDirectiveArg("EvalMap", _mapExpr);
    }
    v.endDirective();
  }

}
