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

package org.webmacro.directive;

import java.util.*;
import org.webmacro.engine.*;
import org.webmacro.*;
import org.webmacro.util.*;
import org.webmacro.resource.*;

/**
  * Utility class to assist in the creation of directives.
  */
public final class DirectiveProvider implements Provider
{

   public static final String DIRECTIVE_KEY = "directive";

   // BULDER CLASS MANAGEMENT

   private final Hashtable _descriptors  = new Hashtable();
   private Log _log;

   /**
     * Register a new directive class, so that a builder
     * of this type can be retrieved later.
     * @exception IntrospectionException something wrong with the class
     * @exception InitException duplicate registration
     */
   private final void register(String dirClassName, String dirName) 
      throws IntrospectionException, InitException
   {
      Class directive = null;
      DirectiveDescriptor descriptor, oldDesc;
      try {
        directive = Class.forName(dirClassName);
      } catch (Exception e) {
         throw new IntrospectionException("No class " + dirClassName, e);
      }

      // Make sure this class is an instance of o.w.directive.Directive
      if (Directive.class.isAssignableFrom(directive)) {
        try {
          descriptor = (DirectiveDescriptor) 
            directive.getMethod("getDescriptor", null).invoke(null, null);
          if (descriptor.dirClass == null)
            descriptor.dirClass = directive;
        } 
        catch (Exception e) {
          throw new IntrospectionException("Class " + dirClassName 
            + " does not have a getDescriptor() method", e);
        }
        String name = (dirName != null && !dirName.equals(""))
                    ? dirName : descriptor.name;
        oldDesc = (DirectiveDescriptor) _descriptors.get(name);
        if (oldDesc == null) {
          _descriptors.put(name, descriptor);
          _log.info("Registered directive: " + name);
        } else if (descriptor.dirClass != oldDesc.dirClass) {
          throw new InitException("Attempt to register directive " + directive
             + " failed because " + oldDesc.dirClass.getName() 
             + " is already registered for type " + name);
        }
      }
   }

   /**
     * Create a builder for the named directive
     */
   public final DirectiveDescriptor getDescriptor(String directiveName)
      throws ClassNotFoundException
   {
      DirectiveDescriptor descriptor = 
         (DirectiveDescriptor) _descriptors.get(directiveName);
      if (descriptor == null) {
        throw new ClassNotFoundException(
               "No directive matched the name " + directiveName);
      }
      return descriptor;
   }



   // RESOURCE PROVIDER API

   public String getType() {
      return DIRECTIVE_KEY;
   }

   private class SettingHandler extends Settings.ListSettingHandler {
      public void processSetting(String settingKey, String settingValue) {
         try {
            register(settingValue, settingKey);
         } catch (Exception ce) {
            _log.warning("Exception loading directive " + settingValue, ce);
         }
      }
   }

   public void init(Broker broker, Settings config) throws InitException
   {
      _log = broker.getLog("directive");
      try {
         config.processListSetting("Directives", new SettingHandler());
      } catch (Exception e) {
         _log.warning("Error initializing DirectiveProvider", e);
        throw new InitException("Could not initialize DirectiveProvider", e);
      }
   }

   public void destroy() 
   {
      _descriptors.clear();
   }

   public Object get(String name) throws NotFoundException
   {
      try {
         return getDescriptor(name);
      } catch (Exception e) {
        throw new NotFoundException("No such directive " + name, e);
      }
   }

   public void flush() { }
}


