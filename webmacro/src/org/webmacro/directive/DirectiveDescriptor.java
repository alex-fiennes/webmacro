package org.webmacro.directive;

import org.webmacro.directive.Directive.ArgDescriptor;
import org.webmacro.directive.Directive.Subdirective;

/**
 * Each directive needs a DirectiveDescriptor to describe how it
 * should be parsed and built by the parser.  The directive descriptor
 * identifies the directive's name, the class of the underlying concrete
 * directive object, a list of directive argument descriptors, and a list
 * of subdirective descriptors.  
 *
 * The args field is an array of Directive.ArgDescriptor objects.  There
 * are static nested classes within Directive for each type of argument 
 * -- Condition, LValue, RValue, Keyword, Punctuation, Block, LiteralBlock, 
 * and special argument descriptors for OptionalGroup and 
 * OptionalRepeatingGroup.  These allow the directive writer to specify 
 * a flexible syntax for directive arguments.  
 * Each directive can have a set of subdirectives, and each subdirective
 * can have its own argument list.  Subdirectives can be required, optional, 
 * or optional-repeating (multiple subdirectives of the same kind, like 
 * #elseif.)  There are constructors for Subdirective in the Directive module
 * as well.  
 */

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
