

package org.webmacro.engine;

import org.webmacro.util.*;
import org.webmacro.*;

class PropertyMethodBuilder implements Builder
{

   String _name;
   ListBuilder _args;

   PropertyMethodBuilder(String name, ListBuilder args) {
      _name = name;
      _args = args;
   }

   public Object build(BuildContext bc) throws BuildException
   {
      Object args = _args.build(bc);
      if (args instanceof Macro) {
         return new PropertyMethod(_name, (Macro) args);
      } else {
         return new PropertyMethod(_name, (Object[]) args);
      }
   }


}
