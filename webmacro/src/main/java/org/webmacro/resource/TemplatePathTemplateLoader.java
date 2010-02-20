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

import org.webmacro.InitException;
import org.webmacro.ResourceException;
import org.webmacro.Template;
import org.webmacro.util.Settings;

import java.io.File;
import java.util.StringTokenizer;

/**
 * Implementation of TemplateLoader that loads templates from a list of
 * paths specified in the TemplatePath setting. This template loader
 * is for compatability with old WebMacro.properties configurations, that
 * still use a TemplatePath setting.
 * @author Sebastian Kanthak (sebastian.kanthak@muehlheim.de)
 */
public class TemplatePathTemplateLoader extends AbstractTemplateLoader
{

    private TemplateLoader[] loaders;

    public void setConfig (String config) throws InitException
    {
        // ignore parameter
        Settings settings = broker.getSettings();
        String templatePath = settings.getSetting("TemplatePath", "");
        log.info("Using legacy template path " + templatePath);
        if (templatePath.length() != 0)
        {
            StringTokenizer st = new StringTokenizer(templatePath, File.pathSeparator);
            loaders = new TemplateLoader[st.countTokens()];
            for (int i = 0; i < loaders.length; i++)
            {
                TemplateLoader loader = new FileTemplateLoader();
                loader.init(broker, settings);
                loader.setConfig(st.nextToken());
                loaders[i] = loader;
            }
        }
    }

    public final Template load (String query, CacheElement ce) throws ResourceException
    {
        if (loaders != null)
        {
            for (int i = 0; i < loaders.length; i++)
            {
                Template t = loaders[i].load(query, ce);
                if (t != null)
                    return t;
            }
        }
        return null;
    }

}
