package org.webmacro.resource;

import org.webmacro.*;
import org.webmacro.util.Settings;

import java.util.*;

/**
 * Utility class to handle creation of TimedReloadContext.<br>
 * TimedReloadContext objects are used to prevent a cached resource
 * of being checked for modification all the time, but only checks
 * for in given intervals.
 * <br>
 * This class stores a mapping of protocol types to delay values.
 * When a provider requests to decorate a reload context it looks up
 * the delay for this protocol and eventually creates a suitable
 * TimedReloadContext.
 * @author Sebastian Kanthak
 */
public class ReloadDelayDecorator {
    /** maps protocol types to Long objects */
    private Map reloadDelays;
    private long defaultDelay;
    private Log log;
    
    public ReloadDelayDecorator() {
        reloadDelays = new HashMap(11);
    }

    /**
     * Initialize object.
     * Reads config settings from key "CheckForReloadDelay"
     */
    public void init(Broker b,Settings config) throws InitException {
        defaultDelay = 0; // no delay
        config.processListSetting("CheckForReloadDelay",
                                  new Settings.ListSettingHandler() {
                                          public void processSetting(String key,String value) {
                                              if (key == null || key.length() == 0) {
                                                  // default reloadDelay
                                                  defaultDelay = Long.parseLong(value);
                                              } else {
                                                  reloadDelays.put(key,Long.valueOf(value));
                                              }
                                          }
                                      });
        log = b.getLog("resource","ReloadDelayDecorator");
    }

    /**
     * Looks up the "check for reload delay" for protocol and creates
     * a suitable TimedReloadContext or passes back the original
     * reload context if delay <= 0
     * @param protocol protocl to look up delay for
     * @param reloadContext reloadContext to decorate
     * @return eventually decorated reload context
     */
    public CacheReloadContext decorate(String protocol,
                                       CacheReloadContext reloadContext) {
        long delay;
        Long l;
        synchronized (reloadDelays) {
            l = (Long)reloadDelays.get(protocol);
        }
        delay = (l != null) ? l.longValue() : defaultDelay;
        if (delay > 0) {
            log.debug("Returning timed reload context with delay "+delay);
            return new TimedReloadContext(reloadContext,delay);
        } else {
            log.debug("Returning unmodified reload context");
            return reloadContext;
        }
    }
}
