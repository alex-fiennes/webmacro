package org.webmacro.directive;

import java.io.*;
import org.webmacro.*;
import org.webmacro.engine.*;

public class TextDirective extends Directive {

  private static final int TEXT_BLOCK = 1;

  private Block block; 

  private static final ArgDescriptor[] 
    myArgs = new ArgDescriptor[] {
      new LiteralBlockArg(TEXT_BLOCK)
    };

  private static final DirectiveDescriptor 
    myDescr = new DirectiveDescriptor("text", null, myArgs, null);

  public static DirectiveDescriptor getDescriptor() {
    return myDescr;
  }

  public Object build(DirectiveBuilder builder, 
                      BuildContext bc) 
  throws BuildException {
    block = (Block) builder.getArg(TEXT_BLOCK, bc);
    return this;
  }

  public void write(FastWriter out, Context context) 
    throws PropertyException, IOException {
    block.write(out, context);
  } 

  public void accept(TemplateVisitor v) {
    v.beginDirective(myDescr.name);
    block.accept(v);
    v.endDirective();
  }

}
