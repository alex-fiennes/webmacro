
package org.webmacro.engine;

import org.webmacro.*;
public class ParamBuilder implements Builder
{
   
   private final Variable _var;

   public ParamBuilder(Object[] names) {
      _var = new Variable(names);
   }

   public final Object build(BuildContext bc) {
      Object context = bc.getParameters();
      return _var.evaluate(context);
   }

   public String toString() {
      return "param:" + _var.toString();
   }

}

