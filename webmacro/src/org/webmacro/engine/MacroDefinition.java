/*
 * Copyright (C) 1998-2001 Semiotek Inc.  All Rights Reserved.
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


/**
 * MacroDefinition.java
 *
 * Represents a (C-style) macro, which gets expanded during the
 * building of a template.  Not to be confused with Macro as used
 * by WebMacro, which is something else.
 * @author Brian Goetz
 */

public class MacroDefinition {

   private String name;
   private String[] argNames;
   Object macroBody;

   public MacroDefinition(String name,
                          String[] argNames,
                          Object macroBody) {
      this.name = name;
      this.argNames = argNames;
      this.macroBody = macroBody;
   }

   public String[] getArgNames() {
      return argNames;
   }

   public String getName() {
      return name;
   }

   public Object getMacroBody() {
      return macroBody;
   }

   public Object expand(Object[] args, BuildContext bc)
         throws BuildException {
      if (args.length != argNames.length)
         throw new BuildException("Macro #" + name + " invoked with "
                                  + args.length + " arguments, expecting "
                                  + argNames.length + " arguments");
      if (macroBody instanceof Builder) {
         if (bc instanceof MacroBuildContext)
            bc = ((MacroBuildContext) bc).getRootContext();
         MacroBuildContext mbc = new MacroBuildContext(this, args, bc);
         return ((Builder) macroBody).build(mbc);
      }
      else
         return macroBody;
   }
}

