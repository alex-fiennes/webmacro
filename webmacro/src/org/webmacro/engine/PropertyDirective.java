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
import org.webmacro.*;

/**
  * This directive marks the named variable as being of property type
  */
abstract class PropertyDirective implements Directive
{

   public static Object build(BuildContext rc, Object variable) 
      throws BuildException
   {

      try {
         Variable v = (Variable) variable;
         String nm[] = v.getPropertyNames();
         if (nm.length > 1) {
            throw new BuildException("Argument to #property cannot be a complex variable, must have only one term.");
         }
         rc.setVariableType(nm[0], Variable.PROPERTY_TYPE);
      } catch (ClassCastException e) {
         throw new BuildException("Argument to #property must be a variable");
      }
      return null;
   }
}


