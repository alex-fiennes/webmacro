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
  * An include directive is used to include text from another file.
  * #include reads in the supplied file 
  * as and includes it as a complete string, without parsing it.
  * <p>
  * Note that a variable can be used to determine which file to read in,
  * so that in practice the named file may not be known until run time.
  * <p>
  * Here are some examples:<pre>
  *
  *     #include "unparsed.txt"
  *     #include "$variableUnparsedFile"
  * </pre>
  */
class IncludeDirective implements Directive
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
     * @param fileName an object representing the file to read in
     */
   IncludeDirective(Macro fileName) {
      _fileName = fileName;
   }

   static private String getFile(Context c, String name) 
      throws GetFileException
   {
      String error = null;
      String result = null;
      String fileName = null;
      try {
         fileName = name.toString();
         result = 
            c.getBroker().get("url", fileName).toString();
      } catch (NotFoundException ne) {
         error = "Cannot include " + fileName + ": NOT FOUND";
      }  catch (NullPointerException ne) {
         ne.printStackTrace();
         error = "Could not load target " + name + ": NULL VALUE";
      }
      if (error != null) {
         throw new GetFileException("#include failed: " + error);
      }
      return result;
   }

   public static Object build(BuildContext rc, Object target)
      throws BuildException
   {
      if (target instanceof Macro) {
         return new IncludeDirective((Macro) target);
      }  else {
         if (target == null) {
            throw new BuildException("Cannot #include null filename");
         }
         try {
            return getFile(rc,target.toString());
         } catch(GetFileException e) {
            throw new BuildException(e.getMessage());
         }
      }
   }

   /**
     * Apply the values in the supplied context to evaluate the 
     * include filename; then read that file in and return it as 
     * a string. 
     * @exception PropertyException is required data is missing
     */ 
   final public Object evaluate(Context context)
      throws PropertyException
   {

      Object fname = _fileName.evaluate(context);
      if (fname == null) {
         throw new PropertyException(
               "#include could not resolve filename: " +
               "target argument resolved to a null.");
      }
      try {
         return getFile(context, fname.toString());
      } catch (GetFileException e) {
         throw new PropertyException(e.getMessage());
      }
   }  

   /**
     * Apply the values in the supplied context to evaluate the 
     * include filename; then read that file in and write it out
     * to the supplied Writer.
     * <p>
     * @exception IOException if an error occurred with out
     * @exception PropertyException if required data was missing
     */
   final public void write(FastWriter out, Context context) 
      throws PropertyException, IOException
   {
      out.write((String) evaluate(context));
   }
}

class GetFileException extends Exception
{
   GetFileException(String msg) {
      super(msg);
   }
}
