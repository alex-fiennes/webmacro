import org.webmacro.as.*;

import org.webmacro.Template;
import org.webmacro.servlet.WebContext;

/**
 * Modified GuestBook example from the original WebMacro 0.89.1 distribution.
 *
 * @author Petr Toman
 */
public class GuestBookServlet extends ActionServlet {
    /**
     * Called only by 'EmailHandler' in this example. Tells form.wm to display
     * error message and restores other input fields.
     */
    protected Template conversionError(WebContext context,
                                      String formName,
                                      String actionName, 
                                      ConversionException e) {
        context.put("badEmail", Boolean.TRUE);
        context.put("name", context.getRequest().getParameter("name"));
        context.put("comment", context.getRequest().getParameter("comment"));

        return getWMTemplate("form.wm");
    }
}