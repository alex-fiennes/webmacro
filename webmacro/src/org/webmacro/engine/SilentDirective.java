
package org.webmacro.engine;

import org.webmacro.*;
import java.io.IOException;

public abstract class SilentDirective implements Directive
{
   static final SilentFilter _sf = new SilentFilter();

   public static Object build(BuildContext bc, Object subject) 
      throws BuildException
   {
      Variable v;
      try {
         v = (Variable) subject;
      } catch (ClassCastException e) {
         throw new BuildException(
            "Silent directive takes a variable as its argument");
      }
      bc.addFilter(v,_sf);
      return null;
   }
}

class SilentFilter implements Filter {


   public Filter getFilter(String name) { return this; }
   public Macro getMacro(Macro source) { return new Silencer(source); }

}

class Silencer implements Macro {

   final private Macro _m;

   public Silencer(Macro m) {
      _m = m;
   }

   public Object evaluate(Context c) throws ContextException {
      Object o = _m.evaluate(c);
      if (o == null) {
         return "";
      } else {
         return o;
      }
   }

   public void write(FastWriter out, Context c) 
      throws IOException, ContextException
   {
      out.write(evaluate(c).toString());
   }
}
