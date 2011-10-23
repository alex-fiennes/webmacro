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

import org.webmacro.Context;
import org.webmacro.FastWriter;
import org.webmacro.Macro;
import org.webmacro.PropertyException;
import org.webmacro.engine.Block;
import org.webmacro.engine.BuildContext;
import org.webmacro.engine.BuildException;
import org.webmacro.engine.UndefinedMacro;
import org.webmacro.engine.Variable;

import java.io.IOException;

/**
 * Syntax:
 * 
 * <pre>
 * #count $i from 1 to 100 [step 1]
 * </pre>
 * 
 * @author Eric B. Ridge (ebr@tcdi.com)
 * @since 1.1b1
 */
public class CountDirective
  extends Directive
{

  private static final int COUNT_ITERATOR = 1;
  private static final int COUNT_FROM_K = 2;
  private static final int COUNT_START = 3;
  private static final int COUNT_TO_K = 4;
  private static final int COUNT_END = 5;
  private static final int COUNT_STEP_K = 6;
  private static final int COUNT_STEP = 7;
  private static final int COUNT_BODY = 8;

  private static final Directive.ArgDescriptor[] _args =
      new Directive.ArgDescriptor[] { new Directive.LValueArg(COUNT_ITERATOR),
          new Directive.KeywordArg(COUNT_FROM_K, "from"), new Directive.RValueArg(COUNT_START),
          new Directive.KeywordArg(COUNT_TO_K, "to"), new Directive.RValueArg(COUNT_END),
          new Directive.OptionalGroup(2), new Directive.KeywordArg(COUNT_STEP_K, "step"),
          new Directive.RValueArg(COUNT_STEP), new Directive.BlockArg(COUNT_BODY), };

  private static final DirectiveDescriptor _desc =
      new DirectiveDescriptor("count", null, _args, null);

  public static DirectiveDescriptor getDescriptor()
  {
    return _desc;
  }

  private Variable _iterator;
  private Macro _body;
  private Object _objStart;
  private Object _objEnd;
  private Object _objStep;

  private int _start, _end, _step = Integer.MAX_VALUE;

  @Override
  public Object build(DirectiveBuilder builder,
                      BuildContext bc)
      throws BuildException
  {
    try {
      _iterator = (Variable) builder.getArg(COUNT_ITERATOR, bc);
    } catch (ClassCastException e) {
      throw new Directive.NotVariableBuildException(_desc.name, e);
    }
    _objStart = builder.getArg(COUNT_START, bc);
    _objEnd = builder.getArg(COUNT_END, bc);
    _objStep = builder.getArg(COUNT_STEP, bc);
    _body = (Block) builder.getArg(COUNT_BODY, bc);

    // attempt to go ahead and force the start, end, and step values
    // into primitive ints
    if (_objStart != null) {
      if (_objStart instanceof Number) {
        _start = ((Number) _objStart).intValue();
        _objStart = null;
      } else if (_objStart instanceof String) {
        _start = Integer.parseInt((String) _objStart);
        _objStart = null;
      }
    }
    if (_objEnd != null) {
      if (_objEnd instanceof Number) {
        _end = ((Number) _objEnd).intValue();
        _objEnd = null;
      } else if (_objEnd instanceof String) {
        _end = Integer.parseInt((String) _objEnd);
        _objEnd = null;
      }
    }
    if (_objStep != null) {
      if (_objStep instanceof Number) {
        _step = ((Number) _objStep).intValue();
        _objStep = null;
      } else if (_objStep instanceof String) {
        _step = Integer.parseInt((String) _objStep);
        _objStep = null;
      }
    }

    return this;
  }

  public void write(FastWriter out,
                    Context context)
      throws PropertyException, IOException
  {
    int start = _start, end = _end, step = _step;
    boolean error = false;

    // if necessary, do run-time evaluation of our
    // start, end, and step objects.
    // If object is null then it has been fully resolved during build.
    try {
      if (_objStart != null)
        start = evalAsInt(context, _objStart);
      if (_objEnd != null)
        end = evalAsInt(context, _objEnd);
      if (_objStep != null)
        step = evalAsInt(context, _objStep);
    } catch (Exception e) {
      out.write(context.getEvaluationExceptionHandler().expand(_iterator, context, e));
      error = true;
    }

    if (!error) {
      // Check if no step explicitly assigned, if so auto-detect it
      if (step == Integer.MAX_VALUE) {
        step = (start > end) ? -1 : +1;
      }

      if (step > 0) {
        for (; start <= end; start += step) {
          _iterator.setValue(context, new Integer(start));
          _body.write(out, context);
        }
      } else if (step < 0) {
        for (; start >= end; start += step) {
          _iterator.setValue(context, new Integer(start));
          _body.write(out, context);
        }
      } else {
        PropertyException pe = new PropertyException.UndefinedVariableException();
        pe.setMessage("#count: step cannot be 0.");
        out.write(context.getEvaluationExceptionHandler().expand(_iterator, context, pe));
      }
    }
  }

  private int evalAsInt(Context context,
                        Object o)
      throws PropertyException
  {
    if (o != null) {
      while (o instanceof Macro && !(o == UndefinedMacro.getInstance())) {
        o = ((Macro) o).evaluate(context);
      }
      if (o == UndefinedMacro.getInstance()) {
        PropertyException pe = new PropertyException.UndefinedVariableException();
        pe.setMessage("Undefined expression in #count directive.");
        throw pe;
      }
    }
    if (o == null) {
      PropertyException pe = new PropertyException.UndefinedVariableException();
      pe.setMessage("Null expression in #count directive.");
      throw pe;
    }
    if (o instanceof Number)
      return ((Number) o).intValue();
    try {
      return Integer.parseInt(o.toString());
    } catch (NumberFormatException e) {
      PropertyException pe = new PropertyException.UndefinedVariableException();
      pe.setMessage("Invalid numeric expression in #count directive: [" + o + "]");
      throw pe;
    }
  }
}
