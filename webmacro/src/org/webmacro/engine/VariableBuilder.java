
package org.webmacro.engine;

import org.webmacro.*;
public class VariableBuilder implements Builder 
{

   private final Object[] _names;
   private final boolean _filtered;

   public VariableBuilder(Object names[], boolean filtered) {
      _names = names;
      _filtered = filtered;
   }

   static Macro newVariable(
         Object names[], BuildContext bc, boolean filtered) 
      throws BuildException
   {

      Variable v = null;

      if (names.length < 1) {
         throw new BuildException("Variable with name of length zero!");
      }

      Object c[] = new Object[ names.length ];
      for (int i = 0; i < c.length; i++) {
         c[i] = (names[i] instanceof Builder) ? 
            ((Builder) names[i]).build(bc) : names[i];
      }

      Object type = bc.getVariableType(c[0].toString());
      if (type == Variable.PROPERTY_TYPE) {
         v = new PropertyVariable(c); 
      } else if (type == Variable.LOCAL_TYPE) {
         v = new LocalVariable(c);
      } else if (type == Variable.TOOL_TYPE) {
         v = new ToolVariable(c);
      } else {
         throw new BuildException("Unrecognized Variable Type: " + type);
      }

      if (filtered) {
        return new FilterMacro(v,bc.getFilter(v.getPropertyNames()));
      } else {
         return v;
      }
   }

   public final Object build(BuildContext bc) throws BuildException
   {
      return newVariable( _names, bc, _filtered);
   }
}

