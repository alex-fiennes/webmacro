
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
     * @exception ContextException If the property does not exist
     */
   public final Object getValue(Context context) 
      throws ContextException
   {
      try {
         return context.getGlobal(_names);
      } catch (Exception e) {
         String warning = "Variable: unable to access " + this + ";";
         context.getBroker().getLog("engine").warning(warning,e);
         throw new ContextException(warning);
      }
   }

   /**
     * Look up my the value of this variable in the specified Map, possibly
     * using introspection, and set it to the supplied value.
     * @exception ContextException If the property does not exist
     */
   public final void setValue(Context context, Object newValue)
      throws ContextException
   {

      try{
         if (!context.setGlobal(_names,newValue)) {
            throw new ContextException("No method to set \"" + _vname + 
               "\" to type " +
               ((newValue == null) ? "null" : newValue.getClass().toString()) 
               + " in supplied context (" + context.getClass() + ")");
         }
      } catch (Exception e) {
         String warning = "Variable.setValue: unable to access " + this + 
            " (is it a public method/field?)";
         context.getBroker().getLog("engine").warning(warning, e);
         throw new ContextException(warning);
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
