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
import org.webmacro.engine.*;

public class AlternateDirective extends Directive {

  private static final int ALTERNATE_TARGET  = 1;
  private static final int ALTERNATE_THROUGH = 2;
  private static final int ALTERNATE_LIST    = 3;

  private Variable target;
  private Object   list;

  private static final ArgDescriptor[] 
    myArgs = new ArgDescriptor[] {
      new LValueArg(ALTERNATE_TARGET), 
      new KeywordArg(ALTERNATE_THROUGH, "through"),
      new RValueArg(ALTERNATE_LIST)
    };

  private static final DirectiveDescriptor 
    myDescr = new DirectiveDescriptor("alternate", null, myArgs, null);

  public static DirectiveDescriptor getDescriptor() {
    return myDescr;
  }

  public Object build(DirectiveBuilder builder, 
                      BuildContext bc) 
  throws BuildException {
    try {
      target = (Variable) builder.getArg(ALTERNATE_TARGET, bc);
    } 
    catch (ClassCastException e) {
      throw new NotVariableBuildException(myDescr.name, e);
    }
    list = builder.getArg(ALTERNATE_LIST, bc);
    return this;
  }

  public void write(FastWriter out, Context context) 
    throws PropertyException, IOException {
    Object[] arr;

    try {
      if (list instanceof Macro) 
        arr = (Object[]) ((Macro) list).evaluate(context);
      else 
        arr = (Object[]) list;

      target.setValue(context, (Object) new Alternator(arr));
    }
    catch (ClassCastException e) {
      String errorText = "#alternate: target is not a list: " + list;
      context.getBroker().getLog("engine").error(errorText);
      writeWarning(errorText, out);
    }
    catch (PropertyException e) {
      String errorText = "#alternate: Unable to set value: " + target
        + "\n" + e.toString();
      context.getBroker().getLog("engine").error(errorText);
      writeWarning(errorText, out);
    }
  } 

  public void accept(TemplateVisitor v) {
    v.beginDirective(myDescr.name);
    v.visitDirectiveArg("AlternateTarget", target);
    v.visitDirectiveArg("AlternateList", list);
    v.endDirective();
  }

}


class Alternator implements Macro { 
  private Object[] list;
  private int index = 0;

  public Alternator(Object[] list) {
    this.list = list;
  }

  public void write(FastWriter out, Context context) 
    throws PropertyException, IOException {
    Object o = evaluate(context);
    if (o != null) 
      out.write(o.toString());
  }

  public Object evaluate(Context context) {
    Object o = list[index++];
    if (index == list.length)
      index = 0;
    return o;
  }
}

