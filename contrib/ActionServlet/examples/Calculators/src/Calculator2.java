import org.webmacro.as.ActionException;

/**
 * ActionServlet component that implements an enhanced calculator.
 *
 * @author Petr Toman
 */
public class Calculator2 extends Calculator {
    /**
     * Handles sin(x).
     *
     * @exception NumberFormatException on bad number format (should not occurr)
     */
    public void sin() {
        try {
            operationJustExecuted = true;
            point = false;
            display = number = String.valueOf(Math.sin(new Double(number).doubleValue() * Math.PI / 180));
        } catch (NumberFormatException e) {
            display = "ERR";
            throw e;
        }
    }

    /**
     * Handles cos(x).
     *
     * @exception NumberFormatException on bad number format (should not occurr)
     */
    public void cos() {
        try {
            operationJustExecuted = true;
            point = false;
            display = number = String.valueOf(Math.cos(new Double(number).doubleValue() * Math.PI / 180));
        } catch (NumberFormatException e) {
            display = "ERR";
            throw e;
        }
    }

    /**
     * Handles sqrt(x).
     *
     * @exception NumberFormatException on bad number format (should not occurr)
     */
    public void sqrt() {
        try {
            operationJustExecuted = true;
            point = false;
            display = number = String.valueOf(Math.sqrt(new Double(number).doubleValue()));
        } catch (NumberFormatException e) {
            display = "ERR";
            throw e;
        }
    }

    /**
     * Handles pow(x,y) = x ^ y.
     *
     * @exception NumberFormatException on bad number format (should not occurr)
     */
    public void pow() throws ActionException {
        operation('^');
    }

    /**
     * Overrides operation() in order to support pow(x,y).
     *
     * @param nextOperation may have values ("+", "-", "*", "/", "^")
     * @exception ActionException on bad operation code
     * @exception NumberFormatException on bad number format (should not occurr)
     */
    public void operation(char nextOperation) throws ActionException {
        if (operation == '^') {
            try {
                result = Math.pow(result, new Double(number).doubleValue());

                number = String.valueOf(result);
                display = number;
            } catch (NumberFormatException e) {
                display = "ERR";
                throw e;
            } catch (StringIndexOutOfBoundsException e) {
                throw new ActionException("Invalid numeric operation '" + nextOperation + "'");
            } finally {
                operationJustExecuted = true;
                point = false;
            }
        } else super.operation(nextOperation);
    }
}
