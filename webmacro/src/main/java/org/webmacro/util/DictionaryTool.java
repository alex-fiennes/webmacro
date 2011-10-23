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


package org.webmacro.util;

import java.util.Dictionary;
import java.util.Enumeration;

/**
 * A HashTool wraps a java.util.Dictionary and provides additional features
 */
@Deprecated
final public class DictionaryTool<K,V> extends Dictionary<K,V>
{


    /**
     * Return a string which concatenates the keys, putting commas
     * between the keys
     */
    public final String keyString ()
    {
        final Enumeration<K> keys = dict.keys();
        String key = null;
        StringBuffer buf = new StringBuffer(10 * dict.size());
        while (keys.hasMoreElements())
        {
            if (key != null)
            {
                buf.append(", ");
            }
            key = (keys.nextElement()).toString();
            buf.append(key);
        }
        return buf.toString();
    }

    /**
     * The dictionary being wrapped
     */
    final private Dictionary<K,V> dict;

    /**
     * Wrap the supplied dictionary.
     */
    public DictionaryTool (final Dictionary<K,V> dict)
    {
        this.dict = dict;
    }

    /**
     * Forward call to Dictionary
     */
    @Override
    final public Enumeration<V> elements ()
    {
        return dict.elements();
    }

    /**
     * Forward call to Dictionary
     */
    @Override
    final public boolean isEmpty ()
    {
        return dict.isEmpty();
    }

    /**
     * Forward call to Dictionary
     */
    @Override
    final public V get (final Object key)
    {
        return dict.get(key);
    }

    /**
     * Forward call to Dictionary
     */
    @Override
    final public Enumeration<K> keys ()
    {
        return dict.keys();
    }

    /**
     * Forward call to Dictionary
     */
    @Override
    final public V put (final K newKey, final V newValue)
    {
        return dict.put(newKey, newValue);
    }

    /**
     * Forward call to Dictionary
     */
    @Override
    final public V remove (final Object key)
    {
        return dict.remove(key);
    }

    /**
     * Forward call to Dictionary
     */
    @Override
    final public int size ()
    {
        return dict.size();
    }

    static public void main (String arg[])
    {

        Dictionary<Object,Object> d = new java.util.Hashtable<Object,Object>();

        System.out.println("Adding arguments to hashtable.");
        for (int i = 0; i < arg.length; i++)
        {
            d.put(arg[i], "argument " + i);
        }

        System.out.println("Wrapping hashtable");
        DictionaryTool<Object,Object> dt = new DictionaryTool<Object,Object>(d);

        System.out.println("keyString: " + dt.keyString());

        System.out.println("Done.");
    }

}
