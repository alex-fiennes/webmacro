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
import java.util.StringTokenizer;
import javax.servlet.http.*;
import javax.servlet.*;
import org.webmacro.servlet.*;
import org.webmacro.*;
import org.webmacro.util.*;



/**
  * This servlet provides action semantic handling and template locating
  * based on presence of an action semantic in the request parameter list.
  * <p>
  * The protocol for this action handling servlet specifies the following
  * request parameter requirements.
  * <pre>
  * al
  * al={comma separated list of permissible action verbs} for example:
  * al={ActionCancel, ActionClose}
  * 
  * for each value in al, there should be a parameter in the request
  * with the form:
  * on{alValue}
  * and al{Value}=path to template for example:
  * onCancel=resource/templates/MainMenu.wm
  * </pre>
  * In this manner, the page governs its own navigation for each action 
  * semantic.
  * <p>
  * Finally, there is a convenience call back which a subclass can implement, 
  * evaluate(HttpRequest, Context). In this manner, the subclass can
  * This provides a subclass to override the location Subclasses can 
  * implement this method and provide a custom template such as an error
  * template or the same template in the case the action cannot be processed.
  */
public class ActionableServlet extends WMServlet {

  /**
   * Subclasses should implement this adapter method
   * to evaluate the current WebContext and determine
   * an alternate template if other than via the standard protocol.
   * <p>
   * This is a good adapter method to override for validation.
   * @param WebContext
   * @return This implementation returns null.
   */
  protected String evaluateAction(WebContext context) {
		return null;
  }
  
  /**
   * A convenience method which will return
   * the action performed.
   * @param context The WebContext for the request.
   * @return The action semantic found in the request parameters.
   */
  protected String getActionName(WebContext context) {
    HttpServletRequest req = context.getRequest();
    StringTokenizer st = new StringTokenizer(
                  req.getParameter("al"), ","
                                            );
    while (st.hasMoreTokens()) {
      String actionName = st.nextToken();
      if (req.getParameter(actionName) != null) {
        return actionName;
      }
    }
    return null; // no action name found
  }
  
  /**
   * Given an action name this method
   * will return the template associated with the action name.
   * @param context The web context
   * @param actionName The name of the action, eg, "Next", "Previous", "Close".
   * @return The name of the template to load onNext.
   */
  protected String getActionTemplate(WebContext context, String actionName) {
        return context.getRequest().getParameter("on" + actionName);
  }

  /**
   * Finds the action and the finds the template labeled by onAction.
   */
  protected String evaluateActionProtocol(WebContext context) 
    throws HandlerException {
    String actionName = getActionName(context);
    if (actionName == null)
      throw new HandlerException("No Action found in request parameter");
    return getActionTemplate(context, actionName);
  }
        
  /**
   * Calls evaluate() and, if null returned, returns
   * the template provided by evaluateActionProtocol.
   */
  public Template handle (WebContext context)
          throws HandlerException {
    String templateName = evaluateAction(context);
    if (templateName == null)
      templateName = evaluateActionProtocol(context);
    try {
      return getTemplate(templateName);
    }
    catch (Exception e) {
      throw new HandlerException(templateName + " not found: " + e.toString());
    }
  }

}

