package org.webmacro.directive;

import java.util.*;
import org.webmacro.*;
import org.webmacro.engine.*;

public final class DirectiveBuilder implements Builder, DirectiveArgs
{
  // The descriptor for the directive we're building
  private final DirectiveDescriptor desc;

  // An instance of the Directive object 
  private Directive directive;

  // The directive arguments
  private ArgsHolder buildArgs;

  // This is an array of either ArgsHolders (for nonrepeating subdirectives)
  // or Vectors of ArgsHolders (for repeating subdirectives)
  private Object[] subdirectives;

  private static final ArgsHolder[] aha = new ArgsHolder[0];
  private static final String
    EXC_GETSUBD_NOTREPEATING = "getSubdirective: attempt to get repeating "
      + "subdirective -- use getRepeatingSubdirective() instead", 
    EXC_GETSUBD_REPEATING = "getRepeatingSubdirective: attempt to get " 
      + "nonrepeating subdirective -- use getSubdirective() instead";

  public DirectiveBuilder(DirectiveDescriptor desc) {
    this.desc = desc;
    if (desc.args != null
        && desc.args.length > 0)
      buildArgs = new ArgsHolder(desc.args);
    if (desc.subdirectives != null
        && desc.subdirectives.length > 0)
      subdirectives = new Object[desc.subdirectives.length];
    try {
      directive = (Directive) desc.dirClass.newInstance();
    } catch (Exception e) { /* @@@ */ };
  }

  private int findSubdirectiveIndex(int id) 
    throws BuildException {
    for (int i=0; i<desc.subdirectives.length; i++) {
      if (desc.subdirectives[i].id == id)
        return i;
    }
    throw new BuildException("Invalid argument ID " + id 
                             + " requested for directive " + desc.name);
  }

  public Object getArg(int argId) 
    throws BuildException {
    if (buildArgs == null)
      return null;
    else 
      return buildArgs.getArg(argId);
  }

  public Object getArg(int argId, BuildContext bc) 
    throws BuildException {
    if (buildArgs == null)
      return null;
    else
      return buildArgs.getArg(argId, bc);
  }

  public int getSubdirectiveCount(int subdId) 
    throws BuildException {
    int i = findSubdirectiveIndex(subdId);
    if (subdirectives[i] == null)
      return 0;
    else 
      return (desc.subdirectives[i].repeating) 
        ? ((ArgsHolder[]) (subdirectives[i])).length
        : 1;
  }

  public void setArg(int argId, Object arg) 
  throws BuildException {
    buildArgs.setArg(argId, arg);
  }

  public DirectiveArgs newSubdirective(int subdId) 
  throws BuildException {
    ArgsHolder ah = null;
    int index = findSubdirectiveIndex(subdId);
    if (!desc.subdirectives[index].repeating) {
      if (subdirectives[index] == null) 
        subdirectives[index] = new ArgsHolder(desc.subdirectives[index].args);
      ah = (ArgsHolder) subdirectives[index]; 
    }
    else {
      Vector v;
      if (subdirectives[index] == null)
        subdirectives[index] = new Vector();
      v = (Vector) subdirectives[index];
      ah = new ArgsHolder(desc.subdirectives[index].args);
      v.addElement(ah);
    }

    return ah;
  }

  public ArgsHolder getSubdirective(int subdId) 
    throws BuildException {
    int index = findSubdirectiveIndex(subdId);
    if (desc.subdirectives[index].repeating) 
      throw new BuildException(EXC_GETSUBD_NOTREPEATING);
    return (ArgsHolder) subdirectives[index];
  }

  public ArgsHolder[] getRepeatingSubdirective(int subdId) 
    throws BuildException {
    int index = findSubdirectiveIndex(subdId);
    if (!desc.subdirectives[index].repeating) 
      throw new BuildException(EXC_GETSUBD_REPEATING);
    if (subdirectives[index] == null)
      return null;
    else
      return (ArgsHolder[]) ((Vector) subdirectives[index]).toArray(aha);
  }

  public Object build(BuildContext bc) throws BuildException {
    return directive.build(this, bc);
  }

}



