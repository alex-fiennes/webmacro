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
import java.util.*;
import org.webmacro.*;
import org.webmacro.engine.*;

/**
 * Directive is an abstract class which directives can extend.  
 * Nested within Directive (as static classes) are a host of classes used
 * for building directive argument lists.  
 * 
 * Directives are Macros, so they must implement the Macro interface.  
 * For convenience, an implementation of evaluate() (written in terms of
 * write()) is provided in the base class.)  
 * 
 * Directives must implement the following static method:
 *   public DirectiveDescriptor getDescriptor();
 * 
 * It is expected that all directives will build a copy of their descriptor
 * statically, using the various XxxArg() constructors, and return a reference
 * to that from getDescriptor().  
 * @author Brian Goetz
 */ 

public abstract class Directive implements Macro, Visitable { 

  public static final int ArgType_CONDITION    = 1;
  public static final int ArgType_LVALUE       = 2;
  public static final int ArgType_RVALUE       = 3;
  public static final int ArgType_KEYWORD      = 4;
  public static final int ArgType_ASSIGN       = 5;
  public static final int ArgType_BLOCK        = 6;
  public static final int ArgType_LITBLOCK     = 7;
  public static final int ArgType_SUBDIRECTIVE = 8;
  public static final int ArgType_QUOTEDSTRING = 9;
  public static final int ArgType_STRING       = 10;
  public static final int ArgType_GROUP        = 50;
  public static final int ArgType_CHOICE       = 51;

  /**
   * Directives must implement a build() method.  The build method
   * should examine the directive arguments (available through the
   * DirectiveBuilder) and return a macro describing the built
   * directive.  In most cases, build() will just set up the
   * directive's private fields and return 'this', but in some
   * cases, no macro needs be returned (such as directives with only
   * side effects on the build context) or some other macro may be
   * returned (such as in the case of "#if (true) { }" -- no IfDirective
   * object need be returned, just return the block.)  
   */

  public abstract Object build(DirectiveBuilder b, 
                               BuildContext bc)
  throws BuildException;

  /* Convenience implementation of evaluate() which Directives can inherit */
  public Object evaluate(Context context)
    throws PropertyException {
      try {
        ByteArrayOutputStream os = new ByteArrayOutputStream(256);
        FastWriter fw = FastWriter.getInstance(context.getBroker());
        write(fw,context);
        return fw.toString();
      } catch (IOException e) {
         context.getBroker().getLog("engine").error(
           "Directive.evaluate: IO exception on write to StringWriter", e);
         return "";
      }
  }  

  /**
   * Convenience method for directives to write HTML warnings into the output
   * stream.  Eventually this will be parameterizable so that HTML is not
   * assumed to be the only underlying language. 
   */

  protected static String getWarningText(String warning, Context context) 
  throws IOException, PropertyException {
    return context.getEvaluationExceptionHandler()
      .warningString("WARNING: " + warning);
  }

  /**
   * Convenience method for directives to write HTML warnings into the output
   * stream.  Eventually this will be parameterizable so that HTML is not
   * assumed to be the only underlying language. 
   */

  protected static void writeWarning(String warning, 
                                     Context context, 
                                     FastWriter writer) 
  throws IOException, PropertyException {
    writer.write(getWarningText(warning, context));
  }

  public void accept(TemplateVisitor v) {
    v.visitUnknownMacro(this.getClass().getName(), this);
  }

  /* Nested static classes */ 


  /**
   * ArgDescriptor is the base class for all the different types
   * of argument descriptors, like ConditionalArg, KeywordArg, RValueArg,
   * etc.  
   * Most ArgDescriptor constructors take an argId parameter, which is
   * an integer which uniquely identifies the argument which will be passed
   * to DirectiveBuilder.getArg when retrieving the argument.  
   */

  public static abstract class ArgDescriptor {
    public final int id;
    public final int type;
    public boolean optional = false;
    public int subordinateArgs = 0, nextArg = 0;
    public int[] children;
    public String keyword;

    protected ArgDescriptor(int id, int type) { 
      this.id = id;
      this.type = type;
    }

    protected void setOptional() {
      optional = true;
    }

    protected void setOptional(int subordinateArgs) {
      optional = true;
      this.subordinateArgs = subordinateArgs;
    }
  }


  /**
   * Condition argument type.  Accepts a WM expression which evaluates
   * to a boolean result, evaluated according to Expression.isTrue.  
   */
  public static class ConditionArg extends ArgDescriptor {
    public ConditionArg(int id) { 
      super(id, ArgType_CONDITION); 
    }
  }

  /**
   * Block argument type.  Accepts a WM Block.
   */
  public static class BlockArg extends ArgDescriptor {
    public BlockArg(int id) { 
      super(id, ArgType_BLOCK); 
    }
  }

  /**
   * Literal block argument type.  Accepts a WM Block, but does not intepret
   * any directives or property references contained in the block.
   */
  public static class LiteralBlockArg extends ArgDescriptor {
    public LiteralBlockArg(int id) { 
      super(id, ArgType_LITBLOCK); 
    }
  }

  /**
   * RValue argument type.  Accepts any WM expression.  
   */
  public static class RValueArg extends ArgDescriptor {
    public RValueArg(int id) { 
      super(id, ArgType_RVALUE); 
    }
  }

  /**
   * LValue argument type.  Accepts any WM property reference.
   */
  public static class LValueArg extends ArgDescriptor {
    public LValueArg(int id) { 
      super(id, ArgType_LVALUE); 
    }
  }

  /**
   * Quoted string argument type.  Accepts a quoted string with embedded
   * property references (e.g., "hello $foo")
   */
  public static class QuotedStringArg extends ArgDescriptor {
    public QuotedStringArg(int id) { 
      super(id, ArgType_QUOTEDSTRING); 
    }
  }

  /**
   * String argument type.  Accepts either a quoted string with embedded
   * property references (e.g., "hello $foo"), or a single property
   * reference ($something).  
   */
  public static class StringArg extends ArgDescriptor {
    public StringArg(int id) { 
      super(id, ArgType_STRING); 
    }
  }

  /**
   * Keyword argument type.  Accepts only the specified keyword. 
   */
  public static class KeywordArg extends ArgDescriptor {
    public KeywordArg(int id, String keyword) { 
      super(id, ArgType_KEYWORD); 
      this.keyword = keyword;
    }
  }

  /**
   * Assignment.  In the standard parser, the parser looks for an = 
   * operator, but other parsers can handle this as they see fit.
   */
  public static class AssignmentArg extends ArgDescriptor {
    public AssignmentArg() { 
      super(0, ArgType_ASSIGN); 
    }
  }

  /**
   * Optional group.  An optional group can begin with a Keyword argument.
   * If the keyword argument is not present in the input text, the entire
   * group is skipped.  Can contain other groups.  The argCount parameter
   * is the number of simple arguments or groups in this argument group.  
   */
  public static class OptionalGroup extends ArgDescriptor {
    public OptionalGroup(int argCount) { 
      super(0, ArgType_GROUP); 
      setOptional(argCount);
    }
  }

  /**
   * The OptionChoice indicates that several optional groups can be accepted
   * in any order.  The groupCount parameter is the number of OptionalGroup
   * arguments following.  Each group in the choice will be accepted zero
   * or one time, in any order.  
   * For example, OptionChoice would allow a directive with the optional
   * groups (Keyword("from"), RValue()) and (Keyword("max"), RValue()) to
   * accept either "from n max m" or "from n" or "max m from n".  
   */
  public static class OptionChoice extends ArgDescriptor {
    public boolean repeating = true;

    public OptionChoice(int groupCount) { 
      super(0, ArgType_CHOICE); 
      setOptional(groupCount);
    }
  }


  /**
   * The SingleOptionChoice indicates that zero or one of several
   * optional groups can be accepted, but only once.  Otherwise works
   * exactly as OptionChoice.  */
  public static class SingleOptionChoice extends OptionChoice {
    public SingleOptionChoice(int groupCount) { 
      super(groupCount); 
      repeating = false;
    }
  }


  /**
   * Subdirectives are like directives, except that they do not have their
   * own class.  The directive is responsible for fetching and processing
   * the subdirective's arguments.  Each Subdirective can have its own 
   * argument list.  
   */
  public static class Subdirective extends ArgDescriptor {
    public final static int BREAKING = 1;

    public final String name; 
    public final ArgDescriptor[] args;
    public boolean repeating = false, isBreaking = false;
    
    public Subdirective(int id, String name,
                        ArgDescriptor[] args) {
      super(id, ArgType_SUBDIRECTIVE);
      this.name = name;
      this.args = args;
    }

    public Subdirective(int id, String name, ArgDescriptor[] args, 
                        int flags) {
      this(id, name, args);
      isBreaking = ((flags & BREAKING) != 0);
    }
  }

  /**
   * Optional subdirective.  This means that the subdirective can appear
   * zero or one time.
   */
  public static class OptionalSubdirective extends Subdirective {
    public OptionalSubdirective(int id, String name,
                                ArgDescriptor[] args) {
      super(id, name, args);
      setOptional();
    }

    public OptionalSubdirective(int id, String name,
                                ArgDescriptor[] args, int flags) {
      super(id, name, args, flags);
      setOptional();
    }
  }

  /**
   * Optional repeating subdirective.  This means that the
   * subdirective can appear zero or more times.  
   */
  public static class OptionalRepeatingSubdirective
    extends OptionalSubdirective {
    public OptionalRepeatingSubdirective(int id, String name,
                                         ArgDescriptor[] args) {
      super(id, name, args);
      repeating = true;
    }

    public OptionalRepeatingSubdirective(int id, String name,
                                         ArgDescriptor[] args, int flags) {
      super(id, name, args, flags);
      repeating = true;
    }
  }


  // Utility exception classes for use by directives

  /**
   * Utility exception used by directives to signal that an argument
   * that was supposed to be a Variable is not a variable.  Subclasses
   * BuildException.
   */
  public static class NotVariableBuildException extends BuildException {
    public NotVariableBuildException(String directive) {
      super("#" + directive + ": Argument must be a variable");
    }

    public NotVariableBuildException(String directive, Exception e) {
      super("#" + directive + ": Argument must be a variable", e);
    }
  }

  /**
   * Utility exception used by directives to signal that an argument
   * that was supposed to be a simple Variable (only one term) is not.  
   * Subclasses BuildException.
   */
  public static class NotSimpleVariableBuildException extends BuildException {
    public NotSimpleVariableBuildException(String directive) {
      super("#" + directive + ": Argument must be a simple variable");
    }

    public NotSimpleVariableBuildException(String directive, Exception e) {
      super("#" + directive + ": Argument must be a simple variable", e);
    }
  }



}

