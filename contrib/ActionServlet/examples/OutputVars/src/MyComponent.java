import org.webmacro.as.ActionServlet;
import org.webmacro.Template;
import org.webmacro.servlet.WebContext;

/**
 * Component for &lt;output-variable&gt; demonstration.
 *
 * @author Petr Toman
 */
public class MyComponent {
    private ActionServlet servlet;

    /**
     * Mandatory public constructor of component.
     */
    public MyComponent(ActionServlet as) {
        servlet = as;
    }

    /** 
     * Implements 'myAction' action.
     */
    public Template myAction(WebContext context) {
       return servlet.getWMTemplate("template2.wm");
    }

    /**
     * Returns value for $first variable.
     */
    public String getFirst() {
       return "FIRST";
    }

    /**
     * Returns value for $second variable.
     */
    public String getSecond() {
       return "SECOND";
    }
}
