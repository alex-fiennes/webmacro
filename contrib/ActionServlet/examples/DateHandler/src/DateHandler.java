import java.util.Calendar;
import org.webmacro.as.CompositeTypeHandler;
import org.webmacro.as.ConversionException;
import org.webmacro.servlet.WebContext;

/**
 * Creates object of type <TT>java.util.Date.</TT>
 *
 * @author Petr Toman
 */
public class DateHandler implements CompositeTypeHandler {
    private static Calendar calendar = Calendar.getInstance();

    public Object convert(WebContext context, 
                          String[] componentNames,
                          String[][] componentValues) 
    throws ConversionException {
        if (componentNames.length < 3) 
            throw new ConversionException("Wrong composite definition for" +
                                          " type java.util.Date");
        int components[] = new int[3];
        int i=0;
         
        // try to convert components to integer values
        try {
           for (; i<3; i++)
               components[i] = Integer.parseInt(componentValues[i][0]);
        } catch (NumberFormatException e) {
           throw new ConversionException("Incorrect format", 
                                         componentNames[i], 
                                         componentValues[i][0],
                                         e);
        }

        // use java.util.Calendar class to create java.util.Date object
        calendar.set(components[2], components[1] - 1, components[0]);
        return calendar.getTime();
    }
}
