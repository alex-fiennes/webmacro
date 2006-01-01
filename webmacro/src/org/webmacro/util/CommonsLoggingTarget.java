package org.webmacro.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;

/**
 *
 * Not sure what the log level mappings should be here but going with:
 *
 * WM Debug -> Commons Debug
 * WM Warning -> Commons Warn
 * WM Info -> Commons Trace
 * WM Notice -> Commons Info
 * WM Error -> Commons Error
 * @author Marc Palmer (marc@anyware.co.uk)
 */
public class CommonsLoggingTarget implements LogTarget
{
    static final Log log = LogFactory.getLog(CommonsLoggingTarget.class);

    public void log(Date date, String type, String level, String message, Throwable e)
    {
        switch (LogSystem.getLevel(level))
        {
            case LogSystem.DEBUG:
                log.debug( message, e);
                break;
            case LogSystem.ERROR:
                log.error( message, e);
                break;
            case LogSystem.INFO:
                log.trace(message, e);
                break;
            case LogSystem.NOTICE:
                log.info(message, e);
                break;
            case LogSystem.WARNING:
                log.warn(message, e);
                break;
        }
    }

    public void flush()
    {
        // Don't think this applies to commons logging
    }

    public boolean subscribe(String category, String type, int logLevel)
    {
        boolean sub = false;
        switch (logLevel)
        {
            case LogSystem.DEBUG:
                sub = log.isDebugEnabled();
                break;
            case LogSystem.ERROR:
                sub = log.isErrorEnabled();
                break;
            case LogSystem.INFO:
                sub = log.isTraceEnabled();
                break;
            case LogSystem.NOTICE:
                sub = log.isInfoEnabled();
                break;
            case LogSystem.WARNING:
                sub = log.isWarnEnabled();
                break;
        }
        return sub;
    }

    public void addObserver(LogSystem ls)
    {
        // Don't think this applies to commons logging
    }

    public void removeObserver(LogSystem ls)
    {
        // Don't think this applies to commons logging
    }
}
