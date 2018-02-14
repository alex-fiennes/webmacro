/*
 * Copyright (C) 1998-2000 Semiotek Inc. All Rights Reserved. Redistribution and use in source and
 * binary forms, with or without modification, are permitted under the terms of either of the
 * following Open Source licenses: The GNU General Public License, version 2, or any later version,
 * as published by the Free Software Foundation (http://www.fsf.org/copyleft/gpl.html); or The
 * Semiotek Public License (http://webmacro.org/LICENSE.) This software is provided "as is", with NO
 * WARRANTY, not even the implied warranties of fitness to purpose, or merchantability. You assume
 * all risks and liabilities associated with its use. See www.webmacro.org for more information on
 * the WebMacro project.
 */

package org.webmacro.engine;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webmacro.Broker;
import org.webmacro.InitException;
import org.webmacro.NotFoundException;
import org.webmacro.Provider;
import org.webmacro.util.Instantiator;
import org.webmacro.util.Settings;

/**
 * Utility class to assist in the creation of directives.
 */
public final class ParserProvider
  implements Provider
{
  static Logger _log = LoggerFactory.getLogger(Instantiator.class);

  // BULDER CLASS MANAGEMENT

  private final Map<String, Parser> _parsers = new ConcurrentHashMap<String, Parser>();

  private Broker _broker = null;
  private final Class<?>[] _brokerParam = { Broker.class };
  private final Object[] _brokerArg = new Object[1];

  /**
   * Register a new parser class.
   * 
   * @exception IntrospectionException
   *              something wrong with the class
   * @exception InitException
   *              duplicate registration
   */
  public final void register(String pClassName,
                             String pType)
      throws IntrospectionException, InitException
  {
    Class<?> pclass;
    String pname = extractName(pClassName);
    String name = (pType != null && !pType.equals("")) ? pType : pname;
    try {
      pclass = _broker.classForName(pClassName);
    } catch (Exception e) {
      throw new IntrospectionException("No class " + pClassName);
    }
    try {
      _log.info("Registering parser: " + name + " (" + pClassName + ")");
      Parser p = _parsers.get(name);
      if (p == null) {
        Constructor<?> ctor = pclass.getConstructor(_brokerParam);
        p = (Parser) ctor.newInstance(_brokerArg);
        _parsers.put(name, p);
      } else if (!pclass.equals(p.getClass())) {
        throw new InitException("Attempt to register parser " + pClassName + " failed because "
                                + p.getClass() + " is already registered for type " + name);
      }
    } catch (InstantiationException ne) {
      throw new IntrospectionException("Parsers could not be instantiated", ne);
    } catch (IllegalAccessException ia) {
      throw new IntrospectionException("Parser class must be public", ia);
    } catch (InvocationTargetException it) {
      throw new InitException("Parser threw an exception", it);
    } catch (NoSuchMethodException nm) {
      throw new IntrospectionException("Parser missing the required constructor", nm);
    }
  }

  public final Parser getParser(String pname)
      throws NotFoundException
  {
    Parser p = _parsers.get(pname);
    if (p == null) {
      throw new NotFoundException("No parser registered for type " + pname);
    }
    return p;
  }

  private static String extractName(String par)
      throws IntrospectionException
  {
    if (!par.endsWith("Parser")) {
      throw new IntrospectionException("Malformed classname (" + par + "), must end with Parser");
    }
    int end = par.length() - 6;
    int start = par.lastIndexOf('.', end) + 1;
    String parName = par.substring(start, end);
    if (par.startsWith("org.webmacro.")) {
      parName = parName.toLowerCase();
    }
    return parName;
  }

  @Override
  public String getType()
  {
    return "parser";
  }

  private class SettingHandler
    extends Settings.ListSettingHandler
  {

    @Override
    public void processSetting(String settingKey,
                               String settingValue)
    {
      try {
        register(settingValue, settingKey);
      } catch (Exception ce) {
        _log.error("Could not load parser: " + settingValue, ce);
      }
    }
  }

  @Override
  public void init(Broker broker,
                   Settings p)
      throws InitException
  {
    _brokerArg[0] = broker;
    _broker = broker;

    try {
      p.processListSetting("Parsers", new SettingHandler());
    } catch (Exception e) {
      throw new InitException("Could not init ParserProvider", e);
    }
  }

  @Override
  public void destroy()
  {
    _parsers.clear();
  }

  @Override
  public Object get(String name)
      throws NotFoundException
  {
    try {
      return getParser(name);
    } catch (Exception e) {
      throw new NotFoundException("No such parser: " + name, e);
    }
  }

  @Override
  public void flush()
  {
  }
}
