
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
  * Exception raised on discovery of a parsing error. To facilitate error
  * recovery, you can extract the parseTool from this error. 
  */
public final class ParseException extends TemplateException
{

   /**
     * We store the parseTool to assist with error recovery
     */
   private ParseTool in;

   /**
     * Create a new exception
     * <p>
     * @param in the parseTool we were reading that contained unparasable data
     * @param reason what exactly failed in our attempt to parse it
     */
   public ParseException(ParseTool in, String reason) 
   {
      super(((in != null) ? in.toString() : "input source unknown" ) + ":" + reason);
      this.in = in;
   }

   /**
     * Return the parseTool that contained the unparsable data
     */
   final ParseTool getParseTool() {
      return in;
   }
}
