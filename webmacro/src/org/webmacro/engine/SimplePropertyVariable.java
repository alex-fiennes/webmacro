/*
 * Copyright (C) 1998-2000 Semiotek Inc.  All Rights Reserved.  
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted under the terms of either of the following
 * Open Source licenses:
 *
 * The GNU General Public License, version 2, or any later version, as
 * published by the Free Software Foundation
 * (http://www.fsf.org/copyleft/gpl.html);
 *
 *  or 
 *
 * The Semiotek Public License (http://webmacro.org/LICENSE.)  
 *
 * This software is provided "as is", with NO WARRANTY, not even the 
 * implied warranties of fitness to purpose, or merchantability. You
 * assume all risks and liabilities associated with its use.
 *
 * See www.webmacro.org for more information on the WebMacro project.  
 */


package org.webmacro.engine;
import org.webmacro.*;


/**
  * Operate on bean properties in a context; special case for simple variables
  * (only one name)
  */
final class SimplePropertyVariable extends Variable
{

   /**
     * No special initialization
     */
   SimplePropertyVariable(Object names[]) {
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
      return context.getProperty(_names[0]);
   }

   /**
     * Look up my the value of this variable in the specified Map, possibly
     * using introspection, and set it to the supplied value.
     * @exception PropertyException If the property does not exist
     */
   public final void setValue(Context context, Object newValue)
      throws PropertyException
   {
      if (!context.setProperty(_names[0], newValue)) {
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
      return "property:" + _vname;
   }

}
