package shop.typehandlers;

import org.webmacro.as.CompositeTypeHandler;
import org.webmacro.as.ConversionException;
import org.webmacro.servlet.WebContext;
import shop.types.Item;

/**
 * Creates Item instance.
 *
 * @author Petr Toman
 */
public class ItemHandler implements CompositeTypeHandler {
    public Object convert(WebContext context,
                          String[] componentNames,
                          String[][] componentValues)
    throws ConversionException {
        if (componentNames.length < 3)
            throw new ConversionException("Wrong composite definition for" +
                                          " type Item");

        try {
            return new Item(componentValues[0][0],
                            Integer.parseInt(componentValues[1][0]),
                            new Double(componentValues[2][0]).doubleValue());
        } catch(NumberFormatException e) {
            throw new ConversionException(e.getMessage(), e);
        }
    }
}
