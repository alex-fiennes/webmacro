package org.webmacro.resource;

import java.util.*;
import java.io.*;
import org.webmacro.util.*;
import org.webmacro.*;
import org.webmacro.engine.*;
import java.net.*;

/**
 * FileTemplate objects read their template data from a text file.
 */

public class URLTemplate extends WMTemplate
{
    public static final String RCS = "@(#) $Id$";
    /**
     * The location of the resourse (file) this template was read from.
     */
    private  URL _url;
    private  String _encoding;
    private  File underLyingFile = null;
    
    /**
     * Instantiate a template based on the specified file
     */
    public URLTemplate(
        Broker broker,
        URL templateURL, 
        String encoding)
    {
        super(broker);
        _url = templateURL;
        _encoding = encoding;
        String _u = _url.toExternalForm();
        if (_u.startsWith("file:")) {
            underLyingFile = new File(_u.substring(5));
        }
        else if (_u.startsWith("jar:")) {
            int p = _u.indexOf("!");
            underLyingFile = new File(_u.substring(4,p));
        }
    }


    /**
     * Get the stream the template should be read from. Parse will
     * call this method in order to locate a stream.
     * 
     */
    protected Reader getReader() throws IOException
    {
        return new BufferedReader(
            new InputStreamReader(
                _url.openStream()
                ,_encoding
            ));
    }

    /**
     * return the URL for the current template.
     */
    public URL getURL()
    {
        return _url;
    }

    /**
     * Return a name for this template. For example, if the template reads
     * from a file you might want to mention which it is--will be used to
     * produce error messages describing which template had a problem.
     */
    public String toString()
    {
        return "URLTemplate:" + _url;
    }
    
    public File getUnderlyingFile() {
        return underLyingFile;
    }
}
