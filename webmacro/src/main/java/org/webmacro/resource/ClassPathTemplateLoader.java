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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webmacro.Broker;
import org.webmacro.InitException;
import org.webmacro.ResourceException;
import org.webmacro.Template;
import org.webmacro.util.Settings;

import java.net.URL;

/**
 * Implementation of TemplateLoader that loads template from the classpath.
 * The "path" setting is used as a prefix for all request, so it should
 * end with a slash.<br>
 * <br>
 * Example: If you have <pre>classpath:templates/</pre> as a
 * TemplateLoaderPath and request the template "foo/bar.wm", it will search for
 * "templates/foo/bar.wm" in your classpath.
 * @author Sebastian Kanthak (sebastian.kanthak@muehlheim.de)
 */
public class ClassPathTemplateLoader extends AbstractTemplateLoader
{

    static Logger _log =  LoggerFactory.getLogger(ClassPathTemplateLoader.class);
    private ClassLoader loader;
    private String path;

    @Override
    public void init (Broker broker, Settings config) throws InitException
    {
        super.init(broker, config);
        loader = broker.getClassLoader();
    }

    public void setConfig (String config)
    {
        // as we'll later use this as a prefix, it should end with a slash
        if (config.length() > 0 && !config.endsWith("/"))
        {
            _log.info("ClassPathTemplateLoader: appending \"/\" to path " + config);
            config = config.concat("/");
        }

        // It isn't clear from the javadocs, whether ClassLoader.getResource()
        // needs a starting slash, so won't add one at the moment. Even worse,
        // most class-loaders require you to _not have_ a slash, so we'll
        // remove it, if it exists
        if (config.startsWith("/"))
        {
            config = config.substring(1);
        }
        this.path = config;
    }

    public Template load (String query, CacheElement ce) throws ResourceException
    {
        if (query.startsWith("/"))
        {
            query = query.substring(1);
        }
        URL url = loader.getResource(path.concat(query));
        if (url != null && _log.isDebugEnabled())
        {
            _log.debug("ClassPathTemplateProvider: Found Template " + url.toString());
        }
        return (url != null) ? helper.load(url, ce) : null;
    }
}
