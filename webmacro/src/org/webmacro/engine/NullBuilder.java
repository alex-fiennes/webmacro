
package org.webmacro.engine;

import org.webmacro.util.*;
import org.webmacro.*;

final public class NullBuilder implements Builder {
   final public Object build(BuildContext pc)
   {
      return null;
   }
}
