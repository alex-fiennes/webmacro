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

/**
 * This interface is used to attach utilities to a context to assist
 * with the generation of views.
 */
public interface ContextTool
{

    /**
     * A new tool object will be instantiated per-request by calling
     * this method. A ContextTool is effectively a factory used to
     * create objects for use in templates. Some tools may simply return
     * themselves from this method; others may instantiate new objects
     * to hold the per-request state.
     */
    public Object init (Context c) throws PropertyException;

}
