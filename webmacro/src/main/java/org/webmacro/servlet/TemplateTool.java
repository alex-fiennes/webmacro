/*
 * TemplateTool.java Created on August 28, 2001, 10:45 PM
 */

package org.webmacro.servlet;

import org.webmacro.Context;
import org.webmacro.PropertyException;
import org.webmacro.Template;
import org.webmacro.ContextTool;
import org.webmacro.engine.StringTemplate;

import java.util.ArrayList;

// import java.util.Map;

/**
 * This is an experimental context tool that allows templates to be used as macros. The tool places
 * a MacroTemplateFactory instance into the context that can be referenced as $Template in WMScript.
 * The factory has two methods, each of which returns a MacroTemplate object, created either from a
 * string or a file.
 * 
 * @author Keats Kirsch
 * @version 0.2
 */
public class TemplateTool
  extends ContextTool
{

  private Context _context = null;

  /**
   * Flag set when destroy is first called, to prevent subsequent calls from invoking the destroy()
   * method on the factory object.
   */
  // private boolean _destroyed = false;

  /** Creates new TemplateTool. */
  public TemplateTool()
  {
  }

  /**
   * Create a factory object that can be accessed from WMScript as $Template for creating
   * MacroTemplate objects.
   * 
   * @param c
   *          The context of the current request.
   * @throws PropertyException
   *           From the ContextTool interface
   * @return a new MacroTemplateFactory for each request.
   */
  @Override
  public Object init(Context c)
      throws PropertyException
  {
    _context = c;
    return new MacroTemplateFactory(_context);
  }

  /**
   * A factory class for creating MacroTemplate objects.
   */
  public class MacroTemplateFactory
  {

    private Context _contextLocal;
    private ArrayList<MacroTemplate> _macros = new ArrayList<MacroTemplate>(10);

    /**
     * Constructor
     * 
     * @param ctx
     *          the context for the current request
     */
    public MacroTemplateFactory(Context ctx)
    {
      _contextLocal = ctx;
    }

    /**
     * Creates a MacroTemplate from a string with a new context.
     * 
     * @param s
     *          the template string
     * @return the new MacroTemplate
     */
    public MacroTemplate fromString(String s)
    {
      MacroTemplate mt = new MacroTemplate(_contextLocal, s);
      _macros.add(mt);
      return mt;
    }

    /**
     * Creates a MacroTemplate from a file reference with a new context.
     * 
     * @param fileRef
     *          a reference to the template file
     * @throws org.webmacro.ResourceException
     *           if the file cannot be found or parsed
     * @return a new MacroTemplate
     */
    public MacroTemplate fromFile(String fileRef)
        throws org.webmacro.ResourceException
    {
      Template t = (Template) _contextLocal.getBroker().getProvider("template").get(fileRef);
      MacroTemplate mt = new MacroTemplate(_contextLocal, t);
      _macros.add(mt);
      return mt;
    }

  }

  /**
   * Encapsulates a template and a context, allowing a template to be used like a function or
   * "macro".
   */
  public class MacroTemplate
  {

    private Template _template;
    private Context _contextLocal, _origContext;

    /**
     * Constructor.
     * 
     * @param c
     *          the current request context
     * @param t
     *          the template to be used as a macro
     */
    public MacroTemplate(Context c,
                         Template t)
    {
      _template = t;
      _origContext = c;
      // @@@ Just get a new one?
      _contextLocal = c.cloneContext();
    }

    /**
     * Construct a MacroTemplate with a StringTemplate.
     * 
     * @param c
     *          The context of the current request
     * @param src
     *          the string for the StringTemplate
     */
    public MacroTemplate(Context c,
                         String src)
    {
      this(c,
           new StringTemplate(c.getBroker(), src));
    }

    /**
     * Exposes the context of the current MacroTemplate. This allows "arguments" to be set from a
     * template. E.g., <CODE>#set $myMacro.Args.Name = $User.Name</CODE>
     * 
     * @return the context of the macro
     */
    public Context getArgs()
    {
      return _contextLocal;
    }

    /**
     * Evaluates the macro's template against its context and returns the resulting string.
     * 
     * @throws PropertyException
     *           runtime errors in evaluating the macro's template
     * @return the resultant string after the template is evaluated.
     */
    public Object eval()
        throws PropertyException
    {
      synchronized (_contextLocal) {
        return _template.evaluateAsString(_contextLocal);
      }
    }

    public Object eval(Object[] args)
        throws PropertyException
    {
      if (args != null) {
        for (int i = 0; i < args.length; i++) {
          _contextLocal.put("arg" + (i + 1), args[i]);
        }
        _contextLocal.put("args", args);
      }
      return eval();
    }

    public Object eval(Object[] args,
                       String[] names)
        throws PropertyException
    {
      if (args == null || names == null || args.length != names.length)
        throw new PropertyException("Usage error: both args must be arrays of equal length!");
      for (int i = 0; i < args.length; i++) {
        _contextLocal.put(names[i], args[i]);
      }
      _contextLocal.put("args", args);
      return eval();
    }

    public Object eval(java.util.Map<?, ?> map)
        throws PropertyException
    {
      _contextLocal.putAll(map);
      return eval();
    }

    /**
     * Copies all variables from the current request context into the context of the macro.
     */
    public void copyCurrentContext()
    {
      synchronized (_contextLocal) {
        _contextLocal.putAll(_origContext);
      }
    }

  }
}
