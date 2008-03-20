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


package org.webmacro;

import org.webmacro.broker.ContextObjectFactory;

/**
 * This class is used as a base class for legacy context tools so they can 
 * fit into the ContextObjectFactory framework.
 */
public abstract class ContextTool implements ContextObjectFactory
{

    /**
     * A new tool object will be instantiated per-request by calling
     * this method. A ContextTool is effectively a factory used to
     * create objects for use in templates. Some tools may simply return
     * themselves from this method; others may instantiate new objects
     * to hold the per-request state.
     */
    public abstract Object init (Context c) throws PropertyException;

    public Object get (Context c) throws PropertyException {
        return init(c);
    }
}
