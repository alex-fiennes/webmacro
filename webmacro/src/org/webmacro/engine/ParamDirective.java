
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
  * This directive is used to set variables in the context data map 
  * if a variable contains a dot operator the variable will be resolved
  * accordingly. Expects the following syntax:
  * #set $abc = Term 
  */
abstract class ParamDirective implements Directive
{

   public static final Object build(BuildContext rc,
         Object lhs, Object rhs)
      throws BuildException
   {
      Hashtable paramContext = rc.getParameters();

      
      // perform static evaluation of parameters
      try {
         if (rhs instanceof Macro) {
            rhs = ((Macro) rhs).evaluate(paramContext);
         }
         Variable v = (Variable) lhs;
         v.setValue(paramContext, rhs);
      } catch (ClassCastException e) {
         throw new BuildException("lhs of param set must be a variable");  
      } catch (Exception e) {
         throw new BuildException("Static evaluation of parameter " + lhs + " failed: " + e + " -- parameters must be statically resolvable based on a context containing other parameters only.");
      }

      return null; // not to be included in output
   }

   public static final String getVerb() { return "="; }

}


