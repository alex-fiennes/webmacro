import org.webmacro.as.*;
import org.webmacro.Template;
import org.webmacro.servlet.WebContext;

/**
 * Modified GuestBook example from the original WebMacro distribution.
 *
 * @author Petr Toman
 */
public class GuestBookServlet extends ActionServlet {
    /**
     * Called only by 'EmailHandler' in this example. Tells form.wm to display
     * error message and restores other input fields.
     */
    protected Template conversionError(WebContext context,
                                       String form,
                                       String action, 
                                       ConversionException e) {
        context.put("badEmail", true);
        context.put("name", context.getForm("name"));
        context.put("comment", context.getForm("comment"));

        return getWMTemplate("form.wm");
    }
}