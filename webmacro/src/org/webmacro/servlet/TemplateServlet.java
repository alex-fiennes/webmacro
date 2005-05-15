/*
 * @(#)TemplateServlet.java
 * 
 * Copyright (c) 1995-2000 Open Doors Software, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Open Doors Software
 * Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Open Doors Software.
 * 
 * Open Doors Software MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. OPEN DOORS SOFTWARE SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * 
 */
package org.webmacro.servlet;
import javax.servlet.http.*;
import javax.servlet.*;
import org.webmacro.*;
import org.webmacro.util.*;

/**
  * A servlet to locate and then evaluate a WM templates
  * using the encapsulator, WMEval. Explicit support
  * provided for a global context evaluated during initialization
  * of the servlet.
  * <p>
  * TODO: Improve configuration options of the servlet.
  */
public class TemplateServlet extends HttpServlet {
  
  protected WMEval wm;
  protected Context globalContext;
  protected String globalName;
  protected String globalTemplate;
  protected Context requestContext;
  protected String requestName;
  protected String requestTemplate;
  private static String defaultTemplate = "index.tml";
  private ServletRouter sr;

  /**
   * Looks for a global template to evaluate (defined in WebMacro.properties).
   * If defined, the global context is made available to every request as a var
   * specified in WebMacro.properties.
   */
  public void init(ServletConfig conf) throws ServletException {
  	super.init (conf);
    // run the application template, Application.tml.
    try {
      wm = new WMEval(this);
      Settings settings = wm.getSettings();
      log("Settings: " + settings.getAsProperties());
      globalName = settings.getSetting("GlobalTemplate.ContextName", null);
      globalTemplate = settings.getSetting("GlobalTemplate.Resource", null);
      requestName = settings.getSetting("RequestTemplate.ContextName", null);
      requestTemplate = settings.getSetting("RequestTemplate.Resource", null);
      String w = settings.getSetting("TemplateServlet.DefaultTemplate");
      if (w != null)
      {
        defaultTemplate = w;
      }
      String sr = settings.getSetting("TemplateServlet.ServletRouter");
      if (sr != null)
      {
         this.sr = (ServletRouter) Class.forName(sr).newInstance();
      }
      refreshGlobalContext();
      log("TemplateServlet initialized.");
    }
    catch (Exception e) {
      wm.error("Unable to initialize", e);
      e.printStackTrace(System.err);
			log("TemplateServlet failed to initialize: " + e.toString());
      throw new ServletException(e.toString());
    }
  }
  
  /**
   * Calls locateTemplate() and then getTemplate(), a base method,
   * to find the template and render it.
   */
   protected void doGet(HttpServletRequest req,
                     HttpServletResponse resp)
              throws ServletException,
              java.io.IOException {
     try {
       doResponse(req, resp);
     }
     catch (Exception e) {
       e.printStackTrace(System.err);
       throw new ServletException(e.toString());
     }
   }
   
  /**
   * Calls locateTemplate() and then getTemplate(), a base method,
   * to find the template and render it.
   */
   protected void doPost(HttpServletRequest req,
                     HttpServletResponse resp)
              throws ServletException,
              java.io.IOException {
     doGet(req, resp);
   }
   
   /**
   * Method which prepares the response.
   * <p>
   * The default implementation is as follows:
   * <pre>
   * 1) Locate the template as the URI.
   * 2) Create a new context, and if there is a global context
   * populate the context with it according to the parameters
   * in WebMacro.properties.
   * </pre>
   * Override this method as required.
   * @param The request which has a uri such as /en/welcome.tml
   * @param The response object
   * @return The template to evaluate and return as the response.
   */
  protected String doResponse(HttpServletRequest request, 
            HttpServletResponse response) throws ServletException 
  {
	String templateName = locateTemplate(request);
    WebContext context = wm.getNewContext(request, response);
    loadGlobalContext(context);
    populateContext(context, templateName, request, response);
    return wm.eval(context, templateName, response);
  }
  
  /**
   * Default implenentation for
   * locating the template.
   */
  protected String locateTemplate(HttpServletRequest request)
  {
	String value = request.getPathInfo().substring(1); // strip out the leading /
    if (value == null || value.trim().length() == 0)
    {
      return defaultTemplate;
    }
    else if (value.endsWith("/"))
    {
      return (value + defaultTemplate);
    }
    else
    {
      return value;
    }
  }
  
  /**
   * Implementation for populating the context: handles
   * a default per request context.
   * <p>
   * Override as required.
   */
  protected void populateContext(WebContext context, String template, 
                 HttpServletRequest request, 
                 HttpServletResponse resp) throws ServletException
  {
    if (requestName != null)
    {
      try {
        Context c = wm.getNewContext();
        wm.eval(c, requestTemplate, null);
        context.put(this.requestName, c);
      }
      catch (Exception e)
      {
        this.log("Unable to evaluate request template " + requestName, e);
        throw new ServletException(e.toString());
      }
    }
    if (this.sr != null)
    {
      try {
        sr.handleWebRequest(this, context, template);
      }
      catch (Exception e)
      {
        this.log("Unable to process router " + sr.getClass().getName(), e);
        throw new ServletException(e.toString());
      }
    }
  }
  
  /**
   * Loads the global context if present.
   */
  private void loadGlobalContext(WebContext context)
  {
    if (globalName == null) return;
    context.put(globalName, globalContext);
  }
  
  /**
   * Call this to refresh the global context.
   */
  protected void refreshGlobalContext() throws Exception
  {
    if (globalName == null) return;
    Context c = wm.getNewContext();
    wm.eval(c, globalTemplate, null);
    globalContext = c;
  }
  
}

