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

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import org.webmacro.Context;
import org.webmacro.util.Settings;
import org.webmacro.util.WMEval;

/**
 * <p>
 * TemplateServlet provides a servlet to evaluate templates directly as long
 * as the template type or types is mapped to this servlet. (Yes, you can
 * map more than one type in a web.xml file to the same servlet).
 * </p>
 * <p>
 * TemplateServlet offers delegation and global processing. You do not need to subclass
 * this servlet. Implement ServletRouter by setting a configuration attribute.
 * Populate the context in a delegate, not a subclass.
 * This servlet provides pre-processing of a 
 * global and request-level template.
 * On servlet initialization, a global template is evaluated once. (This can be refreshed).
 * Since the template may just be creating static blocks, these blocks are then automatically
 * in the context under the name you assign for global processing. Therefore, in your
 * template, you could refer to $Global.CreditCardForm with parameters and evaluate that
 * form as a block. See the wiki page BlockLevelExpressionism for more information on this.
 * Similarly, if your configuration of this servlet specifies a per-Request level template,
 * it will be evaluated and the context placed into the working context prior to evaluation
 * of the actual template.
 * </p>
 * <p>
 * These configuration options make it possible to create automatically
 * a global context and a per request context.
 * A global context means that there is no longer any processing
 * associated with the globals: it is evaluated once. A per-request means that
 * you can always assume that a certain template gets evaluated for every request.
 * </p>
 * <p>
 * This Servlet extends the base class of the JSDK definition eliminating any
 * magic in a super-class.
 * </p>
 * 
 * @since 24 Jan 2004
 * @author lanesharman
 */
public class TemplateServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	/**
	 * The thread-safe evaluator of all templates.
	 */
	protected WMEval wm;

	/**
	 * The global context.
	 */
	protected Context globalContext;

	/**
	 * The global context name to place in the context.
	 */
	protected String globalName;

	/**
	 * The global template.
	 */
	protected String globalTemplate;

	/**
	 * The name that identifies the per-request context.
	 */
	protected String requestName;

	/**
	 * The per-request template.
	 */
	protected String requestTemplate;
	
	/**
	 * The default template to load in a directory such as index.wm, index.wmt, index.tml
	 */
	private static String defaultTemplate = "index.tml";

	/**
	 * The delegate to populate the context for the application.
	 */
	private ServletRouter servletRouter;
	
    protected Logger log;


	/**
	 * Looks for a global template to evaluate (defined in WebMacro.properties).
	 * If defined, the global context is made available to every request as a
	 * var specified in WebMacro.properties.
	 */
	@Override
  public void init(ServletConfig conf) throws ServletException {
		super.init(conf);
		// run the application template, Application.tml.
		try {
			wm = new WMEval(this);
			log = wm.getLog();
			Settings settings = wm.getSettings();
			log("Settings: " + settings.getAsProperties());
			globalName = settings.getSetting("GlobalTemplate.ContextName", null);
			globalTemplate = settings.getSetting("GlobalTemplate.Resource",
					null);
			requestName = settings.getSetting("RequestTemplate.ContextName",
					null);
			requestTemplate = settings.getSetting("RequestTemplate.Resource",
					null);
			String w = settings.getSetting("TemplateServlet.DefaultTemplate");
			if (w != null) {
				defaultTemplate = w;
			}
			String sr = settings.getSetting("TemplateServlet.ServletRouter");
			if (sr != null) {
				this.servletRouter = (ServletRouter) Class.forName(sr).newInstance();
			}
			refreshGlobalContext();
			log("TemplateServlet initialized.");
		} catch (Exception e) {
			wm.error("Unable to initialize", e);
			e.printStackTrace(System.err);
			log("TemplateServlet failed to initialize: " + e.toString());
			throw new ServletException(e.toString());
		}
	}

	/**
	 * Calls locateTemplate() and then getTemplate(), a base method, to find the
	 * template and render it.
	 */
	@Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, java.io.IOException {
		try {
			doResponse(req, resp);
		} catch (Exception e) {
			this.log("Unable to return a template using " + req, e);
			e.printStackTrace(System.err);
			throw new ServletException(e.toString());
		}
	}

	/**
	 * Calls locateTemplate() and then getTemplate(), a base method, to find the
	 * template and render it.
	 */
	@Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, java.io.IOException {
		doGet(req, resp);
	}

	/**
	 * Method which prepares the response and sends it.
	 * <p>
	 * The default implementation is as follows:
	 * 
	 * <pre>
	 *  1) Locate the template as the URI.
	 *  2) Create a new context; if there is a global context
	 *  populate the context with it according to the parameters
	 *  in WebMacro.properties.
	 *  3) If there is a per-request context, perform the equivalent
	 *  action of (2) above.
	 *  4) If there is a
	 * </pre>
	 * 
	 * Override this method to perform your own response handling.
	 * 
	 * @param request The request which has a uri such as /en/welcome.tml
	 * @param response The response object
	 * @return The template to evaluate and return as the response.
	 */
	protected String doResponse(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		String templateName = locateTemplate(request);
		WebContext context = wm.getNewContext(request, response);
		loadGlobalContext(context);
		loadRequestContext(context, templateName, request, response);
		loadDelegationContext(context, templateName, request, response);
		return wm.eval(context, templateName, response);
	}

	/**
	 * Default implenentation for locating the template.
	 */
	protected String locateTemplate(HttpServletRequest request) {
		String value = null; // request.getPathInfo().substring(1); // strip out the
		value = (String) request.getAttribute("javax.servlet.include.servlet_path");
		if (value == null) value = request.getServletPath();
		log.info("request.getPathInfo(): " + request.getPathInfo());
		log.info("javax.servlet.include.servlet_path: " + request.getAttribute("javax.servlet.include.servlet_path"));
		log.info("request.getServletPath(): " + request.getServletPath());
		if (value == null || value.trim().length() == 0) {
			return defaultTemplate;
		} else if (value.endsWith("/")) {
			return (value + defaultTemplate);
		} else {
			return value;
		}
	}

	/**
	 * Implementation for populating the context: handles a default per request
	 * context.
	 * <p>
	 * Override as required.
	 */
	protected void loadRequestContext(WebContext context, String template,
			HttpServletRequest request, HttpServletResponse resp)
			throws ServletException {
		if (this.requestName != null) {
			try {
				Context c = wm.getNewContext();
				wm.eval(c, requestTemplate, null);
				context.put(this.requestName, c);
			} catch (Exception e) {
				this.log("Unable to evaluate request template " + requestName,
						e);
				throw new ServletException(e.toString());
			}
		}
	}

	/**
	 * Implementation for populating the context by a runtime defined delegate.
	 * <p>
	 * Override as required.
	 */
	protected void loadDelegationContext(WebContext context, String template,
			HttpServletRequest request, HttpServletResponse resp)
			throws ServletException {
		if (this.servletRouter != null) {
			try {
				servletRouter.handleWebRequest(this, context, template);
			} catch (Exception e) {
				this.log("Unable to process router " + servletRouter.getClass().getName(),
						e);
				throw new ServletException(e.toString());
			}
		}
	}

	/**
	 * Loads the global context if present.
	 */
	private void loadGlobalContext(WebContext context) {
		if (globalName == null)
			return;
		context.put(globalName, globalContext);
	}

	/**
	 * Call this to refresh the global context.
	 */
	protected void refreshGlobalContext() throws Exception {
		if (globalName == null)
			return;
		Context c = wm.getNewContext();
		wm.eval(c, globalTemplate, null);
		globalContext = c;
	}

}
