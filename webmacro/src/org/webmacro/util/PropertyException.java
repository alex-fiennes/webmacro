
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


package org.webmacro.util;

/**
  * This exception is raised by the Property class when, for some reason,
  * an unrecoverable error occurs while attempting to access the declared 
  * property name from the parent object. Possible reasons are: security 
  * violation, the property doesn't exist, or a supplied argument is of 
  * the wrong type.
  */
final public class PropertyException extends Exception
{
   
   final Throwable t; 

   public PropertyException(String reason, Throwable t) 
   {
      super(reason);
      this.t = t;
   }

   /**
     * You can query a PropertyException to find out what the 
     * underlying exception was, if there was one.
     * <p>
     * @returns null if the property was simply not found
     */ 
   public Throwable getThrowable() {
      return t;
   }
}

