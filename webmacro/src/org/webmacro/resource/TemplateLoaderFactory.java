package org.webmacro.resource;

import org.webmacro.*;

public interface TemplateLoaderFactory {
    TemplateLoader getTemplateLoader(Broker b,String config) throws InitException;
}
