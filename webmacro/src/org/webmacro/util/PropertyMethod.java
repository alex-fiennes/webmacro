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


package org.webmacro.util;
import org.webmacro.*;

/**
  * A property method can function as part of a name in a 
  * property, and represents a method call that should be used
  * to resolve that portion of the name.
  * <p>
  * For example: a.b.get("C").d is equivalent to a.b.hi.d, you can 
  * also use this to access methods which are not normally available
  * through the regular introspection algorithms, for example:
  * #set $thing = a.get("thing")
  * <p>
  * The arguments supplied to a PropertyMethod can be a list 
  * including Macro objects which need to be resolved
  * against a context. The introspection process will supply the 
  * context and resolve these references at execution time.
  */
final public class PropertyMethod implements Named
{

   private Object _args;
   private String _name;
   private boolean _reference;

   /**
     * Create a new PropertyMethod
     * @param name the name of the method to call
     * @param args the arguments, including Macro objects
     */
   public PropertyMethod(String name, Object[] args)
   {
      _name = name;
      _args = args;
      _reference = false;
   }

   /**
     * Create a new PropertyMethod
     * @param name the name of the method to call
     * @param args the arguments, including Macro objects
     */
   public PropertyMethod(String name, Macro args)
   {
      _name = name;
      _args = args;
      _reference = true;
   }

   /**
     * Return the name of this PropertyMethod
     */
   final public String getName() {
      return _name;
   }

   /**
     * Return a signature of this method
     */
   final public String toString() {
      if (_reference) {
         return _name +_args.toString();
      }
      Object[] argList = (Object[]) _args;
      StringBuffer vname = new StringBuffer(); 
      vname.append(_name);
      vname.append("(");
      for (int i = 0; i < argList.length; i++) {
         if (i != 0) {
            vname.append(",");
         }
         vname.append(argList[i]);
      }
      vname.append(")");
      return vname.toString();
   }


   /**
     * Return the arguments for this method, after resolving them
     * against the supplied context. Any arguments which are of 
     * type Macro will be resolved into a regular 
     * object via the Macro.evaluate method.
     * @exception PropertyException a Macro in the arguments failed to resolve against the supplied context
     */
   final public Object[] getArguments(Context context)
      throws PropertyException
   {
      Object[] argList;
      if (_reference) {
         argList = (Object[]) ((Macro) _args).evaluate(context);
      } else {
         argList = (Object[]) _args;
      }

      Object ret[] = new Object[ argList.length ];
      System.arraycopy(argList,0,ret,0,argList.length);
      for (int i = 0; i < ret.length; i++) {
         while (ret[i] instanceof Macro) {
            Object repl = ((Macro) ret[i]).evaluate(context);
            if (repl == ret[i]) {
               break; // avoid infinite loop
            }
            ret[i] = repl;
         }
      }
      return ret;
   }

}
