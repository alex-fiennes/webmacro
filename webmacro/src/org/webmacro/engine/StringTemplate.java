
/*
 * Copyright (c) 2000 Semiotek Inc. All Rights Reserved.
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
