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


package org.webmacro.servlet.bean;

import org.xml.sax.*;

/**
  * Provide Template with access to form data.
  */
public class XMLErrorHandler implements ErrorHandler
{
    private int parseErrors = 0;
    
    public int getErrorCount() {
        return parseErrors;
    }
    
    /**
     * Warning.
     */
    public void warning(SAXParseException ex)
    {
        System.err.println("[Warning] "+
            getLocationString(ex)+": "+
            ex.getMessage());
    }

    /**
     * Error.
     */
    public void error(SAXParseException ex)
    {
        System.err.println("[Error] "+
            getLocationString(ex)+": "+
            ex.getMessage());
        parseErrors++;
    }

    /**
     * Fatal error.
     */
    public void fatalError(SAXParseException ex) throws SAXException
    {
        System.err.println("[Fatal Error] "+
            getLocationString(ex)+": "+
            ex.getMessage());
        throw ex;
    }

    //
    // Private methods
    //
    /**
     * Returns a string of the location.
     */
    private String getLocationString(SAXParseException ex)
    {
        StringBuffer str = new StringBuffer();

        String systemId = ex.getSystemId();
        if (systemId != null)
        {
            int index = systemId.lastIndexOf('/');
            if (index != -1)
                systemId = systemId.substring(index + 1);
            str.append(systemId);
        }
        str.append(':');
        str.append(ex.getLineNumber());
        str.append(':');
        str.append(ex.getColumnNumber());

        return str.toString();
    }



}


