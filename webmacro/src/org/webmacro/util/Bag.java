
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
  * A dictionary like interface, but more restricted--provides a common
  * interface for generic containers which are less functional than 
  * a dictionary, but similar in nature.
  */
public interface Bag
{

   /**
     * Add an item to the bag
     */
   public void put(String itemName, Object item);

   /**
     * Get an item from the bag
     */
   public Object get(String itemName);

   /**
     * Remove an item from the bag
     */
   public void remove(String itemName);


}


