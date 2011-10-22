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


package org.webmacro.servlet;

import org.webmacro.Context;
import org.webmacro.ContextTool;
import org.webmacro.PropertyException;
import org.webmacro.UnsettableException;
import org.webmacro.util.Bag;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * Provide Template with access to Locales.  Also gives access to the static
 * fields e.g., Locale.US
 */
public class LocaleTool extends ContextTool implements Bag
{

    public static final String RCS = "$Id$";

    private static HashMap cache = new HashMap();

    @Override
    public Object init (Context context)
            throws PropertyException
    {
        return this;
    }

    /**
     * return the default locale for this JVM
     */

    final public Locale getDefault ()
    {
        return Locale.getDefault();
    }

    /**
     * wrappers around the 3 constructors for Locale
     */

    final public Locale getLocale (String country)
    {
        return getLocale(country, "", "");
    }

    final public Locale getLocale (String country, String language)
    {
        return getLocale(country, language, "");

    }

    final public Locale getLocale (String country, String language, String variant)
    {
        String key = country + "_" + language + "_" + variant; // language.toUpperCase() ?
        Locale locale = (Locale) cache.get(key);
        if (locale == null)
        {
            locale = new Locale(country, language, "");
            cache.put(key, locale);
        }
        return locale;
    }

    /**
     * access method used by $Locale.xxxxx => LocaleTool.get("xxxxx")
     */
    final public Object get (String field)
    {
        return buildLocale(field);
    }

    /**
     * access to the static members such as Locale.US, etc
     *
     * If that field doesn't exist try to construct a locale
     * from a string so templates can have $Locale.en_GB.
     *
     * This may not be the right thing to do, though, in case of typos
     *
     * e.g., $Locale.ENGLISH => Locale.ENGLISH
     * but   $Locale.ENGLSH => Locale("ENGLSH","","")
     *
     * Could argue that typos aren't caught in the latter anyway
     * (surprisingly?)
     */

    final public static Locale buildLocale (String field)
    {

        Locale locale = (Locale) cache.get(field);
        if (locale == null)
        {
            try
            {
                Field f = Locale.class.getField(field);
                locale = (Locale) f.get(null);
            }
            catch (Exception ne)
            {
                StringTokenizer st = new StringTokenizer(field, "_");
                String[] parts = new String[]{"", "", ""};
                try
                {
                    for (int i = 0; i < 3; i++)
                    {
                        parts[i] = st.nextToken();
                    }
                }
                catch (NoSuchElementException e)
                {
                }
//              System.out.println("Creating Locale: "
//                  +parts[0]+"-"+parts[1]+"-"+parts[2]);
                locale = new Locale(parts[0], parts[1], parts[2]);
            }
//          System.out.println("Returning locale for "+field+" -> "+locale);
            cache.put(field, locale);
        }
        return locale;
    }

    /**
     * Unsupported
     */
    final public void put (String key, Object value)
            throws UnsettableException
    {
        throw new UnsettableException("Cannot set a form property");
    }


    /**
     * Unsupported
     */
    final public void remove (String key)
            throws UnsettableException
    {
        throw new UnsettableException("Cannot unset a form property");
    }

}
