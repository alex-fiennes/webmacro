package org.webmacro.directive;

import java.io.*;
import org.webmacro.*;
import org.webmacro.engine.*;

public class CommentDirective extends Directive {

  private static final int COMMENT_BLOCK = 1;

  private static final ArgDescriptor[] 
    commentArgs = new ArgDescriptor[] {
      new LiteralBlockArg(COMMENT_BLOCK)
    };

  private static final DirectiveDescriptor 
    commentDescr = new DirectiveDescriptor("comment", CommentDirective.class, 
                                           commentArgs, null);

  public static DirectiveDescriptor getDescriptor() {
    return commentDescr;
  }

  public Object build(DirectiveBuilder builder, 
                      BuildContext bc) 
    throws BuildException {
    return null;
  }

  public void write(FastWriter out, Context context) 
    throws ContextException, IOException {
  } 

}
