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
   private Log _log;
   private BrokerTemplateProviderHelper _btpHelper;

   static {
      try {
         _pathSeparator = System.getProperty("path.separator");
      } catch (Throwable t) {
         // do nothing
      }
   }

   private static class TPTimedReference extends TimedReference {
      File file;
      long lastModified;

      TPTimedReference(Object referent, long timeout, 
                       File file, long lastModified) {
         super(referent, timeout);
         this.file = file;
         this.lastModified = lastModified;
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
         _templatePath = config.getSetting("TemplatePath", "");
         if (_templatePath.equals(""))
           _log.info("Template path is empty; will load from class path");
         else {
            StringTokenizer st = 
               new StringTokenizer(_templatePath, _pathSeparator);
            _templateDirectory = new String[ st.countTokens() ];
            for (int i=0; i < _templateDirectory.length; i++) {
               String dir = st.nextToken(); 
               _templateDirectory[i] = dir;
            }
         }
         _btpHelper = new BrokerTemplateProviderHelper();
         _btpHelper.init(b, config);
      } catch(Exception e) {
         throw new InitException("Could not initialize", e);
      }
   }

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
      TimedReference ret = null;

      _log.info("Loading template: " + name);

      File tFile = findFileTemplate(name);
      if (tFile != null) {
         try {
            Template t = new FileTemplate (_broker, tFile);
            ret = new TPTimedReference(t, _cacheDuration, 
                                       tFile, tFile.lastModified());
         }
         catch (NullPointerException npe) {
            _log.warning ("TemplateProvider: Template not found: " + name, 
                          npe);
         }
         catch (Exception e) {  
            // Parse error
            _log.warning ("TemplateProvider: Error occured while parsing " 
                          + name, e);
            throw new NotFoundException("Error parsing template " + name, e);
         }
      }
      else {
         // Let the BrokerTemplateProvider have a crack at it 
         ret = _btpHelper.load(name);
      }

      if (ret == null) {
         throw new NotFoundException(
            this + " could not locate " + name + " on path " + _templatePath);
      }
      return ret;
   }


   private boolean testReference(String fileName, TimedReference ref) {
      // This must have been the BrokerTemplateProviderHelper; dispatch to it
      return _btpHelper.shouldReload(fileName, ref);
   }

   private boolean testReference(String fileName, TPTimedReference ref) {
      // This is one of ours
      TPTimedReference myRef = (TPTimedReference) ref;
      return (myRef.lastModified != myRef.file.lastModified());
   }

   /**
     * if the cached last modified value of the template file
     * differs from the current last modified value of the template file
     * return true.  Otherwise, return false
     *
     * @return true if template should be reloaded
     */
   final public boolean shouldReload(String fileName, TimedReference ref) {
      if (ref == null)
         return true;
      else 
         return testReference(fileName, ref);
   }

   // IMPLEMENTATION


   /**
    * @param fileName the template filename to find, relative to the
    * TemplatePath
    * @return a File object that represents the specified template file.
    * @return null if template file cannot be found.  */
   final private File findFileTemplate(String fileName)
   {
      if (_templateDirectory != null) {
         _log.debug("Looking for template in TemplatePath: " + fileName);
         for (int i=0; i <_templateDirectory.length; i++) {
            String dir = _templateDirectory[i];
            File tFile  = new File(dir,fileName);
            if (tFile.canRead()) {
               _log.debug("TemplateProvider: Found "+fileName+" in "+dir);
               return tFile;
            }      
         }
      }
      return null;
   }
}


