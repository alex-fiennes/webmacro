/*
 * Created on May 14, 2005
 */
package org.webmacro.servlet;

import javax.servlet.http.HttpServlet;

/**
 * Implements a router dispatcher pattern for a servlet handling a request in a WebMacro setting.
 * 
 * @author Lane Sharman
 */
public interface ServletRouter
{
  /**
   * Implement this interface to process a request intercepted by the TemplateServlet.
   * <p>
   * The HTTP request and response object have been embedded in the request object as well as any
   * global or request-based contexts.
   * </p>
   * 
   * @param servlet
   *          A reference to the Servlet invoking this type.
   * @param context
   *          The Web Context for this request.
   * @param template
   *          The template that was requested.
   * @throws Exception
   *           Exceptions thrown by the handler.
   */
  public void handleWebRequest(HttpServlet servlet,
                               WebContext context,
                               String template)
      throws Exception;
}
