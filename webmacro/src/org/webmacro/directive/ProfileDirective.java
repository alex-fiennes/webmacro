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

package org.webmacro.directive;

import java.io.*;
import org.webmacro.*;
import org.webmacro.engine.*;

public class ProfileDirective extends Directive {

  private static final int PROFILE_NAME = 1;
  private static final int PROFILE_BLOCK = 2;

  /**
    * This is the block for which we are generating timing statistics
    */
  private Block myBlock;

  /**
    * This is the name associated with the timing statistics
    */
  private String myName;

  private static final ArgDescriptor[] 
    myArgs = new ArgDescriptor[] {
      new QuotedStringArg(PROFILE_NAME), 
      new BlockArg(PROFILE_BLOCK)
    };

  private static final DirectiveDescriptor 
    myDescr = new DirectiveDescriptor("profile", null, myArgs, null);

  public static DirectiveDescriptor getDescriptor() {
    return myDescr;
  }

  public Object build(DirectiveBuilder builder, 
                      BuildContext bc) 
  throws BuildException {
    // If profiling is not enabled just return the profiled block
    if (!Flags.PROFILE) {
      return builder.getArg(PROFILE_BLOCK, bc);
    }

    Object name = builder.getArg(PROFILE_NAME, bc);
    if (name instanceof Macro) {
      throw new BuildException(
        "Profile name must be a static string, not a dynamic macro");
    }
    myName = name.toString();
    myBlock = (Block)builder.getArg(PROFILE_BLOCK, bc);

    return this;
  }

  public void write(FastWriter out, Context context) 
    throws PropertyException, IOException {

    boolean timing = context.isTiming();
    if (timing) context.startTiming(myName);
    myBlock.write(out,context);
    if (timing) context.stopTiming();
  } 

  public void accept(TemplateVisitor v) {
    v.beginDirective(myDescr.name);
    v.visitDirectiveArg("ProfileName", myName);
    v.visitDirectiveArg("ProfileBlock", myBlock);
    v.endDirective();
  }

}
