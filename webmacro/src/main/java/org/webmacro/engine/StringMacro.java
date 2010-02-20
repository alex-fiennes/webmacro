/*
 * Created on Mar 29, 2005
 *
 */
package org.webmacro.engine;

import java.io.IOException;

import org.webmacro.Context;
import org.webmacro.Macro;

/**
 * @author Keats Kirsch
 *
 */
public class StringMacro implements Macro
{

   /**
    * 
    */
   //** Convenience class to evaluate a string template as a Macro *//
   private String s = "";

   public StringMacro(String text)
   {
      s = text;
   }

   /* (non-Javadoc)
    * @see org.webmacro.Macro#evaluate(org.webmacro.Context)
    */
   public Object evaluate(Context context)
      throws org.webmacro.PropertyException
   {
      org.webmacro.engine.StringTemplate t =
         new org.webmacro.engine.StringTemplate(context.getBroker(), s);
      return t.evaluateAsString(context);
   }

   /* (non-Javadoc)
    * @see org.webmacro.Macro#write(org.webmacro.FastWriter, org.webmacro.Context)
    */
   public void write(org.webmacro.FastWriter fw, Context context)
      throws org.webmacro.PropertyException, IOException
   {
      fw.write((String) evaluate(context));
   }
}
