
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
import java.io.*;
import org.webmacro.*;
import org.webmacro.util.*;



/**
  * A Block is essentially a Macro[] that knows how to write itself
  * out as a String.
  */
final public class Block implements Macro 
{

   final private Macro[] _content;

   /**
     * Create a new Block
     */
   public Block(Macro[] content) {
      _content = content;
   }

   /**
     * Interpret the directive and write it out, using the values in
     * the supplied context as appropriate.
     * <p>
     * @exception ContextException if required data was missing from context
     * @exception IOException if we could not successfully write to out
     */
   public void write(final FastWriter out, final Context context) 
      throws ContextException, IOException
   {
      int len = _content.length;
      for(int i = 0; i < len; i++) {
         _content[i].write(out,context);
      }
   }

   /**
     * same as out but returns a String
     * <p>
     * @exception ContextException if required data was missing from context
     */
   public Object evaluate(Context context) throws ContextException
   {
      try {
         ByteArrayOutputStream os = new ByteArrayOutputStream(_content.length * 16 + 256);
         FastWriter fw = new FastWriter(os, context.getEncoding());
         write(fw,context);
         fw.flush();
         return os.toString(context.getEncoding());
      } catch (IOException e) {
         Engine.log.exception(e);
         Engine.log.error("StringWriter through an IOException!");
         return null;
      }
   }

}

