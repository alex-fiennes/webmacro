
package org.webmacro.engine;

import org.webmacro.*;
public class VariableBuilder implements Builder 
{

   private final Object[] _names;

   public VariableBuilder(Object names[]) {
      _names = names;
   }

   public final Object build(BuildContext bc) throws BuildException
   {
      boolean isMacro = false;
      Object c[] = new Object[ _names.length ];
      for (int i = 0; i < c.length; i++) {
         c[i] = (_names[i] instanceof Builder) ? 
            ((Builder) _names[i]).build(bc) : _names[i];
      }
      return new Variable(c);
   }
}

