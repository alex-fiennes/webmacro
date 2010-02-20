package org.webmacro.adapter.spring;


import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContextException;
import org.webmacro.WM;
import org.webmacro.WebMacro;
import org.webmacro.InitException;

import javax.servlet.ServletContext;

/**
 * User: mpalmer
 * @since 08 Dec 2005
 */
public class WebMacroViewResolver extends AbstractTemplateViewResolver
{
    private WebMacro webMacro;

    public WebMacroViewResolver()
    {
        setViewClass(WebMacroView.class);
    }

    /**
     * Requires WebMacroView.
     * @see WebMacroView
     */
    protected Class requiredViewClass()
    {
        return WebMacroView.class;
    }

    protected AbstractUrlBasedView buildView(String viewName) throws Exception {
        WebMacroView view = (WebMacroView) super.buildView(viewName);

        // Tell it which WebMacro to use
        // Fetch our WebMacro in advance so we don't need to lock for
        // every render
        synchronized (this)
        {
            if (webMacro == null)
            {
                webMacro = findWebMacro(getApplicationContext(),
                    getServletContext(), this.getClass().getClassLoader());
            }
        }

        view.setWebMacro(webMacro);

        view.checkTemplate();

        // do we need to do anything here?

        return view;
    }

    public WebMacro getWebMacro()
    {
        return webMacro;
    }

    /**
     * You can programmatically supply a WebMacro instance if your configuration
     * is performed during execution rather than exclusively from a Spring context
     * @param webMacro
     */
    public void setWebMacro(WebMacro webMacro)
    {
        this.webMacro = webMacro;
    }

    /**
     * Attempt to load WebMacro instance from Spring application context, or fall back
     * to a plain new WM() (note: no webapp classpath template loading!)
     *
     * @throws org.springframework.beans.BeansException
     */
    protected WebMacro findWebMacro(ListableBeanFactory beanFactory, 
                                    ServletContext servletcontext, 
                                    ClassLoader classLoader) 
    {
        try
        {
            // FIXME Surely webmacro = ... ?
            WebMacro wm = (WebMacro) BeanFactoryUtils.beanOfTypeIncludingAncestors(
                beanFactory, WebMacro.class, true, false);
            return wm;
        }
        catch (NoSuchBeanDefinitionException e)
        {
            try
            {
                if (webMacro == null) // Can we find the servlet here using a bean context?
                    webMacro = new WM(servletcontext,classLoader, null);
                return webMacro; 
            }
            catch (InitException initEx)
            {
                throw new ApplicationContextException("Unable to init default WebMacro" +
                    "instance. Fix the problem or create an explicit WebMacro (WM)" +
                    "bean in the application context.");
            }
        }
    }
}
