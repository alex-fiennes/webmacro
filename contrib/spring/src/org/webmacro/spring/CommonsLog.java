package org.webmacro.spring;

import org.webmacro.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation of WebMacro Log interface that logs to Jakarta Commons Logging.
 * This will prefix the webmacro "type" with "org.webmacro". The log level
 * "notice" does not have an equivalent and is mapped to the log level "info".
 * @author Sebastian Kanthak
 */
public class CommonsLog implements Log {
    public static final String WEBMACRO_PREFIX = "org.webmacro.";
    private final org.apache.commons.logging.Log log;

    public CommonsLog(String type) {
        this.log = LogFactory.getLog(WEBMACRO_PREFIX+type);
    }

    public void debug(String msg, Throwable e) {
        log.debug(msg,e);
    }

    public void debug(String msg) {
        log.debug(msg);
    }

    public void info(String msg) {
        log.info(msg);
    }

    public void notice(String msg) {
        log.info(msg);
    }

    public void warning(String msg, Throwable e) {
        log.warn(msg,e);
    }

    public void warning(String msg) {
        log.warn(msg);
    }

    public void error(String msg) {
        log.error(msg);
    }

    public void error(String msg, Throwable e) {
        log.error(msg,e);
    }

    public boolean loggingDebug() {
        return log.isDebugEnabled();
    }

    public boolean loggingInfo() {
        return log.isInfoEnabled();
    }

    public boolean loggingNotice() {
        return log.isInfoEnabled();
    }

    public boolean loggingWarning() {
        return log.isWarnEnabled();
    }
}
