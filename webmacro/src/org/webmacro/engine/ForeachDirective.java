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
final class ForeachDirective implements Directive, Visitable
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
     * @exception PropertyException if required data was missing from context
     * @exception IOException if we could not successfully write to out
     */
   public void write(FastWriter out, Context context) 
      throws PropertyException, IOException
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
               throw new PropertyException("The object used as the list of values in a foreach statement must have some way of returning a list type, or be a list type itself. See the documentation for PropertyOperator.getIterator() for more details. No such property was found on the supplied object: " + list, e);
            }
            while(iter.hasNext()) {
               _iterVar.setValue(context, iter.next());
               _body.write(out, context);
            }
         }
      } catch (PropertyException e) {
         context.getBroker().getLog("engine", "parsing and template execution").error(
            "unable to set a list item of list: " + _list); 
         out.write("<!--\n Unable to resolve list " + _list + " \n-->");
      }
   }

   /**
     * Interpret the foreach directive by looking up the list reference
     * in the supplied context and iterating through it. Return the 
     * result as a string.
     * @exception PropertyException is required data is missing
     */ 
   public Object evaluate(Context context)
      throws PropertyException
   {
      FastWriter fw = FastWriter.getInstance();
      try {
         write(fw,context);
         String ret = fw.toString();
         fw.close();
         return ret;
      } catch (IOException e) {
         e.printStackTrace();
         return null; // never gonna happen
      }
   }  

   public void accept(TemplateVisitor v) {
      v.beginDirective("foreach");
      v.visitDirectiveArg("ForeachTarget", _iterVar);
      v.visitDirectiveArg("ForeachList", _list);
      v.visitDirectiveArg("ForeachBlock", _body);
      v.endDirective();
   }


}


