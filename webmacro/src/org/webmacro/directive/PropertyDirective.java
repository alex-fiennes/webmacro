package org.webmacro.directive;

import java.io.*;
import org.webmacro.*;
import org.webmacro.engine.*;

public class PropertyDirective extends Directive {

  private static final int PROPERTY_TARGET = 1;

  private static final ArgDescriptor[] 
    myArgs = new ArgDescriptor[] {
      new LValueArg(PROPERTY_TARGET), 
    };

  private static final DirectiveDescriptor 
    myDescr = new DirectiveDescriptor("property", null, myArgs, null);

  public static DirectiveDescriptor getDescriptor() {
    return myDescr;
  }

  public Object build(DirectiveBuilder builder, 
                      BuildContext bc) 
  throws BuildException {
    Variable target = null;

    try { 
      target = (Variable) builder.getArg(PROPERTY_TARGET, bc);
    }
    catch (ClassCastException e) { 
      throw new NotVariableBuildException(myDescr.name, e);
    }
    if (!target.isSimpleName()) 
      throw new NotSimpleVariableBuildException(myDescr.name);

    bc.setVariableType(target.getName(), Variable.PROPERTY_TYPE);
    return null;
  }

  public void write(FastWriter out, Context context) 
    throws ContextException, IOException {
  } 

}

