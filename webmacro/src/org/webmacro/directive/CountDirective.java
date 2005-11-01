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

import org.webmacro.Context;
import org.webmacro.FastWriter;
import org.webmacro.Macro;
import org.webmacro.PropertyException;
import org.webmacro.engine.Block;
import org.webmacro.engine.BuildContext;
import org.webmacro.engine.BuildException;
import org.webmacro.engine.Variable;

import java.io.IOException;

/**
 * Allows a template to count
 * according to a range.
 * <pre>
 * #count $i from 1 to 100 [step 1]
 * </pre>
 * @author Eric B. Ridge (ebr@tcdi.com)
 * @since 1.1b1
 */
public class CountDirective extends org.webmacro.directive.Directive
{

    private static final int COUNT_ITERATOR = 1;
    private static final int COUNT_FROM_K = 2;
    private static final int COUNT_START = 3;
    private static final int COUNT_TO_K = 4;
    private static final int COUNT_END = 5;
    private static final int COUNT_STEP_K = 6;
    private static final int COUNT_STEP = 7;
    private static final int COUNT_BODY = 8;

    private static final Directive.ArgDescriptor[] _args = new Directive.ArgDescriptor[]{
        new Directive.LValueArg(COUNT_ITERATOR),
        new Directive.KeywordArg(COUNT_FROM_K, "from"),
        new Directive.RValueArg(COUNT_START),
        new Directive.KeywordArg(COUNT_TO_K, "to"),
        new Directive.RValueArg(COUNT_END),
        new Directive.OptionalGroup(2),
        new Directive.KeywordArg(COUNT_STEP_K, "step"),
        new Directive.RValueArg(COUNT_STEP),
        new Directive.BlockArg(COUNT_BODY),
    };

    private static final DirectiveDescriptor _desc = new DirectiveDescriptor("count", null, _args, null);

    public static DirectiveDescriptor getDescriptor ()
    {
        return _desc;
    }

    private Variable _iterator;
    private Macro _body;
    private Object _objStart;
    private Object _objEnd;
    private Object _objStep;

    private int _start, _end, _step = Integer.MAX_VALUE;

    public Object build (DirectiveBuilder builder, BuildContext bc) throws BuildException
    {
        try
        {
            _iterator = (Variable) builder.getArg(COUNT_ITERATOR, bc);
        }
        catch (ClassCastException e)
        {
            throw new Directive.NotVariableBuildException(_desc.name, e);
        }
        _objStart = builder.getArg(COUNT_START, bc);
        _objEnd = builder.getArg(COUNT_END, bc);
        _objStep = builder.getArg(COUNT_STEP, bc);
        _body = (Block) builder.getArg(COUNT_BODY, bc);

        // attempt to go ahead and force the start, end, and step values into primitive ints
        if (_objStart != null)
        {
            if (_objStart instanceof Number)
            {
                _start = ((Number) _objStart).intValue();
                _objStart = null;
            }
            else if (_objStart instanceof String)
            {
                _start = Integer.parseInt((String) _objStart);
                _objStart = null;
            }
        }
        if (_objEnd != null)
        {
            if (_objEnd instanceof Number)
            {
                _end = ((Number) _objEnd).intValue();
                _objEnd = null;
            }
            else if (_objEnd instanceof String)
            {
                _end = Integer.parseInt((String) _objEnd);
                _objEnd = null;
            }
        }
        if (_objStep != null)
        {
            if (_objStep instanceof Number)
            {
                _step = ((Number) _objStep).intValue();
                _objStep = null;
            }
            else if (_objStep instanceof String)
            {
                _step = Integer.parseInt((String) _objStep);
                _objStep = null;
            }
        }

        return this;
    }

    public void write (FastWriter out, Context context) throws PropertyException, IOException
    {
        int start = _start, end = _end, step = _step;
        Object tmp;

        // if necessary, do run-time evaluation of our
        // start, end, and step objects.
        if ((tmp = _objStart) != null)
        {
            while (tmp instanceof Macro)
                tmp = ((Macro) tmp).evaluate(context);
            if (tmp != null)
                start = Integer.parseInt(tmp.toString());
            else
            {
                writeWarning("#count: Starting value cannot be null.  Not counting", context, out);
                return;
            }
        }

        if ((tmp = _objEnd) != null)
        {
            while (tmp instanceof Macro)
                tmp = ((Macro) tmp).evaluate(context);
            if (tmp != null)
                end = Integer.parseInt(tmp.toString());
            else
            {
                writeWarning("#count: Ending value cannot be null.  Not counting", context, out);
                return;
            }
        }

        if ((tmp = _objStep) != null)
        {
            while (tmp instanceof Macro)
                tmp = ((Macro) tmp).evaluate(context);
            if (tmp != null)
                step = Integer.parseInt(tmp.toString());
            else
            {
                writeWarning("#count: Starting value cannot be null.  Not counting", context, out);
                return;
            }
        }

        // Check if no step explicitly assigned, if so auto-detect it
        if (step == Integer.MAX_VALUE )
        {
            step = (start > end) ? -1 : +1;
        }

        if (step > 0)
        {
            for (; start <= end; start += step)
            {
                _iterator.setValue(context, new Integer(start));
                _body.write(out, context);
            }
        }
        else if (step < 0)
        {
            for (; start >= end; start += step)
            {
                _iterator.setValue(context, new Integer(start));
                _body.write(out, context);
            }
        }
        else
        {
            writeWarning("#count: step cannot be 0.  Not counting", context, out);
        }
    }
}