package org.webmacro.spring;

import org.springframework.beans.factory.*;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.DefaultResourceLoader;
import org.webmacro.WM;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Properties;

/**
 * Factory exposes a WebMacro WM object based on a SpringBroker.
 * The broker can be configured via the "wmProperties" property.
 * The WM object returned by this factory is a singleton and thus
 * will be shared by all clients. The broker will be destroyed and
 * its resources freed when the surrounding context is closed.
 *
 * You can configure the WebMacro broker with two properties
 * <ul>
 * <li>You can set a <code>templatePrefix</code> that will be used
 * to resolve templates. For example, "WEB-INF/templates" may be suitable
 * for web applications.</li>
 * <li>Via the <code>wmProperties</code> property, you can configure WebMacro
 * in the same way you usually do via a "WebMacro.properties" file.</li>
 * </ul>
 * @author Sebastian Kanthak
 */
public class WMFactoryBean implements FactoryBean, ResourceLoaderAware, InitializingBean,
        BeanNameAware, DisposableBean {
    private SpringBroker broker;
    private WM wm;
    private ResourceLoader rl = new DefaultResourceLoader();
    private Properties wmProperties;
    private String templatePrefix;
    private String name;

    private static Log log = LogFactory.getLog(WMFactoryBean.class);

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.rl = resourceLoader;
    }

    /**
     * Set the configuration properties for WebMacro
     * @param wmProperties configuration properties
     */
    public void setWmProperties(Properties wmProperties) {
        this.wmProperties = wmProperties;
    }

    /**
     * Sets the prefix for resolving templates. All request for
     * templates will be prefixed by this path.
     * @param templatePrefix prefix for resolving templates
     */
    public void setTemplatePrefix(String templatePrefix) {
        this.templatePrefix = templatePrefix;
    }

    public void setBeanName(String s) {
        this.name = s;
    }

    public void afterPropertiesSet() throws Exception {
        if (wmProperties == null) wmProperties = new Properties();
        log.info("Creating new WebMacro Broker");
        broker = new SpringBroker("org.webmacro.spring.WMFactoryBean:"+name,wmProperties,rl);
        if (templatePrefix != null) {
            broker.setTemplatePrefix(templatePrefix);
        }
        broker.initialize();
        wm = new WM(broker);
    }

    public Object getObject() throws Exception {
        return wm;
    }

    public boolean isSingleton() {
        return true;
    }

    public Class getObjectType() {
        return WM.class;
    }

    public void destroy() {
        log.info("Destroying WebMacro Broker");
        broker.destroy();
    }
}
