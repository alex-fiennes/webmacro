import org.webmacro.servlet.*;
import org.webmacro.engine.Template;
import org.webmacro.broker.ResourceUnavailableException;

/**
 * LoginServlet example.
 *
 * @author Petr Toman
 */
public class LoginServlet extends ActionServlet {
	// implements 'Login' action
	public Template login(WebContext context, String userName, String password) 
	throws ResourceUnavailableException {
        	// only John may log in...
		if ("John".equals(userName) && "18x79Z".equals(password)) 
			return getTemplate("SuccessfulLogin.wm");
		else {
			// others will get 'Bad login' message back on the login page
			context.put("badLogin", Boolean.TRUE);
			return getTemplate("Login.wm");
		}
	}

	// can't be invoked in this simple example, so...
	protected Template conversionError(WebContext context, 
                                           String actionName,
                                           ConversionException e) {
		return null;   // ... do nothing in this method
	}

	// just display the login page, if action is not equal to 'login'
	protected Template unassignedAction(WebContext context, String actionName) 
	throws ResourceUnavailableException {
		return getTemplate("Login.wm");
	}     
}
