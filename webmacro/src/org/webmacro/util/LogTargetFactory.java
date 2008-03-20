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

import org.webmacro.Broker;
import org.webmacro.WebMacroException;

import java.lang.reflect.Constructor;

/**
 * The LogTargetFactory assists the Broker (and you, if you want) in creating
 * new LogTarget instances.<p>
 *
 * If your LogTarget needs configuration settings from WebMacro, create a
 * constructor with this signature:
 * <pre>
 *
 *     public MyLogTarget (org.webmacro.util.Settings settings);
 *
 * </pre>
 *
 * If you don't need to configuration options, you should have a
 * null constructor.
 *
 * @author  e_ridge
 * @since 0.99
 */
public final class LogTargetFactory
{

    private static LogTargetFactory _instance = new LogTargetFactory();

    /** Thrown when a log cannot be created.  */
    public static class LogCreationException extends WebMacroException
    {

		private static final long serialVersionUID = 1L;

		public LogCreationException (String message, Throwable throwable)
        {
            super(message, throwable);
        }
    }

    /** Creates new LogTargetFactory. */
    private LogTargetFactory ()
    {
    }

    /** Return the only instance of this LogTargetFactory. */
    public static final LogTargetFactory getInstance ()
    {
        return _instance;
    }

    /**
     * Creates a new <code>org.webmacro.util.LogTarget</code>.
     *
     * @param broker the Broker that is requesting to create the log.  The
     *        Broker is used to find the LogTarget class via the Broker's
     *        <code>.classForName()</code> method.
     * @param classname the fully-qualified classname of the LogTarget to create
     * @param settings WebMacro settings that will be passed off to the
     *        new LogTarget during its construction
     */
    public final LogTarget createLogTarget (Broker broker, String classname, 
            Settings settings) 
        throws LogCreationException
    {
        LogTarget lt = null;
        try
        {
            Class targetClass = broker.classForName(classname);
            Class[] args = new Class[]{Settings.class};
            try
            {
                // attempt to use the constructor that takes a Settings object
                Constructor constr = targetClass.getConstructor(args);
                lt = (LogTarget) constr.newInstance(new Object[]{settings});
            }
            catch (NoSuchMethodException nsme)
            {
                // otherwise, use the default constructor
                lt = (LogTarget) targetClass.newInstance();
            }
        }
        catch (Exception e)
        {
            throw new LogCreationException("Cannot create LogTarget "
                    + classname, e);
        }

        return lt;
    }
}

