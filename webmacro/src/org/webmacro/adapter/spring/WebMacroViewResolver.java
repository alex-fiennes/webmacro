package org.webmacro.adapter.spring;


import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.View;
import org.springframework.beans.BeansException;

import java.util.Locale;

/**
 * User: mpalmer
 */
public class WebMacroViewResolver extends AbstractTemplateViewResolver
{

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
        WebMacroView view = (WebMacroView) super.loadView(viewName, locale);

        // do we need to do anything here?

        return view;
    }
}
