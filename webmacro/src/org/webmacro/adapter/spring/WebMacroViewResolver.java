package org.webmacro.adapter.spring;


import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.View;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContextException;
import org.webmacro.WM;
import org.webmacro.WebMacro;
import org.webmacro.InitException;

import javax.servlet.ServletContext;
import java.util.Locale;

/**
 * User: mpalmer
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

    /**
     * Provide any WM-specific customisation of the view object that has been instantiated
     * @param viewName
     * @param locale
     * @return
     * @throws BeansException
     */
    protected View loadView(String viewName, Locale locale) throws Exception
    {
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

        WebMacroView view = (WebMacroView) super.loadView(viewName, locale);

        // Tell it which WebMacro to use
        view.setWebMacro( webMacro);

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
     * @return
     * @throws org.springframework.beans.BeansException
     */
    protected WebMacro findWebMacro(
        ListableBeanFactory beanFactory, ServletContext servletcontext, ClassLoader classLoader) throws BeansException
    {
        try
        {
            WebMacro wm = (WebMacro) BeanFactoryUtils.beanOfTypeIncludingAncestors(
                beanFactory, WebMacro.class, true, false);
            return wm;
        }
        catch (NoSuchBeanDefinitionException e)
        {
            try
            {
                return webMacro == null
                    ? webMacro = new WM(servletcontext,classLoader, null)
                    : webMacro; // Can we find the servlet here using a bean context?
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
