import org.webmacro.as.ActionServlet;
import org.webmacro.Template;
import org.webmacro.servlet.WebContext;

/**
 * ActionServlet component that handles processing of two forms with 
 * the same 'action'. Demo of 'form' parameter usage.
 *
 * @author Petr Toman
 */
public class TwoForms {
    private ActionServlet servlet;

    /**
     * Mandatory public constructor of component.
     */
    public TwoForms(ActionServlet as) {
        servlet = as;
    }

    /** 
     * Implements 'Form1'.'OK' action.
     */
    public Template submit(WebContext context, double number) {
        context.put("number", String.valueOf(number));

        return servlet.getWMTemplate("Form1Handled.wm");
    }

    /** 
     * Implements 'Form2'.'OK' action.
     */
    public Template submit(WebContext context, char ch) {
        context.put("char", String.valueOf(ch));

        return servlet.getWMTemplate("Form2Handled.wm");
    }
}
