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


/**
 * Subclass of CacheableElement used for caching URLs.  By keeping a copy
 * of the URL object and the modification date with the template, the
 * cache manager can check if the resource has changed without having to ask
 * the template provider to look for it again.
 * @since 0.96
 * @author Brian Goetz
 * @see CacheableElement, TemplateProvider
 */
package org.webmacro.resource;

import java.net.*;
import java.io.*;
import org.webmacro.*;

public class UrlTemplateCacheableElement extends CacheableElement {
   long lastModified;
   URL url;

   public UrlTemplateCacheableElement(Object o, URL u, long lastModified) {
      super(o);
      this.lastModified = lastModified;
      this.url = u;
   }

   public boolean shouldReload() {
      return (lastModified != UrlProvider.getUrlLastModified(url));
   }
}

