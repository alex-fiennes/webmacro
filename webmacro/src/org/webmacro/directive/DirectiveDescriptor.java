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

import org.webmacro.directive.Directive.ArgDescriptor;
import org.webmacro.directive.Directive.Subdirective;

/**
 * Each directive needs a DirectiveDescriptor to describe how it
 * should be parsed and built by the parser.  The directive descriptor
 * identifies the directive's name, the class of the underlying concrete
 * directive object, a list of directive argument descriptors, and a list
 * of subdirective descriptors.  
 * 
 * If the directive does not specify the class, the directive provider will
 * fill it in automatically.  
 *
 * The args field is an array of Directive.ArgDescriptor objects.  There
 * are static nested classes within Directive for each type of argument 
 * -- Condition, LValue, RValue, Keyword, Punctuation, Block, LiteralBlock, 
 * and special argument descriptors for OptionalGroup and OptionalChoice.  
 * These allow the directive writer to specify 
 * a flexible syntax for directive arguments.  
 * Each directive can have a set of subdirectives, and each subdirective
 * can have its own argument list.  Subdirectives can be required, optional, 
 * or optional-repeating (multiple subdirectives of the same kind, like 
 * #elseif.)  There are constructors for Subdirective in the Directive module
 * as well.  
 * @author Brian Goetz
 */

public final class DirectiveDescriptor { 
  public String                   name;
  public Class                    dirClass;
  public ArgDescriptor[]          args;
  public Subdirective[]           subdirectives;

  public boolean                  valid=false, 
                                  hasBreakingSubdirectives=false;

  public DirectiveDescriptor(String name, 
                             Class dirClass, 
                             ArgDescriptor[] args, 
                             Subdirective[]  subdirectives) {
    this.name = name;
    this.dirClass = dirClass;
    this.args = args;
    this.subdirectives = subdirectives;

    completeArgs(this.args);
    valid = validateArgs(this.args);

    if (subdirectives != null) {
      for (int i=0; i<this.subdirectives.length; i++) {
        completeArgs(this.subdirectives[i].args);
        valid &= validateArgs(this.subdirectives[i].args);
        if (this.subdirectives[i].isBreaking)
          hasBreakingSubdirectives = true;
      }
    }
  }

  /**
   * Determines the index of the next argument following a given argument.
   * May return an index > args.length.
   */
  private static int nextArg(ArgDescriptor[] args, int i) {
    if (args[i].type == Directive.ArgType_GROUP
        || args[i].type == Directive.ArgType_CHOICE) {
      int k = i+1;
      for (int j=0; j<args[i].subordinateArgs; j++) 
        k = nextArg(args, k);
      return k;
    }
    else
      return i+1;
  }

  /**
   * Set the nextArg, children[] fields as necessary 
   */
  private static void completeArgs(ArgDescriptor[] args) {
    int j, k;

    for (int i=0; i<args.length; i++) 
      args[i].nextArg = nextArg(args, i);

    for (int i=0; i<args.length; i++) {
      switch (args[i].type) {
      case Directive.ArgType_GROUP:
      case Directive.ArgType_CHOICE:
        args[i].children = new int[args[i].subordinateArgs];
        for (j=0, k=i+1; j<args[i].subordinateArgs; j++) {
          args[i].children[j] = k;
          k = args[k].nextArg;
        }
        break;

      default:
        break;
      }
    }
  }

  /**
   * Make sure that the structure of the arguments list is valid.  
   * This means that 
   *   GROUP arguments begin with a keyword, not optional
   *   GROUP arguments cannot contain CHOICE arguments
   *   Each of the children of a CHOICE argument is an OPTIONAL GROUP 
   */
  private static boolean validateArgs(ArgDescriptor[] args) {
    boolean valid = true;
    for (int i=0; i<args.length; i++) {
      if (args[i].type == Directive.ArgType_GROUP) {
        if (args[i].subordinateArgs == 0)
          valid = false;
        else if (args[args[i].children[0]].type != Directive.ArgType_KEYWORD
            || args[args[i].children[0]].optional)
          valid = false;
        for (int j=0; j<args[i].subordinateArgs; j++) {
          if (args[args[i].children[j]].type == Directive.ArgType_CHOICE)
            valid = false;
        };
      }
      else if (args[i].type == Directive.ArgType_CHOICE) {
        for (int j=0; j<args[i].subordinateArgs; j++) {
          if (args[args[i].children[j]].type != Directive.ArgType_GROUP
              || !args[args[i].children[j]].optional)
            valid = false;
        }
      }
    }

    return valid;
  }
}
