
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


package org.webmacro;

import java.util.*;
import java.io.*;


/**
  * Directives, variables, macro calls, blocks, conditions, text, etc., all 
  * have this as their supertype.
  */
public interface Macro extends org.webmacro.util.PropertyReference
{

   /**
     * Interpret the directive and write it out, using the values in
     * the supplied context as appropriate.
     * <p>
     * @exception InvalidContextException if required data was missing from context
     * @exception IOException if we could not successfully write to out
     */
   public void write(Writer out, Object context) 
      throws InvalidContextException, IOException;

   /**
     * same as out but returns a String
     * <p>
     * @exception InvalidContextException if required data was missing from context
     */
   public Object evaluate(Object context)
      throws InvalidContextException;

}

