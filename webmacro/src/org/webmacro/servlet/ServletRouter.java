/*
 * Created on May 14, 2005
 *
 */
package org.webmacro.servlet;


/**
 * 
 * @author Lane Sharman
 */
public interface ServletRouter
{
  /**
   * Implement this interface to process a request intercepted
   * by the TemplateServlet.
   * <p>
   * The HTTP request and response object have been embedded in the
   * request object as well as any global or request-based contexts.
   * </p>
   * @param servlet A reference to the TemplateServlet invoking this type.
   * @param context The Web Context for this request.
   * @param template The template that was requested.
   * @throws Exception
   */
  public void handleWebRequest(TemplateServlet servlet, 
                                WebContext context, 
                                String template) throws Exception;
}
