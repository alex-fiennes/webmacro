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
      this (broker, new File(filename), null);
   }

   /**
     * Instantiate a template based on the specified file using
     * the default encoding from WebMacro.properties (TemplateEncoding), 
     * if not specified there then the UTF-8 encoding.
     */
   public FileTemplate(Broker broker, File templateFile) {
      this (broker, templateFile, null);
   }

   /** 
     * Instantiate a template based on the specified file using
     * the specified encoding to read the template.
     */
   public FileTemplate(Broker broker, File tmplFile, String encoding) {
      super(broker);
      myFile = tmplFile;
      if (encoding == null) {
         myEncoding = getDefaultEncoding();
      } else {
         myEncoding = encoding;
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
