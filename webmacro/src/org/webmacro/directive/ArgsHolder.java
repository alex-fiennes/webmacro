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
  // If the argument is repeating, we'll store a Vector in the corresponding
  // slot instead.   
  private Object[] buildArgs;

  private static final String 
    EXC_GETARG_ARRAY = 
    "getArg(): Can't call getArg(id, bc) for repeating argument; "
      + "use getArg(id) instead";


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
    Object o = buildArgs[index];
    if (args[index].repeating
        && o != null)
      o = ((Vector) o).toArray();
    return o;
  }

  public final Object getArg(int id, BuildContext bc) 
    throws BuildException {
    int index = findArgIndex(id);
    if (args[index].repeating) 
      throw new BuildException(EXC_GETARG_ARRAY);

    Object o = buildArgs[index];
    return (o instanceof Builder) 
      ? ((Builder) o).build(bc)
      : o;
  }

  public final void setArg(int id, Object o) 
    throws BuildException {
    int index = findArgIndex(id);
    if (args[index].repeating) {
      if (buildArgs[index] == null)
        buildArgs[index] = new Vector();
      ((Vector) buildArgs[index]).addElement(o);
    }
    else {
      buildArgs[index] = o;
    }
  }

}

