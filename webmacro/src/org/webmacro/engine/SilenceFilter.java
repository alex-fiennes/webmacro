package org.webmacro.engine;

import org.webmacro.*;
import java.io.IOException;

public class SilenceFilter implements Filter {

  public Filter getFilter(String name) { return this; }
  public Macro getMacro(Macro source) { return new Silencer(source); }

}

class Silencer implements Macro {

   final private Macro _m;

   public Silencer(Macro m) {
      _m = m;
   }

   public Object evaluate(Context c) throws PropertyException {
      Object o = _m.evaluate(c);
      if (o == null) {
         return "";
      } else {
         return o;
      }
   }

   public void write(FastWriter out, Context c) 
      throws IOException, PropertyException
   {
      out.write(evaluate(c).toString());
   }
}
