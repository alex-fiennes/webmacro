package org.webmacro.directive;

import java.io.*;
import org.webmacro.*;
import org.webmacro.engine.*;

public class IncludeDirective extends Directive {

  private static final int INCLUDE_FILE = 1;

  private Macro file;

  private static final ArgDescriptor[] 
    myArgs = new ArgDescriptor[] {
      new QuotedStringArg(INCLUDE_FILE), 
    };

  private static final DirectiveDescriptor 
    myDescr = new DirectiveDescriptor("include", null, myArgs, null);

  public static DirectiveDescriptor getDescriptor() {
    return myDescr;
  }

  public Object build(DirectiveBuilder builder, 
                      BuildContext bc) 
  throws BuildException {
    Object o = builder.getArg(INCLUDE_FILE, bc);
    if (o instanceof Macro) {
      file = (Macro) o;
      return this;
    } 
    else if (o == null) 
      throw new BuildException("#include: file name is null");
    else {
      try {
        return getFile(bc, o.toString());
      } 
      catch(Exception e) {
        throw new BuildException("#include: exception retrieving file "
                                 + o.toString(), e);
      }
    }
  }

  public void write(FastWriter out, Context context) 
    throws PropertyException, IOException {

    Object fname = file.evaluate(context);
    if (fname == null) {
      writeWarning("#include: cannot retrieve file " 
                   + fname.toString(), out);
      throw new PropertyException("#include: file name is null");
    }
    else {
      try {
        out.write(getFile(context, fname.toString()));
      } 
      catch (Exception e) {
        writeWarning("#include: cannot retrieve file " 
                     + fname.toString(), out);
        throw new PropertyException("#include: cannot retrieve file "
                                   + fname.toString(), e);
      }
    }
  }
  
  static private String getFile(Context c, String name) 
    throws IOException {
    String error = null;
    String result = null;
    String fileName = null;

    try {
      fileName = name.toString();
      result = c.getBroker().get("url", fileName).toString();
    } catch (NotFoundException ne) {
      error = "Cannot include " + fileName + ": NOT FOUND";
    }  catch (NullPointerException ne) {
      error = "Could not load target " + name + ": NULL VALUE";
    }
    if (error != null) {
      throw new IOException("#include failed: " + error);
    }
    return result;
  }
  
  public void accept(TemplateVisitor v) {
    v.beginDirective("include");
    v.visitDirectiveArg("IncludeFile", file);
    v.endDirective();
  }

}
