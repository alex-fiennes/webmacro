package org.webmacro.util;

/**
 * Eval.java - static class with one method eval which just returns its
 * argument.  This is a workaround for a WebMacro syntax issue, where an
 * expression isn't recognized properly outside of a directive or method call.
 * The eval() function can be configured in the WebMacro.properties file, e.g.:
 * <pre>
 *    functions.eval=org.webmacro.util.Eval.eval
 * </pre>
 * @since May 6, 2003
 */
public class Eval
{

    /** Private constructor for a static class */
    private Eval ()
    {
    }

    static public Object eval (Object o)
    {
        return o;
    }
}
