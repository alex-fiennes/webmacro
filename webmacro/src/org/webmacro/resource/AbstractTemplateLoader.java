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

import org.webmacro.*;
import org.webmacro.util.Settings;

/**
 * Abstract implementation of TemplateLoader, that stores broker
 * and path and provides a ReloadDelayDecorator and a common log
 * object.
 * @author Sebastian Kanthak <skanthak@muehlheim.de>
 */
public abstract class AbstractTemplateLoader implements TemplateLoader {
    /**
     * Reload delay decorator to create reload context,
     * that limit checks for reload based on protocol.
     */
    protected ReloadDelayDecorator reloadDelay;
    
    /** Our broker */
    protected Broker broker;

    /** Path to search for templates */
    protected String path;

    /** Log to use */
    protected Log log;

    /**
     * Sets up broker, reloadDelayDecorator and log.
     * Don't forget to call super.init() if you override this
     * method.
     */
    public void init(Broker b,Settings config) throws InitException {
        this.broker = b;
        reloadDelay = new ReloadDelayDecorator();
        reloadDelay.init(b,config);
        log = b.getLog("resource","Loading templates");
    }

    /**
     * Sets the path where to search for templates
     */
    public void setPath(String value) {
        this.path = value;
    }
}
