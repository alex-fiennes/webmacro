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
  * ProfileDirective wraps a block of text on a template and 
  * times it. The timing information can be inspected using a
  * tool like the examples/Profile servlet. 
  * <p>
  * @see Macro
  */
final class ProfileDirective implements Directive, Visitable
{

   /**
     * This is the block for which we are generating timing statistics
     */
   final private Macro myBlock;

   /**
     * This is the name associated with the timing statistics
     */
   final private String myName;

   /**
     * Create a new profile timing block
     */
   ProfileDirective(String name, Macro block) 
   { 
      myName = name;
      myBlock = block;
   }

   public static final Object build(BuildContext rc, Object name, Macro block)
      throws BuildException
   {
      if (name instanceof Macro) {
         throw new BuildException(
            "Profile name must be a static string, not a dynamic macro");
      }
      if (Flags.PROFILE) {
         return new ProfileDirective(name.toString(), block);
      } else {
         return block;
      }
   }

   /**
     * Evaluate the current macro and return it as a string. Same
     * basic operation as calling write.
     * @exception PropertyException is required data is missing
     */ 
   public Object evaluate(Context context) throws PropertyException
   {
      Object ret;
      boolean timing = context.isTiming();
      if (timing) context.startTiming(myName);
      try {
         ret = myBlock.evaluate(context);
      } finally {
         if (timing) context.stopTiming();
      }
      return ret;
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
      boolean timing = context.isTiming();
      if (timing) context.startTiming(myName);
      myBlock.write(out,context);
      if (timing) context.stopTiming();
   }

   public void accept(TemplateVisitor v) {
      v.beginDirective("profile");
      v.visitDirectiveArg("ProfileName", myName);
      v.visitDirectiveArg("ProfileBlock", myBlock);
      v.endDirective();
   }
   
}

