
package org.webmacro.engine;
import java.io.*;
import org.webmacro.*;

/**
  * Looks like a Macro, but really it's not. Wrap any object as
  * a Macro via the createMacro method. You can use this when you
  * have to return a Macro, but what you have is something else.
  */
final public class MacroAdapter implements Macro
{

   final private Object _self;

   private MacroAdapter(Object wrapMe) 
      throws BuildException
   {
      _self = wrapMe;
      if (_self == null) {
         throw new BuildException("MacroAdapter cannot wrap null");
      }
   }

   public final String toString() {
      return _self.toString(); 
   }

   /**
     * Returns the wrapped object, context is ignored.
     */
   public final Object evaluate(Object context) {
      return _self;
   }

   /**
     * Just calls toString() and writes that, context is ignored.
     */
   public final void write(Writer out, Object context) 
      throws IOException
   {
      out.write(_self.toString());
   }

   /**
     * If wrapMe is not a Macro, wrap it and return it. If it
     * is a Macro already, just return it.
     */
   public final static Macro createMacro(Object wrapMe) 
      throws BuildException
   {
      if (wrapMe == null) {
         throw new BuildException("Bug in WM: attempt to write null");
      }
      if (wrapMe instanceof Macro) {
         return (Macro) wrapMe;
      } else {
         return new MacroAdapter(wrapMe);
      }
   }

   /**
     * Wrap the string. Slightly more efficient than using the
     * generic Object wrapper, but functionally equivalent (avoids
     * calling toString() when writing the object).
     */
   public final static Macro createMacro(String wrapMe)
      throws BuildException
   {
      if (wrapMe == null) {
         throw new BuildException("Bug in WM: attempt to write null");
      }
      return new StringMacroAdapter(wrapMe);
   }

}

/**
  * Looks like a Macro, but really it's a String. 
  */
final class StringMacroAdapter implements Macro
{

   final private char[] _self;
   private String _cache = null; // assume this will not be used most times

   public StringMacroAdapter(String wrapMe) 
   {
      _self = new char[wrapMe.length()];
      wrapMe.getChars(0, _self.length, _self, 0);
   }

   public final String toString() {
      if (_cache == null) {
         _cache = new String(_self);
      }
      return _cache;
   }

   /**
     * Returns the wrapped object, context is ignored.
     */
   public final Object evaluate(Object context) {
      return toString();
   }

   /**
     * Just calls toString() and writes that, context is ignored.
     */
   public final void write(Writer out, Object context) 
      throws IOException
   {
      out.write(_self);
   }
}
