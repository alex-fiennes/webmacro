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
import org.webmacro.*;
import org.webmacro.util.*;

/**
  * A very simple provider which simply takes the config information
  * passed to it by the broker and returns it. 
  */
public class ConfigProvider implements Provider
{

   private Settings _config;

   public String getType() { return "config"; }

   public void init(Broker b, Settings config) throws InitException
   {  
      _config = config;
      if (_config == null) {
         throw new InitException("Attempt to init with no configuration");
      }
   }

   public void flush() { } 

   public void destroy() { _config = null; }

   public Object get(String key) throws NotFoundException 
   {
      Object o = _config.getSetting(key);
      if (o == null) {
         throw new NotFoundException("No config information for: " + key);
      }
      return o;
   }
}
