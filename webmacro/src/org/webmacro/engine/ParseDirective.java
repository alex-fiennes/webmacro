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
  * An parse directive is used to include text from another Template. 
  * Unfortunately in the present version the Template has to be named
  * as a file. Future versions will allow caching.
  * <p>
  * Note that a variable can be used to determine which file to read in,
  * so that in practice the named file may not be known until run time.
  * <p>
  * Here are some examples:<pre>
  *
  *     #parse   "includeMacros.wm"
  *     #parse   "$someVariableMacro"
  *
  * </pre>
  */
class ParseDirective implements Directive
{

   /**
     * The filename we want to include--may be a macro
     */
   final private Macro _fileName;

   /**
     * Create an include directive. Note that the supplied variable
     * may be a macro, in which case it is evaluated to determine the
     * actual filename (it will be evaluated at run time, once 
     * given a context).
     * <p>
     */
   ParseDirective(Macro fileName) {
      _fileName = fileName;
   }


   public static Object build(BuildContext rc, Object target)
      throws BuildException
   {
      if (target == null) {
         return null;
      }
      if (target instanceof Macro) {
         return new ParseDirective((Macro) target);
      } else {
         try {
            return rc.getBroker().get("template",target.toString());
         } 
         catch (NotFoundException ne) {
            return new BuildException("Template " + target + " not found: " 
                  + ne);
         }
         catch (ResourceException ne) {
            return new BuildException("Template " + target 
                                      + " could not be loaded: " + ne);
         }
      }
   }

   /**
     * Apply the values in the supplied context to evaluate the 
     * include filename; then read that file in and return it as 
     * a string. If we are a parsing directive then the file will
     * be parsed before it is returned as a string.
     * @exception PropertyException is required data is missing
     */ 
   final public Object evaluate(Context context)
      throws PropertyException
   {
      try {
         FastWriter fw = FastWriter.getInstance(context.getBroker());
         write(fw,context);
         String ret = fw.toString();
         fw.close();
         return ret;
      } catch(IOException e) {
         context.getLog("engine", "parsing and template execution").error(
            "Include: evaluate got IO exception on write to StringWriter",e);
         return "";
      }
   }  

   /**
     * Apply the values in the supplied context to evaluate the 
     * include filename; then read that file in and write it out
     * to the supplied Writer. If we are a parsing directive the
     * file will be parsed before it is written.
     * <p>
     * @exception IOException if an error occurred with out
     * @exception PropertyException if required data was missing
     */
   final public void write(FastWriter out, Context context) 
      throws PropertyException, IOException
   {

      if (_fileName == null) {
         context.getLog("engine").error(
            "Include: attempt to write with null filename");
         return;
      }

      // evaluate or toString to find the actual filename to use
      String fname = ((Macro) _fileName).evaluate(context).toString();
      try {
         Template tmpl = (Template) context.getBroker().get("template", fname);
         tmpl.write(out,context);
     } catch (IOException e) {
         String warning = "Error reading file " + fname;
         context.getLog("engine").warning(warning,e);
         out.write("<!--\nWARNING: " + warning + " \n-->");
     } catch (Exception e) {
         String warning = "Template not found: " + fname;
         context.getLog("engine").warning(warning,e);
         out.write("<!--\nWARNING: " + warning + " \n-->");
     }
   }

}

