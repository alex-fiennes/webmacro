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


   // null: because in BuildContext.getVariableType() we can just
   // return null from a HashMap for a never before heard of variable
   // to mean that it is a PROPERTY_TYPE. Only this code right here
   // and BuildContext.getVariableType() needs to know that.
   final static public Object PROPERTY_TYPE = new Object();
   final static public Object LOCAL_TYPE = new Object();

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
   final public Object evaluate(Context context) throws PropertyException {
      try {
         Object val = getValue(context);
         if (val instanceof Macro) {
            val = ((Macro) val).evaluate(context); // recurse
         } 
         return val;
      } catch (NullPointerException e) {
         context.getLog("engine")
           .warning("Variable: " + _vname + " does not exist");
         return context.getEvaluationExceptionHandler()
           .handle(this, context, new NullVariableException(_vname));
      } catch (Exception e) {
         context.getLog("engine")
           .warning("Variable: exception evaluating " + _vname + ":\n" + e, e);
         return context.getEvaluationExceptionHandler()
           .handle(this, context, 
                   new PropertyException("Variable: exception evaluating " 
                                         + _vname, e));
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
            
            if (val instanceof VoidMacro) {
                // log a notice that the user used a variable
                // with a void return type
                // the method actually executes, but since it's return 
                // value is coerced into a VoidMacro, nothing is written
                // to the output stream
                context.getLog("engine")
                       .notice ("Variable: $" + _vname + 
                                " has Void return type");
            }
         } else {
            if (val != null) {
                // if val.toString() evaluates to null
                // it is trapped and logged below
                // since this probably won't happen often
                // we'll let Java throw the exception
                // instead of doing the if (val.toString() == null) thing
                // at this point
               out.write(val.toString());
               
            } else {
                if (isSimpleName ()) {
                    // user accessed a variable that isn't in the context
                    //     $ObjectNotInContext
                    context.getLog("engine")
                           .warning("Variable: $" + _vname + 
                                    " does not exist in context");
              
                    out.write(context.getEvaluationExceptionHandler()
                                     .handle(this, context, 
                                     new VariableNotInContextException(_vname)));
                } else {
                    // user accessed a valid property who's value is null
                    
                    context.getLog("engine")
                           .warning("Variable: $" + _vname + 
                                    " evaluated to null");
              
                    out.write(context.getEvaluationExceptionHandler()
                                     .handle(this, context, 
                                     new NullVariableException(_vname)));
                }
            }
         }
      } catch (PropertyException pe) {
          // most likely, user tried to access a method/property
          // of this Variable that doesn't exist, so treat it as
          // a warning
          
          // log it
         context.getLog("engine")
                .warning("Variable: " + pe.getMessage());

         // write it out to stream
         out.write (context.getEvaluationExceptionHandler()
            .handle (this, context, pe));
      } catch (NullPointerException e) {
         // an NPE at this point indicates that the Variable *does* exist
         // in the context, but it's .toString() method returned null.
         // The variable reference would be written like this:
         //         $ValidVariable
          
         String message = "The .toString() method of $" + _vname + " returned null. " +
                          "This probably indicates a problem with your code, not " +
                          "WebMacro";
         
         // we'll log an error here, but w/o a stack trace
         // b/c the stack trace is useless to the user for this particular error
         // as it's a bug in his code, not ours.
         context.getLog ("engine")
                .error (message);
         
         out.write (context.getEvaluationExceptionHandler ()
                           .handle (this, context, new PropertyException (message)));
      } catch (Exception e) {
          // something we weren't expecting happened!
          // I wonder if we would ever get here?  --eric
         String message = "Variable: unexpected exception evaluating " + _vname + ":\n" + e; 
         context.getLog("engine")
                .error(message, e);
         
         out.write (context.getEvaluationExceptionHandler()
            .handle (this, context, e));
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

   /** 
    * Return the canonical name for this variable
    */
   public String getVariableName() {
      return _vname;
   }

   public void accept(TemplateVisitor v) { v.visitVariable(this, _names); } 

}



