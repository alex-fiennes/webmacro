/*
 * Copyright (c) 1998, 1999 Semiotek Inc. All Rights Reserved.
 *
 * This software is the confidential intellectual property of
 * of Semiotek Inc.; it is copyrighted and licensed, not sold.
 * You may use it under the terms of the GNU General Public License,
 * version 2, as published by the Free Software Foundation. If you 
 * do not want to use the GPL, you may still use the software after
 * purchasing a proprietary developers license from Semiotek Inc.
 *
 * This software is provided "as is", with NO WARRANTY, not even the 
 * implied warranties of fitness to purpose, or merchantability. You
 * assume all risks and liabilities associated with its use.
 *
 * See the attached License.html file for details, or contact us
 * by e-mail at info@semiotek.com to get a copy.
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
