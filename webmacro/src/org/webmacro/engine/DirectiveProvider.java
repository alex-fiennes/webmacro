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

import org.webmacro.engine.*;
import java.util.*;
import java.lang.reflect.*;
import org.webmacro.*;
import org.webmacro.util.*;
import org.webmacro.resource.*;

/**
  * Utility class to assist in the creation of directives.
  */
public final class DirectiveProvider implements Provider
{

   private Log _log;

   // BULDER CLASS MANAGEMENT

   private final Hashtable _prototypes  = new Hashtable();

   /**
     * Register a new directive class, so that a builder
     * of this type can be retrieved later.  If the specified class is not
     * an o.w.engine.Directive, it simply skips the directive, so that
     * both old-style and new-style directives can be specified together. 
     * @exception IntrospectionException something wrong with the class
     * @exception InitException duplicate registration
     */
   public final void register(String dirClassName) 
      throws IntrospectionException, InitException
   {

      String dirName = extractName(dirClassName);
      Class directive = null;
      try {
         directive = Class.forName(dirClassName);
      } catch (Exception e) {
         throw new IntrospectionException("No class " + dirClassName);
      }

      // Make sure this class is an instance of o.w.engine.Directive
      if (Directive.class.isAssignableFrom(directive)) {
        DirectiveBuilder b = (DirectiveBuilder) _prototypes.get(dirName);
        if (b == null) {
          b = new DirectiveBuilder(dirName, directive);
          _prototypes.put(dirName, b);
          _log.info("Registered directive: " + dirName);
        } else if (! directive.equals(b.getDirectiveClass())) {
          throw new InitException(
                 "Attempt to register directive " + directive
                 + " failed because " + b.getDirectiveClass() 
                 + " is already registered for type " + dirName);
        }
      }
   }

   private static String extractName(String dir) 
      throws IntrospectionException
   {
      if (! dir.endsWith("Directive") ) {
         throw new IntrospectionException(
            "Malformed classname, must end with Directive");
      }
      int end = dir.length() - 9;
      int start = dir.lastIndexOf('.',end) + 1;
      String dirName = dir.substring(start,end);
      if (dir.startsWith("org.webmacro.")) {
         dirName = dirName.toLowerCase();
      }
      return dirName;
   }


   /**
     * Create a builder for the named directive
     */
   public final DirectiveBuilder getBuilder(String directiveName)
      throws ClassNotFoundException
   {
      DirectiveBuilder proto = 
         (DirectiveBuilder) _prototypes.get(directiveName);
      if (null == proto) {
        throw new ClassNotFoundException(
               "No directive matched the name " + directiveName);
      }
      try {
         return (DirectiveBuilder) proto.clone();
      } catch(Exception ignore) {
         return null;
      }
   }



   // RESOURCE PROVIDER API

   public String getType() {
      return "directive";
   }

   public void init(Broker broker, Settings config) throws InitException
   {
      try {
         _log = broker.getLog("engine", "parsing and template execution"); 
         String directives = config.getSetting("Directives");
         Enumeration denum = new StringTokenizer(directives);
         while (denum.hasMoreElements()) {
            String dir = (String) denum.nextElement();
            try {
               register(dir);
            } catch (Exception ce) {
               _log.warning("Could not load directive: " + dir,ce);    
            }
         }
      } catch (Exception e) {
         _log.error("Could not init DirectiveProvider", e);
      }
   }

   public void destroy() 
   {
      _prototypes.clear();
   }

   public Object get(String name) 
      throws NotFoundException
   {
      try {
         return getBuilder(name);
      } catch (Exception e) {
        throw new NotFoundException("No such directive: " + name, e);
      }
   }

   public void flush() { }
}


