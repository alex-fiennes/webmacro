package org.webmacro.engine;

import java.io.*;
import org.webmacro.*;
import org.webmacro.util.*;

/**
  * Looks like a Macro, but really it's a String. 
  */
public final class StringMacroAdapter implements Macro, Visitable
{

   private byte[] _self;
   private String _cache = null; // assume this will not be used most times

   public StringMacroAdapter(String wrapMe, String encoding) 
   {
      try {
         _self = wrapMe.getBytes(encoding);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public final String toString() {
      if (_cache == null) {
         _cache = new String(_self);
      }
      return _cache;
   }

   public void accept(TemplateVisitor v) { v.visitString(toString()); } 

   /**
     * Returns the wrapped object, context is ignored.
     */
   public final Object evaluate(Context context) {
      return toString();
   }

   /**
     * Just calls toString() and writes that, context is ignored.
     */
   public final void write(FastWriter out, Context context) 
      throws IOException
   {
      out.write(_self);
   }
}
