
/*
 * Copyright (c) 1998, 1999 Semiotek Inc. All Rights Reserved.
 *
 * This software is the confidential intellectual property of
 * of Semiotek Inc.; it is copyrighted and licensed, not sold.
 * You may use it under the terms of the GNU General Public License,
 * version 2, as published by the Free Software Foundation. If you 
 * do not want to use the GPL, you may still use the software after
 * purchasing a proprietary developers license from Semiotek Inc.
 *
 * This software is provided "as is", with NO WARRANTY, not even the 
 * implied warranties of fitness to purpose, or merchantability. You
 * assume all risks and liabilities associated with its use.
 *
 * See the attached License.html file for details, or contact us
 * by e-mail at info@semiotek.com to get a copy.
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
   public void init(Broker b, Properties config) throws InitException
   {
      super.init(b,config);
      _broker = b;
      _log = b.getLog("resource");

      try {
         try {
            String cacheStr = config.getProperty("TemplateExpireTime");
            _cacheDuration = Integer.valueOf(cacheStr).intValue();
         } catch (Exception ee) {
            // use default
         }
         _templatePath = config.getProperty("TemplatePath");
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
         throw new InitException("Could not initialize: " + e);
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
     * Grab a template based on its name, setting the request event to 
     * contain it if we found it. The template will be parsed using 
     * the specified encoding, or UTF8 if the encoding is left off. 
     * The rest of the name is the filename to be loaded. For example,
     * the template name :EUC-KR:/view.wm loads the "view.wm" template 
     * and encodes it with the EUC-KR encoding.
     * @param name has the format :encodign:name (:encoding: is optional)
     */
   final public TimedReference load(String name) throws NotFoundException 
   {
      String encoding = "UTF8";
      if (name.charAt(0) == ':') {
         int fstart = name.indexOf(':', 1);
         encoding = name.substring(1,fstart);
         name = name.substring(fstart + 1);
      }
      Template t = get(name,encoding);
      if (t == null) {
         throw new NotFoundException(
            this + " could not locate " + name + " on path " + _templatePath);
      }
      return new TimedReference(t, _cacheDuration);   
   }


   // IMPLEMENTATION

   /**
     * Find the specified template in the directory managed by this 
     * template store. Any path specified in the filename is relative
     * to the directory managed by the template store. 
     * <p>
     * @param fileName relative to the current directory fo the store
     * @returns a template matching that name, or null if one cannot be found
     */
   final public Template get(String fileName, String encoding) {
      for (int i=0; i < _templateDirectory.length; i++) {
         Template t;
         String dir = _templateDirectory[i];
         File tFile  = new File(dir,fileName);
         if (tFile.canRead()) {
            try {
               t = new FileTemplate(_broker,tFile,"UTF8");
               t.parse();
               return t;
            } catch (Exception e) {
               _log.warning("TemplateProvider: Could not load template: " 
                     + tFile, e);
            } 
         }
      }
      return null;
   }


}


