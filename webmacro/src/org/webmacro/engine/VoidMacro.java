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
 * VoidMacro does absolutely nothing.  No log output, no FW output.<p>
 *
 * It does log a message in each method if Debugging is turned on
 * @author  e_ridge
 * @version
 */
public final class VoidMacro implements Macro
{
    
    
    /**
     * just log a message
     */
    public void write (FastWriter out,Context context) throws PropertyException, IOException
    {
        Log log = context.getLog ("engine");
        if (log.loggingDebug())
            log.debug ("VoidMacro: write()");
    }
    
    /**
     * always returns an empty string
     * @return an emptry string, always!
     * <p>
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
