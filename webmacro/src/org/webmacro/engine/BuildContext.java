
package org.webmacro.engine;
import java.util.*;
import org.webmacro.*;

/**
  * Contains data structures which are manipulated during the 
  * builder phase of parsing. It extends Hashtable so that 
  * user provided directives can store information in it during
  * the builder phase. Although WebMacro's built in directives
  * make no use of this hashtable (they use the other structures
  * added in the derived class), other user provided directives 
  * might. Therefore you should adopt a sensible naming scheme for 
  * your keys, to avoid conflicting with keys inserted by someone else.
  */
public final class BuildContext extends Hashtable 
{

   private final Hashtable _params = new Hashtable();

   private final Broker _broker;

   public BuildContext(Broker b) {
      _broker = b;
   }

   public final Hashtable getParameters() {
      return _params;
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

   public Broker getBroker() {
      return _broker;
   }

}


