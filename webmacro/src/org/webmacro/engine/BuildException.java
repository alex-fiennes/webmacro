
package org.webmacro.engine;

import org.webmacro.*;

public class BuildException extends TemplateException
{
   public BuildException(String message) {
      super(message);
   }

   public BuildException(String reason, Throwable e) 
   {
      super(reason, e);
   }

}
