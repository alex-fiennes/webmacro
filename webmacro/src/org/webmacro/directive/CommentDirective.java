package org.webmacro.directive;

import java.io.*;
import org.webmacro.*;
import org.webmacro.engine.*;

public class CommentDirective extends Directive {

  private static final int COMMENT_BLOCK = 1;

  private static final ArgDescriptor[] 
    myArgs = new ArgDescriptor[] {
      new LiteralBlockArg(COMMENT_BLOCK)
    };

  private static final DirectiveDescriptor 
    myDescr = new DirectiveDescriptor("comment", null, myArgs, null);

  public static DirectiveDescriptor getDescriptor() {
    return myDescr;
  }

  public Object build(DirectiveBuilder builder, 
                      BuildContext bc) 
    throws BuildException {
    return null;
  }

  public void write(FastWriter out, Context context) 
    throws PropertyException, IOException {
  } 

  public void accept(TemplateVisitor v) {
    v.beginDirective(myDescr.name);
    v.endDirective();
  }
}
