

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


package org.webmacro.servlet;

import org.webmacro.*;
import java.io.*;

/**
  * Provide Template variables that implement the CGI standard for 
  * script variable names. This class demonstrates a useful technique
  * for optimizing performance in a WebContext: it implements macro,
  * and replaces itself in the context with an implementation class 
  * if it gets called. The implementation class has a reference 
  * to the _request object, and can provide functionality based on
  * the context itself. After the first hit on $CGI in the same 
  * template, access to it is rapid because the CGITool itself has
  * been replaced by the implementation class. This works with 
  * WebContext because we can predict what our name will be: It's
  * our class name, minus the word "Tool".
  */
public class CGITool implements Macro
{
   public Object evaluate(Object context) 
      throws InvalidContextException
   {
      try {
         WebContext wc = (WebContext) context;
         CGI_Impersonator cgi = new CGI_Impersonator(wc.getRequest());
         wc.put("CGI", cgi);
         return cgi;
      } catch (ClassCastException ce) {
         throw new InvalidContextException(
               "CGITool only works with WebContext: " + ce);
      }
   }

   public void write(Writer out, Object context) 
      throws InvalidContextException, IOException
   {
      out.write(evaluate(context).toString());
   }

}
