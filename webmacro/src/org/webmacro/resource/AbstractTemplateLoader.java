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
    /** Our broker */
    protected Broker broker;

    /** Log to use */
    protected Log log;

    /** Helper class for loading templates from files or URLs */
    protected TemplateLoaderHelper helper;

    /**
     * Sets up broker, reloadDelayDecorator and log.
     * Don't forget to call super.init() if you override this
     * method.
     */
    public void init(Broker b,Settings config) throws InitException {
        this.broker = b;
        log = b.getLog("resource","Loading templates");
        helper = new TemplateLoaderHelper();
        helper.init(b,config);
    }
}
