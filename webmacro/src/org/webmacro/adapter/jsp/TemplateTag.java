package org.webmacro.adapter.jsp;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.webmacro.InitException;
import org.webmacro.PropertyException;
import org.webmacro.Template;
import org.webmacro.WM;
import org.webmacro.WebMacro;
import org.webmacro.engine.StringTemplate;
import org.webmacro.servlet.WebContext;

/**
 * <p>JSP Custom Tag for evaluating WebMacro templates inside a JSP page, or
 * from external files.</p>
 * <p>
 * The "src" attribute is used to specify the src of a WebMacro template
 * that is loaded relative to:
 * <ol>
 * <li>The ServletContext, using ServletContext.getResoruce
 * <li>The JSP page's classpage, using PageContext.getPage().getClass().getClassLoader()
 * <li>The base Broker.
 * </ol>
 * </p>
 *
 * <p>
 * The "context" attribute can be used to pass in a JSP object into the WebMacro context.
 * If it is a Map all elements will be put into the WebMacro context. If it is not a map,
 * it will be put into the context as a single variable called "Context".
 * </p>
 *
 * <p>
 * The "failFast" attribute, if set to "false", will allow the JSP to continue rendering
 * even if there are errors in the WebMacro script such as undefined variables
 * or parser errors. Default is "true", as yoo generallty want to know if something is wrong.
 * </p>
 *
 * <p>
 * The "Attributes" context variable provides access to all the JSP-style attributes
 * scoped by page, request, session and application. To access these you simply
 * reference $Attributes.SCOPE.VariableName where SCOPE is one of Page, Request,
 * Session or Application
 * </p>
 *
 *
 * @author Marc Palmer (wj5@wangjammers.org)
 */
public class TemplateTag extends BodyTagSupport
{
    private static final long serialVersionUID = -6790150900989142583L;

    private static final int DEFAULT_TEMPLATE_BUFFER_SIZE = 4000;

    /**
     * We might get a nice performance improvement here if we can use a 16bit Unicode
     * encoding that is supported on all platforms, as the encode/decode phase should
     * short circuit to a straight copy.
     */
    private static final String TEMPORARY_ENCODING = "UTF-8";

    /**
     * Name used for the JSP context attributes bean when in the WM context
     */
    private static final String ATTRIBUTES_HELPER_NAME = "Attributes";

    /**
     * Name used for an optional non-Map context object if supplied
     */
    private static final String CONTEXT_VARIABLE_NAME = "Context";

    /**
     * Our shared WebMacro instance
     */
    private WebMacro webmacro;

    /**
     * Optional JSP-page supplied context variables to use
     */
    private Object context;

    /**
     * The external WM src file to use, if any.
     */
    private String src;

    /**
     * If set (default) to try any property errors will hose the JSP page rendering totally,
     * if false it will just dump out some explanatory WM text.
     */
    private boolean propertyErrorsFailFast = true;

    /**
     * 
     * @return the src of an external template to use
     */
    public String getSrc()
    {
        return src;
    }

    /**
     * Set the src of an external template to use
     * @param src
     */
    public void setSrc(String src)
    {
        this.src = src;
    }

    public TemplateTag()
    {
    }

    /**
     * @return a new or the existing WebMacro instance
     * @throws InitException
     */
    public WebMacro getWebMacro() throws InitException
    {
        return webmacro == null ? webmacro = makeWM() : webmacro;
    }

    /**
     * Make a WM instance using a broker with access to the ServletContext
     * for resources.
     * @return a new WebMacro
     * @throws InitException
     */
    private WebMacro makeWM() throws InitException
    {
        // Attempt to get a classloader for JSP servlet
        // that is within the web application so we can access resources in there
        final ClassLoader classLoader = pageContext.getPage().getClass().getClassLoader();

        return new WM( pageContext.getServletContext(),
                classLoader, null );
    }

    /**
     * Called when the wm:template tag starts. If the src attribute is
     * specified we evaluate it now and ignore the body of the tag.
     * @return SKIP_BODY to stop JSP evaluation the WM script
     * @throws JspException
     */
    public int doStartTag() throws JspException
    {
        /*
         * JSP won't give us a doEndTag if the tag is empty, so we need to eval here
         * if they are using the src attribute
         */
        if (src != null)
        {
            doEval();
        }
        return SKIP_BODY; // Don't let JSP evaluate our WM script
    }

    /**
     * Called when the end of the wm:template tag is found. If there is
     * only a src attribute and no body and end tag, this method is not called.
     * If this method is called, we check to see if there was no external template
     * used and if so process the body of the tag as a WebMacro template
     * @return EVAL_PAGE
     * @throws JspException
     */
    public int doEndTag() throws JspException
    {
        /*
         * Eval WM here if it was a body-based tag instance
         */
        if (src == null)
        {
            doEval();
        }
        return EVAL_PAGE;
    }

    /**
     * Evaluate the template file or template text specified.
     * This is rather inefficient as we have to write out to a byte array first
     * because we don't have a FastWriter or an OutputStream.
     * @throws JspTagException
     */
    private void doEval() throws JspTagException
    {
        String servletName = pageContext.getServletConfig().getServletName();
        try
        {
            final BodyContent bodyContent = getBodyContent();

            JspWriter writer = pageContext.getOut();
            if (bodyContent != null)
            {
                writer = bodyContent.getEnclosingWriter();
            }

            /*
             * Create the WM context, complete with request and response tools
             */
            final WebContext webContext = getWebMacro().getWebContext(
                (HttpServletRequest) pageContext.getRequest(),
                (HttpServletResponse) pageContext.getResponse());

            /*
             * If the JSP page supplied a context, if it's a map put all the
             * vars into the WM context, else put it in as a single var
             */
            if (context != null)
            {
                if (context instanceof Map)
                {
                    webContext.putAll( (Map) context);
                }
                else
                {
                    webContext.put( CONTEXT_VARIABLE_NAME, context);
                }
            }

            /*
             * Add the scoped attribute helper
             */
            webContext.put( ATTRIBUTES_HELPER_NAME, new JSPAttributes(pageContext));

            Template templ = null;
            String templateOutput = null;

            try
            {
                if (src != null)
                {
                    templ = webmacro.getTemplate(src);
                }
                else if (bodyContent != null)
                {

                    templ = new StringTemplate( webmacro.getBroker(),
                            bodyContent.getString(),
                            "WebMacro script in JSP page "+servletName);
                }

                if (templ != null)
                {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream(
                            DEFAULT_TEMPLATE_BUFFER_SIZE);

                    templ.write(baos, TEMPORARY_ENCODING, webContext);
                    templateOutput = baos.toString(TEMPORARY_ENCODING);
                    baos = null; // Give the GC a break
                }
            }
            catch (PropertyException e)
            {
                if (!propertyErrorsFailFast)
                {
                    templateOutput = "WebMacro property access error occurred: "+e;
                }
                else
                {
                    throw e;
                }
            }

            writer.print(templateOutput);

            if (bodyContent != null)
            {
                bodyContent.clearBody();
            }
        }
        catch (Exception e)
        {
            throw new JspTagException("WM Template tag error: " + e);
        }

    }

    /**
     * Reset the tag
     */
    public void release()
    {
        src = null;
        context = null;
        propertyErrorsFailFast = true;
    }

    public boolean isFailFast()
    {
        return propertyErrorsFailFast;
    }

    public void setFailFast(boolean failFast)
    {
        this.propertyErrorsFailFast = failFast;
    }

    public Object getContext()
    {
        return context;
    }

    public void setContext(Object context)
    {
        this.context = context;
    }
}
