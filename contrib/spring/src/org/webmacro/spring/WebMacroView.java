package org.webmacro.spring;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.support.RequestContext;
import org.webmacro.Template;
import org.webmacro.WebMacro;
import org.webmacro.PropertyException;
import org.webmacro.servlet.WebContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.util.Map;
import java.io.IOException;

/**
 * Thin wrapper around a WebMacro template.
 * This class encapsulates a WebMacro template and provides the
 * model as a WebMacro <code>WebContext</code>.
 *
 * If the <code>requestContextAttribute</code> property is set, an
 * instance of <code>RequestContext</code> will be put into the context/model
 * under this name.
 *
 * Normally, this class is not used directly, but returned by a ViewResolver. However,
 * it can be constructed manually for testing.
 * @author Sebastian Kanthak
 */
public class WebMacroView implements View {
    private final WebMacro wm;
    private final Template template;

    private String requestContextAttribute;

    public WebMacroView(WebMacro wm, Template template) {
        this.wm = wm;
        this.template = template;
    }

    public void setRequestContextAttribute(String requestContextAttribute) {
        this.requestContextAttribute = requestContextAttribute;
    }

    public void render(Map model, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, PropertyException {
        WebContext context = wm.getWebContext(request, response);
        if (requestContextAttribute != null) {
            model.put(requestContextAttribute,new RequestContext(request,model));
        }
        context.setMap(model);
        template.write(response.getOutputStream(), context);
    }
}
