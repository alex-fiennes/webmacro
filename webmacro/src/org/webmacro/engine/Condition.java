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
import org.webmacro.util.*;
import org.webmacro.*;
import java.io.*;


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
   final public void write(FastWriter out, Context context) 
      throws PropertyException, IOException
   {
      out.write(evaluate(context).toString());
   }

   /**
     * Return an object representing this condition
     */
   public Object evaluate(Context context) 
      throws PropertyException
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



