import org.webmacro.as.ActionServlet;

/**
 * Component for &lt;output-variable&gt; demonstration.
 *
 * @author Petr Toman
 */
public class MyComponent {
    public MyComponent(ActionServlet as) {}

    /** 
     * Implements 'myAction' action.
     */
    public String myAction() {
       // do something...
       return "template2.wm";
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
