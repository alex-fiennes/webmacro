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
import org.webmacro.WebMacro;
import org.webmacro.Context;
import org.webmacro.Template;

/**
 * Interface which is the base for all management
 * services in support of reporting or performing an
 * action against a current instance of WM.
 */
public interface ManagementService {

  /**
   * Services provide a title.
   */
  public String getTitle();

  /**
   * Services provide documentation as a template
   * so the client application can format it for
   * presentation, usually, as html.
   */
  public Template getDocumentation();

  /**
   * Services disclose the author.
   */
  public String getAuthor();

  /**
   * Services indicate version number.
   */
  public String getVersion();

  /**
   * When a service is invoked the service
   * needs to know what is the current WM
   * interface instance to provide the service against.
   */
  public void setWebMacro(WebMacro managedInstance);

  /**
   * Perform the service on behalf of the
   * current WM instance.
   * @param context Provide the current context if the service
   * needs it.
   * @return String The result of the service performed.
   */
  public String performService(Context context);
  
}
