
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
            return rc.getBroker().getValue("template",target.toString());
         } catch (NotFoundException ne) {
            return new BuildException("Template " + target + " not found: " 
                  + ne);
         } catch (InvalidTypeException it) {
            throw new BuildException("Broker cannot load templates: " + it);
         }
      }
   }

   /**
     * Apply the values in the supplied context to evaluate the 
     * include filename; then read that file in and return it as 
     * a string. If we are a parsing directive then the file will
     * be parsed before it is returned as a string.
     * @exception ContextException is required data is missing
     */ 
   final public Object evaluate(Context context)
      throws ContextException
   {
      try {
         ByteArrayOutputStream os = new ByteArrayOutputStream(256);
         FastWriter fw = new FastWriter(os, "UTF8");
         write(fw,context);
         fw.flush();
         return os.toString("UTF8");
      } catch(IOException e) {
         Engine.log.exception(e);
         Engine.log.warning(
              "Include: evaluate got IO exception on write to StringWriter");
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
     * @exception ContextException if required data was missing
     */
   final public void write(FastWriter out, Context context) 
      throws ContextException, IOException
   {

      if (_fileName == null) {
         Engine.log.error("Include: attempt to write with null filename");
         return;
      }

      // evaluate or toString to find the actual filename to use
      String fname = ((Macro) _fileName).evaluate(context).toString();
      if (Log.debug) {
         Engine.log.debug("Include fname: " + fname);
      }

      try {
         Template tmpl = (Template) context.getBroker().getValue("template", fname);
         tmpl.write(out,context);
     } catch (IOException e) {
         Engine.log.exception(e);
         String warning = "Error reading file " + fname;
         Engine.log.warning(warning);
         out.write("<!--\nWARNING: " + warning + " \n-->");
     } catch (Exception e) {
         Engine.log.exception(e);
         String warning = "Template not found: " + fname;
         Engine.log.warning(warning);
         out.write("<!--\nWARNING: " + warning + " \n-->");
     }
 
   }

}

