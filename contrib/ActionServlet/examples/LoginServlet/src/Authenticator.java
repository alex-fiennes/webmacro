import org.webmacro.as.ActionServlet;
import org.webmacro.Template;
import org.webmacro.servlet.WebContext;

/**
 * ActionServlet component that authenticates users.
 *
 * @author Petr Toman
 */
public class Authenticator {
    private ActionServlet servlet;

    /**
     * Mandatory public constructor of component.
     */
    public Authenticator(ActionServlet as) {
        servlet = as;
    }

    /** 
     * Implements 'Login' action.
     */
    public Template login(WebContext context, String userName, String password) {
        // only John may log in...
        if ("John".equals(userName) && "18x79Z".equals(password)) {
            context.put("userName", userName);
            return servlet.getWMTemplate("SuccessfulLogin.wm");
        } else {
            // others will get 'Bad login' message back on the login page
            context.put("badLogin", Boolean.TRUE);
            return servlet.getWMTemplate("Login.wm");
        }
    }
}
