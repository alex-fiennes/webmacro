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
  * The TemplateProvider is the WebMacro class responsible for 
  * loading templates. You could replace it with your own version
  * in the configuration file. This implementation caches templates
  * using soft references for a maximum amount of time specified
  * in the configuration. Templates are loaded from the filesystem,
  * relative to the TemplatePath specified in teh configuration.
  * <p>
  * Ordinarily you would not accses this class directly, but 
  * instead you would call the Broker and it would look up and
  * use the TemplateProvider for you.
  * @see Provider
  * @author Brian Goetz
  * @since 0.96
  */
final public class BrokerTemplateProviderHelper 
  implements CachingProviderMethods
{

   // INITIALIZATION

   private Broker _broker;
   private int _cacheDuration;
   private Log _log;

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
   }

   /**
     * Grab a template based on its name.
     */
   final public CacheableElement load(String name) throws ResourceException 
   {
      Template t = null;
      URL tUrl;
      CacheableElement ret = null;

      tUrl = findTemplate(name);
      try {
         long lastMod = UrlProvider.getUrlLastModified(tUrl);

         t = new StreamTemplate(_broker, 
                                new InputStreamReader(
                                  UrlProvider.getUrlInputStream(tUrl)));
         t.parse ();
         ret = new UrlTemplateCacheableElement(t, tUrl, lastMod);
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
      catch (Exception e) {  
         _log.warning ("BrokerTemplateProvider: Error occured while fetching " 
                       + name, e);
         throw new ResourceException("Error parsing template " + name, e);
      }
      if (ret == null) 
         throw new NotFoundException(this + " could not locate " + name);

      return ret;
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
      URL u = _broker.getResource(fileName);
      if (u != null) 
         if (_log.loggingDebug())
            _log.debug("BrokerTemplateProvider: Found " + fileName);
      return u;
   }
}


