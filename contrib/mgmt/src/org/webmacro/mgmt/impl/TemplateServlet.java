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

import javax.servlet.http.*;
import javax.servlet.*;
import org.webmacro.servlet.*;
import org.webmacro.*;



/**
  * This is a simple servlet which you can subclass. It simply finds and renders
  * a template.
  * <p>
  * The method to subclass is locateTemplate(). This method uses the uri of the
  * the request to create a template input stream.
  * <p>
  * This class sublcasses WMServlet to leverage the services
  * of that class.
  * <p>
  * Once a template is found, it is rendered as is. A context tool or a subclass
  * would be respsonsible for loading application specific options.
  * In a future release, the uri method, locateTemplate will be found in
  * WMServlet.
  */
public class TemplateServlet extends WMServlet {

  /**
   * Method which finds the template by using the uri and then using
   * the resource class path to find the template.
   * @param The request which has a uri such as /en/welcome.tml
   */
  protected String locateTemplate(HttpServletRequest request) {
		return request.getRequestURI().substring(1); // strip out the leading
  }
  
  /**
   * Calls locateTemplate() and then getTemplate(), a base method,
   * to find the template and then it returns it.
   */
  public Template handle (WebContext context)
          throws HandlerException {
    String templateName = locateTemplate(context.getRequest());
    try {
      return getTemplate(templateName);
    }
    catch (Exception e) {
      throw new HandlerException(templateName + 
      " not found as a valid source template");
    }
  }

}

