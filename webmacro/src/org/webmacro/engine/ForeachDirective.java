
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

import org.webmacro.util.java2.*;
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
         BuildContext rc, Object listVar, Object list, Macro block) 
      throws BuildException
   {
      Variable v;
      try {
         v = (Variable) listVar;
      } catch (ClassCastException e) {
         throw new BuildException("Foreach iterator must be a variable");
      }
      return new ForeachDirective(list, v, block);
   }

   public static final String getVerb() { return "in"; }

   /**
     * Interpret the directive and write it out
     * <p>
     * @exception InvalidContextException if required data was missing from context
     * @exception IOException if we could not successfully write to out
     */
   public void write(Writer out, Object context) 
      throws InvalidContextException, IOException
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

      Iterator iter;
      try {
         iter = PropertyOperator.getIterator(list);
      } catch (Exception e) {
         throw new InvalidContextException("The object used as the list of values in a foreach statement must have some way of returning a list type, or be a list type itself. See the documentation for PropertyOperator.getIterator() for more details. No such property was found on the supplied object: " + list);
      }
      Object listItem;

      // deals with an empty list substitute appropriate variables and 
      // prints block out once
      if (iter == null) {
	 listItem = "<!--\n " +  _list + ": is empty \n-->";   
         try {
            _iterVar.setValue(context, listItem);
         } catch (InvalidContextException e) {
            Engine.log.exception(e);
            Engine.log.error("Unable to resolve list" + _list);
            out.write("<!--\n Unable to resolve list " + _list + " \n-->");
         }
         _body.write(out, context);

      // iterates through all items including null ones which will
      // print error messages
      } else {
         while(iter.hasNext()) {
            if ((listItem = iter.next()) == null) {
	       listItem = "<!--\n " +  _list + ": contained a null item \n-->";   
	    }
            try {
               _iterVar.setValue(context, listItem);
               _body.write(out, context);
            } catch (InvalidContextException e) {
               Engine.log.exception(e);
               Engine.log.error("unable to set a list item of list: " + _list); 
               out.write("<!--\n Unable to resolve list " + _list + " \n-->");
            }
         }
      }
   }

   /**
     * Interpret the foreach directive by looking up the list reference
     * in the supplied context and iterating through it. Return the 
     * result as a string.
     * @exception InvalidContextException is required data is missing
     */ 
   public Object evaluate(Object context)
      throws InvalidContextException
   {
      try {
         StringWriter sw = new SizedStringWriter(512);
         write(sw,context);
         return sw.toString();
      } catch (IOException e) {
         Engine.log.exception(e);
         Engine.log.error("evaluate got IO exception on write to StringWriter");
         return "";
      }
   }  


}


