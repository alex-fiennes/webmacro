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

package org.webmacro;

/**
 * Resource could not be loaded for some reason
 * 
 * @since 0.96
 */
public class ResourceException
  extends WebMacroException
{

  private static final long serialVersionUID = 1L;

  public ResourceException(String reason,
                           Exception e)
  {
    super(reason,
          e);
  }

  public ResourceException(String reason)
  {
    super(reason);
  }
}
