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

import org.webmacro.Context;
import org.webmacro.Macro;
import org.webmacro.PropertyException;


/**
 * Operate on bean properties of an existing macro; used when a Macro
 * is passed as an argument to a macro
 * @author Brian Goetz
 * @since 1.1
 */
public class MacroPropertyVariable extends Variable
{

    private Macro value;

    /**
     * No special initialization
     */
    MacroPropertyVariable (Macro value, Object names[])
    {
        super(names);
        this.value = value;
    }

    /**
     * Look up my value in the corresponding Map, possibly using introspection,
     * and return it
     * @exception PropertyException If the property does not exist
     */
    public final Object getValue (Context context)
            throws PropertyException
    {
        Object v = value.evaluate(context);
        if (v == null)
            throw new PropertyException.NullValueException(_names[0].toString());
        else
            return context.getBroker()
                    ._propertyOperators.getProperty(context, v, _names, 1);
    }

    /**
     * Look up my the value of this variable in the specified Map, possibly
     * using introspection, and set it to the supplied value.
     * @exception PropertyException If the property does not exist
     */
    public final void setValue (Context context, Object newValue)
            throws PropertyException
    {
        throw new PropertyException("Cannot set properties of a constant");
    }

    /**
     * Return a string representation naming the variable for
     * debugging purposes.
     */
    public final String toString ()
    {
        return "macro-property:" + getVariableName();
    }

}
