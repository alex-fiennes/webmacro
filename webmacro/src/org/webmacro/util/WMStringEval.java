/*
 * Created on Apr 15, 2005
 *
 */
package org.webmacro.util;

import org.webmacro.Context;
import org.webmacro.Template;
import org.webmacro.WM;
import org.webmacro.WebMacro;
import org.webmacro.engine.StringTemplate;

/**
 * A simple encapsulation of a WebMacro instance
 * for evaluating string-based templates.
 * @author Lane Sharman
 */
public class WMStringEval
{

  private Context context;
  private WebMacro wm;

  /**
   * Constructs an WM String evaluator with a context.
   */
  public WMStringEval()
  {
    // Build a web macro environment for currentTemplate execution.
    try
    {
        wm = new WM();
        context = wm.getContext();
    }
    catch (Exception e)
    {
        e.printStackTrace(System.err);
        throw new IllegalStateException(e.toString());
    }
  }
  
  /**
   * Gets the current context.
   * @return The context.
   */
  public Context getCurrentContext()
  {
    return context;
  }
  
  /**
   * Establish a new context for future evaluations.
   * @return The new context.
   */
  public Context getNewContext()
  {
    context = wm.getContext();
    return context;
  }
  
  /**
   * Adds a reference to the context.
   * @param name The name of the reference.
   * @param value The referent.
   */
  public void addContextReference(String name, Object value)
  {
    context.put(name, value);
  }
  
  /**
   * Evaluate the template using the current context.
   * @param template The string template to evaluate.
   * @return The evaluation of the template against the context.
   * @throws Exception
   */
  public String eval(String template) throws Exception
  {
    Template t = new StringTemplate(wm.getBroker(), template, "anonymous");
    return t.evaluateAsString(context);
  }

}
