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

public class ParseDirective extends Directive {

  private static final int PARSE_TEMPLATE = 1;

  private Macro template;

  private static final ArgDescriptor[] 
    myArgs = new ArgDescriptor[] {
      new QuotedStringArg(PARSE_TEMPLATE), 
    };

  private static final DirectiveDescriptor 
    myDescr = new DirectiveDescriptor("parse", null, myArgs, null);

  public static DirectiveDescriptor getDescriptor() {
    return myDescr;
  }

  public Object build(DirectiveBuilder builder, 
                      BuildContext bc) 
  throws BuildException {
    Object o = builder.getArg(PARSE_TEMPLATE, bc);
    if (o instanceof Macro) {
      template = (Macro) o;
      return this;
    } 
    else 
      try {
        return bc.getBroker().get("template", o.toString());
      } catch (NotFoundException ne) {
        throw new BuildException("#parse: Template " + o + " not found: ", 
                                 ne); 
      } catch (ResourceException ne) {
        throw new BuildException("#parse: Template " + o 
                                 + " could not be loaded: ", ne); 
      }
  }

  public void write(FastWriter out, Context context) 
    throws PropertyException, IOException {

    String fname = template.evaluate(context).toString();
    try {
      Template tmpl = (Template) context.getBroker().get("template", fname);
      tmpl.write(out,context);
    } catch (IOException e) {
      String warning = "#parse: Error reading template: " + fname;
      context.getLog("engine").warning(warning, e);
      writeWarning(warning, out);
    } catch (Exception e) {
      String warning = "#parse: Template not found: " + fname;
      context.getLog("engine").warning(warning,e);
      writeWarning(warning, out);
    }
  }
  
  public void accept(TemplateVisitor v) {
    v.beginDirective(myDescr.name);
    v.visitDirectiveArg("ParseTemplate", template);
    v.endDirective();
  }

}
