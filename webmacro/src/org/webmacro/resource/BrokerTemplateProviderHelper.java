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

import  org.webmacro.*;
import  org.webmacro.engine.*;
import  org.webmacro.util.*;
import  java.util.*;
import  java.io.*;
import  java.net.*;

/**
  * This class does the actual work of retrieving templates using the
  * Broker.  It is called by both BrokerTemplateProvider and
  * TemplateProvider.
  * @see Provider
  * @see TemplateProvider
  * @see BrokerTemplateProvider
  * @author Brian Goetz
  * @since 0.96 
  */
final public class BrokerTemplateProviderHelper 
  implements ResourceLoader
{

   // INITIALIZATION

   private Broker _broker;
   private int _cacheDuration;
   private Log _log;
   private boolean _cacheSupportsReload = true;
    private ReloadDelayDecorator reloadDelay;

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
     * Create a new TemplateProvider that uses the specified directory
     * as the source for Template objects that it will return
     * @exception ResourceInitException provider failed to initialize
     */
   public void init(Broker b, Settings config) throws InitException
   {
      _broker = b;
      _log = b.getLog("resource", "Object loading and caching");
      _cacheDuration = config.getIntegerSetting("TemplateExpireTime", 0);
      reloadDelay = new ReloadDelayDecorator();
      reloadDelay.init(b,config);
   }

   /**
     * Grab a template based on its name.
     */
   final public Object load(String name, CacheElement ce) 
   throws ResourceException {
      Template t = null;
      URL tUrl;
      Object ret = null;

      tUrl = findTemplate(name);
      try {
         long lastMod = UrlProvider.getUrlLastModified(tUrl);
         String encoding = tUrl.openConnection().getContentEncoding();
         // encoding may be null. Will be handled by StreamTemplate
         t = new StreamTemplate(_broker, 
                                UrlProvider.getUrlInputStream(tUrl),
                                encoding);
         t.setName(name);
         t.parse ();
         ret = t;
         if (_cacheSupportsReload) {
             CacheReloadContext reloadContext = new UrlReloadContext(tUrl, lastMod);
             ce.setReloadContext(reloadDelay.decorate(tUrl.getProtocol(),
                                                      reloadContext));
         }
      }
      catch (NullPointerException npe) {
         _log.warning ("BrokerTemplateProvider: Template not found: " + name);
      }
      catch (ParseException e) {  
         _log.warning ("BrokerTemplateProvider: Error occured while parsing " 
                       + name, e);
         throw new InvalidResourceException("Error parsing template " + name, 
                                            e);
      }
      catch (IOException e) {
          _log.warning("BrokerTemplateProvider: IOException while parsing "
                       + name, e);
          throw new InvalidResourceException("Error parsing template "+name,
                                             e);
      }
      catch (Exception e) {  
         _log.warning ("BrokerTemplateProvider: Error occured while fetching " 
                       + name, e);
         throw new ResourceException("Error parsing template " + name, e);
      }
      if (ret == null) 
         throw new NotFoundException(this + " could not locate " + name);

      return ret;
   }


   /** We don't implement this one */
   public Object load(Object query, CacheElement ce)
     throws ResourceException {
     throw new ResourceException("CachingProvider: load(Object) not supported, use load(String)");
   }

   public void setReload(boolean reload) {
      _cacheSupportsReload = reload;
   }

   // IMPLEMENTATION

   /**
    * @param fileName the template filename to find
    * @return a File object that represents the specified template file.
    * @return null if template file cannot be found.  */
   final private URL findTemplate(String fileName)
   {
      if (_log.loggingDebug())
         _log.debug("Looking for template in class path: " + fileName);
      URL u = _broker.getTemplate(fileName);
      if (u != null) 
         if (_log.loggingDebug())
            _log.debug("BrokerTemplateProvider: Found " + fileName 
                       + " at " + u.toString());
      return u;
   }
}


