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


package org.webmacro.engine;

import org.webmacro.util.*;
import org.webmacro.*;

/**
  * Exception raised on discovery of a parsing error. 
  */
public class ParseToolException extends ParseException
{

  private ParseTool _pt;

  public ParseToolException() {
    super();
  }

   /**
     * Create a new exception
     * @param loc where we are now
     * @param reason what went wrong
     */
  public ParseToolException(ParseTool loc, String reason) {
    super(reason);
    _pt = loc;
  }

   /**
     * Create a new exception, wrapping another exception
     * @param loc where we are now
     * @param reason what went wrong
     * @param reason what went wrong
     * @param e The exception that caused us to raise this exception
     */
  public ParseToolException(ParseTool loc, String reason, Exception e) {
    super(reason, e);
    _pt = loc;
  }

  /**
    * Return the parsetool so we can try and recover.
    */
  public ParseTool getParseTool() {
     return _pt;
  }

}
