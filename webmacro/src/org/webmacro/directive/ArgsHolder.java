package org.webmacro.directive;

import java.util.*;
import org.webmacro.*;
import org.webmacro.engine.*;

public final class ArgsHolder implements DirectiveArgs
{
  // The argument descriptors for the arguments we hold
  private Directive.ArgDescriptor[] args;

  // The parser will call setArg(), and we'll store them here.  For
  // each argument in the argument list, the corresponding argument data
  // will be in the same index into buildArgs.  
  private Object[] buildArgs;


  public ArgsHolder(Directive.ArgDescriptor[] args) {
    this.args = args;
    buildArgs = new Object[args.length];
  }

  private final int findArgIndex(int id) 
    throws BuildException {
    for (int i=0; i<args.length; i++) {
      if (args[i].id == id)
        return i;
    }
    throw new BuildException("Invalid argument ID " + id + " requested ");
  }

  public final Object getArg(int id) 
    throws BuildException {
    int index = findArgIndex(id);
    return buildArgs[index];
  }

  public final Object getArg(int id, BuildContext bc) 
    throws BuildException {
    int index = findArgIndex(id);

    Object o = buildArgs[index];
    return (o instanceof Builder) 
      ? ((Builder) o).build(bc)
      : o;
  }

  public final void setArg(int id, Object o) 
    throws BuildException {
    int index = findArgIndex(id);
    buildArgs[index] = o;
  }

}

