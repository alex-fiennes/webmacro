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


package org.webmacro.engine;


/**
 * MacroBuilder.java
 *
 * Represents the invocation of a (C-style) macro, which gets expanded
 * during the building of a template.  Not to be confused with Macro
 * as used by WebMacro, which is something else.
 * @author Brian Goetz
 */

public class MacroBuilder implements Builder
{

    private String name;
    private ListBuilder argsBuilder;
    private int lineNo, colNo;
    private String[] nullArgs = new String[0];

    public MacroBuilder (String name,
                         ListBuilder argsBuilder,
                         int lineNo, int colNo)
    {
        this.name = name;
        this.argsBuilder = argsBuilder;
        this.lineNo = lineNo;
        this.colNo = colNo;
    }

    /**
     * Expand the macro.  Gets the macro definition from the build context.
     */
    public Object build (BuildContext bc) throws BuildException
    {
        MacroDefinition md = bc.getMacro(name);
        if (md == null)
        {
            boolean relax = bc.getBroker().getBooleanSetting("RelaxedDirectiveBuilding");
            if (relax)
                return "#" + name + " ";
            else
                throw new BuildException("#" + name + ": no such Macro or Directive");
        }
        Object[] args = argsBuilder.buildAsArray(bc);
        return md.expand(args, bc);
    }
}

