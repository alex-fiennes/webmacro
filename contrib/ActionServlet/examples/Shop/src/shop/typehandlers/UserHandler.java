package shop.typehandlers;

import org.webmacro.as.CompositeTypeHandler;
import org.webmacro.as.ConversionException;
import org.webmacro.servlet.WebContext;
import shop.types.Email;
import shop.types.User;

/**
 * Creates User instance.
 *
 * @author Petr Toman
 */
public class UserHandler implements CompositeTypeHandler {
    public Object convert(WebContext context,
                          String[] componentNames,
                          String[][] componentValues)
    throws ConversionException {
        if (componentNames.length < 8)
            throw new ConversionException("Wrong composite definition for" +
                                          " type User");

        return new User(componentValues[0][0],
                        new Email(componentValues[1][0]),
                        componentValues[2][0],
                        componentValues[3][0],
                        componentValues[4][0],
                        componentValues[5][0],
                        componentValues[6][0],
                        componentValues[7][0]);
    }
}
