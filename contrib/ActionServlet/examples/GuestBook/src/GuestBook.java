import java.util.Vector;
import org.webmacro.as.ActionServlet;

/**
 * ActionServlet component that implements a guest book.
 *
 * @author Petr Toman
 */
public class GuestBook {
    /**
     * Keeps guest book entries.
     */
    public Vector book = new Vector();

    /**
     * Component constructor.
     */
    public GuestBook(ActionServlet as) {}
   
    /**
     * Handles 'show' action.
     */
    public String show(String target) {
        return target;
    }

    /**
     * Handles 'SUBMIT' action.
     */
    public void verify(Email email) {}

    /**
     * Handles 'PROCEED' action.
     */
    public void proceed(String name, Email email, String comment) {
        book.addElement(new GuestEntry(name, email, comment));
    }

    /**
     * Represents a guest book entry.
     */
    public class GuestEntry {
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