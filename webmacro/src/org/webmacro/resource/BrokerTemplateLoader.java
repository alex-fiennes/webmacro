package org.webmacro.resource;

import org.webmacro.*;
import java.net.URL;

public class BrokerTemplateLoader extends AbstractTemplateLoader {

    public void setConfig(String config) {
        // ignore config
        if (config != null && config.length() > 0) {
            if (log.loggingWarning()) {
                log.warning("BrokerTemplateProvider: Ignoring configuration options "+config);
            }
        }
    }

    public final Template load(String query,CacheElement ce) throws ResourceException {
        URL url = broker.getTemplate(query);
        if (url != null && log.loggingDebug()) {
            log.debug("BrokerTemplateLoader: Found Template "+url.toString());
        }
        return (url != null) ? helper.load(url,ce) : null;
    }
}
