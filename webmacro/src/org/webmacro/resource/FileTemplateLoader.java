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
import org.webmacro.engine.FileTemplate;
import java.io.File;

/**
 * Implementation of TemplateLoader that loads templates from a given directory.
 * Objects of this class are responsible for searching exactly one
 * directory for templates. If it handles a request, it takes path as
 * the base path to find the template.
 * @author Sebastian Kanthak (skanthak@muehlheim.de)
 */
public class FileTemplateLoader extends AbstractTemplateLoader {

    /** 
     * ReloadContext for file templates.  Uses last-modified to determine
     * if resource should be reloaded.
     */
    private static class FTReloadContext extends CacheReloadContext {
        private File file;
        private long lastModified;

        public FTReloadContext(File f, long lastModified) {
            this.file = f;
            this.lastModified = lastModified;
        }

        public boolean shouldReload() {
            return (lastModified != file.lastModified());
        }
    }

    /**
     * Tries to load a template by interpreting query as
     * a path relative to the path set by setPath.
     */
    public final Template load(String query,CacheElement ce) {
        File tFile = new File(path,query);
        if (tFile.isFile() && tFile.canRead()) {
            if (log.loggingDebug())
                log.debug("FileTemplateProvider: Found template "+tFile.getAbsolutePath());
            Template template = new FileTemplate(broker,tFile);
            if (ce != null) {
                CacheReloadContext reloadContext = 
                    new FTReloadContext(tFile, tFile.lastModified());
                ce.setReloadContext(reloadDelay.decorate("file",
                                                         reloadContext));
            }
            return template;
        } else {
            return null;
        }
    }
}
