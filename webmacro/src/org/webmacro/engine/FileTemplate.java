
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


package org.webmacro.engine;
import java.util.*;
import java.io.*;
import org.webmacro.util.*;
import org.webmacro.*;

/**
  * FileTemplate objects read their template data from a text file.
  */

public class FileTemplate extends WMTemplate
{

   /**
     * The name of the file to read this template from
     */
   private final File myFile;

   /**
     * What encoding I use to read my templates
     */
   private final String myEncoding;

   /**
     * Instantiate a template based on the specified filename using
     * the default encoding from WebMacro.properties (TemplateEncoding), 
     * or if not  specified there then the UTF-8 encoding.
     */
   public FileTemplate(Broker broker, String filename)
   {
      this (broker, new File(filename), defaultEncoding(broker));
   }

   /**
     * Instantiate a template based on the specified file using
     * the default encoding from WebMacro.properties (TemplateEncoding), 
     * if not specified there then the UTF-8 encoding.
     */
   public FileTemplate(Broker broker, File templateFile) {
      this (broker, templateFile, defaultEncoding(broker));
   }

   /** 
     * Instantiate a template based on the specified file using
     * the specified encoding to read the template.
     */
   public FileTemplate(Broker broker, File tmplFile, String encoding) {
      super(broker);
      myFile = tmplFile;
      myEncoding = encoding;
   }

   private static final String defaultEncoding(Broker b) {
      try {
         return (String) b.get("config", "TemplateEncoding");
      } catch (Exception e) {
         return System.getProperty("file.encoding");
      }

   }


   /**
     * Get the stream the template should be read from. Parse will 
     * call this method in order to locate a stream.
     */
   protected Reader getReader() throws IOException {
      return new BufferedReader(new InputStreamReader(
            new FileInputStream(myFile), myEncoding));
   }

   /**
     * Return a name for this template. For example, if the template reads
     * from a file you might want to mention which it is--will be used to
     * produce error messages describing which template had a problem.
     */
   public String toString() {
      return "FileTemplate:" + myFile;
   }

}
