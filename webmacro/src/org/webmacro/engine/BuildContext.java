
package org.webmacro.engine;
import java.util.*;
import org.webmacro.*;
import org.webmacro.util.*;

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

   private final FilterTable _filters = new FilterTable();

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

   public Filter getFilter(Object names[]) {
      return _filters.getFilter(names);
   }

   public Filter getFilter(String name) {
      return _filters.getFilter(name);
   }


   /**
     * Add a filter for this variable. The variable must be a simple top 
     * level variable (no sub-properties specified).
     */
   public void addFilter(Variable v, Filter f) 
      throws BuildException
   {
      Object[] names = v.getNameArray();
      if (names.length != 1) {
         throw new BuildException("Only top level, single-property variable names can be specified as filter targets. Maybe in a later version you will be able to directly filter a sub-property of a variable, but not in this version.");
      }
      String name = (names[0] instanceof Named) ?
         ((Named) names[0]).getName() : (String) names[0];
      addFilter(name, f);
   }

   /**
     * Add a filter for the supplied property. NOTE: do not use the WM template
     * notation for the variable. It must be a simple top-level variable name,
     * and must not include the $ or $$ notation.
     */
   public void addFilter(String name, Filter f)
      throws BuildException
   {
      _filters.addFilter(name,f);
   }

   public FilterTable getFilters() {
      return _filters;
   }

}


