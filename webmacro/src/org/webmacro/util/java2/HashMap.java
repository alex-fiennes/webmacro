
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


package org.webmacro.util.java2;

/**
  * This really should be of type java.util.Map, but Java 1.2 is not 
  * out yet, so we can't subclass that. However all the operations on 
  * this object have been chosen so that it will be easy to convert to
  * the JDK/ODMG collection API once it is available. Only a subset of
  * that API is provided here, but eventually all of it will be available
  * as this will eventually be a subclass of Map.
  *
  */
public class HashMap extends com.sun.java.util.collections.HashMap
{ }

