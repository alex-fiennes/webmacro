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
  * A predicate is a term passed to a Directive. It consists of a string 
  * word representing the action to beformed, and a target object on which 
  * to perform the action. The target object can be any kind of WebMacro
  * term. 
  */
public final class Argument
{

   private String _name;
   private Object _value;

   /**
     * Create a new predicate
     */
   public Argument(String name, Object value) {
      _name = name;
      _value = value;
   }

   /**
     * Return the action code for this predicate
     */
   final public String getName() { return _name; }

   /**
     * Return the object on which this predicate operates
     */
   final public Object getValue() { return _value; }

   /**
     * Make sure that _value is not a builder! Not a public method,
     * used by Directivebuilder and other things that build Argument
     * lists.
     */
   final void build(BuildContext bc) throws BuildException
   {
      if (_value instanceof Builder) {
         _value = ((Builder) _value).build(bc);
      }
   }

}
