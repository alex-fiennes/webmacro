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


package org.webmacro.servlet;

import org.webmacro.Context;
import org.webmacro.ContextTool;
import org.webmacro.PropertyException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpUtils;

/**
 * Provide Template with access to url handing routines.
 * @author Sebastian Kanthak (mailto:skanthak@muehlheim.de)
 */
public class URLTool extends ContextTool
{

    /**
     * Tool implementation.
     */
    public class URLToolImpl
    {

        private final WebContext context;

        public URLToolImpl (WebContext context)
        {
            super();
            this.context = context;
        }

        /**
         * Get the url that was used to access this page,
         * as it is returned by HttpUtils.getRequestURL.
         * @return URL used to access this page without query string
         */
        public String getRequestURL ()
        {
            return HttpUtils.getRequestURL(context.getRequest()).toString();
        }

        /**
         * Return the complete url, that was used to access this page
         * including path_info and query_string if present.
         * <br>
         * This method uses HttpUtils.getRequestURL to
         * create the url and appends query_string if not null
         * @return URL used to access this page, including query string
         */
        public String getCompleteRequestURL ()
        {
            HttpServletRequest req = context.getRequest();
            StringBuffer b = HttpUtils.getRequestURL(req);
            String query = req.getQueryString();
            if (query != null)
            {
                b.append("?");
                b.append(query);
            }
            return b.toString();
        }

        public String encode (String url)
        {
            return context.getResponse().encodeURL(url);
        }
    }

    public Object init (Context context) throws PropertyException
    {
        try
        {
            WebContext wc = (WebContext) context;
            return new URLToolImpl(wc);
        }
        catch (ClassCastException ce)
        {
            throw new PropertyException("URLTool only works with WebContext", ce);
        }
    }

}
