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
public abstract class HTMLEscapeDirective implements Directive {
    static final Filter _filter = new HTMLEscapeFilter();
    
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
                                     "HTMLEscape directive takes a variable as its argument");
        }
        bc.addFilter(v,_filter);
        return null;
    }

    /**
     * Filter factory, that defines recursive behaviour
     * and provides filter macro.
     **/
    static class HTMLEscapeFilter implements Filter {
        /**
         * Return this to indicate that we will recursively
         * filter sub-properties.
         * @param name name of property to filter
         * @return this
         **/
        public Filter getFilter(String name) {
            return this;
        }

        /**
         * Return a macro for filtering.
         * This will return a macro, that filters
         * source through HTMLEscaper.escape() method.
         * @param source macro to html escape
         * @return filter macro for source
         **/
        public Macro getMacro(Macro source) {
            return new HTMLEscapeFilterMacro(source);
        }
    }

    
    /**
     * Filter macro that html escapes another macro.
     * This macro uses HTMLEscaper's escape method
     * to filter another macro. It will evaluate
     * the source macro, call toString() on the
     * resulting object and return the escaped string.
     **/
    static class HTMLEscapeFilterMacro implements Macro {
        final private Macro _m;

        /**
         * Create a new filter macro filtering m
         * @param m source macro to filter
         **/
        public HTMLEscapeFilterMacro(Macro m) {
            _m = m;
        }

        /**
         * Evaluate this macro.
         * This will be done by first evaluating
         * the source macro, calling toString() on
         * the resulting object and return the escaped
         * string.
         * @param c context to evaluate against
         * @return html escaped string
         **/
        public Object evaluate(Context c) throws ContextException {
            Object o = _m.evaluate(c);
            return (o != null) ? HTMLEscaper.escape(o.toString()) : null;
        }

        /**
         * Write evaluated object to out.
         * @param out writer to write to
         * @param c context to evaluate against
         **/
        public void write(FastWriter out,Context c)
            throws IOException,ContextException {
            out.write(evaluate(c).toString());
        }
    }
}
    
