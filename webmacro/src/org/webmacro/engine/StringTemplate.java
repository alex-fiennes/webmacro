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
  * They do not need an encoding since strings are already converted
  * to utf by java.
  * @author Brian Goetz
  */

public class StringTemplate extends WMTemplate
{

   /** The text associated with this template */
   private final String templateText;

    /** (Optional) name of this template */
    private String templateName;

   /**
     * Instantiate a template. Encoding information
     * is not needed, as strings are already converted
     * to utf in java.
     */
    public StringTemplate(Broker broker, String templateText) {
        this(broker,templateText,"unknown");
    }

   /**
     * Instantiate a template. Encoding information
     * is not needed, as strings are already converted
     * to utf in java.
     * @param name name of string template to display in 
     * error messages and logs
     */
   public StringTemplate(Broker broker, String templateText,String name)
   {
       super(broker);
       this.templateText = templateText;
       this.templateName = name;
   }

   public void setName(String name) {
      templateName = name;
      _content.setTemplateName(name);
   }

    public String getName() {
       return templateName;
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
        StringBuffer b = new StringBuffer();
        b.append("StringTemplate(\"");
        b.append(templateName);
        if (templateText != null) {
            b.append("\";\"");
            // be sure to only show first 100 characters,
            // otherwise it can get somewhat messy...
            if (templateText.length() <= 100) {
                b.append(templateText);
            } else {
                b.append(templateText.substring(0,100));
                b.append("...");
            }
        }
        b.append("\")");
        return b.toString();
    }

}
