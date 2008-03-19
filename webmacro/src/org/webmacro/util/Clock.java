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

import java.util.Date;

/**
 * This is an optimization. "System.currentTimeMillis()" is a relatively
 * slow method, and "new Date()" is an incredibly expensive operation.
 * 
 * Update: System.ctm is no longer all that slow; replaced with a version
 * that caches the Date but not the time.
 */
final public class Clock
{

    /** Disallow instantiation.  */
    private Clock()
    {
    }

    /**
     * Every tick interval the following variable is updated with the current system time.
     */
    static public long TIME = System.currentTimeMillis();

    /**
     * Date information.
     */
    private static Date date = new Date();

    /**
     * The current date. This object is updated not faster than once per second.
     */
    public synchronized static Date getDate ()
    {
        long time = System.currentTimeMillis();
        if ((TIME - time) > 1000) {
            TIME = time;
            date = new Date(TIME);
        }
        return date;
    }

}


