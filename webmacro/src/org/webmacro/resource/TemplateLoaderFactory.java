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

import org.webmacro.Broker;
import org.webmacro.InitException;

/**
 * Interface for template loader factories.
 * Implementations of this interface have to create template loaders
 * based on a string describing the template loader.
 * @author Sebastian Kanthak (sebastian.kanthak@muehlheim.de)
 */
public interface TemplateLoaderFactory {

   /**
    * Create a template loader
    * @param b broker to use
    * @param config string describing template loader to construct
    * @return template loader
    * @exception InitException if template loader could not be constructed
    */
   TemplateLoader getTemplateLoader(Broker b, String config) throws InitException;
}
