package org.webmacro.directive;

import java.io.*;
import org.webmacro.*;
import org.webmacro.engine.*;

public class EscapeDirective extends Directive {

  private static final int ESCAPE_TARGET = 1;

  private static final EscapeFilter escapeFilter = new EscapeFilter();

  private static final ArgDescriptor[] 
    myArgs = new ArgDescriptor[] {
      new LValueArg(ESCAPE_TARGET), 
    };

  private static final DirectiveDescriptor 
    myDescr = new DirectiveDescriptor("escape", null, myArgs, null);

  public static DirectiveDescriptor getDescriptor() {
    return myDescr;
  }

  public Object build(DirectiveBuilder builder, 
                      BuildContext bc) 
  throws BuildException {
    Variable target = null;

    try { 
      target = (Variable) builder.getArg(ESCAPE_TARGET, bc);
      bc.addFilter(target, escapeFilter);
    }
    catch (ClassCastException e) { 
      throw new NotVariableBuildException(myDescr.name, e);
    }
    return null;
  }

  public void write(FastWriter out, Context context) 
    throws PropertyException, IOException {
  } 

}
