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
package org.webmacro.resource;

import org.webmacro.Broker;
import org.webmacro.InitException;
import org.webmacro.Log;
import org.webmacro.util.Settings;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to handle creation of TimedReloadContext.<br>
 * TimedReloadContext objects are used to prevent a cached resource
 * of being checked for modification all the time, but only checks
 * for in given intervals.
 * <br>
 * This class stores a mapping of protocol types to delay values.
 * When a provider requests to decorate a reload context it looks up
 * the delay for this protocol and eventually creates a suitable
 * TimedReloadContext.
 * @author Sebastian Kanthak <sebastian.kanthak@muehlheim.de>
 */
public class ReloadDelayDecorator
{

    /** maps protocol types to Long objects */
    private Map reloadDelays;
    private long defaultDelay;
    private Log log;

    public ReloadDelayDecorator ()
    {
        reloadDelays = new HashMap(11);
    }

    /**
     * Initialize object.
     * Reads config settings from key "CheckForReloadDelay"
     */
    public void init (Broker b, Settings config) throws InitException
    {
        defaultDelay = 0; // no delay
        synchronized (reloadDelays)
        {
            config.processListSetting("CheckForReloadDelay",
                    new Settings.ListSettingHandler()
                    {
                        public void processSetting (String key, String value)
                        {
                            if (key == null || key.length() == 0)
                            {
                                // default reloadDelay
                                defaultDelay = Long.parseLong(value);
                            }
                            else
                            {
                                reloadDelays.put(key, Long.valueOf(value));
                            }
                        }
                    });
        }
        log = b.getLog("resource", "ReloadDelayDecorator");
    }

    /**
     * Looks up the "check for reload delay" for protocol and creates
     * a suitable TimedReloadContext or passes back the original
     * reload context if delay <= 0
     * @param protocol protocol to look up delay for
     * @param reloadContext reloadContext to decorate
     * @return eventually decorated reload context
     */
    public CacheReloadContext decorate (String protocol,
                                        CacheReloadContext reloadContext)
    {
        long delay;
        Long l;
        synchronized (reloadDelays)
        {
            l = (Long) reloadDelays.get(protocol);
        }
        delay = (l != null) ? l.longValue() : defaultDelay;
        if (delay > 0)
        {
            if (log.loggingDebug())
            {
                log.debug("Returning timed reload context with delay " + delay);
            }
            return new TimedReloadContext(reloadContext, delay);
        }
        else
        {
            if (log.loggingDebug())
            {
                log.debug("Returning unmodified reload context");
            }
            return reloadContext;
        }
    }
}
