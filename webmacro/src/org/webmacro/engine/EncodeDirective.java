
package org.webmacro.engine;

import org.webmacro.*;
import java.io.IOException;

public abstract class EncodeDirective implements Directive
{
   static final EncodeFilter _ef = new EncodeFilter();

   public static Object build(BuildContext bc, Object subject) 
      throws BuildException
   {
      Variable v;
      try {
         v = (Variable) subject;
      } catch (ClassCastException e) {
         throw new BuildException(
            "Encode directive takes a variable as its argument");
      }
      bc.addFilter(v,_ef);
      return null;
   }
}

class EncodeFilter implements Filter {


   public Filter getFilter(String name) { return this; }
   public Macro getMacro(Macro source) { return new Encoder(source); }

}

class Encoder implements Macro {

   final private Macro _m;

   public Encoder(Macro m) {
      _m = m;
   }

   public Object evaluate(Context c) throws ContextException {
      Object o = _m.evaluate(c);
      return (o != null) ? java.net.URLEncoder.encode(o.toString()) : "";
   }

   public void write(FastWriter out, Context c) 
      throws IOException, ContextException
   {
      out.write(evaluate(c).toString());
   }
}
