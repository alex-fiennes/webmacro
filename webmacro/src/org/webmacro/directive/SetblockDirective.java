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

package org.webmacro.directive;

import java.io.*;

import org.webmacro.*;
import org.webmacro.engine.BuildContext;
import org.webmacro.engine.BuildException;
import org.webmacro.engine.Variable;

public class SetblockDirective extends Directive {

   private static final int SETBLOCK_AS = 1;
   private static final int SETBLOCK_MACRO = 2;
   private static final int SETBLOCK_TARGET = 3;
   private static final int SETBLOCK_RESULT = 4;

   private Variable target;
   private Object result;
   private boolean asMacro = false;

   private static final ArgDescriptor[]
         myArgs = new ArgDescriptor[]{
            new OptionalGroup(2),
            new KeywordArg(SETBLOCK_AS, "as"),
            new KeywordArg(SETBLOCK_MACRO, "macro"),
            new LValueArg(SETBLOCK_TARGET),
            new BlockArg(SETBLOCK_RESULT)
         };

   private static final DirectiveDescriptor
         myDescr = new DirectiveDescriptor("setblock", null, myArgs, null);

   public static DirectiveDescriptor getDescriptor() {
      return myDescr;
   }

   public SetblockDirective() {
   }

   public Object build(DirectiveBuilder builder,
                       BuildContext bc)
         throws BuildException {
      try {
         target = (Variable) builder.getArg(SETBLOCK_TARGET, bc);
      }
      catch (ClassCastException e) {
         throw new NotVariableBuildException(myDescr.name, e);
      }
      Object macroKeyword = builder.getArg(SETBLOCK_MACRO, bc);
      asMacro = (macroKeyword != null);
      result = builder.getArg(SETBLOCK_RESULT, bc);
      return this;
   }

   public void write(FastWriter out, Context context)
         throws PropertyException, IOException {

      try {
         if (result instanceof Macro && !asMacro)
            target.setValue(context, ((Macro) result).evaluate(context));
         else
            target.setValue(context, result);
      }
      catch (PropertyException e) {
         throw e;
      }
      catch (Exception e) {
         String errorText = "#setblock: Unable to set " + target;
         context.getBroker().getLog("engine").error(errorText);
         writeWarning(errorText, context, out);
      }
   }

   public void accept(TemplateVisitor v) {
      v.beginDirective(myDescr.name);
      if (asMacro){
         v.visitDirectiveArg("SetblockKeyword", "as");
         v.visitDirectiveArg("SetblockKeyword", "macro");
      }
      v.visitDirectiveArg("SetblockTarget", target);
      v.visitDirectiveArg("SetblockValue", result);
      v.endDirective();
   }

}
