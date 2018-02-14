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
import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webmacro.Broker;
import org.webmacro.InitException;
import org.webmacro.NotFoundException;
import org.webmacro.ResourceException;
import org.webmacro.Template;
import org.webmacro.engine.FileTemplate;
import org.webmacro.engine.ParseException;
import org.webmacro.engine.StreamTemplate;
import org.webmacro.util.Settings;

/**
 * This class does the actual work of retrieving templates using the Broker. It is called by both
 * BrokerTemplateProvider and TemplateProvider.
 * 
 * @see org.webmacro.Provider
 * @see TemplateProvider
 * @see BrokerTemplateProvider
 * @author Brian Goetz
 * @since 0.96
 */
final public class BrokerTemplateProviderHelper
  implements ResourceLoader
{

  static Logger _log = LoggerFactory.getLogger(BrokerTemplateProviderHelper.class);

  // INITIALIZATION

  private Broker _broker;
  // private int _cacheDuration;

  private boolean _cacheSupportsReload = true;
  private ReloadDelayDecorator reloadDelay;

  private static class UrlReloadContext
    extends CacheReloadContext
  {

    private long lastModified;
    private URL url;

    public UrlReloadContext(URL url,
                            long lastModified)
    {
      this.url = url;
      this.lastModified = lastModified;
    }

    @Override
    public boolean shouldReload()
    {
      return (lastModified != UrlProvider.getUrlLastModified(url));
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
    _broker = b;
    // _cacheDuration = config.getIntegerSetting("TemplateExpireTime", 0);
    reloadDelay = new ReloadDelayDecorator();
    reloadDelay.init(b, config);
  }

  /**
   * Grab a template based on its name.
   */
  @Override
  final public Object load(String name,
                           CacheElement ce)
      throws ResourceException
  {
    Template t = null;
    URL tUrl;
    Object ret = null;

    tUrl = findTemplate(name);
    try {
      String protocol = tUrl.getProtocol();
      // Treat files as a special case
      if (protocol.equals("file")) {
        File f = new File(tUrl.getFile());
        long lastMod = f.lastModified();

        t = new FileTemplate(_broker, f);
        t.parse();
        ret = t;
        if (_cacheSupportsReload) {
          CacheReloadContext reloadContext = new TemplateProvider.FTReloadContext(f, lastMod);
          ce.setReloadContext(reloadDelay.decorate("file", reloadContext));
        }
      } else {
        long lastMod = UrlProvider.getUrlLastModified(tUrl);
        String encoding = tUrl.openConnection().getContentEncoding();
        // encoding may be null. Will be handled by StreamTemplate
        t = new StreamTemplate(_broker, UrlProvider.getUrlInputStream(tUrl), encoding);
        t.setName(name);
        t.parse();
        ret = t;
        if (_cacheSupportsReload) {
          CacheReloadContext reloadContext = new UrlReloadContext(tUrl, lastMod);
          ce.setReloadContext(reloadDelay.decorate(tUrl.getProtocol(), reloadContext));
        }
      }
    } catch (NullPointerException npe) {
      _log.warn("BrokerTemplateProvider: Template not found: " + name);
    } catch (ParseException e) {
      _log.warn("BrokerTemplateProvider: Error occured while parsing " + name, e);
      throw new InvalidResourceException("Error parsing template " + name, e);
    } catch (IOException e) {
      _log.warn("BrokerTemplateProvider: IOException while parsing " + name, e);
      throw new InvalidResourceException("Error parsing template " + name, e);
    } catch (Exception e) {
      _log.warn("BrokerTemplateProvider: Error occured while fetching " + name, e);
      throw new ResourceException("Error parsing template " + name, e);
    }
    if (ret == null)
      throw new NotFoundException(this + " could not locate " + name);

    return ret;
  }

  /** We don't implement this one. */
  @Override
  public Object load(Object query,
                     CacheElement ce)
      throws ResourceException
  {
    throw new ResourceException("CachingProvider: load(Object) not supported, use load(String)");
  }

  public void setReload(boolean reload)
  {
    _cacheSupportsReload = reload;
  }

  // IMPLEMENTATION

  /**
   * @param fileName
   *          the template filename to find
   * @return a File object that represents the specified template file, null if template file cannot
   *         be found.
   */
  final private URL findTemplate(String fileName)
  {
    _log.debug("Looking for template in class path: " + fileName);
    URL u = _broker.getTemplate(fileName);
    if (u != null)
      _log.debug("BrokerTemplateProvider: Found " + fileName + " at " + u.toString());
    return u;
  }
}
