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


package org.webmacro.util;
import org.webmacro.InitException;
import java.util.*;
import java.io.*;

public class SubSettings extends Settings {

   private final Settings _settings;
   private final String _prefix;

   /**
     * Get a subset of the settings in this Settings. The 
     * returned Settings object will be just those settings 
     * beginning with the supplied prefix, with the 
     * prefix chopped off. So if the Settings had 
     * a setting "LogLevel.foo" then the settings file
     * returned by getSubSettings("LogLevel") would contain
     * the key "foo".
     */
   public SubSettings(Settings settings, String prefix) {
      super();
      _settings = settings;
      _prefix = (prefix == null) ? "" : prefix + ".";
   }

   public String[] getKeys() {
      String[] underlyingKeys = _settings.getKeys();
      ArrayList al = new ArrayList(underlyingKeys.length);
      for (int i=0; i<underlyingKeys.length; i++) {
         String key = underlyingKeys[i];
         if (key.startsWith(_prefix)) 
            al.add(key.substring(_prefix.length()));
      }
      return (String[]) al.toArray(stringArray);
   }

   /**
     * Prefix the key for use with the underlying Settings
     */
   private String prefix(String key) {
      return _prefix + key;
   }

   /**
     * Find out if a setting is defined
     */
   public boolean containsKey(String key) {
      return _settings.containsKey(prefix(key));
   }

   /**
     * Get a setting
     */
   public String getSetting(String key) {
      return _settings.getSetting(prefix(key));
   }
}

