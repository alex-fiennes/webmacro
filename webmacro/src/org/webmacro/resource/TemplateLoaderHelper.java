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

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

import org.webmacro.*;
import org.webmacro.engine.FileTemplate;
import org.webmacro.engine.StreamTemplate;
import org.webmacro.util.Settings;

/**
 * Helper class for template loaders to actuall load a Template.
 * This class should be used for loading templates from URLs
 * or files. Thus, the template loader only has to care for
 * locating the template.
 * <br>
 * This class will take care of loading the template and setting
 * up a correct reload context, depending on the protocol used.
 * @author Sebastian Kanthak (sebastian.kanthak@muehlheim.de)
 */
public class TemplateLoaderHelper {

   private Broker broker;
   private ReloadDelayDecorator reloadDelay;

   /**
    * Construct a new TemplateLoaderHelper object
    */
   public TemplateLoaderHelper() {
      super();
   }

   /**
    * Initialize this object
    * @param b broker to use
    * @param config configuration to initialize from
    */
   public void init(Broker b, Settings config) throws InitException {
      this.broker = b;
      reloadDelay = new ReloadDelayDecorator();
      reloadDelay.init(b, config);
   }

   /**
    * Load a template from a file.
    * This will load the template from the specified file and
    * set up an appropriate reload context, if ce is not null.
    * It will use the check-for-reload delay specified for protocol
    * file.
    * @param file file to load template from
    * @param ce cache element for reload context or null, if no caching is used.
    * @return loaded template
    * @exception ResourceException if template could not be loaded or parsed.
    */
   public Template load(File file, CacheElement ce) throws ResourceException {
      Template t = new FileTemplate(broker, file);
      parseTemplate(t);
      if (ce != null) {
         CacheReloadContext reloadContext = new FTReloadContext(file, file.lastModified());
         ce.setReloadContext(reloadDelay.decorate("file", reloadContext));
      }
      return t;
   }

   /**
    * Load a template from an url.
    * This will load the template from the specified url and
    * set up an appropriate reload context, if ce is not null.
    * It will use the check-for-reload delay specified for the url's
    * protocol. If protocl is "file", the more efficient file operations
    * of java are used.
    * @param url url to load template from
    * @param ce cache element for reload context or null, if no caching is used.
    * @return loaded template
    * @exception ResourceException if template could not be loaded or parsed.
    */
   public Template load(URL url, CacheElement ce) throws ResourceException {
      if (url.getProtocol().equals("file")) {
         // handle files directly, because it is more efficient
         File file = new File(url.getFile());
         return load(file, ce);
      }
      else {
         try {
            URLConnection conn = url.openConnection();
            long lastMod = conn.getLastModified();
            String encoding = conn.getContentEncoding();
            // encoding may be null. Will be handled by StreamTemplate
            Template t = new StreamTemplate(broker, conn.getInputStream(), encoding);
            t.setName(url.toExternalForm());
            parseTemplate(t);
            if (ce != null) {
               CacheReloadContext reloadContext = new UrlReloadContext(url, lastMod);
               ce.setReloadContext(reloadDelay.decorate(url.getProtocol(), reloadContext));
            }
            return t;
         }
         catch (IOException e) {
            throw new InvalidResourceException("IOException while reading template from " + url, e);
         }
      }
   }

   /**
    * Encapsulates calls to Template.parse and wraps
    * checked exceptions into ResourceExceptions
    */
   private void parseTemplate(Template template) throws ResourceException {
      try {
         template.parse();
      }
      catch (IOException e) {
         throw new InvalidResourceException("IOException while reading template " + template.getName(), e);
      }
      catch (TemplateException e) {
         throw new InvalidResourceException("Error while parsing template: " + template.getName(), e);
      }
   }

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
    * ReloadContext for url templates.  Uses last-modified to determine
    * if resource should be reloaded.
    */
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
}
