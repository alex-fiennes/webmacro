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
package org.webmacro.mgmt.impl;
import org.webmacro.mgmt.*;
import org.webmacro.*;
import org.webmacro.resource.*;

/**
 * An instance of this class will flush the
 * cache associated with the provided
 * WebMacro reference.
 */
public class FlushWMCache implements ManagementService {

  private WebMacro wm;

  public String getTitle() {
    return "Flushes the template and url caches";
  }

  public Template getDocumentation() {
    try {
      return wm.getTemplate("org/webmacro/mgmt/impl/FlushWMCache.wm");
    }
    catch (Exception e) {
      e.printStackTrace(System.err);
      return null;
    }
  }

  public String getAuthor() { return "Lane Sharman";}

  public String getVersion() { return "Version 1.1";}

  public String toString() {
    return getTitle() + " " + getVersion() + " " + getAuthor();
  }

  public void setWebMacro(WebMacro managedInstance) {
    wm = managedInstance;
  }

  /**
   * Flushes the caches for url and templates and returns
   * the results as action taken.
   */
  public String performService(Context context) {
    StringBuffer action = new StringBuffer("Caches Flushed: \n");
    try {
      Object c = wm.getBroker().getProvider("template");
      if (c instanceof CachingProvider) {
        ((CachingProvider) c).flush();
        action.append("Template cache flushed. \n");
      }
      c = (CachingProvider) wm.getBroker().getProvider("url");
      if (c instanceof CachingProvider) {
        ((CachingProvider) c).flush();
        action.append("URL cache flushed.\n");
      }
    }
    catch (Exception e) {
      e.printStackTrace(System.err);
    }
    finally {
      return action.toString();
    }
  }
  
}
  
