/*
 * Copyright (C) 1998-2001 Semiotek Inc.  All Rights Reserved.  
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


package org.webmacro.resource;
import  org.webmacro.*;
import  org.webmacro.util.TimeLoop;

/**
 * TimedReloadContext acts as an Decorator for Reload context to support
 * cache resources that are expensive to check for change. An example is a
 * resource fetch via the network, but could also be a file.
 * It is constructed with a reference to another CacheReloadContext and an interval.
 * Its shouldReload() method will pass calls to the referenced CacheReloadContext, but
 * will take sure, that this is done only once in the specified interval. In all other
 * cases it will return "false", to indicate, that the resource should not be reloaded.
 * @since 0.96
 * @author skanthak@muehlheim.de
 **/

public class TimedReloadContext extends CacheReloadContext {
    private CacheReloadContext reloadContext;
    private long nextCheck;
    private long checkInterval;
    static volatile long now;
    
    static {
        now = System.currentTimeMillis();
        Thread timer = new Thread() {
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {}
                        now = System.currentTimeMillis();
                    }
                }
            };
        timer.setDaemon(true);
        timer.start();
    }

    /**
     * Construct a new TimedReloadContext decorator.
     * This is just a wrapper object for another CacheReloadContext, ensuring
     * that the shouldReload() method of the refrenced reload context is only
     * called once per checkInterval milliseconds.
     * @param reloadContext reload context to wrap around
     * @param checkInterval interval to check for reload at most in milliseconds
     **/
    public TimedReloadContext(CacheReloadContext reloadContext,long checkInterval) {
        super();
        this.reloadContext = reloadContext;
        this.checkInterval = checkInterval;
        this.nextCheck = now + checkInterval;
    }

    /**
     * Check, whether the underlying resource should be reloaded.
     * This method will simply call the shouldReload() method of the
     * referenced reload context, except when this method was called
     * again in the last checkInterval milliseconds. In this case,
     * this method will simply return false.
     * @return whether resource should be reloaded.
     **/
    public boolean shouldReload() {
        //long time = System.currentTimeMillis();
        if (now >= nextCheck) {
            nextCheck = now + checkInterval;
            return reloadContext.shouldReload();
        } else {
            return false;
        }
    }
}
