
package org.webmacro.engine;

import org.webmacro.*;
public class VariableBuilder implements Builder 
{

   private final Object[] _names;
   private final boolean _filtered;

   public VariableBuilder(Object names[], boolean filtered) {
      _names = names;
      _filtered = filtered;
   }

   static Variable newVariable(
         Object names[], BuildContext bc, boolean filtered) 
      throws BuildException
   {
      Object c[] = new Object[ names.length ];
      for (int i = 0; i < c.length; i++) {
         c[i] = (names[i] instanceof Builder) ? 
            ((Builder) names[i]).build(bc) : names[i];
      }
      return new Variable(c, (filtered ? bc.getFilter(names) : null));
      
   }

   public final Object build(BuildContext bc) throws BuildException
   {
      return newVariable( _names, bc, _filtered);
   }
}

