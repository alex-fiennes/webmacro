/*
 * Copyright (C) 1998-2002 Semiotek Inc. All Rights Reserved. Redistribution and use in source and
 * binary forms, with or without modification, are permitted under the terms of either of the
 * following Open Source licenses: The GNU General Public License, version 2, or any later version,
 * as published by the Free Software Foundation (http://www.fsf.org/copyleft/gpl.html); or The
 * Semiotek Public License (http://webmacro.org/LICENSE.) This software is provided "as is", with NO
 * WARRANTY, not even the implied warranties of fitness to purpose, or merchantability. You assume
 * all risks and liabilities associated with its use. See www.webmacro.org for more information on
 * the WebMacro project.
 */

package org.webmacro.engine;

import org.webmacro.Macro;

/**
 * Builder for standalone function calls $a($foo)
 * 
 * @author Brian Goetz
 * @since 1.1
 */
public class FunctionCallBuilder
  implements Builder
{

  String _name;
  ListBuilder _args;

  public FunctionCallBuilder(String name,
                             ListBuilder args)
  {
    _name = name;
    _args = args;
  }

  @Override
  public Object build(BuildContext bc)
      throws BuildException
  {
    Object args = _args.build(bc);
    if (args instanceof Macro) {
      return new FunctionCall(_name, (Macro) args);
    } else {
      return new FunctionCall(_name, (Object[]) args);
    }
  }

}
