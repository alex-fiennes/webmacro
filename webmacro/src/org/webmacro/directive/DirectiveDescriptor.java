package org.webmacro.directive;

import org.webmacro.directive.Directive.ArgDescriptor;
import org.webmacro.directive.Directive.Subdirective;

public final class DirectiveDescriptor { 
  public String                   name;
  public Class                    dirClass;
  public ArgDescriptor[]          args;
  public Subdirective[]           subdirectives;
  public boolean                  valid=false;

  public DirectiveDescriptor(String name, 
                             Class dirClass, 
                             ArgDescriptor[] args, 
                             Subdirective[]  subdirectives) {
    this.name = name;
    this.dirClass = dirClass;
    this.args = args;
    this.subdirectives = subdirectives;

    validateArgs();
  }

  public void validateArgs() {
    valid = true;
    for (int i=0; i<args.length; i++) {
      if (args[i].type == Directive.ArgType_GROUP) {
        if (args[i+1].type != Directive.ArgType_KEYWORD)
          valid = false;
        for (int j=0; j<args[i].subordinateArgs; j++) {
          if (args[i].repeating)
            args[i+1+j].setRepeating();
          if (args[i+1+j].type == Directive.ArgType_GROUP
              || args[i+1+j].type == Directive.ArgType_CHOICE)
            valid = false;
        };
      }
      else if (args[i].type == Directive.ArgType_CHOICE) {
        int k=i+1;
        for (int j=0; j<args[i].subordinateArgs; j++) {
          if (args[k].type != Directive.ArgType_GROUP)
            valid = false;
          k += 1 + args[k].subordinateArgs;
        }
      }
    }
  }
}
