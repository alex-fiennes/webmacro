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
import org.webmacro.util.*;
import org.webmacro.*;


final class TermConditionBuilder implements Builder {
   
   final private Object _term;

   public TermConditionBuilder(Object term) {
      _term = term;
   }

   public Object build(BuildContext bc) throws BuildException
   {
      Object term = (_term instanceof Builder) ? 
         ((Builder) _term).build(bc) : _term;
      return (term instanceof Macro) ?
         new TermCondition((Macro) term) : term;
   }
}


/**
  * Utility class
  */
final class TermCondition extends Condition implements Macro, Visitable {

   final private Macro _term;

   TermCondition(Macro term) {
      _term = term;
   }

   final public Object evaluate(Context context) 
      throws PropertyException
   {
      return _term.evaluate(context);
   }

   final public boolean test(Context context) {
      try {
         return isTrue(_term.evaluate(context));
      } catch (Exception e) {
         return false;
      }
   }

   final public String toString() {
      return "TermCondition:" + _term;
   }

   public void accept(TemplateVisitor v) { 
     if (_term instanceof Macro) 
       v.visitMacro(_term);
     else
       v.visitString(_term.toString());
   }

}

