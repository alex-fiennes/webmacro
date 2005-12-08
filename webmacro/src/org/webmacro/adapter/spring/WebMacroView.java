package org.webmacro.adapter.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContextException;
import org.springframework.web.servlet.view.AbstractTemplateView;
import org.webmacro.*;
import org.webmacro.servlet.WebContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Author: mpalmer
 */
public class WebMacroView extends AbstractTemplateView
{

    private WebMacro webMacro;

    private static WM sharedWM;

    /**
     * Invoked on startup. Looks for a single WebMacro bean in the application
     * context and if it cannot be found it will create an implicit shared instance.
     * Actually this is called by the view resolved, not the real applicationContext
     * as there is one view instance per view URL
     */
    protected void initApplicationContext() throws BeansException
    {
        super.initApplicationContext();

        // Fetch our WebMacro in advance so we don't need to lock for
        // every render
        if (webMacro == null)
        {
            webMacro = findWebMacro();
        }

        checkTemplate();
    }

    /**
     * Verify that the template specified exists
     */
    private void checkTemplate()
    {
        try
        {
            // This is arguably redundant and non-performant as the template could disappear
            // during execution
            webMacro.getTemplate(getUrl());
        }
        catch (ResourceException re)
        {
            throw new ApplicationContextException("Cannot find WebMacro template for URL [" + getUrl() + "]", re);
        }
    }

    /**
     * Attempt to load WebMacro instance from Spring application context, or fall back
     * to a plain new WM() (note: no webapp classpath template loading!)
     *
     * @return
     * @throws BeansException
     */
    protected synchronized WebMacro findWebMacro() throws BeansException
    {
        try
        {
            WebMacro wm = (WebMacro) BeanFactoryUtils.beanOfTypeIncludingAncestors(
                getApplicationContext(), WebMacro.class, true, false);
            return wm;
        }
        catch (NoSuchBeanDefinitionException e)
        {
            try
            {
                return sharedWM == null
                    ? sharedWM = new WM(getServletContext(),
                          this.getClass().getClassLoader(), null)
                    : sharedWM; // Can we find the servlet here using a bean context?
            }
            catch (InitException initEx)
            {
                throw new ApplicationContextException("Unable to init default WebMacro" +
                    "instance. Fix the problem or create an explicit WebMacro (WM)" +
                    "bean in the application context.");
            }
        }
    }

    public WebMacro getWebMacro()
    {
        return webMacro;
    }

    /**
     * Render the template
     *
     * @todo Add code here to put in standard context variables used for Spring form stuff
     * 
     * @param model
     * @param request
     * @param response
     * @throws Exception
     */
    protected void renderMergedTemplateModel(Map model,
                                             HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        WebContext context = webMacro.getWebContext(request, response);
        context.putAll(model);

        Template template = webMacro.getTemplate(getUrl());

        // @todo Need handling for template encodings here
        template.write(response.getOutputStream(), context);
    }


}
