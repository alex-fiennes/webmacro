
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


/**
  * This is an exception, which Config may throw if something is wrong with 
  * the config file eg default variables cannot be located 
  */
public class ConfigException extends WebMacroException 
{

   /**
     * Constructor only requires a reason 
     * <p>
     * @param reason explains what went wrong
     */
   public ConfigException(String reason) {
      super(reason);
   }
 
}


