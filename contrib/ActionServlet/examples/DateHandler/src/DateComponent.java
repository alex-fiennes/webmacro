import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * ActionServlet component that demonstrates <TT>CompositeTypeHandler</TT> usage.
 *
 * @author Petr Toman
 */
public class DateComponent {
    private static SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy", Locale.US);
    private Date date;

    /** 
     * Implements 'OK' action.
     */
    public void submit(Date date) {
        this.date = date;
    }

    /**
     * Returns formated date.
     */
    public String getDateAsString() {
        return formatter.format(date);
    }
}
