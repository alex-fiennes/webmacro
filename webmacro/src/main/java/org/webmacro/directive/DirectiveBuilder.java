/*
 * Copyright (C) 1998-2000 Semiotek Inc. All Rights Reserved. Redistribution and use in source and
 * binary forms, with or without modification, are permitted under the terms of either of the
 * following Open Source licenses: The GNU General Public License, version 2, or any later version,
 * as published by the Free Software Foundation (http://www.fsf.org/copyleft/gpl.html); or The
 * Semiotek Public License (http://webmacro.org/LICENSE.) This software is provided "as is", with NO
 * WARRANTY, not even the implied warranties of fitness to purpose, or merchantability. You assume
 * all risks and liabilities associated with its use. See www.webmacro.org for more information on
 * the WebMacro project.
 */

package org.webmacro.directive;

import java.util.ArrayList;
import java.util.List;

import org.webmacro.engine.BuildContext;
import org.webmacro.engine.BuildException;
import org.webmacro.engine.Builder;


/**
 * DirectiveBuilder manages the building of directives. It is created by the parser, which populates
 * it with the directive arguments, and the DirectiveBuilder.build() method calls the build() method
 * for the appropriate directive.
 * 
 * @author Brian Goetz
 */

public final class DirectiveBuilder
  implements Builder, DirectiveArgs
{

  // The descriptor for the directive we're building
  private final DirectiveDescriptor desc;

  // The directive arguments
  private ArgsHolder buildArgs;

  // This is an array of either ArgsHolders (for nonrepeating subdirectives)
  // or Lists of ArgsHolders (for repeating subdirectives)
  private Object[] subdirectives;

  private static final ArgsHolder[] aha = new ArgsHolder[0];
  private static final String EXC_GETSUBD_NOTREPEATING = "getSubdirective: attempt to get repeating "
                                                         + "subdirective -- use getRepeatingSubdirective() instead",
      EXC_GETSUBD_REPEATING = "getRepeatingSubdirective: attempt to get "
                              + "nonrepeating subdirective -- use getSubdirective() instead";

  public DirectiveBuilder(DirectiveDescriptor desc)
  {
    this.desc = desc;
    if (desc.args != null && desc.args.length > 0)
      buildArgs = new ArgsHolder(desc.args);
    if (desc.subdirectives != null && desc.subdirectives.length > 0)
      subdirectives = new Object[desc.subdirectives.length];
  }

  private int findSubdirectiveIndex(int id)
      throws BuildException
  {
    for (int i = 0; i < desc.subdirectives.length; i++) {
      if (desc.subdirectives[i].id == id)
        return i;
    }
    throw new BuildException("Invalid argument ID " + id + " requested for directive " + desc.name);
  }

  @Override
  public Object getExactArg(int idx)
      throws BuildException
  {
    if (buildArgs == null)
      return null;
    else
      return buildArgs.getExactArg(idx);
  }

  /**
   * How many arguments does this builder have?
   */
  @Override
  public int getArgCount()
  {
    if (buildArgs == null)
      return 0;
    else
      return buildArgs.getArgCount();
  }

  /**
   * Retrieve the argument whose id is the specified id.
   */
  @Override
  public Object getArg(int argId)
      throws BuildException
  {
    if (buildArgs == null)
      return null;
    else
      return buildArgs.getArg(argId);
  }

  /**
   * Retrieve the argument whose id is the specified id, and if it is a Builder, build it with the
   * specified build context.
   */
  @Override
  public Object getArg(int argId,
                       BuildContext bc)
      throws BuildException
  {
    if (buildArgs == null)
      return null; // FIXME Should throw exception here, as non-null branch does
    else
      return buildArgs.getArg(argId, bc);
  }

  /**
   * Find out how many subdirectives of the specified id were present.
   */
  public int getSubdirectiveCount(int subdId)
      throws BuildException
  {
    int i = findSubdirectiveIndex(subdId);
    if (subdirectives[i] == null)
      return 0;
    else
      return (desc.subdirectives[i].repeating) ? ((ArgsHolder[]) (subdirectives[i])).length : 1;
  }

  /**
   * Set the argument whose id is the specified id. If the argument has already been set, it is
   * overwritten. Generally not used by directives, only used by the parser.
   */
  @Override
  public void setArg(int argId,
                     Object arg)
      throws BuildException
  {
    buildArgs.setArg(argId, arg);
  }

  /**
   * Create a new subdirective of the specified id and create an ArgsHolder for its arguments. Not
   * used by directives.
   */
  @SuppressWarnings("unchecked")
  public DirectiveArgs newSubdirective(int subdId)
      throws BuildException
  {
    ArgsHolder ah = null;
    int index = findSubdirectiveIndex(subdId);
    if (!desc.subdirectives[index].repeating) {
      if (subdirectives[index] == null)
        subdirectives[index] = new ArgsHolder(desc.subdirectives[index].args);
      ah = (ArgsHolder) subdirectives[index];
    } else {
      List<ArgsHolder> v;
      if (subdirectives[index] == null)
        subdirectives[index] = new ArrayList<ArgsHolder>();
      v = (List<ArgsHolder>) subdirectives[index];
      ah = new ArgsHolder(desc.subdirectives[index].args);
      v.add(ah);
    }

    return ah;
  }

  /**
   * Check to see if the specified subdirective is OK at this point. The only reason it wouldn't be
   * is because we've already got one and its not a repeating subdirective
   */
  public boolean subdirectiveOk(int subdId)
      throws BuildException
  {
    int index = findSubdirectiveIndex(subdId);
    return desc.subdirectives[index].repeating || subdirectives[index] == null;
  }

  /**
   * Retrieves the ArgsHolder for the associated subdirective so that the subdirective arguments can
   * be retrieved. Only valid if the specified subdirective is not repeating.
   */
  public ArgsHolder getSubdirective(int subdId)
      throws BuildException
  {
    int index = findSubdirectiveIndex(subdId);
    if (desc.subdirectives[index].repeating)
      throw new BuildException(EXC_GETSUBD_NOTREPEATING);
    return (ArgsHolder) subdirectives[index];
  }

  /**
   * Retrieves an array of ArgsHolders for the associated subdirective so that the subdirective
   * arguments can be retrieved. Only valid if the specified subdirective is repeating.
   */
  @SuppressWarnings("unchecked")
  public ArgsHolder[] getRepeatingSubdirective(int subdId)
      throws BuildException
  {
    int index = findSubdirectiveIndex(subdId);
    if (!desc.subdirectives[index].repeating)
      throw new BuildException(EXC_GETSUBD_REPEATING);
    if (subdirectives[index] == null)
      return null;
    else
      return ((List<ArgsHolder>) subdirectives[index]).toArray(aha);
  }

  /**
   * Build the directive. Calls the build() method of the directive.
   */
  @Override
  public Object build(BuildContext bc)
      throws BuildException
  {
    Directive d;
    try {
      d = (Directive) desc.dirClass.newInstance();
    } catch (Exception e) {
      throw new BuildException("Error instantiating Directive object for #" + desc.name);
    }
    ;
    return d.build(this, bc);
  }

  /**
   * Get the name this directive was registered as
   */
  public String getName()
  {
    return desc.name;
  }
}
