
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


package org.webmacro.engine;

import java.util.*;
import java.io.*;
import org.webmacro.util.*;
import java.lang.reflect.*;
import org.webmacro.*;


final class ListBuilder extends Vector implements Builder
{
   public final Object build(BuildContext bc) throws BuildException
   {

      boolean isMacro = false;
      Object c[] = new Object[ size() ];

      for (int i = 0; i < c.length; i++) {
         Object elem = elementAt(i);
         c[i] = (elem instanceof Builder) ? 
            ((Builder) elem).build(bc) : elem;
         if (c[i] instanceof Macro) {
            isMacro = true;
         }
      }

      if (isMacro) {
         return new List(c);
      } else {
         return c;
      }
   }
}


/**
  * A list is a sequence of terms. It's used in two common cases:
  * the items in an array initializer; and the arguments to a 
  * method call. 
  */
class List implements Macro
{

   final private Object[] _content; // the list data

   /**
     * create a new list
     */
   List(Object[] content) {
      _content = content;
   }

   public void write(Writer out, Object context)
      throws InvalidContextException, IOException
   {
      out.write(evaluate(context).toString());
   }

   public String toString()
   {
      StringBuffer sb = new StringBuffer();
      sb.append("(");
      for(int i =0; i<_content.length; i++) {
         if (i != 0) {
            sb.append(", ");
         }
         sb.append(_content[i].toString());
      }
      sb.append(")");
      return sb.toString();
   }

   public Object evaluate(Object context) 
      throws InvalidContextException
   {
      Object[] ret = new Object[ _content.length ];
      for (int i = 0; i < _content.length; i++) {
         Object m = _content[i];
         if (m instanceof Macro) {
            m = ((Macro) m).evaluate(context);
         }
         ret[i] = m;
      }
      return ret;
   }
}
