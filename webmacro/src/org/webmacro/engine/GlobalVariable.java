
package org.webmacro.engine;
import org.webmacro.*;


/**
  * Operate on bean properties in a context
  */
final class GlobalVariable extends Variable
{

   /**
     * No special initialization
     */
   GlobalVariable(Object names[]) {
      super(names);
   }

   /**
     * Look up my value in the corresponding Map, possibly using introspection,
     * and return it
     * @exception PropertyException If the property does not exist
     */
   public final Object getValue(Context context) 
      throws PropertyException
   {
      return context.get(_names);
   }

   /**
     * Look up my the value of this variable in the specified Map, possibly
     * using introspection, and set it to the supplied value.
     * @exception PropertyException If the property does not exist
     */
   public final void setValue(Context context, Object newValue)
      throws PropertyException
   {

      if (!context.set(_names,newValue)) {
         throw new PropertyException("No method to set \"" + _vname + 
            "\" to type " +
            ((newValue == null) ? "null" : newValue.getClass().toString()) 
            + " in supplied context (" + context.getClass() + ")");
      }
   }

   /**
     * Return a string representation naming the variable for 
     * debugging purposes.
     */
   public final String toString() {
      return "global:" + _vname;
   }

}
