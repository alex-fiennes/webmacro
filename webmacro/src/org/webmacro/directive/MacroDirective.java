/*
 * Copyright (C) 1998-2001 Semiotek Inc.  All Rights Reserved.
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
import org.webmacro.TemplateVisitor;
import org.webmacro.engine.BuildContext;
import org.webmacro.engine.BuildException;
import org.webmacro.engine.MacroDefinition;

import java.io.IOException;

/**
 * MacroDirective
 *
 * @author Brian Goetz
 */

public class MacroDirective extends Directive
{

    private static final int MACRO_NAME = 1;
    private static final int MACRO_ARGS = 2;
    private static final int MACRO_BODY = 3;

    private static final ArgDescriptor[]
            myArgs = new ArgDescriptor[]{
                new NameArg(MACRO_NAME),
                new FormalArgListArg(MACRO_ARGS),
                new BlockArg(MACRO_BODY)
            };

    private static final DirectiveDescriptor
            myDescr = new DirectiveDescriptor(null, null, myArgs, null);

    public static DirectiveDescriptor getDescriptor ()
    {
        return myDescr;
    }

    public MacroDirective ()
    {
    }

    public Object build (DirectiveBuilder builder,
                         BuildContext bc)
            throws BuildException
    {
        String name = (String) builder.getArg(MACRO_NAME, bc);
        Object[] args = (Object[]) builder.getArg(MACRO_ARGS, bc);
        String[] argNames = new String[args.length];
        for (int i = 0; i < args.length; i++)
            argNames[i] = (String) args[i];
        Object body = builder.getArg(MACRO_BODY);
        MacroDefinition macro = new MacroDefinition(name, argNames, body);
        bc.putMacro(name, macro);
        return null;
    }

    public void write (FastWriter out, Context context)
            throws PropertyException, IOException
    {
    }

    public void accept (TemplateVisitor v)
    {
    }

}
