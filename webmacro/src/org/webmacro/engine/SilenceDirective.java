
package org.webmacro.engine;

import org.webmacro.*;

public abstract class SilenceDirective implements Directive
{
   static final SilenceFilter _sf = new SilenceFilter();

   public static Object build(BuildContext bc, Object subject) 
      throws BuildException
   {
      Variable v;
      try {
         v = (Variable) subject;
      } catch (ClassCastException e) {
         throw new BuildException(
            "Silence directive takes a variable as its argument");
      }
      bc.addFilter(v,_sf);
      return null;
   }
}

