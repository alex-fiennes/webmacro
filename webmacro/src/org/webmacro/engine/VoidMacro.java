/*
 * VoidMacro.java
 *
 * Created on March 26, 2001, 12:33 AM
 */

package org.webmacro.engine;

import java.io.IOException;
import org.webmacro.Macro;
import org.webmacro.FastWriter;
import org.webmacro.Context;
import org.webmacro.PropertyException;
import org.webmacro.Log;

/**
 * VoidMacro doesn't output data to the output stream, but will log 
 * a debug message (if debugging is turned on) when either of it's methods
 * are called.<p>
 *
 * In addition, since this is a special-case Macro, and really only used by
 * the PropertyOperator and Variable classes, we have a public static field called
 * <code>instance</code> that will return an already created instance of this
 * guy.  Since it doesn't do anything, we really only need 1 of them around.
 *
 * @author  e_ridge
 * @version
 */
public final class VoidMacro implements Macro
{
    public static final VoidMacro instance = new VoidMacro ();
    
    /**
     * just log a message if debugging is turned on
     */
    public void write (FastWriter out,Context context) throws PropertyException, IOException
    {
        Log log = context.getLog ("engine");
        if (log.loggingDebug())
            log.debug ("VoidMacro: write()");
    }
    
    /**
     * always returns an empty string.<p>
     *
     * Currently, VoidMacro causes #if to always evaluate to true, b/c we don't
     * return null here.  Should this instead return null, or should #if
     * do a check for <code>instanceof VoidMacro</code>?
     *
     * @return an emptry string, always!
     * @exception PropertyException if required data was missing from context
     */
    public Object evaluate (Context context) throws PropertyException
    {
        Log log = context.getLog ("engine");
        if (log.loggingDebug())
            log.debug ("VoidMacro: write()");

        return "";
    }
    
}
