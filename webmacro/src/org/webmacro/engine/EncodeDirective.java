
package org.webmacro.engine;

import org.webmacro.*;

public abstract class EncodeDirective implements Directive
{
   static final EncodeFilter _ef = new EncodeFilter();

   public static Object build(BuildContext bc, Object subject) 
      throws BuildException
   {
      Variable v;
      try {
         v = (Variable) subject;
      } catch (ClassCastException e) {
         throw new BuildException(
            "Encode directive takes a variable as its argument");
      }
      bc.addFilter(v,_ef);
      return null;
   }
}

