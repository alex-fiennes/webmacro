
package org.webmacro.engine;

import org.webmacro.*;

public class TestTool implements ContextTool
{
   public Object init(Context c) throws InvalidContextException
   {
      return "It appears to work!";   
   } 
}
