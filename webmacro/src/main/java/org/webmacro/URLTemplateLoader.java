/*
 * Copyright (C) 1998-2000 Semiotek Inc. All Rights Reserved. Redistribution and use in source and
 * binary forms, with or without modification, are permitted under the terms of either of the
 * following Open Source licenses: The GNU General Public License, version 2, or any later version,
 * as published by the Free Software Foundation (http://www.fsf.org/copyleft/gpl.html); or The
 * Semiotek Public License (http://webmacro.org/LICENSE.) This software is provided "as is", with NO
 * WARRANTY, not even the implied warranties of fitness to purpose, or merchantability. You assume
 * all risks and liabilities associated with its use. See www.webmacro.org for more information on
 * the WebMacro project.
 */

package org.webmacro;

import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webmacro.resource.AbstractTemplateLoader;
import org.webmacro.resource.CacheElement;

/**
 * Implementation of TemplateLoader that loads templates from a given URL. Objects of this class are
 * responsible for searching exactly one directory for templates. If it handles a request, it takes
 * URL as the base path to find the template.
 * 
 * @author Marc Palmer (marc@anyware.co.uk)
 */
public class URLTemplateLoader
  extends AbstractTemplateLoader
{

  static Logger LOGGER = LoggerFactory.getLogger(URLTemplateLoader.class);
  private URL _baseURI;

  @Override
  public void setConfig(String config)
  {
    try {
      _baseURI = new URL(config);
    } catch (MalformedURLException e) {
      LOGGER.error("Cannot init url template loader, bad URL", e);
    }
  }

  /**
   * Tries to load a template by interpreting query as a path relative to the path set by setPath.
   */
  @Override
  public final Template load(String query,
                             CacheElement ce)
      throws ResourceException
  {
    try {
      URL url = new URL(_baseURI, query);
      LOGGER.debug("FileTemplateProvider: Found template " + url);
      return helper.load(url, ce);
    } catch (MalformedURLException e) {
      throw new ResourceException("Bad template URL", e);
    }
  }
}
