import java.util.Vector;
import org.webmacro.broker.ResourceUnavailableException;
import org.webmacro.engine.Template;
import org.webmacro.servlet.*;
import org.webmacro.util.Log;

/**
 * Modified GuestBook example from the original WebMacro distribution.
 * See also config files: GuestBook.properties and GuestBook_handlers.properties
 *
 * @author Petr Toman
 */
public class GuestBook extends ActionServlet {
   static private Log log = new Log("guestbk", "GuestBook example");

   Vector books = new Vector();

   /**
    * Handles 'form' action.
    */
   public Template showEmptyForm(WebContext context) throws ResourceUnavailableException {
       return getTemplate("form.wm");
   }
   
   /**
    * Handles 'SUBMIT' action.
    */
   public Template verify(WebContext context, String name, Email email, String comment) 
   throws ResourceUnavailableException {
       books.addElement(new GuestEntry(name, email, comment));
       context.put("registry", books);
      
       return getTemplate("verify.wm");
   }

   /**
    * Handles 'allguest' action.
    */
   public Template showAllGuests(WebContext context) throws ResourceUnavailableException {
       context.put("registry", books);

       return getTemplate("allguest.wm");
   }

   /**
    * Called when 'action' parameter is not assigned to any method of this servlet.
    */
   protected Template unassignedAction(WebContext context, String actionName) 
   throws ActionException {
       throw new ActionException("Action '" + actionName + "' is not understood " +
                                 "by this servlet.");
   }

    /**
     * Called only by 'EmailHandler' in this example. Tells form.wm to display
     * error message and restores other input fields.
     */
   protected Template conversionError(WebContext context, String actionName, ConversionException e)
   throws ResourceUnavailableException {
       context.put("badEmail", Boolean.TRUE);
       
       String str;
       
       if ((str = context.getRequest().getParameter("name")) != null) 
          context.put("name", str);

       if ((str = context.getRequest().getParameter("comment")) != null) 
          context.put("comment", str);

       return getTemplate("form.wm");
   }

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
   
   /**
    * Type handler for checking e-mail format.
    */
   public static class EmailHandler implements SimpleTypeHandler {
       public Object convert(WebContext context, String email) 
       throws ConversionException {
          return new Email(email);
       }
   }
}