
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
  * For some ridiculous reason the StringWriter in java.io has a 
  * constructor to set the size of the initial buffer that is protected
  * instead of public. This class opens it up. For convenience I've also
  * added the ensureCapacity argument as well.
  */
final public class SizedStringWriter extends java.io.StringWriter
{

   /** 
     * Create a StringWriter with an initial buffer of the specified size
     */
   public SizedStringWriter(int size) {
      super(size);
   }

  /**
    * Make sure the internal buffer is at least this big
    */
  final public synchronized void ensureCapacity(int minimumCapacity) 
  {
     getBuffer().ensureCapacity(minimumCapacity);   
  }


}

