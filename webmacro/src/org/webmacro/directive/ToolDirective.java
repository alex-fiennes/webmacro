package org.webmacro.directive;

import java.io.*;
import org.webmacro.*;
import org.webmacro.engine.*;

public class ToolDirective extends Directive {

  private static final int TOOL_TARGET = 1;

  private static final ArgDescriptor[] 
    myArgs = new ArgDescriptor[] {
      new LValueArg(TOOL_TARGET), 
    };

  private static final DirectiveDescriptor 
    myDescr = new DirectiveDescriptor("tool", null, myArgs, null);

  public static DirectiveDescriptor getDescriptor() {
    return myDescr;
  }

  public Object build(DirectiveBuilder builder, 
                      BuildContext bc) 
  throws BuildException {
    Variable target = null;

    try { 
      target = (Variable) builder.getArg(TOOL_TARGET, bc);
    }
    catch (ClassCastException e) { 
      throw new NotVariableBuildException(myDescr.name, e);
    }
    if (!target.isSimpleName()) 
      throw new NotSimpleVariableBuildException(myDescr.name);

    bc.setVariableType(target.getName(), Variable.TOOL_TYPE);
    return null;
  }

  public void write(FastWriter out, Context context) 
    throws PropertyException, IOException {
  } 

}



