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
    target = (Variable) builder.getArg(ALTERNATE_TARGET, bc);
    list = builder.getArg(ALTERNATE_LIST, bc);
    return this;
  }

  public void write(FastWriter out, Context context) 
    throws ContextException, IOException {
    boolean done=false;
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
    catch (ContextException e) {
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
    throws ContextException, IOException {
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

