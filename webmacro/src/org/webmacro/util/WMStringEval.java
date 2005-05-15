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
 * 
 * @author Lane Sharman
 */
public class WMStringEval
{

  private Context context;
  private WebMacro wm;

  /**
   * 
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
  
  public Context getCurrentContext()
  {
    return context;
  }
  
  public Context getNewContext()
  {
    context = wm.getContext();
    return context;
  }
  
  public void addContextReference(String name, Object value)
  {
    context.put(name, value);
  }
  
  public String eval(String template) throws Exception
  {
    Template t = new StringTemplate(wm.getBroker(), template, "anonymous");
    return t.evaluateAsString(context);
  }

}
