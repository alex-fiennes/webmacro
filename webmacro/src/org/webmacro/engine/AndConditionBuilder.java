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
import org.webmacro.util.*;

/**
  * Utility class
  */
final class AndConditionBuilder implements Builder {
  
   private final Builder _l,_r;
   AndConditionBuilder(Builder l, Builder r) { _l = l; _r = r; }

   /**
     * Returns Boolean.TRUE, Boolean.FALSE, or a Condition
     */
   public final Object build(BuildContext pc) 
      throws BuildException
   {
      Object l, r;
      l = _l.build(pc);
      r = _r.build(pc);

      
      return ((l instanceof Condition) && (r instanceof Condition)) ?
         new AndCondition((Condition) l, (Condition) r)
         :
         (l instanceof Condition) ?
            Condition.isTrue(r) ? l : Boolean.FALSE
            :
            Condition.isTrue(l) ? r : Boolean.FALSE;
   }
}

/**
  * Utility class
  */
final class AndCondition extends Condition implements Macro, Visitable {
  
   private final Condition _l,_r;
   AndCondition(Condition l, Condition r) { _l = l; _r = r; }

   public final boolean test(Context context) {
      return (_l.test(context) && _r.test(context));
   }

   public void accept(TemplateVisitor v) { 
     v.visitBinaryOperation("AndCondition", _l, _r); 
   }
}

