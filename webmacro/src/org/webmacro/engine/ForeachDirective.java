
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

import org.webmacro.*;
import java.util.*;
import java.io.*;
import org.webmacro.util.*;

/**
  * This directive is used to iterate through the members of a list. 
  * <p>
  * It takes an argument of type Variable and iterates through 
  * each of its members. Each time through the loop a scalar with the 
  * same name of the list is defined, and takes on the value of the list 
  * for that element. The scalar will have the same type as the value of
  * the list element, so if the list contains hashtables, referencing the
  * members of the hashtable will work as expected.
  */
final class ForeachDirective implements Directive
{

   final static private String[] _verbs = { "in" };

   /**
     * This is a reference to the list we iterate through
     */
   final private Object _list;

   /**
     * This is the block that is output each time through the iteration
     */
   final private Macro _body;

   /**
     * This is the variable reference used as the iterator
     */
   final private Variable _iterVar;

   /**
     * Whether or not our list var is a macro
     */
   final private boolean _macro;

   /**
     *  Constuctor for ForeachDirective.
     */
   ForeachDirective(Object list, Variable iterVar, Macro body) {
      _list = list; 
      _iterVar = iterVar;
      _body = body;
      _macro = (list instanceof Macro);
   }

   /**
     * Builder method
     */
   public static final Object build(
         BuildContext rc, Object listVar, Argument[] args,  Macro block) 
      throws BuildException
   {
      Variable v;


      if ((args.length != 1) || (! args[0].getName().equals("in"))) {
         throw new BuildException("foreach expects a single argument: in");
      }
      Object list = args[0].getValue();   

      try {
         v = (Variable) listVar;
      } catch (ClassCastException e) {
         throw new BuildException("Foreach iterator must be a variable");
      }
      return new ForeachDirective(list, v, block);
   }

   public static final String[] getArgumentNames() { return _verbs; }

   /**
     * Interpret the directive and write it out
     * <p>
     * @exception ContextException if required data was missing from context
     * @exception IOException if we could not successfully write to out
     */
   public void write(FastWriter out, Context context) 
      throws ContextException, IOException
   {
      // now clobber values outside the loop:
      // Map listMap = new HashMap();
      // listMap.include(context);

      Object list = _list;
      if (_macro) {
         while (list instanceof Macro) {
            list = ((Macro) list).evaluate(context);
         }
      }

      try {
         if (list instanceof Object[]) {
            Object[] alist = (Object[]) list;
            for (int i = 0; i < alist.length; i++) {
               _iterVar.setValue(context, alist[i]);
               _body.write(out, context);
            }
         } else {
            Iterator iter;
            try {
               iter = PropertyOperator.getIterator(list);
            } catch (Exception e) {
               throw new ContextException("The object used as the list of values in a foreach statement must have some way of returning a list type, or be a list type itself. See the documentation for PropertyOperator.getIterator() for more details. No such property was found on the supplied object: " + list + ": " + e);
            }
            while(iter.hasNext()) {
               _iterVar.setValue(context, iter.next());
               _body.write(out, context);
            }
         }
      } catch (ContextException e) {
         context.getBroker().getLog("engine").error(
            "unable to set a list item of list: " + _list); 
         out.write("<!--\n Unable to resolve list " + _list + " \n-->");
      }
   }

   /**
     * Interpret the foreach directive by looking up the list reference
     * in the supplied context and iterating through it. Return the 
     * result as a string.
     * @exception ContextException is required data is missing
     */ 
   public Object evaluate(Context context)
      throws ContextException
   {
      try {
         ByteArrayOutputStream os = new ByteArrayOutputStream(256);
         FastWriter fw = new FastWriter(os, "UTF8");
         write(fw,context);
         fw.flush();
         return os.toString("UTF8");
      } catch (IOException e) {
         context.getBroker().getLog("engine").error(
            "evaluate got IO exception on write to StringWriter");
         return "";
      }
   }  


}


