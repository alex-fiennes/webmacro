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

/**
  * This condition is set with a term value which is already 
  * resolved (ie: not a Macro). It resolves to false if this value
  * is null or Boolean.FALSE, and true otherwise. No macro resolution
  * is performed--this is a utility which is convenient in the 
  * implementation of the DirectiveBuilder and other classes, which
  * helps with static resolution.
  *
  * Note that this class DOES NOT implement Macro
  */
final class ConstantCondition extends Condition {

   private final boolean _value;

   ConstantCondition(Object cond) { 
      _value = isTrue(cond);
   }

   final public boolean test(Context context) {
      return _value;
   }

}
