
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
import org.webmacro.*;
import org.webmacro.util.*;
import java.lang.reflect.*;

/**
  * A block represents the text between two {}'s in a template, or else 
  * the text that begins at the start of the template and runs until its
  * end ({}'s around the whole document are not required). It contains 
  * all of the other directives, strings, etc. that can be in a template. 
  */
public class BlockBuilder extends Vector implements Builder
{
   final public Object build(BuildContext bc) throws BuildException
   {
      ArrayList content = new ArrayList(size());
      {
         Iterator i = iterator();
         while (i.hasNext()) {
            Object o = i.next();
            if (o instanceof Builder) {
               o = ((Builder) o).build(bc);
            }
            if (o instanceof Block) {
               ((Block) o).appendTo(content); 
            } else {
               content.add(o);
            }
         }
     } 
   
     // flatten everything and view the content as being: 
     //        string (macro string)* string
     // store that as an array of strings and an array of 
     // Macro objects and create a block.

     ArrayList strings = new ArrayList((size() + 1) / 2);
     ArrayList macros = new ArrayList((size() + 1) / 2);  
     {
        Iterator i = content.iterator();        
        StringBuffer s = new StringBuffer();
        while (i.hasNext()) {
           Object o = i.next();

           if (o instanceof Macro) {
              strings.add(s.toString());
              s = new StringBuffer(); // do not reuse: othewise all strings will contain char[] of max length!!
              macros.add(o);
           } else if (o != null) {
              s.append(o.toString()); 
           }
        }
        strings.add(s.toString());
     }
   
     Macro m[] = (Macro[]) macros.toArray(new Macro[macros.size()]);
     String s[] = (String[]) strings.toArray(new String[strings.size()]);
     return new Block(s,m);
   }
}

