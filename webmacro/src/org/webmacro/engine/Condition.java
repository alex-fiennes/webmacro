
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
import org.webmacro.util.*;
import org.webmacro.*;
import java.io.*;
import org.webmacro.*;


/**
  *  Conditions evaluate() to Boolean.TRUE or Boolean.FALSE
  *  depending on its contents and the supplied context.
  *  You can build expressions from a combination of the
  *  operators ! && || == and Terms; you can also group 
  *  sub-expressions  with parentheses. An expression is true if 
  *  it is non-null; and false if it is Boolean.FALSE or null. 
  *  Beware that null values incur a slight performance penalty; 
  *  therefore we recommend you use boolean values in expressions 
  *  wherever possible.
  *
  *  Note that Condition DOES NOT implement macro: it supports all
  *  the required methods, but does not implement it. All of the
  *  implementations of Condition implement Macro except for one:
  *  ConstantCondition. 
  */
abstract public class Condition 
{

   /**
     * Evaluate the condition and write it out
     */
   final public void write(Writer out, Context context) 
      throws InvalidContextException, IOException
   {
      out.write(evaluate(context).toString());
   }

   /**
     * Return an object representing this condition
     */
   public Object evaluate(Context context) 
      throws InvalidContextException
   {
      if (test(context)) {
         return Boolean.TRUE;
      } else {
         return Boolean.FALSE;
      }
   }

   /**
     * Returns true if the condition is logically true; returns false if the
     * condition is either logically false or undefined.
     */
   public abstract boolean test(Context context);


   /**
     * Does the object correspond to something that would be considered true? In
     * particular, it is not null, not Boolean.FALSE, and not a zero Integer or Long.
     */
   static public final boolean isTrue(Object o) {
      return ((o != null) && !o.equals(FALSE) && !o.equals(ZERO) && !o.equals(LZERO));
   }
   final private static Integer ZERO = new Integer(0);
   final private static Long LZERO = new Long(0);
   final private static Boolean FALSE = Boolean.FALSE;

}



