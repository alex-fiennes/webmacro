package shop.components;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import org.webmacro.Log;
import org.webmacro.servlet.WebContext;
import org.webmacro.as.ActionServlet;

/**
 * Target template dispatcher.
 *
 * @author Petr Toman
 */
public class Show {
    /** Name of the template to show.*/
    public String target;

    private Log log;
    private static final Locale CZECH = new Locale("cs","");
    private ResourceBundle messages = ResourceBundle.getBundle("messages",Locale.ENGLISH);
    private Locale currentLocale = Locale.ENGLISH;

    public Show(ActionServlet as, String name) {
        log = as.getLog("shop");
        target = as.getProperty(name, "initial.target");
    }

    /**
     * Sets target template.
     */
    public void show(String target) {
        this.target = target;
    }

    /**
     * Sets target template and changes language.
     */
    public void show(String target, String language) {
        this.target = target;

        if ("cs".equals(language)) {
            messages = ResourceBundle.getBundle("messages", CZECH);
            currentLocale = CZECH;
        } else if ("en".equals(language)) {
            messages = ResourceBundle.getBundle("messages", Locale.ENGLISH);
            currentLocale = Locale.ENGLISH;
        }
    }

    /**
     * Sets target template and sets HTTP parameters.
     */
    public void show(WebContext context, String target, String[] paramNames, String[] paramValues) {
        this.target = target;

        for (int i=0; i < paramNames.length; i++) {
           if ("true".equalsIgnoreCase(paramValues[i])) context.put(paramNames[i], true);
               else if ("false".equalsIgnoreCase(paramValues[i])) context.put(paramNames[i], false);
                   else context.put(paramNames[i], paramValues[i]);
        }
    }

    /**
     * Returns localized message.
     */
    public String getString(String key) {
        try {
            return messages.getString(key);
        } catch (MissingResourceException e) {
            log.error("Cannot retrieve localized message for key: '" + key + "'");
            return key;
        }
    }

    /**
     * Returns current locale.
     */
    public Locale getLocale() {
       return currentLocale;
    }
}
