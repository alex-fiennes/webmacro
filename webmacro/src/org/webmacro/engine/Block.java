
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


/**
  * A Block is essentially a Macro[] that knows how to write itself
  * out as a String.
  */
final public class Block implements Macro, Visitable
{

   private final String[] _strings;
   private final Macro[] _macros;
   private final int _length;
   private final int _remainder;

   /**
     * A Block must be constructed from a BlockBuilder. The format
     * of a block is:  String (Macro String)* String 
     * and the constructor expects to receive two arrays matching
     * this structure. The output of the block will be the first
     * string, followed by the first macro, followed by the second 
     * string, followed by the second macro, etc., and terminated 
     * by the final string.
     */
   protected Block(String[] strings, Macro[] macros) {
      _strings = strings;
      _macros = macros;

      _length = _macros.length;
      _remainder = 10 - _length % 10;
   }

   /**
     * Interpret the directive and write it out, using the values in
     * the supplied context as appropriate.
     * <p>
     * @exception PropertyException if required data was missing from context
     * @exception IOException if we could not successfully write to out
     */
   final public void write(final FastWriter out, final Context context) 
      throws PropertyException, IOException
   {
      int i = 0;  
      final byte[][] bcontent = out.getEncodingCache().encode(_strings);
      byte[] b;

      switch(_remainder) {
         case 1: b=bcontent[i]; out.write(b,0,b.length); _macros[i++].write(out,context);
         case 2: b=bcontent[i]; out.write(b,0,b.length); _macros[i++].write(out,context);
         case 3: b=bcontent[i]; out.write(b,0,b.length); _macros[i++].write(out,context);
         case 4: b=bcontent[i]; out.write(b,0,b.length); _macros[i++].write(out,context);
         case 5: b=bcontent[i]; out.write(b,0,b.length); _macros[i++].write(out,context);
         case 6: b=bcontent[i]; out.write(b,0,b.length); _macros[i++].write(out,context);
         case 7: b=bcontent[i]; out.write(b,0,b.length); _macros[i++].write(out,context);
         case 8: b=bcontent[i]; out.write(b,0,b.length); _macros[i++].write(out,context);
         case 9: b=bcontent[i]; out.write(b,0,b.length); _macros[i++].write(out,context);
      }

      while (i < _length) {
         b=bcontent[i]; out.write(b,0,b.length); _macros[i++].write(out,context);
         b=bcontent[i]; out.write(b,0,b.length); _macros[i++].write(out,context);
         b=bcontent[i]; out.write(b,0,b.length); _macros[i++].write(out,context);
         b=bcontent[i]; out.write(b,0,b.length); _macros[i++].write(out,context);
         b=bcontent[i]; out.write(b,0,b.length); _macros[i++].write(out,context);
         b=bcontent[i]; out.write(b,0,b.length); _macros[i++].write(out,context);
         b=bcontent[i]; out.write(b,0,b.length); _macros[i++].write(out,context);
         b=bcontent[i]; out.write(b,0,b.length); _macros[i++].write(out,context);
         b=bcontent[i]; out.write(b,0,b.length); _macros[i++].write(out,context);
         b=bcontent[i]; out.write(b,0,b.length); _macros[i++].write(out,context);
      }
      b=bcontent[_length]; out.write(b,0,b.length);
   }

   final void appendTo(List l) {
      final int len = _macros.length;
      for (int i = 0; i < _macros.length; i++) {
         l.add(_strings[i]);
         l.add(_macros[i]);
      }
      l.add(_strings[len]);
   }

  final public void accept(TemplateVisitor v) { 
    v.beginBlock();
    final int len = _macros.length;
    for(int i = 0; i < len; i++) 
    {
       v.visitMacro(_macros[i]);
       v.visitString(_strings[i]);
    }
    v.visitString(_strings[len]);
    v.endBlock(); 
  } 

   /**
     * same as out but returns a String
     * <p>
     * @exception PropertyException if required data was missing from context
     */
   final public Object evaluate(Context context) throws PropertyException
   {
      try {
         ByteArrayOutputStream os = new ByteArrayOutputStream(_strings.length * 128);
         FastWriter fw = FastWriter.getInstance();
         write(fw,context);
         String ret = fw.toString();
         fw.close();
         return ret;
      } catch (IOException e) {
         context.getBroker().getLog("engine").error("StringWriter threw an IOException!",e);
         return null;
      }
   }

}

