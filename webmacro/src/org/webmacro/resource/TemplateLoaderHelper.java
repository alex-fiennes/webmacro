package org.webmacro.resource;

import org.webmacro.*;
import org.webmacro.util.Settings;
import org.webmacro.engine.FileTemplate;
import org.webmacro.engine.StreamTemplate;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class TemplateLoaderHelper {
    private Broker broker;
    private ReloadDelayDecorator reloadDelay;
    
    public TemplateLoaderHelper() {
        super();
    }

    public void init(Broker b,Settings config) throws InitException {
        this.broker = b;
        reloadDelay = new ReloadDelayDecorator();
        reloadDelay.init(b,config);
    }

    public Template load(File file,CacheElement ce) throws ResourceException {
        Template t = new FileTemplate(broker,file);
        if (ce != null) {
            CacheReloadContext reloadContext = new FTReloadContext(file,file.lastModified());
            ce.setReloadContext(reloadDelay.decorate("file",reloadContext));
        }
        return t;
    }

    public Template load(URL url,CacheElement ce) throws ResourceException {
        if (url.getProtocol().equals("file")) {
            // handle files directly, because it is more efficient
            File file = new File(url.getFile());
            return load(file,ce);
        } else {
            try {
                URLConnection conn = url.openConnection();
                long lastMod = conn.getLastModified();
                String encoding = conn.getContentEncoding();
                // encoding may be null. Will be handled by StreamTemplate
                Template t = new StreamTemplate(broker,conn.getInputStream(),encoding);
                t.setName(url.toExternalForm());
                if (ce != null) {
                    CacheReloadContext reloadContext = new UrlReloadContext(url,lastMod);
                    ce.setReloadContext(reloadDelay.decorate(url.getProtocol(),reloadContext));
                }
                return t;
            } catch (IOException e) {
                throw new InvalidResourceException("IOException while reading template from "+url,e);
            }
        }
    }

    /** 
     * ReloadContext for file templates.  Uses last-modified to determine
     * if resource should be reloaded.
     */
    private static class FTReloadContext extends CacheReloadContext {
        private File file;
        private long lastModified;

        public FTReloadContext(File f, long lastModified) {
            this.file = f;
            this.lastModified = lastModified;
        }

        public boolean shouldReload() {
            return (lastModified != file.lastModified());
        }
    }

    private static class UrlReloadContext extends CacheReloadContext { 
        private long lastModified;
        private URL url;
        
        public UrlReloadContext(URL url, long lastModified) {
            this.url = url;
            this.lastModified = lastModified;
        }

        public boolean shouldReload() {
            return (lastModified != UrlProvider.getUrlLastModified(url));
        }
    }
}
