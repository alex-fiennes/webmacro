import org.webmacro.as.*;
import org.webmacro.Template;
import org.webmacro.servlet.WebContext;

/**
 * ActionServlet component that implements a simple calculator.
 *
 * @author Petr Toman
 */
public class Calculator {
    private ActionServlet servlet;

    /**
     * Mandatory public constructor of component.
     */
    public Calculator(ActionServlet as) {
        servlet = as;
    }

    ///////////////////////// Calculator variables  ////////////////////////
    
    /**
     * Typed number. 
     */
    private String number = "0";
    
    /**
     * Was pressed the decimal point?
     */
    private boolean point = false;
    
    /**
     * Keeps the last result.
     */
    private double result = 0;
    
    /**
     * Operation to be executed - values '=', '+', '-', '*', '/'.
     */
    private char operation = '=';
    
    /**
     * Indicates whether 'operation' has just been excecuted.
     * (false when user is typing digits or point)
     */
    private boolean operationJustExecuted = false;

    ///////////////////////////// Action methods //////////////////////////
    
    /**
     * Handles entering digits of number.
     * @param digit typed digit "0"..."9"
     */
    public Template digit(WebContext context, String digit) {
        if (operationJustExecuted) {
            operationJustExecuted = false;
            number = "0";
        }
        
        if ("0".equals(number)) number = digit.trim();
            else number += digit.trim();

        return getCalcTemplate(context, number);
    }
    
    /**
     * Handles pressing decimal point.
     */
    public Template point(WebContext context) {
        if (operationJustExecuted) {
            operationJustExecuted = false;
            number = "0";
        }

        if (!point) number += ".";
        point = true;

        return getCalcTemplate(context, number);
    }

    /**
     * Handles '+/-' button.
     */
    public Template plusminus(WebContext context) {
        if (number.charAt(0) == '-') number = number.substring(1);
            else if (number.charAt(0) != '0') number = "-"+number;

        return getCalcTemplate(context, number);
    }
    
    /**
     * Handles addition, subtraction, multiplication and division.
     * @param nextOperation may have values ("+", "-", "*", "/")
     */
    public Template operation(WebContext context, String nextOperation) 
    throws ActionException {
        try {
            switch (operation) {
                case '+': result += new Double(number).doubleValue(); break;
                case '-': result -= new Double(number).doubleValue(); break;
                case '*': result *= new Double(number).doubleValue(); break;
                case '/': result /= new Double(number).doubleValue(); break;
                case '=': result = new Double(number).doubleValue(); break;
                default: throw new ActionException("Invalid numeric operation '" + 
                                                   operation + "'");
            }

            operation = nextOperation.trim().charAt(0);
        } catch (NumberFormatException e) {
            return getCalcTemplate(context, "ERROR!");
        } catch (StringIndexOutOfBoundsException e) {
            throw new ActionException("Invalid numeric operation '" + nextOperation + "'");
        } finally {
            operationJustExecuted = true;
            point = false;
        }

        return getCalcTemplate(context, number = String.valueOf(result));
    }

    /**
     * Handles pressing 'CE'.
     */
    public Template ce(WebContext context) {
        number = "0";
        point = false;

        return getCalcTemplate(context, "0");
    }

    /**
     * Handles pressing 'C'.
     */
    public Template c(WebContext context) {
        operation = '=';
        result = 0;
        operationJustExecuted = true;
        return ce(context);
    }

    /**
     * Sets the display value on the calculator template.
     */
    private Template getCalcTemplate(WebContext context, String display) {
        context.put("display", display);
        return servlet.getWMTemplate("Calculator.wm");
    }
}
