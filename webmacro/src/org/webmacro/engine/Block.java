/*
 * Copyright (C) 1998-2000 Semiotek Inc.  All Rights Reserved.  
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted under the terms of either of the following
 * Open Source licenses:
 *
 * The GNU General Public License, version 2, or any later version, as
 * published by the Free Software Foundation
 * (http://www.fsf.org/copyleft/gpl.html);
 *
 *  or 
 *
 * The Semiotek Public License (http://webmacro.org/LICENSE.)  
 *
 * This software is provided "as is", with NO WARRANTY, not even the 
 * implied warranties of fitness to purpose, or merchantability. You
 * assume all risks and liabilities associated with its use.
 *
 * See www.webmacro.org for more information on the WebMacro project.  
 */


package org.webmacro.engine;
import java.util.*;
import java.io.*;
import org.webmacro.*;
import org.webmacro.util.Encoder;

/**
  * A Block is essentially a Macro[] that knows how to write itself
  * out as a String.
  */
final public class Block implements Macro, Visitable
{

   private final String[] _strings;
   private final Encoder.Block _block;
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

      // we'll use this to encode our strings for output to the user
      _block = new Encoder.Block(_strings);
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
      final byte[][] bcontent = out.getEncoder().encode(_block);
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
       v.visitString(_strings[i]);
       v.visitMacro(_macros[i]);
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
         FastWriter fw = FastWriter.getInstance(context.getBroker());
         write(fw,context);
         String ret = fw.toString();
         fw.close();
         return ret;
      } catch (IOException e) {
         context.getBroker().getLog("engine","parsing and template execution").error("StringWriter threw an IOException!",e);
         return null;
      }
   }

}

