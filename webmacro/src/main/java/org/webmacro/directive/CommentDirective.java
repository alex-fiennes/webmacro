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

import org.webmacro.Context;
import org.webmacro.FastWriter;
import org.webmacro.PropertyException;
import org.webmacro.TemplateVisitor;
import org.webmacro.engine.BuildContext;
import org.webmacro.engine.BuildException;

import java.io.IOException;

/**
 * Allows a template writer to make a block comment.
 */
public class CommentDirective
  extends Directive
{

  private static final int COMMENT_BLOCK = 1;

  private static final ArgDescriptor[] myArgs =
      new ArgDescriptor[] { new LiteralBlockArg(COMMENT_BLOCK) };

  private static final DirectiveDescriptor myDescr =
      new DirectiveDescriptor("comment", null, myArgs, null);

  public static DirectiveDescriptor getDescriptor()
  {
    return myDescr;
  }

  @Override
  public Object build(DirectiveBuilder builder,
                      BuildContext bc)
      throws BuildException
  {
    return null;
  }

  @Override
  public void write(FastWriter out,
                    Context context)
      throws PropertyException, IOException
  {
  }

  @Override
  public void accept(TemplateVisitor v)
  {
    v.beginDirective(myDescr.name);
    v.endDirective();
  }
}
