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

import org.webmacro.ResourceException;
import org.webmacro.Template;

import java.net.URL;

/**
 * @author sk
 * @since  03 Feb 2002 
 */
public class BrokerTemplateLoader extends AbstractTemplateLoader
{

    public void setConfig (String config)
    {
        // ignore config
        if (config != null && config.length() > 0)
        {
            if (log.loggingWarning())
            {
                log.warning("BrokerTemplateProvider: Ignoring configuration options " + config);
            }
        }
    }

    public final Template load (String query, CacheElement ce) throws ResourceException
    {
        URL url = broker.getTemplate(query);
        if (url != null && log.loggingDebug())
        {
            log.debug("BrokerTemplateLoader: Found Template " + url.toString());
        }
        return (url != null) ? helper.load(url, ce) : null;
    }
}
