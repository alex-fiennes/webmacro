
package org.webmacro.engine;
import org.webmacro.*;


/**
  * Operate on bean properties in a context
  */
final class LocalVariable extends Variable
{

   /**
     * No special initialization
     */
   LocalVariable(Object names[]) {
      super(names);
   }

   /**
     * Look up my value in the corresponding Map, possibly using introspection,
     * and return it
     * @exception InvalidContextException If the property does not exist
     */
   final Object getValue(Context context) 
      throws InvalidContextException
   {
      try {
         return context.getLocal(_names);
      } catch (Exception e) {
         Engine.log.exception(e);
         String warning = "Variable: unable to access " + this + ";";
         throw new InvalidContextException(warning);
      }
   }

   /**
     * Look up my the value of this variable in the specified Map, possibly
     * using introspection, and set it to the supplied value.
     * @exception InvalidContextException If the property does not exist
     */
   final void setValue(Context context, Object newValue)
      throws InvalidContextException
   {

      try{
         if (!context.setLocal(_names,newValue)) {
            throw new InvalidContextException("No method to set \"" + _vname + 
               "\" to type " +
               ((newValue == null) ? "null" : newValue.getClass().toString()) 
               + " in supplied context (" + context.getClass() + ")");
         }
      } catch (Exception e) {
         Engine.log.exception(e);
         String warning = "Variable.setValue: unable to access " + this + 
            " (is it a public method/field?)";
         throw new InvalidContextException(warning);
      }
   }

   /**
     * Return a string representation naming the variable for 
     * debugging purposes.
     */
   public final String toString() {
      return "local:" + _vname;
   }

}
