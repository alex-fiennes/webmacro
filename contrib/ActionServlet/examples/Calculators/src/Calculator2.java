import org.webmacro.as.*;

/**
 * ActionServlet component that implements an enhanced calculator.
 *
 * @author Petr Toman
 */
public class Calculator2 extends Calculator {
    /**
     * Component constructor.
     */
    public Calculator2(ActionServlet as) {
        super(as);
    }

    /**
     * Handles sin(x).
     *
     * @return OK
     */
    public int sin() {
        try {
            operationJustExecuted = true;
            point = false;
            display = number = String.valueOf(Math.sin(new Double(number).doubleValue() * Math.PI / 180));
            return OK;
        } catch (NumberFormatException e) {
            display = "ERR";
            return ERROR;
        }
    }

    /**
     * Handles cos(x).
     *
     * @return OK
     */
    public int cos() {
        try {
            operationJustExecuted = true;
            point = false;
            display = number = String.valueOf(Math.cos(new Double(number).doubleValue() * Math.PI / 180));
            return OK;
        } catch (NumberFormatException e) {
            display = "ERR";
            return ERROR;
        }
    }

    /**
     * Handles sqrt(x).
     *
     * @return OK
     */
    public int sqrt() {
        try {
            operationJustExecuted = true;
            point = false;
            display = number = String.valueOf(Math.sqrt(new Double(number).doubleValue()));
            return OK;
        } catch (NumberFormatException e) {
            display = "ERR";
            return ERROR;
        }
    }

    /**
     * Handles pow(x,y) = x ^ y.
     *
     * @return OK
     */
    public int pow() throws ActionException {
        return operation('^');
    }

    /**
     * Overrides operation() in order to support pow(x,y).
     */
    public int operation(char nextOperation) throws ActionException {
        if (operation == '^')
            try {
                result = Math.pow(result, new Double(number).doubleValue());

                number = String.valueOf(result);
                display = number;

                return OK;
            } catch (NumberFormatException e) {
                display = "ERR";
                return ERROR;
            } catch (StringIndexOutOfBoundsException e) {
                throw new ActionException("Invalid numeric operation '" + nextOperation + "'");
            } finally {
                operationJustExecuted = true;
                point = false;
            }

        return super.operation(nextOperation);

    }
}
