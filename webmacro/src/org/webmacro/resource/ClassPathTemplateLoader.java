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

import java.net.URL;

/**
 * Implementation of TemplateLoader that loads template from the classpath.
 * The "path" setting is used as a prefix for all request, so it should
 * end with a slash.<br>
 * <br>
 * Example: If you have <pre>&lt;classpath&gt;templates/</pre> in your
 * TemplatePath and request the template "foo/bar.wm", it will search for
 * "templates/foo/bar.wm" in your classpath.
 * @author Sebastian Kanthak (skanthak@muehlheim.de)
 */
public class ClassPathTemplateLoader extends AbstractTemplateLoader {
    private ClassLoader loader;
    private String path;

    public void init(Broker broker,Settings config) throws InitException {
        super.init(broker,config);
        loader = broker.getClassLoader();
    }
    
    public void setConfig(String config) {
        this.path = config;
    }

    public Template load(String query,CacheElement ce) throws ResourceException {
        URL url = loader.getResource(path.concat(query));
        if (url != null && log.loggingDebug()) {
            log.debug("ClassPathTemplateProvider: Found Template "+url.toString());
        }
        return (url != null) ? helper.load(url,ce) : null;
    }
}
