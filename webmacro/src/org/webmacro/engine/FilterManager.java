
package org.webmacro.engine;

import org.webmacro.*;
import java.util.*;

class FilterNode {
   String name;
   FilterTool tool;
   FilterNode(String name, FilterTool tool) {
      this.name = name;
      this.tool = tool;
      this.next = null;
   }
   FilterNode next;
}

/**
  * This class creates a list of FilterTools each with a corresponding
  * name. You can add to the end of the list, and you can extract a
  * filter for a name. When you get the filter for a name you may
  * actaully get a chain of filters: your macro will be filtered by
  * the first filter, which in turn will be filtered by the second, 
  * and so on--for each filter that matches the supplied name.
  */
public class FilterManager {

   FilterNode _head = new FilterNode(null,null);
   FilterNode _tail = _head;

   /**
     * The registered FilterTool will be used to generate filter
     * chains for the supplied name from here on.
     */
   public synchronized void addFilter(String name, FilterTool ft) {
      FilterNode fn = new FilterNode(name,ft);
      _tail.next = fn;
      _tail = fn;
   }

   /**
     * Deregister all the filters for the supplied name. Note that 
     * clearFilters("ALL") only clears the filters assigned to ALL,
     * leaving any filters that are assigned to a specific name.
     */
   public synchronized void clearFilters(String name) {
      FilterNode fn = _head;
      while (fn.next != null) {
         if (name.equals(fn.name))  // note the order of arguments
         {
            fn.next = fn.next.next;
            if (fn.next == null) {
               _tail = fn;
            }
         } else {
            fn = fn.next;
         }
      }
   }

   /**
     * Get a filter chain for the supplied Variale. The returned
     * Macro may be the Variable, or some chain of filters wrapping it:
     * getFilter(v).evaluate(c) is v filtered through all of the filters 
     * registered for name.
     */
   public synchronized Macro getFilter(Variable v) {
      String name[] = v.getPropertyNames();
      Macro last = v;
      FilterNode fn = _head;
      TOOLS: 
      while (fn.next != null) {
         fn = fn.next;
         if (match(fn.name,name[0]))  // note the order of arguments
         {
            FilterTool ft = fn.tool;
            for (int j = 1; j < name.length; j++) {
               if (ft != null) {
                  ft = ft.getFilterTool(name[j]); 
               } else continue TOOLS;
            }
            Macro cur = ft.getFilter(last);
            if (cur == null) { return v; }
            last = cur;
         }
      }
      return last;
   }

   /**
     * Determine whether a filter name matches a property name. If
     * fname is the string "ALL" return true, else return true iff
     * the two strings are equal.
     * @param fname this is the match expression
     * @param pname this is the name of a property
     */
   public boolean match(String fname, String pname) {
      return (fname.equals("ALL") || fname.equals(pname));
   }
}
