package org.webmacro.directive;

import java.io.*;
import java.util.Iterator;
import org.webmacro.*;
import org.webmacro.engine.*;
import org.webmacro.util.*;

public class ForeachDirective extends Directive {

  private static final int FOREACH_TARGET     = 1;
  private static final int FOREACH_IN_K       = 2;
  private static final int FOREACH_LIST       = 3;
  private static final int FOREACH_BODY       = 4;
  private static final int FOREACH_INDEXING_K = 5;
  private static final int FOREACH_INDEX      = 6;
  private static final int FOREACH_LIMIT_K    = 7;
  private static final int FOREACH_LIMIT      = 8;
  private static final int FOREACH_FROM_K     = 9;
  private static final int FOREACH_FROM       = 10;

  private Variable target, index;
  private Object   list, indexFromExpr, limitExpr;
  private Macro    body;

  // Syntax:
  // #foreach list-var in list-expr 
  //   [ limit n ] [ indexing $i [ from m ] ] 
  // { block } 

  private static final ArgDescriptor[] 
    myArgs = new ArgDescriptor[] {
      new LValueArg(FOREACH_TARGET), 
      new KeywordArg(FOREACH_IN_K, "in"),
      new RValueArg(FOREACH_LIST), 
      new OptionChoice(2),
        new OptionalGroup(3), 
          new KeywordArg(FOREACH_INDEXING_K, "indexing"),
          new LValueArg(FOREACH_INDEX),
          new OptionalGroup(2), 
            new KeywordArg(FOREACH_FROM_K, "from"),
            new RValueArg(FOREACH_FROM),
        new OptionalGroup(2),
          new KeywordArg(FOREACH_LIMIT_K, "limit"),
          new RValueArg(FOREACH_LIMIT),
      new BlockArg(FOREACH_BODY)
    };

  private static final DirectiveDescriptor 
    myDescr = new DirectiveDescriptor("foreach", null, myArgs, null);
  
  public static DirectiveDescriptor getDescriptor() {
    return myDescr;
  }

  public Object build(DirectiveBuilder builder, 
                      BuildContext bc) 
  throws BuildException {
    try {
      target = (Variable) builder.getArg(FOREACH_TARGET, bc);
      index  = (Variable) builder.getArg(FOREACH_INDEX, bc);
    }
    catch (ClassCastException e) {
      throw new NotVariableBuildException(myDescr.name, e);
    }
    list   = builder.getArg(FOREACH_LIST, bc);
    body   = (Block) builder.getArg(FOREACH_BODY, bc);
    indexFromExpr = builder.getArg(FOREACH_FROM, bc);
    limitExpr     = builder.getArg(FOREACH_LIMIT, bc);
    return this;
  }

  public void write(FastWriter out, Context context) 
    throws ContextException, IOException {

    Object l, limit, from;
    long loopLimit=-1, loopStart=1, loopIndex=0;

    l = list;
    while (l instanceof Macro) 
      l = ((Macro) l).evaluate(context);

    if (limitExpr != null) {
      limit = limitExpr;
      while (limit instanceof Macro)
        limit = ((Macro) limit).evaluate(context);
      if (Expression.isNumber(limit)) 
        loopLimit = Expression.numberValue(limit);
      else
        throw new ContextException("#foreach: Cannot evaluate limit");
    }

    if (index != null && indexFromExpr != null) {
      from = indexFromExpr;
      while (from instanceof Macro)
        from = ((Macro) from).evaluate(context);
      if (Expression.isNumber(from)) 
        loopStart = Expression.numberValue(from);
      else
        throw new ContextException("#foreach: Cannot evaluate loop start");
    }

    try {
      if (l instanceof Object[]) {
        Object[] alist = (Object[]) l;
        for (int i = 0; i < alist.length; i++) {
          target.setValue(context, alist[i]);
          if (index != null) 
            index.setValue(context, new Long(loopIndex + loopStart));
          body.write(out, context);
          ++loopIndex;
          if (loopLimit > 0 && loopIndex >= loopLimit) 
            break;
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
          if (index != null) 
            index.setValue(context, new Long(loopIndex + loopStart));
          body.write(out, context);
          ++loopIndex;
          if (loopLimit > 0 && loopIndex >= loopLimit) 
            break;
        }
      }
    } catch (ContextException e) {
      String errorText = "#foreach: Unable to set list index";
      context.getBroker().getLog("engine").error(errorText);
      writeWarning(errorText, out);
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
