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
import org.webmacro.engine.StreamTemplate;

import java.net.URL;
import java.io.IOException;

/**
 * Abstract implementation of TemplateLoader to fetch Templates from URLs.
 * Non-abstract subclasses still have to implement load(), to create the
 * actual URL to load the template from.
 * @author Sebastian Kanthak (skanthak@muehlheim.de)
 */
public abstract class URLBasedTemplateLoader extends AbstractTemplateLoader {
    private static class UrlReloadContext extends CacheReloadContext { 
        private long lastModified;
        private URL url;

        public UrlReloadContext(URL url, long lastModified) {
            this.url = url;
            this.lastModified = lastModified;
        }

        public boolean shouldReload() {
            return (lastModified != UrlProvider.getUrlLastModified(url));
        }
    }

    /**
     * Loads a template form URL and provides a cache reload context for it
     */
    protected Template load(URL url,CacheElement ce) throws ResourceException {
        try {
            long lastMod = UrlProvider.getUrlLastModified(url);
            String encoding = url.openConnection().getContentEncoding();
            // encoding may be null. Will be handled by StreamTemplate
            Template t = new StreamTemplate(broker, 
                                            UrlProvider.getUrlInputStream(url),
                                            encoding);
            if (ce != null) {
                CacheReloadContext reloadContext = new UrlReloadContext(url, lastMod);
                ce.setReloadContext(reloadDelay.decorate(url.getProtocol(),
                                                         reloadContext));
            }
            return t;
        } catch (IOException e) {
            log.warning("URLBasedTemplateLoader: IOException while loading from "
                        + url, e);
            throw new InvalidResourceException("Error loading template from "+url,
                                               e);
        }
    }
}
