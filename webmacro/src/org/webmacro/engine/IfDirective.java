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
  * An if directive is used to include a block only when a condition 
  * is true. Structurally it has a condition, followed by a block, 
  * followed by an optional "#else" statmeent and another block.
  * 
  * Here is an example:<pre>
  *
  *     #if ($userID) {
  *        Your user id is: $userID
  *     } #else {
  *        &lt;a href="register"&gt;Click here to register&lt;/a&gt;
  *     }
  *
  * </pre> The condition is true if it evaluates to a non-null value. The 
  * #if block will be included if (and only if) the condition evaluates
  * to a non-null value. The #else block will be included if (and
  * only if) the condition evaluates to null.
  * <p>
  * When evaluating whether a condition is null or not, the IfDirective
  * will check whether it is a Macro. If it is a Macro, it calls 
  * Macro.evaluate() and checks whether the return value is null. If 
  * it is not a macro, then it is true or false simply based on whether
  * the value passed in was actually null itself.
  * <p>
  * @see Macro
  */
final class IfDirective implements Directive, Visitable
{

   /**
     * We are true if this object is defined and not null
     */
   final private Condition myCondition;

   /**
     * This is the block we include if myCondition is not null
     */
   final private Macro myIfBlock;

   /**
     * This is the block we include if myCondition is null
     */
   final private Macro myElseBlock;

   /**
     * Create a new IfCondition
     * <p>
     * @param condition True when this evaluates to a non-Null value
     * @param ifBlock included when condition is non-null
     * @param elseBlock (may be null) included when condition is null
     */
   IfDirective(Condition cond, Macro ifBlock, Macro elseBlock) 
   { 
       myCondition = cond; 
       myIfBlock =  ifBlock;
       myElseBlock =  elseBlock;
   }

   public static final String[] getSubDirectives() {
      String[] deps = { "else" };
      return deps;
   }

   public static final Object build(BuildContext rc,
         Condition cond, Macro body, Object elseDir)
      throws BuildException
   {
      if (cond instanceof Macro) {
         Macro elseMacro = (elseDir != null) ? 
            MacroAdapter.createMacro(elseDir) : null;
         return new IfDirective(cond, body, elseMacro);  
      } else {
         return (cond.test(rc)) ? body : elseDir;
      }
   }

   public static final String[] getSubordinateNames()
   {
      String[] names = { "else" };
      return names;
   }


   /**
     * Evaluate the current macro and return it as a string. Same
     * basic operation as calling write.
     * @exception PropertyException is required data is missing
     */ 
   public Object evaluate(Context context)
      throws PropertyException
   {
      FastWriter fw = FastWriter.getInstance(context.getBroker());
      try {
         write(fw,context);
         String ret = fw.toString();
         fw.close();
         return ret;
      } catch (IOException e) {
         e.printStackTrace(); // never gonna happen
         return null;
      }
   }  

   /**
     * Interpret the directive and write it out to the supplied stream.
     * <p>
     * @exception PropertyException if required data was missing from context
     * @exception IOException if we could not successfully write to out
     */
   public void write(FastWriter out, Context context) 
      throws PropertyException, IOException
   {

      if (myCondition.test(context)) 
      {
         if (myIfBlock != null) {
            myIfBlock.write(out, context);
         } else {
            context.getLog("engine").warning("If: Block for an #if directive was null");
         }
      } else { 
         if (myElseBlock != null) {
            myElseBlock.write(out, context);
         }
      } 
   }

   public void accept(TemplateVisitor v) {
      v.beginDirective("if");
      v.visitDirectiveArg("IfCondition", myCondition);
      v.visitDirectiveArg("IfBlock", myIfBlock);
      v.visitDirectiveArg("ElseBlock", myElseBlock);
      v.endDirective();
   }
   
}


