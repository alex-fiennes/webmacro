
package org.webmacro.engine;

import org.webmacro.*;

public class ParamBuilder implements Builder
{
   

   private Object[] _names;

   public ParamBuilder(Object[] names) {
      _names = names;
   }

   public final Object build(BuildContext bc) 
      throws BuildException
   {
      try {
         Object ret = bc.getGlobal(_names);
         // XXX: Filter me
         return ret;
      } catch (Exception e) {
         throw new BuildException("Property error occured during parameter "
               + "build pocess: " + e);
      }
   }

   public String toString() {
      return "param:" + Variable.makeName(_names);
   }

}

