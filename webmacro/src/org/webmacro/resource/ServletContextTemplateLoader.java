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

import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.ServletContext;

import org.webmacro.Broker;
import org.webmacro.InitException;
import org.webmacro.ResourceException;
import org.webmacro.Template;
import org.webmacro.servlet.ServletBroker;
import org.webmacro.util.Settings;

/**
 * Implementation of TemplateLoader that loads template from web-apps.
 * This will require a broker that is an instance of org.webmacro.servlet.ServletBroker
 * to have access to the ServletContext.
 * <br>
 * Templates will be loaded by ServletContext.getResource().
 * The "path" setting is used as a prefix for all request, so it should
 * end with a slash.<br>
 * <br>
 * Example: If you have <pre>webapp:/WEB-INF/templates/</pre> as a
 * TemplateLoaderPath and request the template "foo/bar.wm", it will search for
 * "WEB-INF/templates/foo/bar.wm" in your web-app directory.
 * <br>
 * This TemplateLoader will automatically add trailing and starting slashed, if they
 * are missing. The starting slash is required by the servlet specification.
 * @author Sebastian Kanthak (sebastian.kanthak@muehlheim.de)
 */
public class ServletContextTemplateLoader extends AbstractTemplateLoader {

   private ServletContext loader;
   private String path;

   public void init(Broker broker, Settings config) throws InitException {
      super.init(broker, config);
      if (broker instanceof ServletBroker) {
         loader = ((ServletBroker) broker).getServletContext();
      }
      else {
         throw new InitException("ServletContextTemplateLoader only works with instances of " +
                                 "org.webmacro.servlet.ServletBroker");
      }
   }

   public void setConfig(String config) {
      // as we'll later use this as a prefix, it should end with a slash
      if (config.length() > 0 && !config.endsWith("/")) {
         if (log.loggingInfo())
            log.info("ServletContextTemplateLoader: appending \"/\" to path " + config);
         config = config.concat("/");
      }

      // the spec says, this has to start with a slash
      if (!config.startsWith("/")) {
         if (log.loggingInfo())
            log.info("ServletContextTemplateLoader: adding \"/\" at the beginning of path " + config);
         config = "/".concat(config);
      }
      this.path = config;
   }

   public Template load(String query, CacheElement ce) throws ResourceException {
      try {
         URL url = loader.getResource(path.concat(query));
         if (url != null && log.loggingDebug()) {
            log.debug("ServletContextTemplateProvider: Found Template " + url.toString());
         }
         return (url != null) ? helper.load(url, ce) : null;
      }
      catch (MalformedURLException e) {
         throw new InvalidResourceException("ServletContextTemplateLoader: Could not load " + query, e);
      }
   }
}
