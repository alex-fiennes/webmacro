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

package org.webmacro.resource;

import org.webmacro.Broker;
import org.webmacro.InitException;
import org.webmacro.ResourceException;
import org.webmacro.Template;
import org.webmacro.util.Settings;

/**
 * Interface for template loaders. Template loaders are responsible to search for and load templates
 * from a single location. They are used by DelegatingTemplateProvider to do the actual work.
 * 
 * @author Sebastian Kanthak (sebastian.kanthak@muehlheim.de)
 */
public interface TemplateLoader
{

  /**
   * Init this template loader*
   */
  void init(Broker b,
            Settings settings)
      throws InitException;

  /**
   * Set the config options for this template loader. The config option is the path after the colon
   * (":") in the TemplatePath setting for this loader.<br>
   * This can be an path as well as JDBC settings or something completely different.
   * 
   * @param config
   *          config options for this template loader
   */
  void setConfig(String config)
      throws InitException;

  /**
   * Try to load a template. This method will create and return a template found in the location
   * described by query or return null, if no such template exists. If a resource is found at the
   * location, but no template could be created for some reason, a ResourceException is thrown. <br>
   * If the cache element ce is not null, this method should set a reload context on the cache
   * element to enable reload-on-demand for this template. If ce is null, caching is disabled.
   * 
   * @param query
   *          location to load template from
   * @param ce
   *          cache element that will be used for this template or null if no cache is used.
   * @exception ResourceException
   *              if an error occured while loading the template
   */
  Template load(String query,
                CacheElement ce)
      throws ResourceException;
}
