package org.webmacro.engine;

import org.webmacro.*;
import org.webmacro.util.HTMLEscaper;
import java.io.IOException;

/**
 * Directive that escape non-HTML characters properly in variables.
 * You can use this variable to set up a filter for a variable and
 * its sub-properties to change all non-HTML characters into their
 * correct encoded form (like quotation marks to &amp;quot;).
 * <br>
 * You can use the directive in your templates like this:<br>
 * <code>
 * #htmlescape $a
 * </code>
 * This will escape non-HTML characters in $a and all sub-properties
 * like $a.b, $a.b.c and so on.
 * @author skanthak@muehlheim.de
 **/
public abstract class EscapeDirective implements Directive {
    static final Filter _filter = new EscapeFilter();
    
    /**
     * Build this directive.
     * This will return null to indicate that no output
     * is desired. Instead, it sets up the correct filter
     * for subject. It expects subject to be a variable.
     * @param bc build context to build against
     * @param subject variable to html escape
     * @exception BuildException if subject is not an instance of Variable
     **/
    public static Object build(BuildContext bc,Object subject) 
        throws BuildException {
        Variable v;
        try {
            v = (Variable) subject;
        } catch (ClassCastException e) {
            throw new BuildException(
                                     "Escape directive takes a variable as its argument");
        }
        bc.addFilter(v,_filter);
        return null;
    }

}
    
