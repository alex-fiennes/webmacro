
/*
 * Copyright (c) 1998, 1999 Semiotek Inc. All Rights Reserved.
 *
 * This software is the confidential intellectual property of
 * of Semiotek Inc.; it is copyrighted and licensed, not sold.
 * You may use it under the terms of the GNU General Public License,
 * version 2, as published by the Free Software Foundation. If you 
 * do not want to use the GPL, you may still use the software after
 * purchasing a proprietary developers license from Semiotek Inc.
 *
 * This software is provided "as is", with NO WARRANTY, not even the 
 * implied warranties of fitness to purpose, or merchantability. You
 * assume all risks and liabilities associated with its use.
 *
 * See the attached License.html file for details, or contact us
 * by e-mail at info@semiotek.com to get a copy.
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
  * including PropertyReference objects which need to be resolved
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
     * @param args the arguments, including PropertyReference objects
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
     * @param args the arguments, including PropertyReference objects
     */
   public PropertyMethod(String name, PropertyReference args)
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
     * type PropertyReference will be resolved into a regular 
     * object via the PropertyReference.evaluate method.
     * @exception ContextException a PropertyReference in the arguments failed to resolve against the supplied context
     */
   final public Object[] getArguments(Context context)
      throws ContextException
   {
      Object[] argList;
      if (_reference) {
         argList = (Object[]) ((PropertyReference) _args).evaluate(context);
      } else {
         argList = (Object[]) _args;
      }

      Object ret[] = new Object[ argList.length ];
      System.arraycopy(argList,0,ret,0,argList.length);
      for (int i = 0; i < ret.length; i++) {
         while (ret[i] instanceof PropertyReference) {
            Object repl = ((PropertyReference) ret[i]).evaluate(context);
            if (repl == ret[i]) {
               break; // avoid infinite loop
            }
            ret[i] = repl;
         }
      }
      return ret;
   }

}
