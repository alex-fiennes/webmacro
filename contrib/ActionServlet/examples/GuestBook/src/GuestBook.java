import java.util.Vector;
import org.webmacro.Template;
import org.webmacro.servlet.WebContext;
import org.webmacro.as.*;
import org.webmacro.Log;

/**
 * ActionServlet component that implements a guest book.
 *
 * @author Petr Toman
 */
public class GuestBook {
    private Log log;

    private ActionServlet servlet;

    /**
     * Keeps guest book entries.
     */
    private Vector book = new Vector();

    /**
     * Mandatory public constructor of component.
     */
    public GuestBook(ActionServlet as) {
        servlet = as;
	log = servlet.getLog("guestbk", "GuestBook example");
    }
    
    /**
     * Handles 'form' action.
     */
    public Template showEmptyForm(WebContext context) {
        return servlet.getWMTemplate("form.wm");
    }
   
    /**
     * Handles 'SUBMIT' action.
     */
    public Template verify(WebContext context, String name, Email email, String comment) {
        book.addElement(new GuestEntry(name, email, comment));
      
        return servlet.getWMTemplate("verify.wm");
    }

    /**
     * Handles 'allguest' action.
     */
    public Template showAllGuests(WebContext context) {
        context.put("registry", book);

        return servlet.getWMTemplate("allguest.wm");
    }

    /**
     * Represents a guest book entry.
     */
    public final class GuestEntry {
        private String name;
        private Email email;
        private String comment;
   
        GuestEntry(String inName, Email inEmail, String inComment) {
            name = inName;
            email = inEmail; 
            comment = inComment;
        }
   
        final public String getName() {
            return name; 
        }
       
        final public String getEmail() {
            return email.toString(); 
        }
   
        final public String getComment() {
            return comment; 
        }
    }
}