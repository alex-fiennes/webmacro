import java.text.SimpleDateFormat;
import java.util.Locale;
import org.webmacro.as.ActionServlet;
import org.webmacro.Template;
import org.webmacro.servlet.WebContext;

/**
 * ActionServlet component that demonstrates <TT>CompositeTypeHandler</TT> usage.
 *
 * @author Petr Toman
 */
public class DateComponent {
    private static SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy", Locale.US);
    private ActionServlet servlet;

    /**
     * Mandatory public constructor of component.
     */
    public DateComponent(ActionServlet as) {
        servlet = as;
    }

    /** 
     * Implements 'OK' action.
     */
    public Template submit(WebContext context, java.util.Date date) {
        context.put("date", formatter.format(date));

        return servlet.getWMTemplate("SubmittedDate.wm");
    }
}
