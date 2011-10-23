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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webmacro.ResourceException;
import org.webmacro.Template;

import java.io.File;

/**
 * Implementation of TemplateLoader that loads templates from a given directory. Objects of this
 * class are responsible for searching exactly one directory for templates. If it handles a request,
 * it takes path as the base path to find the template.
 * 
 * @author Sebastian Kanthak (sebastian.kanthak@muehlheim.de)
 */
public class FileTemplateLoader
  extends AbstractTemplateLoader
{

  static Logger _log = LoggerFactory.getLogger(FileTemplateLoader.class);

  private String path;

  public void setConfig(String config)
  {
    // leading slash isn't needed, because
    // we use File constructor.
    this.path = config;

    // However, we can check, if the directory exists.
    File f = new File(path);
    if (!f.exists()) {
      _log.warn("FileTemplateLoader: " + f.getAbsolutePath() + " does not exist.");
    } else if (!f.isDirectory()) {
      _log.warn("FileTemplateLoader: " + f.getAbsolutePath() + " is not a directory.");
    }
  }

  /**
   * Tries to load a template by interpreting query as a path relative to the path set by setPath.
   */
  public final Template load(String query,
                             CacheElement ce)
      throws ResourceException
  {
    File tFile = new File(path, query);
    if (tFile.isFile() && tFile.canRead()) {
      _log.debug("FileTemplateProvider: Found template " + tFile.getAbsolutePath());
      return helper.load(tFile, ce);
    } else {
      return null;
    }
  }
}
