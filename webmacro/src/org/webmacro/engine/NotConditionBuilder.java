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

final class NotConditionBuilder implements Builder {

   private Builder _cond;

   NotConditionBuilder(Builder condition) {
      _cond = condition;
   }

   public Object build(BuildContext bc) throws BuildException
   {
      Object c = _cond.build(bc);
      return (c instanceof Condition) ?
         (Object) new NotCondition((Condition) c) 
         :
         (Object) (Condition.isTrue(c) ? Boolean.FALSE : Boolean.TRUE);
   }
}

/**
  * Utility class
  */
final class NotCondition extends Condition implements Macro, Visitable {

   private final Condition _cond;

   NotCondition(Condition c) {
      _cond = c;
   }

   final public boolean test(Context context) {
      return (! _cond.test(context) );
   }

   public void accept(TemplateVisitor v) { 
     v.visitUnaryOperation("NotCondition", _cond); 
   }

}
