
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
   /**
     * Create a block with the least number of elements
     */
   final public Object build(BuildContext bc) 
      throws BuildException
   {
      Vector block = new Vector(20);
      StringBuffer buf = new StringBuffer(512);
      flatten(bc, block, buf, elementData);
      if (buf.length() > 0) {
         block.addElement(new StringMacroAdapter(buf.toString(),
                                                 bc.getEncoding()));
      }
      Macro[] result = new Macro[block.size()];
      block.copyInto(result);
      return new Block(result);       
   }

   /**
     * Merge sub-blocks and concatenate non-Macro elements
     */
   private void flatten(BuildContext bc,
                        Vector block, 
                        StringBuffer buf,  
                        Object[] elements)
      throws BuildException
   {
      for (int i=0; i<elements.length; i++) {
         Object o = elements[i];
         if (o instanceof BlockBuilder) 
            flatten(bc, block, buf, ((BlockBuilder) o).elementData);
         else {
            if (o instanceof Builder) 
               o = ((Builder) o).build(bc);

            if (o instanceof Block) {
              flatten(bc, block, buf, ((Block) o).getContent());
            }
            else if (o instanceof Macro) {
               if (buf.length() > 0) {
                  block.addElement(new StringMacroAdapter(buf.toString(),
                                                          bc.getEncoding()));
                  buf.setLength(0);
               }
               block.addElement((Macro) o);
            } 
            else {
               if (o != null) {
                  buf.append(o.toString());
               }
            }
         }
      }
   }
}




