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

public class VariableBuilder implements Builder 
{

   private final Object[] _names;
   private final boolean _filtered;

   public VariableBuilder(Object names[], boolean filtered) {
      _names = names;
      _filtered = filtered;
   }

   static Macro newVariable(
         Object names[], BuildContext bc, boolean filtered) 
      throws BuildException
   {

      Variable v = null;

      if (names.length < 1) {
         throw new BuildException("Variable with name of length zero!");
      }

      Object c[] = new Object[ names.length ];
      for (int i = 0; i < c.length; i++) {
         c[i] = (names[i] instanceof Builder) ? 
            ((Builder) names[i]).build(bc) : names[i];
      }

      Object type = bc.getVariableType(c[0].toString());
      if (type == Variable.PROPERTY_TYPE) {
         if (c.length == 1) 
            v = new SimplePropertyVariable(c);
         else
            v = new PropertyVariable(c); 
      } else if (type == Variable.LOCAL_TYPE) {
         v = new GlobalVariable(c);
      } else {
         throw new BuildException("Unrecognized Variable Type: " + type);
      }

      return filtered ? bc.getFilterMacro(v) : v;
   }

   public final Object build(BuildContext bc) throws BuildException
   {
      return newVariable( _names, bc, _filtered);
   }
}

