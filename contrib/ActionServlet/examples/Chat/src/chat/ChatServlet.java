package chat;

import org.webmacro.as.ActionServlet;
import org.webmacro.servlet.WebContext;
import org.webmacro.Template;
import chat.components.*;

/**
 * Ensures the user logs in.
 */
public class ChatServlet extends ActionServlet {
    /**
     * Ensures the user logs in.
     */
    protected Object beforeInvoke(WebContext context,
                                  String form,
                                  String action,
                                  Object[] convertedParams) {
        Authenticator auth = (Authenticator) getComponent("authenticator", true);

        if (!auth.isLoggedIn() &&
            !("show".equals(action) && convertedParams.length>0 && "ChatLogin.wm".equals(convertedParams[0])) &&
            !"Login".equals(action) &&
            !"Logoff".equals(action)) {
            context.put("error", "You must log in first.");
            return getWMTemplate("ChatLogin.wm");
        }

        return null;
    }

    /**
     * Joins user to default room on login, invalidates session on logoff.
     */
    protected Object afterInvoke(Object retValue,
                                 WebContext context,
                                 String form,
                                 String action,
                                 Object[] convertedParams) {
        if ("Login".equals(action) && ((Integer)retValue).intValue() == Authenticator.OK) {
            // ensure proper initialization
            getComponent("MessageCenter", true);
            ((Rooms) getComponent("rooms", true)).joinRoom(Rooms.DEFAULT_ROOM);
        } else if ("Logoff".equals(action)) destroySession();

        return retValue;
    }
}
