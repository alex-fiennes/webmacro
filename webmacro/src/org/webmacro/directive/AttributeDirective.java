package org.webmacro.directive;

import java.io.*;
import org.webmacro.*;
import org.webmacro.engine.*;

/**
 * The #attribute directive allows you to set a template attribute such
 * that it is accessible from the servlet.  
 */

public class AttributeDirective extends Directive {

  private static final int ATTRIBUTE_TARGET = 1;
  private static final int ATTRIBUTE_RESULT = 2;

  private static final ArgDescriptor[] 
    myArgs = new ArgDescriptor[] {
      new LValueArg(ATTRIBUTE_TARGET), 
      new AssignmentArg(),
      new RValueArg(ATTRIBUTE_RESULT)
    };

  private static final DirectiveDescriptor 
    myDescr = new DirectiveDescriptor("attribute", null, myArgs, null);

  public static DirectiveDescriptor getDescriptor() {
    return myDescr;
  }

  public Object build(DirectiveBuilder builder, 
                      BuildContext bc) 
  throws BuildException {
    Variable target = null;
    try {
      target = (Variable) builder.getArg(ATTRIBUTE_TARGET, bc);
      Object result = builder.getArg(ATTRIBUTE_RESULT, bc);
      if (!target.isSimpleName()) 
        throw new NotSimpleVariableBuildException(myDescr.name);

      target.setValue(bc, result);
    }
    catch (ClassCastException e) {
      throw new NotVariableBuildException(myDescr.name, e);
    }
    catch (ContextException e) {
      throw new BuildException("#attribute: Exception setting variable " 
                               + target.toString(), e);
    }
    return null;
  }

  public void write(FastWriter out, Context context) 
    throws ContextException, IOException {
  } 

}

