/*
 * Copyright (C) 1998-2000 Semiotek Inc.  All Rights Reserved.  
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted under the terms of either of the following
 * Open Source licenses:
 *
 * The GNU General Public License, version 2, or any later version, as
 * published by the Free Software Foundation
 * (http://www.fsf.org/copyleft/gpl.html);
 *
 *  or 
 *
 * The Semiotek Public License (http://webmacro.org/LICENSE.)  
 *
 * This software is provided "as is", with NO WARRANTY, not even the 
 * implied warranties of fitness to purpose, or merchantability. You
 * assume all risks and liabilities associated with its use.
 *
 * See www.webmacro.org for more information on the WebMacro project.  
 */

package org.webmacro.directive;

import java.util.*;
import org.webmacro.*;
import org.webmacro.engine.*;

/**
 * ArgsHolder is a container for directive arguments.  The parser creates
 * and populates the ArgsHolder based on the contents of the directive
 * descriptor.  The directive, in the build() method, will call the getArg()
 * methods to retrieve the arguments and build the directive.  
 * @author Brian Goetz
 */

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

  /**
   * Retrieve the argument whose id is the specified id.  
   */
  public final Object getArg(int id) 
    throws BuildException {
    int index = findArgIndex(id);
    return buildArgs[index];
  }

  /**
   * Retrieve the argument whose id is the specified id, and if it is a 
   * Builder, build it with the specified build context.
   */
  public final Object getArg(int id, BuildContext bc) 
    throws BuildException {
    int index = findArgIndex(id);

    Object o = buildArgs[index];
    return (o instanceof Builder) 
      ? ((Builder) o).build(bc)
      : o;
  }

  /**
   * Set the argument whose id is the specified id.  If the argument has
   * already been set, it is overwritten.  Generally not used by directives,
   * only used by the parser. 
   */

  public final void setArg(int id, Object o) 
    throws BuildException {
    int index = findArgIndex(id);
    buildArgs[index] = o;
  }

}

