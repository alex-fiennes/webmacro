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
import  org.webmacro.engine.FileTemplate;
import  org.webmacro.util.*;
import  java.util.*;
import  java.io.*;

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
  */
final public class TemplateProvider extends CachingProvider
{

   // INITIALIZATION

   private static String _pathSeparator = ";";
   private String _templateDirectory[] = null;
   private Broker _broker = null;
   private String _templatePath;
   private int _cacheDuration;
   private HashMap _lastModifiedCache = new HashMap (100);	// key = fileName, value=lastModified

   static {
      try {
         _pathSeparator = System.getProperty("path.separator");
      } catch (Throwable t) {
         // do nothing
      }
   }

   /**
     * Create a new TemplateProvider that uses the specified directory
     * as the source for Template objects that it will return
     * @exception ResourceInitException provider failed to initialize
     */
   public void init(Broker b, Settings config) throws InitException
   {
      super.init(b,config);
      _broker = b;
      _log = b.getLog("resource", "Object loading and caching");

      try {
         _cacheDuration = config.getIntegerSetting("TemplateExpireTime", 0);
         _templatePath = config.getSetting("TemplatePath");
         if (_templatePath == null) {
           _log.error("TemplatePath not specified in properties");
           _templatePath = "";
         }
         StringTokenizer st = 
            new StringTokenizer(_templatePath, _pathSeparator);
         _templateDirectory = new String[ st.countTokens() ];
         int i;
         for (i=0; i < _templateDirectory.length; i++) 
         {
            String dir = st.nextToken(); 
            _templateDirectory[i] = dir;
         }

      } catch(Exception e) {
         throw new InitException("Could not initialize",e);
      }
   }

   /**
     * Where we write our log messages 
     */
   private Log _log;

   /**
     * Supports the "template" type
     */
   final public String getType() {
      return "template";
   }

   /**
     * Grab a template based on its name.
     */
   final public TimedReference load(String name) throws NotFoundException 
   {
      _log.info("Loading template: " + name);
      Template t = getTemplate(name);
      if (t == null) {
         throw new NotFoundException(
            this + " could not locate " + name + " on path " + _templatePath);
      }
      return new TimedReference(t, _cacheDuration);   
   }

   /**
     * if the cached last modified value of the template file
     * differs from the current last modified value of the template file
     * return true.  Otherwise, return false
     *
     * @return true if template should be reloaded
     */
   final public boolean shouldReload (String fileName) {
      File tFile = findTemplate (fileName);
      Long lm = (Long) _lastModifiedCache.get (fileName);
      return (lm == null || lm.longValue() != tFile.lastModified());
   }

   // IMPLEMENTATION

   /**
     * Find the specified template in the directory managed by this 
     * template store. Any path specified in the filename is relative
     * to the directory managed by the template store. 
     * <p>
     * @param fileName relative to the current directory fo the store
     * @return a template matching that name, or null if one cannot be found
     */
   final public Template getTemplate(String fileName) {
      File tFile = findTemplate (fileName);
      Template t = null;
		
      try {
         t = new FileTemplate (_broker, tFile);
         t.parse ();
         _lastModifiedCache.put (fileName, new Long (tFile.lastModified()));
         return t;
      }
      catch (NullPointerException npe) {
         _log.warning ("TemplateProvider: Template not found: " + fileName);
      }
      catch (Exception e) {  
         // this probably occured b/c of a parsing error.
         // should throw some kind of ParseErrorException here instead
         _log.warning ("TemplateProvider: Error occured while getting " + fileName, e);
      }

      return null;
   }
   
   /**
    * @param fileName the template filename to find, relative to the TemplatePath
    * @return a File object that represents the specified template file.
    * @return null if template file cannot be found.
    */
   final private File findTemplate (String fileName)
   {
      _log.debug("Looking for template: " + fileName);
      for (int i=0; i <_templateDirectory.length; i++) {
         Template t;
         String dir = _templateDirectory[i];
         File tFile  = new File(dir,fileName);
         if (tFile.canRead()) {
            _log.debug("TemplateProvider: Found " + fileName + " in " + dir);
             return tFile;
         }      
      }
      return null;
   }
}


