package org.webmacro.directive;

import java.io.*;
import org.webmacro.*;
import org.webmacro.engine.*;

public class SilenceDirective extends Directive {

  private static final int SILENCE_TARGET = 1;

  private static final SilenceFilter silenceFilter = new SilenceFilter();

  private static final ArgDescriptor[] 
    myArgs = new ArgDescriptor[] {
      new LValueArg(SILENCE_TARGET), 
    };

  private static final DirectiveDescriptor 
    myDescr = new DirectiveDescriptor("silence", null, myArgs, null);

  public static DirectiveDescriptor getDescriptor() {
    return myDescr;
  }

  public Object build(DirectiveBuilder builder, 
                      BuildContext bc) 
  throws BuildException {
    Variable target = null;

    try { 
      target = (Variable) builder.getArg(SILENCE_TARGET, bc);
      bc.addFilter(target, silenceFilter);
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
