/*
 * Copyright (C) 1998-2000 Semiotek Inc.  All Rights Reserved.  
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted under the terms of either of the following
 * Open Source licenses:
 *
 * The GNU General Public License, version 2, or any later version, as
 * published by the Free Software Foundation
 * (http://www.fsf.org/copyleft/gpl.html);
 *
 *  or 
 *
 * The Semiotek Public License (http://webmacro.org/LICENSE.)  
 *
 * This software is provided "as is", with NO WARRANTY, not even the 
 * implied warranties of fitness to purpose, or merchantability. You
 * assume all risks and liabilities associated with its use.
 *
 * See www.webmacro.org for more information on the WebMacro project.  
 */


package org.webmacro.resource;

import org.webmacro.*;
import org.webmacro.util.Settings;
import java.util.List;
import java.util.ArrayList;

/**
 * Alternative implementation of a TemplateProvider that uses TemplateLoader to do the actual work.
 * This template loader controls a list of TemplateLoaders to do the actual work of loading
 * a template. It asks the template loaders one by one, until a template is found or the end of
 * the list is reached. It is configured by the "TemplatePath" setting in WebMacro.properties.<br>
 * <br>
 * Different template loaders are separated by colons (":"). Each entry consists of a key for
 * the template loader in smaller/greater signs ("<>") and a path, that is passed directly to
 * the template loader. For each key, a "TemplateLoader.key" setting must give the fully qualified
 * classname of the template load to be used for this key. If no key is given, "default" is assumed
 * as the value for key.<br>
 * Example configuration:<br>
 * <pre>
 * TemplatePath=.:&lt;classpath&gt;:&lt;webapp&gt;/WEB-INF/templates/
 * TemplateLoader.default=org.webmacro.resource.FileTemplateLoader
 * TemplateLoader.classpath=org.webmacro.resource.ClassPathTemplateLoader
 * TemplateLoader.webapp=org.webmacro.resource.ServletContextTemplateLoader
 * </pre>
 * This configuration will search for templates at three locations in this order:
 * <ol>
 * <li>The current directory (".")
 * <li>The classpath ("&lt;classpath&gt;")
 * <li>The directory WEB-INF/templates/ in the web-app directory ("&lt;webapp&gt;/WEB-INF/templates/")
 * </ol>
 * Note, that this setup only makes sense in a web-app environment, because the webapp template loader
 * won't work otherwise.
 * @author Sebastian Kanthak (skanthak@muehlheim.de)
 */
public class DelegatingTemplateProvider extends CachingProvider {
    private Broker broker;
    private Log log;
    private TemplateLoaderFactory factory;
    private TemplateLoader[] templateLoaders;

    public void init(Broker broker,Settings config) throws InitException {
        super.init(broker,config);
        this.broker = broker;
        log = broker.getLog("resource","DelegatingTemplateProvider");

        String factoryClass = config.getSetting("TemplateLoaderFactory","");
        log.info("DelegatingTemplateProvider: Using TemplateLoaderFactory "+factoryClass);
        factory = createFactory(factoryClass);

        List loaders = new ArrayList();
        int i = 0;
        String loader = config.getSetting("TemplateLoaderPath.".concat(String.valueOf(i+1)));
        while (loader != null) {
            loaders.add(factory.getTemplateLoader(broker,loader));
            i++;
            loader = config.getSetting("TemplateLoaderPath.".concat(String.valueOf(i+1)));
        }
        templateLoaders = new TemplateLoader[loaders.size()];
        loaders.toArray(templateLoaders);
    }
    
    public String getType() {
        return "template";
    }

    /**
     * Ask all template loaders to load a template from query.
     * Returns the template from the first provider, that returns a non-null value
     * or throws a NotFoundException, if all providers return null.
     */
    public Object load(String query,CacheElement ce) throws ResourceException {
        for (int i=0; i < templateLoaders.length; i++) {
            Template t = templateLoaders[i].load(query,ce);
            if (t != null) {
                return t;
            }
        }
        throw new NotFoundException("Could not locate template "+query);
    }

    protected TemplateLoaderFactory createFactory(String classname) throws InitException {
        try {
            return (TemplateLoaderFactory)Class.forName(classname).newInstance();
        } catch (ClassNotFoundException e) {
            throw new InitException("Class "+classname+" for template loader factory not found",e);
        } catch (InstantiationException e) {
            throw new InitException("Could not instantiate class "+classname+" for template loader factory",e);
        } catch (IllegalAccessException e) {
            throw new InitException("Could not instantiate class "+classname+" for template loader facory",e);
        } catch (ClassCastException e) {
            throw new InitException("Class "+classname+" for template loader factory does not implement "+
                                    "interface org.webmacro.resource.TemplateLoaderFactory",e);
        }
    }
}
