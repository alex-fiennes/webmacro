
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
  * This class allows you to use an object to represent a constant. The
  * default implementation allows the object to have both a String 
  * and an integer representation. All subclasses must have both 
  * String and integer representations as well--the integer value 
  * is used to order the constants.
  * <p>
  * Constants are immutable, and usually declared final.
  */
final public class Constant
{

   final private String name;
   final private int order;

   public Constant(String name, int ord)
   {
      this.name = name;
      this.order = ord;
   }

   /**
     * Return the declared String name for this constant
     */
   final public String toString() {
      return name;
   }

   /**
     * Same as toString()
     */
   final public String getName() {
      return name;
   }

   /**
     * Return the integer (order) value of this constant
     */
   final public int getOrder() {
      return order;
   }

}
