
package org.webmacro.engine;
import java.util.*;
import org.webmacro.*;
import org.webmacro.util.*;


/**
  * Contains data structures which are manipulated during the 
  * builder phase of parsing. It extends Map so that 
  * user provided directives can store information in it during
  * the builder phase. Although WebMacro's built in directives
  * make no use of this hashtable (they use the other structures
  * added in the derived class), other user provided directives 
  * might. Therefore you should adopt a sensible naming scheme for 
  * your keys, to avoid conflicting with keys inserted by someone else.
  */
public final class BuildContext extends Context
{

   private final HashMap _filters = new HashMap();

   private final Map _types = new HashMap();

   public BuildContext(Broker b) {
      super(b);
   }

   public final Parser getParser(String pname) 
      throws NotFoundException
   {
      try {
         return (Parser) getBroker().getValue("parser", pname);
      } catch (InvalidTypeException te) {
         Engine.log.exception(te);
         Engine.log.error("Broker unable to load parsers");
         throw new NotFoundException("ERROR: Broker cannot load any parsers");
      }
   }

   /**
     * Find out whether the named variable is a tool, local variable,
     * or property variable.
     */
   protected Object getVariableType(String name) {
      return _types.get(name);
   }

   /**
     * Declare whether the named variable is to be treated as a tool,
     * local variable, or property variable type.
     */
   protected void setVariableType(String name, Object type)  {
      if (name == null) {
         return;
      } 
      if (type == null) {
         _types.remove(name);
      } else {
         _types.put(name,type);
      }
   }

}

