package org.webmacro.directive;

import java.io.*;
import java.util.*;
import org.webmacro.*;
import org.webmacro.engine.*;

public abstract class Directive implements Macro { 

  public static final int ArgType_CONDITION    = 1;
  public static final int ArgType_LVALUE       = 2;
  public static final int ArgType_RVALUE       = 3;
  public static final int ArgType_KEYWORD      = 4;
  public static final int ArgType_PUNCT        = 5;
  public static final int ArgType_BLOCK        = 6;
  public static final int ArgType_LITBLOCK     = 7;
  public static final int ArgType_SUBDIRECTIVE = 8;
  public static final int ArgType_GROUP        = 50;
  public static final int ArgType_CHOICE       = 51;

  public static final int Punct_COMMA          = 1;
  public static final int Punct_LPAREN         = 2;
  public static final int Punct_RPAREN         = 3;
  public static final int Punct_EQUALS         = 4;

  public abstract Object build(DirectiveBuilder b, 
                               BuildContext bc)
  throws BuildException;

  /* Convenience implementation of evaluate() which Directives can inherit */
  public Object evaluate(Context context)
    throws ContextException {
      try {
        ByteArrayOutputStream os = new ByteArrayOutputStream(256);
        FastWriter fw = new FastWriter(os, "UTF8");
        write(fw,context);
        fw.flush();
        return os.toString("UTF8");
      } catch (IOException e) {
        // @@@ Log something 
        return "";
      }
  }  


  /* Nested static classes */ 


  public static class ArgDescriptor {
    public final int id;
    public final int type;
    public boolean optional = false, repeating = false;
    public int subordinateArgs = 0;
    public int punctId;
    public String keyword;

    protected ArgDescriptor(int id, int type) { 
      this.id = id;
      this.type = type;
    }

    public int getSubordinateArgs() {
      return subordinateArgs;
    }

    protected void setOptional() {
      optional = true;
    }

    protected void setOptional(int subordinateArgs) {
      optional = true;
      this.subordinateArgs = subordinateArgs;
    }

    protected void setRepeating() {
      repeating = true;
    }
  }


  public static class ConditionArg extends ArgDescriptor {
    public ConditionArg(int id) { 
      super(id, ArgType_CONDITION); 
    }
  }

  public static class BlockArg extends ArgDescriptor {
    public BlockArg(int id) { 
      super(id, ArgType_BLOCK); 
    }
  }

  public static class LiteralBlockArg extends ArgDescriptor {
    public LiteralBlockArg(int id) { 
      super(id, ArgType_LITBLOCK); 
    }
  }

  public static class RValueArg extends ArgDescriptor {
    public RValueArg(int id) { 
      super(id, ArgType_RVALUE); 
    }
  }

  public static class LValueArg extends ArgDescriptor {
    public LValueArg(int id) { 
      super(id, ArgType_LVALUE); 
    }
  }

  public static class KeywordArg extends ArgDescriptor {
    public KeywordArg(int id, String keyword) { 
      super(id, ArgType_KEYWORD); 
      this.keyword = keyword;
    }
  }

  public static class PunctArg extends ArgDescriptor {
    public PunctArg(int id, int punctId) { 
      super(id, ArgType_PUNCT); 
      this.punctId = punctId;
    }
  }

  public static class OptionalGroup extends ArgDescriptor {
    public OptionalGroup(int argCount) { 
      super(0, ArgType_GROUP); 
      setOptional(subordinateArgs);
    }
  }

  public static class OptionalRepeatingGroup extends ArgDescriptor {
    public OptionalRepeatingGroup(int argCount) { 
      super(0, ArgType_GROUP); 
      setOptional(subordinateArgs);
      setRepeating();
    }
  }

  public static class OptionChoice extends ArgDescriptor {
    public OptionChoice(int groupCount) { 
      super(0, ArgType_CHOICE); 
      setOptional(groupCount);
    }
  }

  public static class Subdirective extends ArgDescriptor {
    public final String name; 
    public final ArgDescriptor[] args;
    
    public Subdirective(int id, String name,
                        ArgDescriptor[] args) {
      super(id, ArgType_SUBDIRECTIVE);
      this.name = name;
      this.args = args;
    }
  }

  public static class OptionalSubdirective extends Subdirective {
    public OptionalSubdirective(int id, String name,
                                ArgDescriptor[] args) {
      super(id, name, args);
      setOptional();
    }
  }

  public static class OptionalRepeatingSubdirective
    extends OptionalSubdirective {
    public OptionalRepeatingSubdirective(int id, String name,
                                         ArgDescriptor[] args) {
      super(id, name, args);
      setRepeating();
    }
  }

}
