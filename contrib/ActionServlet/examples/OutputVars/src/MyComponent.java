import org.webmacro.as.ActionServlet;

/**
 * Component for &lt;output-variable&gt; demonstration.
 *
 * @author Petr Toman
 */
public class MyComponent {
    public MyComponent(ActionServlet as) {}

    /** 
     * Implements 'myAction1' action.
     */
    public String myAction1() {
       // do something...
       return "template2.wm";
    }

    /** 
     * Implements 'myAction2' action.
     */
    public void myAction2() {
       // do something...
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
