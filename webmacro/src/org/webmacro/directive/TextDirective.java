package org.webmacro.directive;

import java.io.*;
import org.webmacro.*;
import org.webmacro.engine.*;

public class TextDirective extends Directive {

  private static final int TEXT_BLOCK = 1;

  private Block block; 

  private static final ArgDescriptor[] 
    textArgs = new ArgDescriptor[] {
      new LiteralBlockArg(TEXT_BLOCK)
    };

  private static final DirectiveDescriptor 
    textDescr = new DirectiveDescriptor("text", TextDirective.class, textArgs, 
                                        null);

  public static DirectiveDescriptor getDescriptor() {
    return textDescr;
  }

  public Object build(DirectiveBuilder builder, 
                      BuildContext bc) 
  throws BuildException {
    block = (Block) builder.getArg(TEXT_BLOCK, bc);
    return this;
  }

  public void write(FastWriter out, Context context) 
    throws ContextException, IOException {
    block.write(out, context);
  } 

  public void accept(MacroVisitor v) {
    v.beginDirective("text");
    block.accept(v);
    v.endDirective();
  }

}
