package org.webmacro.directive;

import java.io.*;
import org.webmacro.*;
import org.webmacro.engine.*;

public class GlobalDirective extends Directive {

  private static final int GLOBAL_TARGET = 1;

  private static final ArgDescriptor[] 
    myArgs = new ArgDescriptor[] {
      new LValueArg(GLOBAL_TARGET), 
    };

  private static final DirectiveDescriptor 
    myDescr = new DirectiveDescriptor("global", null, myArgs, null);

  public static DirectiveDescriptor getDescriptor() {
    return myDescr;
  }

  public Object build(DirectiveBuilder builder, 
                      BuildContext bc) 
  throws BuildException {
    Variable target = null;

    try { 
      target = (Variable) builder.getArg(GLOBAL_TARGET, bc);
    }
    catch (ClassCastException e) { 
      throw new NotVariableBuildException(myDescr.name, e);
    }
    if (!target.isSimpleName()) 
      throw new NotSimpleVariableBuildException(myDescr.name);

    bc.setVariableType(target.getName(), Variable.LOCAL_TYPE);
    return null;
  }

  public void write(FastWriter out, Context context) 
    throws PropertyException, IOException {
  } 

}
