package org.webmacro.resource;

import org.webmacro.*;

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
