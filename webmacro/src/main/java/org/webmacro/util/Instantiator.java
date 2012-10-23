/*
 * Copyright (C) 1998-2005 Semiotek Inc. All Rights Reserved. Redistribution and use in source and
 * binary forms, with or without modification, are permitted under the terms of either of the
 * following Open Source licenses: The GNU General Public License, version 2, or any later version,
 * as published by the Free Software Foundation (http://www.fsf.org/copyleft/gpl.html); or The
 * Semiotek Public License (http://webmacro.org/LICENSE.) This software is provided "as is", with NO
 * WARRANTY, not even the implied warranties of fitness to purpose, or merchantability. You assume
 * all risks and liabilities associated with its use. See www.webmacro.org for more information on
 * the WebMacro project.
 */

package org.webmacro.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webmacro.Broker;
import org.webmacro.WebMacroException;

/**
 * Utility class for loading and instantiating classes subject to package restrictions specified by
 * the AllowedPackages property, and using implicit package names specified by the ImpliedPackages
 * configuration property. Used by BeanDirective and SetpropsDirective. NOTE If AllowedPackages is
 * empty then all packages are allowed.
 * 
 * @author Keats Kirsch
 * @see org.webmacro.directive.BeanDirective
 * @see org.webmacro.directive.SetpropsDirective
 */
final public class Instantiator
{
  static Logger _log = LoggerFactory.getLogger(Instantiator.class);

  private static final String IMPLIED_PACKAGES = "ImpliedPackages";

  private static final String ALLOWED_PACKAGES = "AllowedPackages";

  private static final String DEPRECATED_IMPLIED_PACKAGES = "BeanDirective.ImpliedPackages";

  private static final String DEPRECATED_ALLOWED_PACKAGES = "BeanDirective.AllowedPackages";

  private static final String INSTANTIATOR_KEY = "org.webmacro.util.Instantiator";

  private List<String> _impliedPackages;

  private List<String> _allowedPackages;

  private Broker _broker;

  private Instantiator(Broker b)
  {
    _broker = b;
    String s = b.getSetting(IMPLIED_PACKAGES);
    if (s == null) {
      s = b.getSetting(DEPRECATED_IMPLIED_PACKAGES);
      if (s != null) {
        _log.warn("The configuration parameter \"" + DEPRECATED_IMPLIED_PACKAGES
                  + "\" has been deprecated! Use \"" + IMPLIED_PACKAGES + "\" instead.");
      }
    }
    if (s == null) {
      _impliedPackages = Collections.emptyList();
    } else {
      _impliedPackages = Arrays.asList(org.webmacro.servlet.TextTool.split(s, ","));
    }

    // get allowed packages setting
    s = b.getSetting(ALLOWED_PACKAGES);
    if (s == null) {
      s = b.getSetting(DEPRECATED_ALLOWED_PACKAGES);
      if (s != null) {
        _log.warn("The configuration parameter \"" + DEPRECATED_ALLOWED_PACKAGES
                  + "\" has been deprecated! Use \"" + ALLOWED_PACKAGES + "\" instead.");
      }
    }
    if (s == null) {
      _allowedPackages = Collections.emptyList();
    } else {
      _allowedPackages = Arrays.asList(org.webmacro.servlet.TextTool.split(s, ","));
    }
  }

  public List<String> getImpliedPackages()
  {
    return _impliedPackages;
  }

  public List<String> getAllowedPackages()
  {
    return _allowedPackages;
  }

  public Class<?> classForName(String className)
      throws WebMacroException
  {
    Class<?> c = null;
    if (className.indexOf('.') >= 0) // it is a fully specified class name
    {
      try {
        c = _broker.classForName(className);
      } catch (ClassNotFoundException e) {
        throw new WebMacroException("Unable to load class " + className, e);
      }
    } else { // try with implied packages prepended
      ClassNotFoundException exception = null;

      for (int i = 0; i < _impliedPackages.size(); i++) {
        String s = _impliedPackages.get(i);
        try {
          c = _broker.classForName(s + "." + className);
          break;
        } catch (ClassNotFoundException e) {
          exception = e;
        }
      }
      if (c == null) {
        if (exception == null)
          throw new WebMacroException("Unable to load class " + className + ", property "
                                      + IMPLIED_PACKAGES + " contains " + _impliedPackages.size()
                                      + " items");
        else
          throw new WebMacroException("Unable to load class " + className, exception);
      }
    }

    if (!_allowedPackages.isEmpty()) {
      // check if class is in a permitted package
      String pkg = c.getPackage().getName();
      if (!_allowedPackages.contains(pkg)) {
        throw new WebMacroException("You are not permitted to load classes from this package ("
                                    + pkg + ").  Check the \"" + ALLOWED_PACKAGES
                                    + "\" parameter in the WebMacro configuration.");
      }
    }
    // else allowed packages not specified so all allowed

    return c;
  }

  public Object instantiate(Class<?> c,
                            Object[] args)
      throws Exception
  {
    Object o = null;
    if (args == null) {
      o = c.newInstance();
    } else {
      java.lang.reflect.Constructor<?>[] cons = c.getConstructors();
      for (int i = 0; i < cons.length; i++) {
        if (cons[i].getParameterTypes().length == args.length) {
          // try to instantiate using this constructor
          try {
            o = cons[i].newInstance(args);
            break; // if successful, we're done!
          } catch (Exception e) {
            // ignore
          }
        }
      }
      if (o == null) {
        throw new InstantiationException("Unable to construct object of type " + c.getName()
                                         + " using the supplied arguments: "
                                         + java.util.Arrays.asList(args).toString());
      }
    }
    return o;
  }

  synchronized public static Instantiator getInstance(Broker b)
  {
    Instantiator instantiator = (Instantiator) b.getBrokerLocal(INSTANTIATOR_KEY);
    if (instantiator == null) {
      instantiator = new Instantiator(b);
      b.setBrokerLocal(INSTANTIATOR_KEY, instantiator);
    }
    return instantiator;
  }
}
