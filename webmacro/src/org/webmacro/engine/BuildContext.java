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

   private final Map _types = new HashMap();

   private final FilterManager _filters = new FilterManager();

   public BuildContext(Broker b) {
      super(b);
   }

   public final Parser getParser(String pname) 
      throws NotFoundException
   {
      try {
         return (Parser) getBroker().get("parser", pname);
      }
      catch (NotFoundException e) { throw e; }
      catch (ResourceException e) { 
        throw new NotFoundException(e.toString(), e); 
      }
   }

   /**
     * Find out whether the named variable is a tool, local variable,
     * or property variable.
     */
   public Object getVariableType(String name) {
      Object ret =  _types.get(name);
      return (ret == null) ? Variable.PROPERTY_TYPE : ret;
   }

   /**
     * Declare whether the named variable is to be treated as a tool,
     * local variable, or property variable type.
     */
   public void setVariableType(String name, Object type)  {
      if (name == null) {
         return;
      } 
      if (type == null) {
         _types.remove(name);
      } else {
         _types.put(name,type);
      }
   }

   /**
     * Register a new filter, adding it to the chain for the supplied name.
     * The name is either a top level property name or * to mean "all".
     * @param name the top level property name that is being filtered
     * @param ft the Filter which will handle this property
     */
   public void addFilter(Variable var, Filter ft) {
      _filters.addFilter(var,ft);   
   }

   /**
     * Clear all the filtered for the supplied name. Cleaing * clears
     * only global filters, leaving filters for specific properties.
     */
   public void clearFilters(Variable var) {
      _filters.clearFilters(var);
   }

   /**
     * Get the filter that applies to a specific variable. Returning
     * null from this method means that the entire variable should
     * be dropped from the output since it's been filtered to null.
     * @return the Macro to be used to filter it, or null
     */
   public Macro getFilterMacro(Variable v) {
      return _filters.getMacro(v);
   }
}

