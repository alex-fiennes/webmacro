
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


package org.webmacro.engine;

import java.util.*;
import java.io.*;
import org.webmacro.util.*;
import java.lang.reflect.*;
import org.webmacro.*;

// PRIMARY CLASS: Variable

/**
  * A Variable is a reference into a Propertymap.
  * <p>
  * A variable name contains a list of names separated by dots, for
  * example "$User.Identity.email.address" is the list: User, Identity,
  * email, and address. 
  * <p>
  * PLEASE NOTE: Case-sensitivity is enforced. "User" is the the same
  * name as "user".
  * <p>
  * What that means: When a template is interpreted, it is interpreted
  * in terms of data in a hashtable/map called the "context". This is 
  * actually a Map of type Map. The context contains all the 
  * local variables that have been set, as well as other information 
  * that Macros may use to evaluate the request.
  * <p>
  * Variable depends heavily on Property introspection: It is defined
  * as a list of one or more names (separated by dots when written). 
  * <p>
  * Those names are references to sub-objects within the context. The
  * Variable instance, when interpreted, will decend through the context
  * following fields, method references, or hash table look-ups based
  * on its names.
  * <p>
  * For example, the variable "$User.Identity.email.address" implies
  * that there is a "User" object under the Map--either it is
  * a field within the map, or the map has a getUser() method, or 
  * the User can be obtained by calling Map.get("User").
  * <p>
  * The full expansion of $User.Identity.email.address might be:<pre>
  * 
  *    Map.get("User").getIdentity().get("email").address
  *
  * </pre>. Variable (actually the Property class it uses) will figure
  * out how to decend through the object like this until it finds the
  * final reference--which is the "value" of the variable.
  * <p>
  * When searchin for subfields Variable prefers fields over getFoo() 
  * methods, and getFoo() over get("Foo").
  *
  */
public abstract class Variable implements Macro, Visitable
{


   final static public Object PROPERTY_TYPE = null;
   final static public Object LOCAL_TYPE = new Object();
   final static public Object TOOL_TYPE = new Object();

   /**
     * The name of this variable.
     */
   protected String _vname;

   /**
     * The name as an array
     */
   protected Object[] _names;

   /**
     * Create a variable with the supplied name. The elements of the name 
     * are either strings, or a method reference. 
     */
   Variable(Object names[]) {
      _vname = makeName(names).intern();
      _names = names;
   
   }

   /**
     * Return the property names for this variable. These are stringified
     * names corresponding to the names of the variable; if one of the 
     * elements of the variable name is a method call then the name of
     * the method is inserted at that point as if it were a property name.
     */
   static final String[] makePropertyNames(Object names[]) {
      String[] sn = new String[ names.length ];
      for (int i = 0; i < sn.length; i++) {
         sn[i] = (names[i] instanceof Named) ? 
            ((Named) names[i]).getName() : (String) names[i];
      }
      return sn;
   }

   public final String[] getPropertyNames() {
      return makePropertyNames(_names);
   }

   /**
    * Like getPropertyNames, but only works if isSimpleName is true 
    */
   public final String getName() {
     return (_names[0] instanceof Named) ? 
       ((Named) _names[0]).getName() 
       : (String) _names[0];
   }
  
  /**
   * Returns true if the Variable describes a simple name (one with only
   * one element) 
   */
   public final boolean isSimpleName() {
      return (_names.length == 1);
   }

   /**
     * Looks in the hashTable (context) for a value keyed to this variables
     * name and returns the value string. If the resulting value is a Macro,
     * recursively call its evaluate method.
     * @return String 
     */
   final public Object evaluate(Context context)
   {
      try {
         Object val = getValue(context);
         if (val instanceof Macro) {
            val = ((Macro) val).evaluate(context); // recurse
         } 
         return val;
      } catch (NullPointerException e) {
         context.getLog("engine").warning("Variable: " + _vname + " does not exist",e);
         return "<!--\n unable to access variable " 
            + _vname + ": not found in " + context + "\n -->";
      } catch (Exception e) {
         context.getLog("engine").warning("Variable: " + _vname + " does not exist",e);
         return "<!--\n unable to access variable " 
            + _vname + ": " + e + " \n-->";
      }
   }

   /**
     * Look in the hashtable (context) for a value keyed to this variables 
     * name and write its value to the stream.
     * @exception PropertyException is required data is missing
     * @exception IOException if could not write to output stream
     */
   final public void write(FastWriter out, Context context) 
       throws PropertyException, IOException
   {
      try {
         Object val = getValue(context);
         if (val instanceof Macro) {
            ((Macro) val).write(out,context);
         } else {
            if (val != null) {
               out.write(val.toString());
            } else {
               String warning = "Attempt to write out null variable: "
                                 + _vname;
               context.getLog("engine").warning(warning);
               out.write("<!--\n warning: " + warning + " \n-->");
            }
         }
      } catch (Exception e) {
         context.getLog("engine").warning("Variable: " + _vname + 
               " failed to evaluate:" + e, e);
         out.write("<!-- error: variable " + _vname + 
               " failed to evaluate: " + e + " -->");
      }
   }

   /**
     * Helper method to construct a String name from a Object[] name
     */
   final static String makeName(Object[] names) {
      StringBuffer buf = new StringBuffer();
      for (int i = 0; i < names.length; i++) {
         if (i != 0) {
            buf.append(".");
         }
         buf.append(names[i]);
      }
      return buf.toString();
   }

   /**
     * The code to get the value represented by the variable from the 
     * supplied context.
     */
   public abstract Object getValue(Context context) throws PropertyException;

   /**
     * The code to set the value represented by the variable in the 
     * supplied context.
     */
   public abstract void setValue(Context c, Object v) throws PropertyException;

   /**
     * Return the String name of the variable prefixed with a string
     * representing its type. For example local:a.b.c
     */
   public abstract String toString();

   public void accept(TemplateVisitor v) { v.visitVariable(this, _names); } 

}



