import org.webmacro.as.*;

/**
 * ActionServlet component that implements a simple calculator.
 *
 * @author Petr Toman
 */
public class Calculator {
    /**
     * Return code.
     */
    public static final int OK = 0;

    /**
     * Return code.
     */
    public static final int ERROR = 1;

    ///////////////////////// Calculator variables  ////////////////////////

    /**
     * Displayed number;
     */
    protected String display;

    /**
     * Typed number.
     */
    protected String number = "0";

    /**
     * Keeps the last result.
     */
    protected double result = 0;

    /**
     * Operation to be executed - values '=', '+', '-', '*', '/'.
     */
    protected char operation = '=';

    /**
     * Indicates whether 'operation' has just been excecuted.
     * (false when user is typing digits or point)
     */
    protected boolean operationJustExecuted = false;

    /**
     * Was pressed the decimal point?
     */
    protected boolean point = false;

    ////////////////////////////// Constructor ////////////////////////////

    public Calculator(ActionServlet as) {}

    ///////////////////////////// Action methods //////////////////////////

    /**
     * Handles entering digits of number.
     *
     * @param digit typed digit "0"..."9"
     * @return OK
     */
    public int digit(int digit) {
        if (operationJustExecuted) {
            operationJustExecuted = false;
            number = "0";
        }

        if ("0".equals(number)) number = String.valueOf(digit);
            else number += digit;

        display = number;
        return OK;
    }

    /**
     * Handles pressing decimal point.
     *
     * @return OK
     */
    public int point() {
        if (operationJustExecuted) {
            operationJustExecuted = false;
            number = "0";
        }

        if (!point) number += ".";
        point = true;

        display = number;
        return OK;
    }

    /**
     * Handles '+/-' button.
     *
     * @return OK
     */
    public int plusminus() {
        if (number.charAt(0) == '-') number = number.substring(1);
            else if (Character.isDigit(number.charAt(0))) number = "-" + number;

        display = number;
        return OK;
    }

    /**
     * Handles addition, subtraction, multiplication and division.
     *
     * @param nextOperation may have values ("+", "-", "*", "/")
     * @exception ActionException on bad operation code
     * @return OK or ERROR
     */
    public int operation(char nextOperation)
    throws ActionException {
        try {
            switch (operation) {
                case '+': result += new Double(number).doubleValue(); break;
                case '-': result -= new Double(number).doubleValue(); break;
                case '*': result *= new Double(number).doubleValue(); break;
                case '/': result /= new Double(number).doubleValue(); break;
                case '=': result =  new Double(number).doubleValue(); break;
                default: throw new ActionException("Invalid numeric operation '" +
                                                   operation + "'");
            }

            operation = nextOperation;
        } catch (NumberFormatException e) {
            display = "ERR";
            return ERROR;
        } catch (StringIndexOutOfBoundsException e) {
            throw new ActionException("Invalid numeric operation '" + nextOperation + "'");
        } finally {
            operationJustExecuted = true;
            point = false;
        }

        number = String.valueOf(result);
        display = number;

        return OK;
    }

    /**
     * Handles pressing 'CE'.
     *
     * @return OK
     */
    public int ce() {
        number = "0";
        point = false;
        display = number;

        return OK;
    }

    /**
     * Handles pressing 'C'.
     *
     * @return OK
     */
    public int c() {
        operation = '=';
        result = 0;
        operationJustExecuted = true;

        return ce();
    }

    /**
     * Returns the value to display (number or "ERR").
     */
    public String getDisplay() {
        return display;
    }
}
