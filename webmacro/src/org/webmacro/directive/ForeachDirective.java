package org.webmacro.directive;

import java.io.*;
import java.util.Iterator;
import org.webmacro.*;
import org.webmacro.engine.*;
import org.webmacro.util.*;

public class ForeachDirective extends Directive {

  private static final int FOREACH_TARGET = 1;
  private static final int FOREACH_IN     = 2;
  private static final int FOREACH_LIST   = 3;
  private static final int FOREACH_BODY   = 4;

  private Variable target;
  private Object   list;
  private Macro    body;

  private static final ArgDescriptor[] 
    myArgs = new ArgDescriptor[] {
      new LValueArg(FOREACH_TARGET), 
      new KeywordArg(FOREACH_IN, "in"),
      new RValueArg(FOREACH_LIST), 
      new BlockArg(FOREACH_BODY)
    };

  private static final DirectiveDescriptor 
    myDescr = new DirectiveDescriptor("foreach", ForeachDirective.class, 
                                      myArgs, null);
  
  public static DirectiveDescriptor getDescriptor() {
    return myDescr;
  }

  public Object build(DirectiveBuilder builder, 
                      BuildContext bc) 
  throws BuildException {
    target = (Variable) builder.getArg(FOREACH_TARGET, bc);
    list   = builder.getArg(FOREACH_LIST, bc);
    body   = (Block) builder.getArg(FOREACH_BODY, bc);
    return this;
  }

  public void write(FastWriter out, Context context) 
    throws ContextException, IOException {

    Object l = list;
    while (l instanceof Macro) 
      l = ((Macro) l).evaluate(context);
      
    try {
      if (l instanceof Object[]) {
        Object[] alist = (Object[]) l;
        for (int i = 0; i < alist.length; i++) {
          target.setValue(context, alist[i]);
          body.write(out, context);
        }
      } else {
        Iterator iter;
        try {
          iter = PropertyOperator.getIterator(l);
        } catch (Exception e) {
          throw new ContextException("The object used as the list of values in a foreach statement must have some way of returning a list type, or be a list type itself. See the documentation for PropertyOperator.getIterator() for more details. No such property was found on the supplied object: " + l + ": ", e);
        }
        while(iter.hasNext()) {
          target.setValue(context, iter.next());
          body.write(out, context);
        }
      }
    } catch (ContextException e) {
      //Engine.log.exception(e);
      //Engine.log.error("unable to set a list item of list: " + _list); 
      out.write("<!--\n Unable to resolve list " + list + " \n-->");
    }
  } 

  public void accept(TemplateVisitor v) {
    v.beginDirective(myDescr.name);
    v.visitDirectiveArg("ForeachTarget", target);
    v.visitDirectiveArg("ForeachList", list);
    v.visitDirectiveArg("ForeachBlock", body);
    v.endDirective();
  }
  
}
