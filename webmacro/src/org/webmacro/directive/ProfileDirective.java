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
