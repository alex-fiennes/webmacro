import org.webmacro.as.ActionServlet;

/**
 * A simple component for authenticating users.
 *
 * @author Petr Toman
 */
public class Authenticator {
    /** Return code. */
    public static final int OK = 1;

    /** Return code. */
    public static final int BAD_USERNAME_OR_PASSWORD = 2;

    /** Indicates that bad user name or bassword was entered. */
    private boolean isLoggedIn = false;

    /**
     * Component constructor.
     */
    public Authenticator(ActionServlet as) {}

    /** 
     * Implements 'Login' action.
     */
    public int login(String userName, String password) {
        // only John may log in...
        if ("John".equals(userName) && "18x79Z".equals(password)) {
            isLoggedIn = true;
            return OK;
        } else {
            isLoggedIn = false;
            return BAD_USERNAME_OR_PASSWORD;
        }
    }

    /**
     * Returns true if wrong user name or password was entered.
     */
    public boolean isLoggedIn() {
        return isLoggedIn;
    }
}
