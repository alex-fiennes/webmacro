package org.webmacro.spring;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.webmacro.Template;
import org.webmacro.WebMacro;
import org.webmacro.ResourceException;

import java.util.Locale;

/**
 * Simple view resolver for WebMacro. This view resolver simply loads
 * a template via WebMacro, using the view name as the template path.
 * It relies on WebMacro to take care of caching issues (as it does in
 * its default configuration).
 *
 * It requires a <code>WebMacro</code> instance be supplied as a property,
 * which is usually done via a <code>WMFactoryBean</code>, but can be set
 * manually for testing, too.
 *
 * You can set a suffix that will be appended to all views, for example
 * ".wmt" to add this extension to all view names.
 *
 * If you set the <code>requestContextAttribute</code> property, an instance
 * of <code>RequestContext</code> will be supplied in the template's
 * <code>Context</code> under this name.
 * @author Sebastian Kanthak
 */
public class WebMacroViewResolver implements ViewResolver {
    private WebMacro wm;
    private String requestContextAttribute;
    private String suffix;

    public void setWebmacro(WebMacro wm) {
        this.wm = wm;
    }

    public void setRequestContextAttribute(String requestContextAttribute) {
        this.requestContextAttribute = requestContextAttribute;
    }

    /**
     * Set the suffix to append to the view name.
     * @param suffix suffix to append
     */
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    /**
     * Resolve a view by using it as a path to a template. The view name will
     * be suffixed by the suffix if it is set. Note that this does not use the locale parameter
     * and thus is not capable of resolving views based on the locale.
     * @param name view name to resolve
     * @param locale ignored
     * @return instance of WebMacroView
     * @throws ResourceException if there is problem loading the template
     */
    public View resolveViewName(String name, Locale locale) throws ResourceException {
        if (suffix != null) name += suffix;
        Template template = wm.getTemplate(name);
        WebMacroView view = new WebMacroView(wm, template);
        if (requestContextAttribute != null) {
            view.setRequestContextAttribute(requestContextAttribute);
        }
        return view;
    }
}
