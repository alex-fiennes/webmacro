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

import org.webmacro.*;

import java.io.IOException;

public class EncodeFilter implements Filter
{

    public Filter getFilter (String name)
    {
        return this;
    }

    public Macro getMacro (Macro source)
    {
        return new Encoder(source);
    }

}

class Encoder implements Macro
{

    final private Macro _m;

    public Encoder (Macro m)
    {
        _m = m;
    }

    public Object evaluate (Context c) throws PropertyException
    {
        Object o = _m.evaluate(c);
        return (o != null) ? java.net.URLEncoder.encode(o.toString()) : "";
    }

    public void write (FastWriter out, Context c)
            throws IOException, PropertyException
    {
        out.write(evaluate(c).toString());
    }
}
