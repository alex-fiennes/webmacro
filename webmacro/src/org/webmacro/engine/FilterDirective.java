
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
  * This directive is used to set variables in the context data map 
  * if a variable contains a dot operator the variable will be resolved
  * accordingly. Expects the following syntax:
  * #set $abc = Term 
  */
final class FilterDirective implements Directive
{


   private static final String[] _verbs = { "through" };
   public static final String[] getArgumentNames() { return _verbs; }

   public static final Object build(BuildContext rc, 
         Object subject, Argument[] args) throws BuildException
   {
      Variable v = null;
      Filter f = null;
      Object rhs;

      // get the variable we want to operate on
      try {
         v = (Variable) subject;
      } catch (ClassCastException ce) {
         throw new BuildException("First argument to #filter must be a variable");
      }

      // get the filter target

      if ((args.length != 1) || (! args[0].getName().equals("through"))) {
         throw new BuildException(
               "Filter directive takes a single argument: through");
      }
      rhs = args[0].getValue();

      try {
         if (rhs instanceof Macro) {
            rhs = ((Macro) rhs).evaluate(rc);
         }
      } catch (InvalidContextException e) {
         throw new BuildException("You can only specify statically resolvable terms in a filter directive, since it is resolved at compile time: " + e);
      }

      if (rhs instanceof Filter) {
         // it already is a filter
         f = (Filter) rhs;
      } else if (rhs != null) {
         // we need to look it up
         try {
            f = (Filter) rc.getBroker().getValue("template", rhs.toString());
         } catch (InvalidTypeException e) {
            throw new BuildException("There appears to be a programming error in FilterDirective, please report it to the WebMacro mailing list! Here is the error: " + e);
         } catch (NotFoundException e) {
            throw new BuildException(
                  "The template you are attempting to filter through (" 
                  + rhs + ") could not be loaded: " + e);
         }
      }

      String[] names = v.getPropertyNames();
      if (names.length != 1) {
         throw new BuildException(
               "Filters can only be set on top level property names."
               + " You tried to set one on a complex variable: " + v);
      }
      rc.addFilter(names[0], f);
      return null;
   }

   /**
     * Interpret the directive and write it out
     * @exception InvalidContextException is required data is missing
     * @exception IOException if could not write to output stream
     */
   public final void write(Writer out, Context context) 
      throws InvalidContextException, IOException
   {
      // do nothing
   }

   /**
     * Evaluate the current macro. The evaluate function for the list
     * directive
     */ 
   public final Object evaluate(Context context)
   {
      // do nothing
      return null;
   }  
}

