import org.webmacro.servlet.ConversionException;

/**
 * Represents e-mail address.
 *
 * @author Petr Toman
 */
public class Email {
    private String address;
    
    /**
     * Does some basic validation of e-mail address, which must have format: 
     * alphanum* '@' [ alphanum | '.' ]* alphanum
     *
     * @exception ConversionException on bad address format
     */
    public Email(String address) throws ConversionException {
        boolean at = false, next = false;   // parsed parts
        
        BadFormat: {
            for (int i=0; i < address.length(); i++) {
                char ch = address.charAt(i);
                
                if (!Character.isLetterOrDigit(ch)) {
                    switch (ch) {
                        case '@':
                            if (at || i==0) break BadFormat;
                            at = true;
                            continue;
                            
                        case '.': 
                            if (i+2 >= address.length()) break BadFormat;
                            continue;
                        
                        case '_': continue;
                        
                        default: break BadFormat;
                    }
                } else if (at) next = true;
            }
            
            if (!next) break BadFormat;
            this.address = address;
            return;
        }
        
        throw new ConversionException("Bad format of e-mail address");
    }

    /** 
     * Returns (validated) e-mail address.
     */
    public String toString() {
        return address;
    }
}