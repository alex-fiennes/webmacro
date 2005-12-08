package org.webmacro.adapter.jsp;

import javax.servlet.jsp.PageContext;
import java.util.Enumeration;

/**
 *
 * <p>
 * Class that provides access to the JSP pageContext attributes with scoping,
 * such that $Attributes.SCOPE.xxxx returns appropriate values.
 * </p>
 * <p>
 * SCOPE can be one of "Page", "Request", "Session" or "Application".
 * </p>
 *
 * @author Marc Palmer (wj5@wangjammers.org)
 * @since 2.0
 */
public class JSPAttributes
{

    private PageContext pc;

    private SessionAttributes sessionAttributes;

    private PageAttributes pageAttributes;

    private RequestAttributes requestAttributes;

    private ApplicationAttributes applicationAttributes;

    /**
     * Utility class to provide access to scoped attributes, JSP-style
     */
    abstract class ScopedAttributes
    {
        protected int scope;

        protected ScopedAttributes(int scope)
        {
            this.scope = scope;
        }

        public int getScope()
        {
            return scope;
        }

        public Object get(Object name)
        {
            return pc.getAttribute(name.toString(), scope);
        }

        public void set(Object name, Object value)
        {
            pc.setAttribute(name.toString(), value, scope);
        }

        public Enumeration getKeys()
        {
            return pc.getAttributeNamesInScope(scope);
        }
    }

    public final class PageAttributes extends ScopedAttributes
    {
        public PageAttributes()
        {
            super(PageContext.PAGE_SCOPE);
        }
    }

    public final class RequestAttributes extends ScopedAttributes
    {
        public RequestAttributes()
        {
            super(PageContext.REQUEST_SCOPE);
        }
    }

    public final class SessionAttributes extends ScopedAttributes
    {
        public SessionAttributes()
        {
            super(PageContext.SESSION_SCOPE);
        }
    }

    public final class ApplicationAttributes extends ScopedAttributes
    {
        public ApplicationAttributes()
        {
            super(PageContext.APPLICATION_SCOPE);
        }
    }

    public JSPAttributes(PageContext pc)
    {
        this.pc = pc;
    }

    /**
     *
     * @return The bean that provides access to session attributes
     */
    public SessionAttributes getSession()
    {
        return sessionAttributes == null
                ? sessionAttributes = new SessionAttributes()
                : sessionAttributes;
    }

    /**
     *
     * @return The bean that provides access to page attributes
     */
    public PageAttributes getPage()
    {
        return pageAttributes == null
                ? pageAttributes = new PageAttributes()
                : pageAttributes;
    }

    /**
     *
     * @return The bean that provides access to request attributes
     */
    public RequestAttributes getRequest()
    {
        return requestAttributes == null
                ? requestAttributes = new RequestAttributes()
                : requestAttributes;
    }

    /**
     *
     * @return The bean that provides access to application attributes
     */
    public ApplicationAttributes getApplication()
    {
        return applicationAttributes == null
                ? applicationAttributes = new ApplicationAttributes()
                : applicationAttributes;
    }

}
