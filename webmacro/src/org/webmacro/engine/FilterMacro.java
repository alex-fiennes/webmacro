
package org.webmacro.engine;
import org.webmacro.*;
import java.io.*;

final class FilterMacro implements Macro
{

   final private Filter _f;
   final private Macro  _m;

   public FilterMacro(Macro m, Filter f)  {
      _m = m;
      _f = f;
   }

   public void write(Writer out, Context context) 
      throws InvalidContextException, IOException
   {
      context.push(_m.evaluate(context));      
      _f.write(out,context);
      context.pop();
   }

   public Object evaluate(Context context)
      throws InvalidContextException
   {
      Object ret;
      context.push(_m.evaluate(context));
      ret = _f.evaluate(context);
      context.pop();
      return ret;
   }
}
