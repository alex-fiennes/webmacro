
package org.webmacro.engine;
import org.webmacro.*;

public class IntrospectionException extends WebMacroException
{
   public IntrospectionException(String message, Exception e) {
      super(message, e);
   }

   public IntrospectionException(String message) {
      super(message);
   }
}
