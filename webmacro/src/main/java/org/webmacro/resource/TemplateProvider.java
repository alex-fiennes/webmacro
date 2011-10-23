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

import java.io.File;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.webmacro.Broker;
import org.webmacro.InitException;
import org.webmacro.NotFoundException;
import org.webmacro.ResourceException;
import org.webmacro.Template;
import org.webmacro.TemplateException;
import org.webmacro.engine.FileTemplate;
import org.webmacro.util.Settings;

/**
 * The TemplateProvider is the WebMacro class responsible for loading templates. You could replace
 * it with your own version in the configuration file. Templates are loaded from the filesystem,
 * relative to the TemplatePath specified in the configuration.
 * <p>
 * Ordinarily you would not access this class directly, but instead you would call the Broker and it
 * would look up and use the TemplateProvider for you.
 * 
 * @see org.webmacro.Provider
 */
final public class TemplateProvider
  extends CachingProvider
{

  static Logger _log = LoggerFactory.getLogger(TemplateProvider.class);
  // INITIALIZATION

  private static String _pathSeparator = ";";
  private String _templateDirectory[] = null;
  private Broker _broker = null;
  private String _templatePath;
  private BrokerTemplateProviderHelper _btpHelper;

  /**
   * Responsible for deciding, how often resources should be checked for modification at max.
   */
  private ReloadDelayDecorator reloadDelay;

  static {
    try {
      _pathSeparator = System.getProperty("path.separator");
    } catch (Throwable t) {
      // do nothing
    }
  }

  /**
   * ReloadContext for file templates. Uses last-modified to determine if resource should be
   * reloaded.
   */
  public static class FTReloadContext
    extends CacheReloadContext
  {

    private File file;
    private long lastModified;

    public FTReloadContext(File f,
                           long lastModified)
    {
      this.file = f;
      this.lastModified = lastModified;
    }

    @Override
    public boolean shouldReload()
    {
      return (lastModified != file.lastModified());
    }
  }

  /**
   * Create a new TemplateProvider that uses the specified directory as the source for Template
   * objects that it will return.
   * 
   * @exception InitException
   *              provider failed to initialize
   */
  @Override
  public void init(Broker b,
                   Settings config)
      throws InitException
  {
    super.init(b, config);
    _broker = b;

    try {
      _templatePath = config.getSetting("TemplatePath", "");
      if (_templatePath.equals(""))
        _log.info("Template path is empty; will load from class path");
      else {
        StringTokenizer st = new StringTokenizer(_templatePath, _pathSeparator);
        _templateDirectory = new String[st.countTokens()];
        for (int i = 0; i < _templateDirectory.length; i++) {
          String dir = st.nextToken();
          _templateDirectory[i] = dir;
        }
      }
      reloadDelay = new ReloadDelayDecorator();
      reloadDelay.init(b, config);

      _btpHelper = new BrokerTemplateProviderHelper();
      _btpHelper.init(b, config);
      _btpHelper.setReload(_cacheSupportsReload);
    } catch (Exception e) {
      throw new InitException("Could not initialize", e);
    }
  }

  /**
   * Supports the "template" type.
   */
  final public String getType()
  {
    return "template";
  }

  /**
   * Grab a template based on its name.
   */
  final public Object load(String name,
                           CacheElement ce)
      throws ResourceException
  {
    Object ret = null;

    _log.info("Loading template: " + name);

    File tFile = findFileTemplate(name);
    if (tFile != null) {
      try {
        Template t = new FileTemplate(_broker, tFile);
        t.parse();
        ret = t;
        if (_cacheSupportsReload) {
          CacheReloadContext reloadContext = new FTReloadContext(tFile, tFile.lastModified());
          ce.setReloadContext(reloadDelay.decorate("file", reloadContext));
        }
      } catch (NullPointerException npe) {
        _log.warn("TemplateProvider: Template not found: " + name, npe);
        throw new ResourceException("Error fetching template " + name, npe);
      } catch (TemplateException e) {
        // Parse error
        _log.warn("TemplateProvider: Error occured while parsing " + name, e);
        throw new InvalidResourceException("Error parsing template " + name, e);
      } catch (Exception e) {
        _log.warn("TemplateProvider: Error occured while fetching " + name, e);
        throw new ResourceException("Error fetching template " + name, e);
      }
    } else {
      // Let the BrokerTemplateProvider have a crack at it
      ret = _btpHelper.load(name, ce);
    }

    if (ret == null) {
      throw new NotFoundException(this + " could not locate " + name + " on path " + _templatePath);
    }
    return ret;
  }

  // IMPLEMENTATION

  /**
   * @param fileName
   *          the template filename to find, relative to the TemplatePath
   * @return a File object that represents the specified template file null if template file cannot
   *         be found.
   */
  final private File findFileTemplate(String fileName)
  {
    if (_templateDirectory != null) {
      _log.debug("Looking for template in TemplatePath: " + fileName);
      for (int i = 0; i < _templateDirectory.length; i++) {
        String dir = _templateDirectory[i];
        File tFile = new File(dir, fileName);
        if (tFile.canRead()) {
          _log.debug("TemplateProvider: Found " + fileName + " in " + dir);
          return tFile;
        }
      }
    }
    return null;
  }
}
