package org.webmacro.directive;

import java.io.*;
import org.webmacro.*;
import org.webmacro.engine.*;

public class EncodeDirective extends Directive {

  private static final int ENCODE_TARGET = 1;

  private static final EncodeFilter encodeFilter = new EncodeFilter();

  private static final ArgDescriptor[] 
    myArgs = new ArgDescriptor[] {
      new LValueArg(ENCODE_TARGET), 
    };

  private static final DirectiveDescriptor 
    myDescr = new DirectiveDescriptor("encode", null, myArgs, null);

  public static DirectiveDescriptor getDescriptor() {
    return myDescr;
  }

  public Object build(DirectiveBuilder builder, 
                      BuildContext bc) 
  throws BuildException {
    Variable target = null;

    try { 
      target = (Variable) builder.getArg(ENCODE_TARGET, bc);
      bc.addFilter(target, encodeFilter);
    }
    catch (ClassCastException e) { 
      throw new NotVariableBuildException(myDescr.name, e);
    }
    return null;
  }

  public void write(FastWriter out, Context context) 
    throws ContextException, IOException {
  } 

}
