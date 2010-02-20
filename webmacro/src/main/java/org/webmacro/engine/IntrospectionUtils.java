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

/**
 *
 * @author  Keats
 * @since May 25, 2002
 */
public final class IntrospectionUtils
{

    /** Deny instantiation */
    private IntrospectionUtils ()
    {
    }

    static boolean matches (Class[] sig, Class[] args)
    {
        if (args.length != sig.length)
            return false;

        try
        {
            for (int i = 0; i < sig.length; i++)
            {
                Class s = sig[i];
                Class a = args[i];
                if (s.isPrimitive())
                {
                    if ((s == Integer.TYPE && a == Integer.class) ||
                            (s == Boolean.TYPE && a == Boolean.class) ||
                            (s == Character.TYPE && a == Character.class) ||
                            (s == Long.TYPE && a == Long.class) ||
                            (s == Short.TYPE && a == Short.class) ||
                            (s == Double.TYPE && a == Double.class) ||
                            (s == Float.TYPE && a == Float.class) ||
                            (s == Void.TYPE && a == Void.class) ||
                            (s == Byte.TYPE && a == Byte.class))
                        continue;
                    else
                        return false;
                }
                else if (a == null || s.isAssignableFrom(a))
                    continue;
                else
                    return false;
            }
        }
        catch (NullPointerException e)
        {
            return false; // XXX: block nulls, isAssignableFrom throws this
        }
        return true;
    }

    static public Class[] createTypesFromArgs (Object[] args)
    {
        Class[] types = new Class[args.length];
        for (int i = 0; i < args.length; i++)
        {
            try
            {
                types[i] = args[i].getClass();
            }
            catch (NullPointerException e)
            {
                types[i] = null;
            }
        }
        return types;
    }

    /** Attempt to instantiate a class with the supplied args. */
    static public Object instantiate (Class c, Object[] args)
            throws Exception
    {
        Object o = null;
        if (args == null || args.length == 0)
        {
            o = c.newInstance(); // no arg constructor
        }
        else
        {
            // try each constructor with the right number of args,
            // until one works or all have failed
            java.lang.reflect.Constructor[] cons = c.getConstructors();
            for (int i = 0; i < cons.length; i++)
            {
                if (cons[i].getParameterTypes().length == args.length)
                {
                    // try to instantiate using this constructor
                    try
                    {
                        o = cons[i].newInstance(args);
                        break; // if successful, we're done!
                    }
                    catch (Exception e)
                    {
                      // ignore this failure, try the next one
                    }
                }
            }
            if (o == null)
            {
                throw new InstantiationException(
                        "Unable to construct object of type " + c.getName()
                        + " using the supplied arguments: "
                        + java.util.Arrays.asList(args).toString());
            }
        }
        return o;
    }
}
