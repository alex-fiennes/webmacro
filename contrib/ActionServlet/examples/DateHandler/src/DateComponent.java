import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.webmacro.as.ActionServlet;

/**
 * ActionServlet component that demonstrates <TT>CompositeTypeHandler</TT> usage.
 *
 * @author Petr Toman
 */
public class DateComponent {
    private static SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy", Locale.US);
    private Date date;

    public DateComponent(ActionServlet as) {}

    /** 
     * Implements 'OK' action.
     */
    public String submit(Date date) {
        this.date = date;

        return "SubmittedDate.wm";
    }

    /**
     * Returns formated date.
     */
    public String getDateAsString() {
        return formatter.format(date);
    }
}
