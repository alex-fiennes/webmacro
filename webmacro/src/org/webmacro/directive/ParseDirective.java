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

  // We store one or the other of these; a Macro or a String.  
  // This is an optimization, so we don't have to evaluate it if its
  // not a macro, and we don't have to use instanceof on the common code path
  private Macro template = null;
  private String templateString = null;

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
    if (o instanceof Macro) 
      template = (Macro) o;
    else 
      templateString = o.toString();

    return this;
  }

  public void write(FastWriter out, Context context) 
    throws PropertyException, IOException {

    String fname;

    fname = templateString;
    if (fname == null) {
      try {
        fname = template.evaluate(context).toString();
      }
      catch (Exception e) {
        String warning = "#parse: Can't resolve template name";
        context.getLog("engine").warning(warning, e);
        writeWarning(warning, context, out);
      }
    }
    if (fname == null) {
      writeWarning("#parse: file name not specified ", context, out);
      throw new PropertyException("#parse: file name is null");
    }

    try {
      Template tmpl = (Template) context.getBroker().get("template", fname);
      tmpl.write(out,context);
    } catch (IOException e) {
      String warning = "#parse: Error reading template: " + fname;
      context.getLog("engine").warning(warning, e);
      writeWarning(warning, context, out);
    } 
    catch (NotFoundException e) {
      String warning = "#parse: Template not found: " + fname;
      context.getLog("engine").warning(warning,e);
      writeWarning(warning, context, out);
    }
    catch (Exception e) {
      String warning = "#parse: Template could not be loaded: " + fname;
      context.getLog("engine").warning(warning, e);
      writeWarning(warning, context, out);
    }
  }
  
  public void accept(TemplateVisitor v) {
    v.beginDirective(myDescr.name);
    v.visitDirectiveArg("ParseTemplate", template);
    v.endDirective();
  }

}

