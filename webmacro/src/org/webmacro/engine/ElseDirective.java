
package org.webmacro.engine;

import org.webmacro.*;
/**
  * Sub directive representing the second half of an #if
  */
abstract public class ElseDirective implements SubDirective 
{
   public static final Object build(BuildContext bc, Macro body) {
      return body;
   }

}
