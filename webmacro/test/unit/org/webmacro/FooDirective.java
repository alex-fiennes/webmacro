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

package org.webmacro;

import java.io.*;
import org.webmacro.*;
import org.webmacro.directive.Directive;
import org.webmacro.directive.DirectiveDescriptor;
import org.webmacro.directive.DirectiveBuilder;
import org.webmacro.engine.*;

public class FooDirective extends Directive {

  private static final ArgDescriptor[] 
    myArgs = new ArgDescriptor[] {
    };

  private static final DirectiveDescriptor
    myDescr = new DirectiveDescriptor("foo", null, myArgs, null);

  public static DirectiveDescriptor getDescriptor() {
    return myDescr;
  }

  public Object build(DirectiveBuilder builder,
                      BuildContext bc) 
  throws BuildException {
    return "foo";
  }

  public void write(FastWriter out, Context context) 
    throws PropertyException, IOException {
      out.write ("foo");
  }

  public void accept(TemplateVisitor v) {
    v.beginDirective(myDescr.name);
    v.endDirective();
  }

}
