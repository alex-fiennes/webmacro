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
  * StringTemplate objects read their template data from a string.
  * @author Brian Goetz
  */

public class StringTemplate extends WMTemplate
{

   /**
     * What encoding I use to read my templates
     */
   private final String myEncoding;

   /** The text associated with this template */
   private final String templateText;

   /**
     * Instantiate a template using the default encoding 
     * from WebMacro.properties (TemplateEncoding), 
     * or if not specified there then the UTF-8 encoding.
     */
   public StringTemplate(Broker broker, String templateText)
   {
      this(broker, templateText, null);
   }

   /** 
     * Instantiate a template based on the specified text using
     * the specified encoding to read the template.
     */
   public StringTemplate(Broker broker, String templateText, String encoding) {
      super(broker);
      this.templateText = templateText;
      if (encoding == null) 
         myEncoding = getDefaultEncoding();
      else 
         myEncoding = encoding;
   }

   /**
     * Get the stream the template should be read from. Parse will 
     * call this method in order to locate a stream.
     */
   protected Reader getReader() throws IOException {
      return new StringReader(templateText);
   }

   /**
     * Return a name for this template. For example, if the template reads
     * from a file you might want to mention which it is--will be used to
     * produce error messages describing which template had a problem.
     */
   public String toString() {
      return "StringTemplate: " + templateText;
   }

}
