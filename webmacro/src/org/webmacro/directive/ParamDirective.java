package org.webmacro.directive;

import java.io.*;
import org.webmacro.*;
import org.webmacro.engine.*;

/**
 * The #param directive is provided for backward compatibility.  
 * Invoking
 *   #param $a="stuff"
 * is equivalent to
 *   #attribute $a="stuff"
 *   #set $a="stuff"
 */

public class ParamDirective extends Directive {

  private static final int PARAM_TARGET = 1;
  private static final int PARAM_RESULT = 2;

  private static final String SIMPLE_VAR_EXC =  
    "#param: Attribute variable must have only one term.";

  private static final ArgDescriptor[] 
    myArgs = new ArgDescriptor[] {
      new LValueArg(PARAM_TARGET), 
      new AssignmentArg(),
      new RValueArg(PARAM_RESULT)
    };

  private static final DirectiveDescriptor 
    myDescr = new DirectiveDescriptor("param", null, myArgs, null);

  public static DirectiveDescriptor getDescriptor() {
    return myDescr;
  }

  public Object build(DirectiveBuilder builder, 
                      BuildContext bc) 
  throws BuildException {
    Variable target = null;
    Object result = null;
    try {
      target = (Variable) builder.getArg(PARAM_TARGET, bc);
      result = builder.getArg(PARAM_RESULT, bc);
      if (!target.isSimpleName()) 
        throw new BuildException(SIMPLE_VAR_EXC);

      target.setValue(bc, result);
    }
    catch (ContextException e) {
      throw new BuildException("#param: Exception setting variable " 
                               + target.toString(), e);
    }
    return new SetDirective(target, result);
  }

  public void write(FastWriter out, Context context) 
    throws ContextException, IOException {
  } 

}

