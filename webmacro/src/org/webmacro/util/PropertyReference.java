
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
import org.webmacro.*;

/**
  * This is a generic interface which PropertyOperator users may 
  * wish to subclass from. The intent is that implementors of this
  * class may use PropertyOperator to examine the context in order
  * to extract some kind of value, which is returned.
  */
public interface PropertyReference
{

   /**
     * Apply this object to the supplied context and return 
     * the appropriate reference.
     * @param context An object which may be introspected for information
     * @return The appropriate value given this context
     * @exception InvalidContextException required data not found in context
     */
   public Object evaluate(Object context) throws 
      InvalidContextException;

}
