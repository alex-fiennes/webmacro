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

/**
 * Default implementation of TemplateLoaderFactory interface.
 * This implementation expects config strings to be in an url-like
 * format: [protocol:][path]. It will then look for a key in the
 * WebMacro configuration of the form "TemplateLoader.&lt;protocol&gt;", where
 * protocol is replaced by "default", if it is ommited in the url. The
 * value of this configuration option has to be a fully qualified class
 * name of a class with a no-args constructor, implementing the TemplateLoader
 * interface.
 * <br>
 * After instantiating an object of this class, "path" is passed as configuration
 * option to this object.
 * @author Sebastian Kanthak (sebastian.kanthak@muehlheim.de)
 */
public class DefaultTemplateLoaderFactory implements TemplateLoaderFactory {
    public TemplateLoader getTemplateLoader(Broker b,String config) throws InitException {
        String protocol;
        String options;
        int pos = config.indexOf(":");
        if (pos == -1) {
            protocol = "default";
            options = config;
        } else {
            protocol = config.substring(0,pos);
            if (pos + 1 < config.length()) {
                options = config.substring(pos+1);
            } else {
                options = "";
            }
        }

        String classname = b.getSetting("TemplateLoader.".concat(protocol));
        if (classname == null || classname.length() == 0)
            throw new InitException("No class found for template loader protocol "+protocol);
            
        try {
            TemplateLoader loader = (TemplateLoader)Class.forName(classname).newInstance();
            loader.init(b,b.getSettings());
            loader.setConfig(options);
            return loader;
        } catch (ClassNotFoundException e) {
            throw new InitException("Class "+classname+" for template loader "+protocol+" not found",e);
        } catch (InstantiationException e) {
            throw new InitException("Could not instantiate class "+classname+" for template loader "+protocol,e);
        } catch (IllegalAccessException e) {
            throw new InitException("Could not instantiate class "+classname+" for template loader "+protocol,e);
        } catch (ClassCastException e) {
            throw new InitException("Class "+classname+" for template loader"+protocol+" does not implement "+
                                    "interface org.webmacro.resource.TemplateLoader",e);
        }
    }
}
