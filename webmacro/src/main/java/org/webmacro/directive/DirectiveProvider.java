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

package org.webmacro.directive;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webmacro.Broker;
import org.webmacro.InitException;
import org.webmacro.Provider;
import org.webmacro.engine.IntrospectionException;
import org.webmacro.util.Settings;

/**
 * Utility class to assist in the creation of directives.
 */
public final class DirectiveProvider
  implements Provider
{

  static Logger _log = LoggerFactory.getLogger(DirectiveProvider.class);

  public static final String DIRECTIVE_KEY = "directive";

  // BULDER CLASS MANAGEMENT

  private final Map<String, DirectiveDescriptor> _descriptors =
      new ConcurrentHashMap<String, DirectiveDescriptor>();
  private Broker _broker;

  /**
   * a simple class to take care of registering Directives specified in the Settings of the Broker.
   */
  private class SettingHandler
    extends Settings.ListSettingHandler
  {

    @Override
    public void processSetting(String settingKey,
                               String settingValue)
    {
      try {
        registerDirective(settingValue, settingKey);
      } catch (Exception ce) {
        _log.warn("Exception loading directive " + settingValue, ce);
      }
    }
  }

  /**
   * Register an org.webmacro.directive.DirectiveDescriptor to be used as if it were a real
   * Directive named <code>dirName</code>.
   * <p>
   * If the specified <code>dirName</code> is already registered, it is happily, and silently
   * replaced.
   * <p>
   * Once registered, one can use this "directive" from a template like so:
   * 
   * <pre>
   *    #dirName arg1 arg2 argN
   * 
   * where the args are dependant on the DirectiveDescriptor
   * </pre>
   * 
   * @param dd
   *          the DirectiveDescriptor
   * @param dirName
   *          name of the "directive"
   */
  public final void registerDescriptor(DirectiveDescriptor dd,
                                       String dirName)
  {
    _descriptors.put(dirName, dd);
  }

  /**
   * Register a new directive class, so that a builder of this type can be retrieved later.
   * 
   * @exception IntrospectionException
   *              something wrong with the class
   * @exception InitException
   *              duplicate registration
   */
  public final void registerDirective(String dirClassName,
                                      String dirName)
      throws IntrospectionException, InitException
  {
    Class<?> directive = null;
    DirectiveDescriptor templateDesc, newDesc, oldDesc;
    try {
      directive = _broker.classForName(dirClassName);
    } catch (Exception e) {
      throw new IntrospectionException("No class " + dirClassName, e);
    }

    // Make sure this class is an instance of o.w.directive.Directive
    if (Directive.class.isAssignableFrom(directive)) {
      try {
        templateDesc =
            (DirectiveDescriptor) directive.getMethod("getDescriptor", (Class<?>[]) null)
                                           .invoke(null, (Object[]) null);
        newDesc =
            new DirectiveDescriptor(templateDesc.name,
                                    templateDesc.dirClass,
                                    templateDesc.args,
                                    templateDesc.subdirectives);
        if (newDesc.dirClass == null)
          newDesc.dirClass = directive;
      } catch (Exception e) {
        throw new IntrospectionException("Class " + dirClassName
                                         + " does not have a getDescriptor() method", e);
      }

      // added by Keats 5Jul01
      // use introspection to invoke the static init method of directive, if it exists
      Class<?>[] cArg = { Broker.class };
      try {
        java.lang.reflect.Method m = directive.getMethod("init", cArg);
        Object[] brokerArg = { _broker };
        try {
          m.invoke(null, brokerArg);
        } catch (Exception e) {
          _log.warn("Unable to invoke the init method for the directive " + directive.getName(), e);
        }
      } catch (Exception e) {
      }

      newDesc.name = (dirName != null && !dirName.equals("")) ? dirName : templateDesc.name;
      oldDesc = _descriptors.get(newDesc.name);
      if (oldDesc == null) {
        _descriptors.put(newDesc.name, newDesc);
        _log.info("Registered directive: " + newDesc.name);
      } else if (newDesc.dirClass != oldDesc.dirClass) {
        throw new InitException("Attempt to register directive " + directive + " failed because "
                                + oldDesc.dirClass.getName() + " is already registered for type "
                                + newDesc.name);
      }
    }
  }

  /**
   * Create a builder for the named directive
   */
  private final DirectiveDescriptor getDescriptor(String directiveName)
  {
    return _descriptors.get(directiveName);
  }

  // RESOURCE PROVIDER API

  @Override
  public String getType()
  {
    return DIRECTIVE_KEY;
  }

  @Override
  public void init(Broker broker,
                   Settings config)
      throws InitException
  {
    _broker = broker;
    try {
      config.processListSetting("Directives", new SettingHandler());
    } catch (Exception e) {
      _log.warn("Error initializing DirectiveProvider", e);
      throw new InitException("Could not initialize DirectiveProvider", e);
    }
  }

  @Override
  public void destroy()
  {
    _descriptors.clear();
  }

  /**
   * The DirectiveProvider doesn't throw an exception when it can't find the directive -- it just
   * returns null.
   */
  @Override
  public Object get(String name)
  {
    return getDescriptor(name);
  }

  @Override
  public void flush()
  {
  }
}
