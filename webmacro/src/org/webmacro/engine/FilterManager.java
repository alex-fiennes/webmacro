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


package org.webmacro.engine;

import org.webmacro.Filter;
import org.webmacro.Macro;

class FilterNode {

   String vname[];
   Filter tool;

   FilterNode(String vname[], Filter tool) {
      this.vname = vname;
      this.tool = tool;
      this.next = null;
   }

   FilterNode next;
}

/**
 * This class creates a list of Filters each with a corresponding
 * name. You can add to the end of the list, and you can extract a
 * filter for a name. When you get the filter for a name you may
 * actaully get a chain of filters: your macro will be filtered by
 * the first filter, which in turn will be filtered by the second,
 * and so on--for each filter that matches the supplied name.
 */
class FilterManager {

   FilterNode _head = new FilterNode(null, null);
   FilterNode _tail = _head;

   /**
    * The registered Filter will be used to generate filter
    * chains for the supplied variable from here on, and
    * potentially for any of its sub-properties as well.
    */
   synchronized void addFilter(Variable v, Filter ft) {
      FilterNode fn = new FilterNode(v.getPropertyNames(), ft);
      _tail.next = fn;
      _tail = fn;
   }

   /**
    * Deregister all the filters for the supplied variable, or for
    * any sub-property of the supplied variable.
    */
   synchronized void clearFilters(Variable var) {
      FilterNode fn = _head;
      String vname[] = var.getPropertyNames();
      while (fn.next != null) {
         if (match(vname, fn.vname))  // note the order of arguments
         {
            fn.next = fn.next.next;
            if (fn.next == null) {
               _tail = fn;
            }
         }
         else {
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
   public synchronized Macro getMacro(Variable v) {
      String vname[] = v.getPropertyNames();
      Macro last = v;
      FilterNode fn = _head;
      TOOLS:
      while (fn.next != null) {
         fn = fn.next;
         if (match(fn.vname, vname))  // note the order of arguments
         {
            Filter ft = fn.tool;
            int start = fn.vname.length + 1;
            for (int j = start; j < vname.length; j++) {
               if (ft != null) {
                  ft = ft.getFilter(vname[j]);
               }
               else
                  continue TOOLS;
            }
            Macro cur = ft.getMacro(last);
            if (cur == null) {
               return v;
            }
            last = cur;
         }
      }
      return last;
   }

   /**
    * Determine whether a filter name matches a property name. The
    * names match if the second name is the same or the same but
    * longer than the first. For example local:a.b matches
    * local:a.b.c
    */
   boolean match(String[] lhs, String[] rhs) {
      if (lhs.length > rhs.length) return false;
      for (int i = 0; i < lhs.length; i++) {
         if (!lhs[i].equals(rhs[i])) return false;
      }
      return true;
   }
}
