package org.webmacro.spring;

import org.webmacro.Broker;
import org.webmacro.InitException;
import org.webmacro.Log;
import org.webmacro.Provider;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.Resource;

import java.util.Properties;
import java.util.Iterator;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;

/**
 * WebMacro broker for usage in a spring environment.
 * Will load resources via spring's resource loader
 * and use jakarat's commong logging system as this is
 * the same spring uses.
 *
 * Although it can be created directly, it is normally only
 * used internally by <code>WMFactoryBean</code> to provide
 * a <code>WM</code> instance.
 * @author Sebastian Kanthak
 */
public class SpringBroker extends Broker {
    private final ResourceLoader rl;
    private String templatePrefix;

    public SpringBroker(String name,Properties properties,ResourceLoader rl)
            throws InitException {
        super(null,name);
        this.rl = rl;
        _log = getLog("broker");
        loadDefaultSettings();
        loadSettings(properties);
        loadSystemSettings();
        _log.notice("Loaded settings from "+WEBMACRO_DEFAULTS+
                ", (applicationContext properties), (System Properties)");
    }

    public void setTemplatePrefix(String templatePrefix) {
        if (!templatePrefix.startsWith("/")) {
            templatePrefix = "/"+templatePrefix;
        }
        if (templatePrefix.endsWith("/")) {
            templatePrefix = templatePrefix.substring(0,templatePrefix.length()-1);
        }
        this.templatePrefix = templatePrefix;
    }

    protected void initLog() {
        // do nothing to prevent initialization of WebMacro log system
    }

    /**
     * Returns a log using jakarta commons logging. The description is ignored.
     */
    public Log getLog(String type, String description) {
        return getLog(type);
    }

    public Log getLog(String type) {
        return new CommonsLog(type);
    }

    public URL getResource(String r) {
        URL url=null;
        try {
            Resource res = rl.getResource(r);
            if (res != null) {
                url = res.getURL();
            }
        } catch (IOException e) {
            _log.error("Could not load resource",e);
        }
        return url;
    }

    public InputStream getResourceAsStream(String s) {
        InputStream is=null;
        try {
            Resource res = rl.getResource(s);
            if (res != null) is = res.getInputStream();
        } catch (IOException e) {
            _log.error("Could not load resource as stream",e);
        }
        return is;
    }

    public URL getTemplate(String template) {
        if (!template.startsWith("/")) {
            template = "/"+template;
        }
        if (templatePrefix != null) {
            template = templatePrefix + template;
        }
        return getResource(template);
    }

    /**
     * Initialize the broker when all properties are set.
     * This is an adapter method for the spring framework, so
     * that the Broker isn't already initialized in the constructor,
     * but only after all properties are set.
     * @throws InitException if initializiation fails
     */
    void initialize() throws InitException {
        init();
    }

    /**
     * Destroy this broker and deallocate all possibly used resources.
     */
    public void destroy() {
        for (Iterator i=_providers.values().iterator(); i.hasNext();) {
            ((Provider)i.next()).destroy();
        }
    }


}
