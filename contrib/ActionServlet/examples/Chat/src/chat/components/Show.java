package chat.components;

import org.webmacro.as.ActionServlet;

/**
 * Shows templates.
 *
 * @author Petr Toman
 */
public class Show {
    public Show(ActionServlet as) {}

    /**
     * Returns target template.
     */
    public String show(String target) {
        return target;
    }
}
