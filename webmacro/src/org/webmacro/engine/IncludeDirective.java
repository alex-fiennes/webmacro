
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

import org.webmacro.util.java2.*;
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
   final private Object _fileName;
   final private Broker _broker;

   /**
     * Create an include directive. Note that the supplied variable
     * may be a macro, in which case it is evaluated to determine the
     * actual filename (it will be evaluated at run time, once 
     * given a context).
     * <p>
     * @param fileName an object representing the file to read in
     */
   IncludeDirective(Broker broker, Object fileName) {
      _broker = broker;
      _fileName = fileName;
   }


   public static Object build(BuildContext rc, 
         Object target)
   {
      IncludeDirective id = new IncludeDirective(rc.getBroker(),target);
      if (! (target instanceof Macro)) {
         try {
            return (id.evaluate(null));
         } catch (Exception e) {
            // try again at runtime
         }
      } 
      return id;
   }

   /**
     * Apply the values in the supplied context to evaluate the 
     * include filename; then read that file in and return it as 
     * a string. 
     * @exception InvalidContextException is required data is missing
     */ 
   final public Object evaluate(Object context)
      throws InvalidContextException
   {

      Object fname = null;
      try {
         fname = (_fileName instanceof Macro) ?
            ((Macro) _fileName).evaluate(context) : _fileName;

         if (fname == null) {
            throw new InvalidContextException("Attempt to include " 
                  + _fileName + " which evaluates to null");
         }
         return _broker.getValue("url",fname.toString());
      } catch (NotFoundException e) {
         throw new InvalidContextException("Attempt to include " 
               + _fileName + " which evaluates to the non-existant " + fname 
               + ": " + e); 
      } catch (InvalidTypeException e) {
         throw new InvalidContextException("Attempt to load URLs failed "
               + " broker does not have a URL provider:" + e);
      }
   }  

   /**
     * Apply the values in the supplied context to evaluate the 
     * include filename; then read that file in and write it out
     * to the supplied Writer.
     * <p>
     * @exception IOException if an error occurred with out
     * @exception InvalidContextException if required data was missing
     */
   final public void write(Writer out, Object context) 
      throws InvalidContextException, IOException
   {
      out.write((String) evaluate(context));
   }
}

