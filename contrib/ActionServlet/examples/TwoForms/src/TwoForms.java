import org.webmacro.servlet.WebContext;

/**
 * ActionServlet component that handles processing of two forms with 
 * the same 'action'. Demo of 'form' parameter usage.
 *
 * @author Petr Toman
 */
public class TwoForms {
    /** 
     * Implements 'Form1'.'OK' action.
     */
    public String submit(WebContext context, double number) {
        context.put("number", number);

        return "Form1Handled.wm";
    }

    /** 
     * Implements 'Form2'.'OK' action.
     */
    public String submit(WebContext context, char ch) {
        context.put("char", ch);

        return "Form2Handled.wm";
    }
}
