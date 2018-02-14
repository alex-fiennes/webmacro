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

package org.webmacro.engine;

import java.io.IOException;

import org.webmacro.Context;
import org.webmacro.FastWriter;
import org.webmacro.Macro;
import org.webmacro.TemplateVisitor;
import org.webmacro.Visitable;

/**
 * Looks like a Macro, but really it's a String.
 */
public final class StringMacroAdapter
  implements Macro, Visitable
{

  final String _self;

  public StringMacroAdapter(String wrapMe)
  {
    _self = wrapMe;
  }

  @Override
  public final String toString()
  {
    return _self;
  }

  @Override
  public void accept(TemplateVisitor v)
  {
    v.visitString(toString());
  }

  /**
   * Returns the wrapped object, context is ignored.
   */
  @Override
  public final Object evaluate(Context context)
  {
    return _self;
  }

  /**
   * Just calls toString() and writes that, context is ignored.
   */
  @Override
  public final void write(FastWriter out,
                          Context context)
      throws IOException
  {
    out.writeStatic(_self);
  }
}
