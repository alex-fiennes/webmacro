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
import java.lang.reflect.*;
import org.webmacro.*;
import org.webmacro.resource.*;
import org.webmacro.util.*;

/**
  * Utility class to assist in the creation of directives.
  */
public final class ParserProvider implements Provider
{

   // BULDER CLASS MANAGEMENT

   private static final Hashtable _parsers  = new Hashtable();

   private Broker _broker = null;
   private Log _log;
   private final Class[] _brokerParam = { Broker.class };
   private final Object[] _brokerArg = new Object[1];

   /**
     * Register a new parser class,
     * @exception IntrospectionException something wrong with the class
     * @exception InitException duplicate registration
     */
   public final void register(String pClassName, String pType) 
      throws IntrospectionException, InitException
   {
      Class pclass;
      String pname = extractName(pClassName);
      String name = (pType != null && !pType.equals("")) 
                  ? pType : pname;
      try {
         pclass = _broker.classForName(pClassName);
      } catch (Exception e) {
         throw new IntrospectionException("No class " + pClassName);
      }
      try {
         _log.info("Registering parser: " + name + " (" + pClassName + ")"); 
         Parser p = (Parser) _parsers.get(name);
         if (p == null) {
            Constructor ctor = pclass.getConstructor(_brokerParam);
            p = (Parser) ctor.newInstance(_brokerArg);
            _parsers.put(name, p);
         } else if (! pclass.equals(p.getClass())) {
            throw new InitException(
                  "Attempt to register parser " + pClassName
                  + " failed because " + p.getClass() 
                  + " is already registered for type " + name);
         }
      } catch (InstantiationException ne) {
        throw new IntrospectionException("Parsers could not be instantiated", 
                                         ne);
      } catch (IllegalAccessException ia) {
        throw new IntrospectionException("Parser class must be public", ia);
      } catch (InvocationTargetException it) {
        throw new InitException("Parser threw an exception", it);
      } catch (NoSuchMethodException nm) {
         throw new IntrospectionException(
               "Parser missing the required constructor", nm);
      }
   }

   public final Parser getParser(String pname) 
      throws NotFoundException
   {
      Parser p = (Parser) _parsers.get(pname);
      if (p == null) {
         throw new NotFoundException("No parser registered for type " 
               + pname);
      }
      return p;
   }

   private static String extractName(String par) 
      throws IntrospectionException
   {
      if (! par.endsWith("Parser") ) {
         throw new IntrospectionException(
            "Malformed classname (" + par + "), must end with Parser");
      }
      int end = par.length() - 6;
      int start = par.lastIndexOf('.',end) + 1;
      String parName = par.substring(start,end);
      if (par.startsWith("org.webmacro.")) {
         parName = parName.toLowerCase();
      }
      return parName;
   }


   public String getType() {
      return "parser";
   }

   private class SettingHandler extends Settings.ListSettingHandler {
      public void processSetting(String settingKey, String settingValue) {
         try {
               register(settingValue, settingKey);
         } catch (Exception ce) {
           _log.error("Could not load parser: " + settingValue, ce);
         }
      }
   }

   public void init(Broker broker, Settings p) throws InitException
   {
      _brokerArg[0] = broker;
      _broker = broker;
      _log = broker.getLog("engine");

      try {
         p.processListSetting("Parsers", new SettingHandler());
      } catch (Exception e) {
         throw new InitException("Could not init ParserProvider",e);
      }
   }

   public void destroy() 
   {
      _parsers.clear();
   }

   public Object get(String name) throws NotFoundException
   {
      try {
         return getParser(name);
      } catch (Exception e) {
        throw new NotFoundException("No such parser: " + name, e);
      }
   }

   public void flush() { }
}


