
package org.webmacro.engine;

import org.webmacro.util.*;
import org.webmacro.*;

public interface Builder {
   public Object build(BuildContext pc)
      throws BuildException;


   static final Log log = new Log("build", "Template Builder");
}
