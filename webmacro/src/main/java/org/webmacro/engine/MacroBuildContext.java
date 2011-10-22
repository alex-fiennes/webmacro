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


package org.webmacro.engine;

import org.webmacro.PropertyException;

import java.util.HashMap;
import java.util.Map;

/**
 * A chained build context, for use in expanding macros.
 * When called upon to evaluate a reference which is one of the macros
 * formals, it evaluates the actual argument and returns that, otherwise
 * it chains back to the build context.
 */
public class MacroBuildContext extends BuildContext
{

    private BuildContext chainedContext, rootContext;
    private MacroDefinition macro;
    private Object[] args;
    private Map macroArgs = new HashMap();

    public MacroBuildContext (MacroDefinition macroP,
                              Object[] argsP,
                              BuildContext bc)
    {
        super(bc.getBroker());
        this.chainedContext = bc;
        this.args = argsP;
        this.macro = macroP;
        String[] argNames = macro.getArgNames();
        for (int i = 0; i < argNames.length; i++)
            macroArgs.put(argNames[i], args[i]);

        rootContext = chainedContext;
        while (rootContext instanceof MacroBuildContext)
            rootContext = ((MacroBuildContext) rootContext).chainedContext;
    }

    public BuildContext getRootContext ()
    {
        return rootContext;
    }

    @Override
    public void putMacro (String name, MacroDefinition macro)
    {
        rootContext.putMacro(name, macro);
    }

    @Override
    public MacroDefinition getMacro (String name)
    {
        return rootContext.getMacro(name);
    }

    @Override
    public boolean containsKey (Object name)
    {
        return macroArgs.containsKey(name) || chainedContext.containsKey(name);
    }

    @Override
    protected Object internalGet (Object name) throws PropertyException
    {
        if (macroArgs.containsKey(name))
            return macroArgs.get(name);
        else
        {
            return rootContext.get(name);
        }
    }
}

