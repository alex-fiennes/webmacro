
package org.webmacro.util;
import java.util.*;
import org.webmacro.*;

/**
  * This is a utility class to store filters by name
  */
public class FilterTable {

   private Hashtable _filters = new Hashtable();

   private String[] resolve(Object[] names) {
      String c[] = new String[ names.length ];
      for (int i = 0; i < names.length; i++) {
         c[i] = (names[i] instanceof Named) ?
           ((Named) names[i]).getName() : (String) names[i];
      }
      return c;

   }

   /**
     * Return the filter corresponding to the supplied variable name array.
     * If there is no corresponding filter, a null will be returned. The 
     * names array contains either strings or instances of things that 
     * are of type Named. In the case of Named objects, Named.getName() 
     * will be used as the name of the subfilter to look up. The most 
     * common instance of Named objects is PropertyMethod, whose name 
     * is the name of the method. eg: Foo.someMethod(args). A limitation
     * of this system is that you cannot have two different filters for 
     * properties with the same name but different argument types.
     */
   public Filter getFilter(Object[] names) {
      if (names.length == 0) { 
         return null; 
      }
      String cnames[] = resolve(names);
      Filter f = getFilter(cnames[0]);
      for (int i = 1; i < cnames.length; i++) {
        if (f == null) {
           return null;
        }
        f = f.getFilter(cnames[i]);
      }
      return f;
   }

   public Filter getFilter(String name) {
      return (Filter) _filters.get(name);
   }

   /**
     * Add a Filter for the supplied name. Must be a single property name 
     * without any '.' in it
     */
   public void addFilter(String propertyName, Filter f) {
      _filters.put(propertyName, f);
   }

}

