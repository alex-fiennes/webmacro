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


package org.webmacro;


public final class WMConstants 
{
    public final static String TEMPLATE_OUTPUT_ENCODING = "TemplateOutputEncoding";
    public final static String TEMPLATE_INPUT_ENCODING = "TemplateEncoding";
    public final static String TEMPLATE_LOCALE = "TemplateLocale";

    /**
     * name of the local properties file.  Only limited properties are 
     * read from this (currently to do with file encodings)
     */
    public static final String WEBMACRO_LOCAL_FILE = "WebMacro.local";

    /**
     * name of the default properties file
     */
    public static final String WEBMACRO_DEFAULTS_FILE = "WebMacro.defaults";

    /**
     * name of the properties file
     */
    public static final String WEBMACRO_PROPERTIES_FILE = "WebMacro.properties";
   
}
