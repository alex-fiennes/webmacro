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
import  org.webmacro.engine.StreamTemplate;
import  org.webmacro.util.*;
import  java.util.*;
import  java.io.*;
import  java.net.*;

/**
  * The BrokerTemplateProvider loads templates through
  * Broker.getResource().  This implementation caches templates using
  * soft references for a maximum amount of time specified in the
  * configuration.  Templates might be loaded from a file, from a WAR,
  * from a JAR, etc.  It just passes the requests on to a
  * BrokerTemplateProviderHelper object.
  * @author Brian Goetz
  * @since 0.96
  * @see Provider 
  * @see BrokerTemplateProviderHelper 
  */
final public class BrokerTemplateProvider extends CachingProvider
{

   private BrokerTemplateProviderHelper _helper;
   private Log _log;

   public void init(Broker b, Settings config) throws InitException
   {
      super.init(b,config);
      _helper.init(b, config);
      _log = b.getLog("resource", "Object loading and caching");
   }

   final public String getType() {
      return "template";
   }

   final public CacheableElement load(String name) throws ResourceException 
   {
      if (_log.loggingInfo())
         _log.info("Loading template: " + name);
      return _helper.load(name);
   }

}


