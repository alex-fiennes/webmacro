
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
  * This directive marks the named variable as being of local type
  */
abstract class LocalDirective implements Directive
{

   public static Object build(BuildContext rc, Object variable) 
      throws BuildException
   {

      try {
         Variable v = (Variable) variable;
         String nm[] = v.getPropertyNames();
         if (nm.length > 1) {
            throw new BuildException("Argument to #local cannot be a complex variable, must have only one term.");
         }
         rc.setVariableType(nm[0], Variable.LOCAL_TYPE);
      } catch (ClassCastException e) {
         throw new BuildException("Argument to #local must be a variable");
      }
      return null;
   }
}


