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
  * This directive is used to set variables in the context data map 
  * if a variable contains a dot operator the variable will be resolved
  * accordingly. Expects the following syntax:
  * #set $abc = Term 
  */
final class SetDirective implements Directive, Visitable
{

   final private Variable myVariable;
   final private Object myValue;
   final private static String[] _verbs = { "=" };

   final public static String[] getArgumentNames() { return _verbs; }

   /**
     *  Constuctor for SetDirective.
     */
   SetDirective(Object inVariable, Object inValue) {
      myVariable = (Variable) inVariable;
      myValue= inValue;
   }


   public static final Object build(BuildContext rc, 
         Object lhs, Argument[] args) throws BuildException
   {

      if ((args.length != 1) || (! args[0].getName().equals("="))) {
         throw new BuildException("SetDirective requires an '=' argument");
      }
      Object rhs = args[0].getValue();

      Variable lhsVar;
      try {
         lhsVar = (Variable) lhs;
      } catch (ClassCastException e) {
         throw new BuildException("lhs of set must be a variable" +
               "(was " + lhs.getClass() + ")");
      }
      return new SetDirective(lhsVar, rhs); 
   }

   public static final String getVerb() { return "="; }

   /**
     * Interpret the directive and write it out
     * @exception PropertyException is required data is missing
     * @exception IOException if could not write to output stream
     */
   public final void write(FastWriter out, Context context) 
      throws PropertyException, IOException
   {
       
      Object errorString;
      // on fail evaluate returns a String
      if ((errorString = evaluate(context)) != null) {
         out.write(errorString.toString());  
      } 
       
   }

   /**
     * Evaluate the current macro. The evaluate function for the list
     * directive
     */ 
   public final Object evaluate(Context context)
   {
      try {
         if (myValue instanceof Macro) {
            myVariable.setValue(context, ((Macro) myValue).evaluate(context));
         } else {
            myVariable.setValue(context, myValue);
         }
      } catch (PropertyException e) {
         context.getLog("engine", "parsing and template execution").warning("Set: Unable to set value: " + myVariable);
         return ("<!--\n Unable to set value: " + myVariable + " \n-->");
      } 
      return null;
   }  

   public void accept(TemplateVisitor v) {
      v.beginDirective("set");
      v.visitDirectiveArg("SetTarget", myVariable);
      v.visitDirectiveArg("SetValue", myValue);
      v.endDirective();
   }

}


