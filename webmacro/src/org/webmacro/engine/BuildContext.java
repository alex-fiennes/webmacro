
package org.webmacro.engine;
import java.util.*;
import org.webmacro.*;
import org.webmacro.util.*;
import org.webmacro.util.java2.*;

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
public final class BuildContext extends Context
{

   private final Hashtable _filters = new Hashtable();

   private final Hashtable _types = new Hashtable();

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
     * Add a filter for this name
     */
   void addFilter(String name, Filter f) 
   {
      _filters.put(name,f);
   }

   /**
     * Get a filter for this name array
     */
   Filter getFilter(String[] name) 
   {
      if (name.length == 0) {
         return null;
      }

      Filter f = (Filter) _filters.get(name[0]);

      for (int i = 0; i < name.length; i++) {
         f = f.getFilter(name[i]);
         if (f == null) {
            return null;
         }
      }
      return f;
   }


   public Hashtable getFilters() {
      return _filters;
   }

   protected Object getVariableType(String name) {
      return _types.get(name);
   }

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

