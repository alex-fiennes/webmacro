

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

/**
  * Build in WebContext tools implement this interface. WebContext is
  * then able to use the ContextTool interface to extract properties 
  * efficiently from objects with standard names.
  */
public interface ContextTool
{

   /**
     * A ContextTool's sub-properties can be accessed via a simple 
     * get interface. The argument must be a string, so this is not 
     * exactly like a Map or Hashtable.
     */
   public Object get(String key);


   /**
     * A ContextTool's sub-properties can be set via this simple 
     * set interface. The key has to be a String, so this is not exactly
     * like a Map or Hashtable. The value can be any Object at all. The
     * tool may throw an UnsettableException if it does not support 
     * setting the supplied key (or does not support setting at all).
     * @exception UnsettableException Cannot set this key
     */
   public void set(String key, Object value)
      throws UnsettableException;

}

