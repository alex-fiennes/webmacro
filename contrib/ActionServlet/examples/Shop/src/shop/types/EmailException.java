package shop.types;

import org.webmacro.as.ConversionException;

/**
 * Thrown on bad e-mail format.
 */
public class EmailException extends ConversionException {
    public EmailException(String message, String email) {
       super(message + ("".equals(email)? ": " + email: ""));
    }
}