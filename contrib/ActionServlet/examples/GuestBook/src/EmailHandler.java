import org.webmacro.as.SimpleTypeHandler;
import org.webmacro.as.ConversionException;
import org.webmacro.servlet.WebContext;
   
/**
 * Type handler for checking e-mail format.
 *
 * @author Petr Toman
 */
public class EmailHandler implements SimpleTypeHandler {
    public Object convert(WebContext context, String email) throws ConversionException {
        return new Email(email);
    }
}
