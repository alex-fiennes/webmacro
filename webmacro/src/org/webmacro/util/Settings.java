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
import java.net.URL;

public class Settings {

   protected static final String[] stringArray = new String[0];

   private Properties _props;

   /**
     * Create an empty Settings object
     */
   public Settings() {
      _props = new Properties();
   }

   /**
     * Search for the named settingsFile on the classpath 
     * and instantiate a Settings object based on its values
     */
   public Settings(String settingsFile) 
      throws InitException, IOException
   {
      this();
      load(settingsFile);
   }

   /**
     * Search for the named settingsFile from the supplied URL
     * and instantiate a Settings object based on its values
     */
   public Settings(URL settingsFile)
      throws InitException, IOException
   {
      this();
      load(settingsFile);
   }


   /**
     * Instantiate a new Settings object using the properties 
     * supplied as the settings values.
     */
   protected Settings(Properties values) {
      this();
      load(values);
   }

   /**
     * Instantaite a new Settings object using the supplied 
     * Settings as the defaults
     */
   public Settings(Settings defaults) {
      this();
      load(defaults);
   }

   /**
     * Load settings from the supplied fileName, searching for 
     * the file along the classpath.
     */
   public void load(String fileName) throws InitException, IOException 
   {  
      ClassLoader cl = this.getClass().getClassLoader();
      URL u = cl.getResource(fileName);
      if (u == null) {
         u = ClassLoader.getSystemResource(fileName);
      }
      if (u == null) {
         StringBuffer error = new StringBuffer();
         error.append("Unable to load the properties file: ");
         error.append(fileName);
         error.append(" via the class path\n");
         error.append("This indicates a configuration error.");
         throw new InitException(error.toString());
      }
      load(u);       
   }

   /**
     * Load settings from the supplied URL
     */
   public void load(URL u) throws IOException {
      InputStream in = u.openStream();
      _props.load(in);
      in.close();
   }

   /**
     * Load settings from the supplied input stream
     */
   public void load(InputStream in) throws IOException {
      _props.load(in);
   }

   /**
    * Load settings from a Settings
    */
   public void load(Settings defaults) {
      String keys[] = defaults.getKeys();
      for (int i = 0; i < keys.length; i++) {
         _props.setProperty(keys[i], defaults.getSetting(keys[i]));
      }
   }

   /**
    * Load settings from a Properties, only extracting properties
    * which have the specified prefix
    */
   public void load(Properties props, String prefix) {
      Enumeration e = props.propertyNames();
      String dotPrefix = (prefix == null) ? "" : prefix + ".";
      while (e.hasMoreElements()) {
         String key = (String) e.nextElement();
         if (prefix == null)
            _props.setProperty(key, props.getProperty(key));
         else if (key.startsWith(dotPrefix)) 
            _props.setProperty(key, props.getProperty(key)
                                         .substring(dotPrefix.length()));
      }
   }

   /**
    * Load settings from a Properties
    */
   public void load(Properties props) {
      load (props, null);
   }

   private static
   void buildPath(StringBuffer b, String fileName, Enumeration e)
   {  
      while (e.hasMoreElements()) {
         b.append("\t");
         b.append(e.nextElement().toString());
         b.append(fileName);
         b.append("\n");
      }
   }

   /**
     * Find out if a setting is defined
     */
   public boolean containsKey(String key) {
      return _props.containsKey(key);
   }

   /**
     * Get a setting
     */
   public String getSetting(String key) {
      return _props.getProperty(key);
   }

   /**
     * Get a setting with a default value in case it is not set
     */
   public String getSetting(String key, String defaultValue) {
      String ret = getSetting(key);
      return (ret != null) ? ret : defaultValue;
   }

   /**
     * Get a setting and convert it to an int 
     */
   public int getIntegerSetting(String key) {
      try {
         return Integer.parseInt(getSetting(key));
      } catch (Exception e) {
         return 0;
      }
   }

   /**
     * Get a setting with a default value in case it is not set
     */
   public int getIntegerSetting(String key, int defaultValue) {
      if (containsKey(key)) {
         try {
            return Integer.parseInt(getSetting(key));
         }
         catch (Exception e) {
            return defaultValue;
         }
      } else {
         return defaultValue;
      }
   }

   /**
     * Get a setting and convert it to a boolean. The 
     * values "on", "true", and "yes" are considered to 
     * be TRUE values, everything else is FALSE.
     */
   public boolean getBooleanSetting(String key) {
      String setting = getSetting(key);
      return ((setting != null) &&
              ((setting.equalsIgnoreCase("on"))
              || (setting.equalsIgnoreCase("true"))
              || (setting.equalsIgnoreCase("yes")) ));
   }

   /**
     * Get a setting with a default value in case it is not set
     */
   public boolean getBooleanSetting(String key, boolean defaultValue) 
   {
      if (containsKey(key)) {
         return getBooleanSetting(key);
      } else {
         return defaultValue;
      }
   }

   /**
     * Get the keys for this settings object as an array
     */
   public String[] getKeys() {
      return (String[]) _props.keySet().toArray(stringArray);
   }


   /**
     * Brief test
     */
   public static void main(String arg[]) throws Exception
   {

      Settings s = new Settings();
      s.load("Test.properties");

      Settings sb = new SubSettings(s, "b");
      String[] keys = sb.getKeys();
      for (int i = 0; i < keys.length; i++) {
         System.out.println("sub-prop " + keys[i] + " = " + sb.getSetting(keys[i]));
      }

      Settings ssb = new SubSettings(sb, "b");
      keys = ssb.getKeys();
      for (int i = 0; i < keys.length; i++) {
         System.out.println("sub-sub-prop " + keys[i] + " = " + ssb.getSetting(keys[i]));
      }

      System.out.println("LogTraceExceptions is: " + s.getBooleanSetting("LogTraceExceptions"));

   }
}

