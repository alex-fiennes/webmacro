
/*
 * Copyright (c) 1998, 1999 Semiotek Inc. All Rights Reserved.
 *
 * This software is the confidential intellectual property of
 * of Semiotek Inc.; it is copyrighted and licensed, not sold.
 * You may use it under the terms of the GNU General Public License,
 * version 2, as published by the Free Software Foundation. If you 
 * do not want to use the GPL, you may still use the software after
 * purchasing a proprietary developers license from Semiotek Inc.
 *
 * This software is provided "as is", with NO WARRANTY, not even the 
 * implied warranties of fitness to purpose, or merchantability. You
 * assume all risks and liabilities associated with its use.
 *
 * See the attached License.html file for details, or contact us
 * by e-mail at info@semiotek.com to get a copy.
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
