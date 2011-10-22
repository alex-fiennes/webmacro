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
import org.webmacro.PropertyException;
import org.webmacro.engine.BuildContext;
import org.webmacro.engine.BuildException;
import org.webmacro.engine.Variable;

import java.io.IOException;

/**
 * The #attribute directive allows you to set a template attribute such
 * that it is accessible from the servlet.
 */

public class AttributeDirective extends Directive
{

    private static final int ATTRIBUTE_TARGET = 1;
    private static final int ATTRIBUTE_RESULT = 2;

    private static final ArgDescriptor[]
            myArgs = new ArgDescriptor[]{
                new LValueArg(ATTRIBUTE_TARGET),
                new AssignmentArg(),
                new RValueArg(ATTRIBUTE_RESULT)
            };

    private static final DirectiveDescriptor
            myDescr = new DirectiveDescriptor(null, null, myArgs, null);

    public static DirectiveDescriptor getDescriptor ()
    {
        return myDescr;
    }

    @Override
    public Object build (DirectiveBuilder builder,
                         BuildContext bc)
            throws BuildException
    {
        Variable target = null;
        Object result = null;
        try { 
            target = (Variable) builder.getArg(ATTRIBUTE_TARGET, bc);
            result = builder.getArg(ATTRIBUTE_RESULT, bc);
        }
        catch (ClassCastException e)
        {
            throw new NotVariableBuildException(myDescr.name, e);
        }
        if (!target.isSimpleName())
            throw new NotSimpleVariableBuildException(myDescr.name);

        try
        {
            target.setValue(bc, result);
        }
        catch (PropertyException e)
        {
            throw new BuildException("#attribute: Exception setting variable "
                    + target.toString(), e);
        }
        return null;
    }

    public void write (FastWriter out, Context context)
            throws PropertyException, IOException
    {
    }

}

