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
import org.webmacro.FastWriter;
import org.webmacro.Macro;
import org.webmacro.PropertyException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * MapBuilder is used during the parsing/building phase of a template to create
 * user defined Map types, using a syntax that is similar to:<pre>
 *     #set $map = { "key" => "value", "key2" => "value2" }
 * </pre>
 *
 * MapBuilder ultimately creates a Map that understands <code>org.webmacro.Macro</code>'s
 * for <i>keys</i> and <i>values</i>, so $Variable references are okay for both.
 *
 * @author e_ridge
 * @since May 13, 2003
 */
public class MapBuilder extends HashMap implements Builder
{
    public Object build (BuildContext pc) throws BuildException
    {
        Map ret = new HashMap(size());
        boolean isMacro = false;
        for (Iterator itr = entrySet().iterator(); itr.hasNext();)
        {
            Map.Entry entry = (Map.Entry) itr.next();
            Object key = entry.getKey();
            Object value = entry.getValue();

            if (key instanceof Builder)
                key = ((Builder) key).build(pc);
            if (value instanceof Builder)
                value = ((Builder) value).build(pc);

            if (!isMacro)
                isMacro = key instanceof Macro || value instanceof Macro;

            ret.put(key, value);
        }

        if (isMacro)
            return new MapMacro(ret);
        else
            return ret;
    }
}

class MapMacro implements Macro
{
    private final Map _map;

    MapMacro (Map map)
    {
        _map = map;
    }

    public void write (FastWriter out, Context context) throws PropertyException, IOException
    {
        out.write(evaluate(context).toString());
    }

    public String toString ()
    {
        return _map.toString();
    }

    public Object evaluate (Context context) throws PropertyException
    {
        Map ret = new HashMap(_map.size());
        for (Iterator itr = _map.entrySet().iterator(); itr.hasNext();)
        {
            Map.Entry entry = (Map.Entry) itr.next();
            Object key = entry.getKey();
            Object value = entry.getValue();

            if (key instanceof Macro)
                key = ((Macro) key).evaluate(context);
            if (value instanceof Macro)
                value = ((Macro) value).evaluate(context);
            ret.put(key, value);
        }

        return ret;
    }
}
