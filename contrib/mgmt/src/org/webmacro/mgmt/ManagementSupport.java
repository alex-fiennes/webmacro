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

package org.webmacro.mgmt;
import org.opendoors.cache.UpdateableCache;
import org.opendoors.cache.immutable.CacheFactory;
import org.webmacro.WebMacro;
import org.webmacro.Context;
import org.webmacro.Template;

/**
 * ManagementSupport is a singleton which,
 * when loaded, reads in all classes in the path
 * org/webmacro/mgmt/impl. Every class in this
 * path is registered with ManagementSupport.
 * <p>
 * Use ManagementSupport.getInstance() in your
 * servlets to expose the management functions
 * which have been provided by various WM authors.
 * <p>
 * A sample template is provided to show the
 * services, reporting and action-based.
 */
public class ManagementSupport {


  /** The singleton instance. */
  protected static ManagementSupport instance = new ManagementSupport();

  /** The services. */
  private UpdateableCache services;


  /** Temporary! */
  private String packageName = "org.webmacro.mgmt.impl.";
  private String[] serviceRegistry = {
                            "FlushWMCache"
                              };

  protected ManagementSupport() {
    try {
      init();
      instance = this;
    }
    catch (Exception e) {
      e.printStackTrace(System.err);
      System.err.println("ManagementSupport: Unable to load");
    }
  }

  /** Initialize this instance. */
  protected void init() throws Exception {
    services = CacheFactory.create();
    String last = null;
    // build registry dynamically here.
    for (int index = 0; index < serviceRegistry.length; index++) {
      try {
        String className = packageName + serviceRegistry[index];
        Object o = Class.forName(className).newInstance();
        if (o instanceof ManagementService) {
          services.put(className, o);
          last = className;
        }
      }
      catch (Exception e) {
        System.err.println( e.toString() +
          " serviceRegistry[index] not added as Management Service" );
      }
    }
    services.update(); // brings the cache up to date.
    System.out.println(last + " in cache=" + services.get(last));
    
  }

  /** Gets the singleton instance provided it was loaded successfully. */
  public static ManagementSupport getInstance() {
    if (instance == null)
      throw new IllegalStateException("ManagementSupport could not load!");
    return instance;
  }

  /**
   * Provides a list of all services currently registered.
   */
  public ManagementService[] getServices() {
    Object[] value = services.values();
    ManagementService[] result = new ManagementService[value.length];
    System.arraycopy(value, 0, result, 0, value.length);
    return result;
  }

  /**
   * Provides a list of all services by keys currently registered.
   */
  public String[] getServiceKeys() {
    Object[] value = services.keys();
    String[] result = new String[value.length];
    System.arraycopy(value, 0, result, 0, value.length);
    return result;
  }

  /** Registers an action service. */
  public void addService(ManagementService managementService) {
    services.put(managementService.getClass().getName(), managementService);
  }

  /** Perform a management action. */
  public String performService(String registryValue, WebMacro wm, Context context) throws Exception {
    if (! registryValue.startsWith(packageName) )
      registryValue = packageName + registryValue;
    ManagementService s = (ManagementService) services.get(registryValue);
    if (s != null) {
      s.setWebMacro(wm);
      return s.performService(context);
    }
    else {
      return "No service associated with " + registryValue;
    }
  }

  
  
}
