
package org.webmacro.engine;

import org.webmacro.*;
import java.io.IOException;

public class EncodeFilter implements Filter {

   public Filter getFilter(String name) { return this; }
   public Macro getMacro(Macro source) { return new Encoder(source); }

}

class Encoder implements Macro {

   final private Macro _m;

   public Encoder(Macro m) {
      _m = m;
   }

   public Object evaluate(Context c) throws PropertyException {
      Object o = _m.evaluate(c);
      return (o != null) ? java.net.URLEncoder.encode(o.toString()) : "";
   }

   public void write(FastWriter out, Context c) 
      throws IOException, PropertyException
   {
      out.write(evaluate(c).toString());
   }
}
