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
     * Handles 'form' action.
     */
    public String showEmptyForm() {
        return "form.wm";
    }

    /**
     * Handles 'SUBMIT' action.
     */
    public String verify(Email email) {
        return "verify.wm";
    }

    /**
     * Handles 'PROCEED' action.
     */
    public String proceed(String name, Email email, String comment) {
        book.addElement(new GuestEntry(name, email, comment));
      
        return "allguest.wm";
    }

    /**
     * Handles 'allguest' action.
     */
    public String showAllGuests() {
        return "allguest.wm";
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