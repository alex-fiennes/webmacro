package org.webmacro.engine;

import java.io.*;
import org.webmacro.*;
import org.webmacro.util.*;

/**
  * Looks like a Macro, but really it's a String. 
  */
public final class StringMacroAdapter implements Macro, Visitable
{

   final String _self;

   public StringMacroAdapter(String wrapMe) 
   {
      _self = wrapMe;
   }

   public final String toString() {
      return _self;
   }

   public void accept(TemplateVisitor v) { v.visitString(toString()); } 

   /**
     * Returns the wrapped object, context is ignored.
     */
   public final Object evaluate(Context context) {
      return _self;
   }

   /**
     * Just calls toString() and writes that, context is ignored.
     */
   public final void write(FastWriter out, Context context) 
      throws IOException
   {
      out.writeStatic(_self);
   }
}
