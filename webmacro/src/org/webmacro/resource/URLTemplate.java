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
    /**
     * CVS revision
     */
    public static final String RCS = "@(#) $Id$";
    /**
     * key to extract from local properties file (and WM global settings)
     * to determine the encoding
     */
    private static final String TEMPLATE_ENCODING_KEY = "TemplateEncoding";
    /**
     * name of local properties file
     */
    private static final String WEBMACRO_LOCAL_FILE = "WebMacro.local";
    /**
     * The location of the resourse (file) this template was read from.
     */
    private final URL _url;
    /**
     * The physical file referred to by "file:" and "jar:" URLs
     */
    private File underLyingFile = null;
    /**
     * The time the underlying file was last modified
     */
    private long underLyingFileLastModTime = 0;
    /**
     * Cache for any per-directory encoding files
     */
    private final HashMap encodingCache = new HashMap();
    
    /**
     * A dummy object used as a place holder in the encoding cache
     */

    private static final Object dummy = new Object();
    /**
     * Instantiate a template based on the specified file
     *
     * We use can use the special case or URLs like
     *
     *        file:xxxxxx
     * or
     *        jar:xxxxxx!yyyyyy
     *
     * extracting the xxxxxx.  In the latter case the reference is to the jar containing the
     * template.
     */
    public URLTemplate(
        Broker broker,
        URL templateURL
        )
    {
        super(broker);
        _url = templateURL;
        String _u = _url.toExternalForm();

        if (_u.startsWith("jar:"))
        {
            int p = _u.indexOf("!");
            _u = _u.substring(4,p);
        }

        if (_u.startsWith("file:"))
        {
            underLyingFile = new File(_u.substring(5));
            underLyingFileLastModTime = underLyingFile.lastModified();
        }
    }

    /**
     * URL based templates are difficult to detect as changed in general, but the two
     * special cases for "file:[path]" and "jar:file:[jarpath]![path]" mean we have reference
     * to the actual file
     */
    public boolean shouldReload()
    {
        if (underLyingFile == null) return false;
        long lastMod = underLyingFile.lastModified();

        return ((lastMod > 0) && (lastMod> underLyingFileLastModTime));
    }
    /**
     * return the default encoding either from the WebMacro config
     * or the JVM settings
     *
     * Note for Unix users: you may need to set the environmental variable
     * LC_ALL=[locale] to get the default one set up.
     */
    private final String getDefaultEncoding()
    {
        try
        {
            return(String) _broker.get("config", TEMPLATE_ENCODING_KEY);
        }
        catch (Exception e)
        {
            return System.getProperty("file.encoding");
        }
    }

    /**
     * Look for TemplateEncoding in a file WebMacro.local in the same
     * directory as the template 
     *
     * TODO - should make the encoding cache a bit smarter- doesn't detect
     * if the file changes
     */
     
    private String getEncodingFromFile()
    throws IOException
    {
        InputStream is = null;
        URL u = null;

        try
        {
            u = new URL(_url, WEBMACRO_LOCAL_FILE);
            _log.debug("Looking for encodings file: "+u);
            Object obj = encodingCache.get(u);

            if (obj == dummy) 
            {
                return null;
            }
            else if (obj != null)
            {
                return (String) obj;
            } 

            Properties p = new Properties();
            is = u.openStream();
            p.load(is);
            String enc = p.getProperty(TEMPLATE_ENCODING_KEY);
            _log.debug("Read encoding as "+enc);
            encodingCache.put(u,enc);
            return enc;
        }
        catch (Exception e)
        {
            // if there's an Exception here, put a dummy object here so
            // we don't try to look for the file every time
            //
            if (u != null) {
                encodingCache.put(u, dummy);
            }
            return null;
        }
        finally {
            if (is != null)
            {
                is.close();
            }
        }
    }


    /**
     * Get the stream the template should be read from. Parse will
     * call this method in order to locate a stream.
     *
     */
    protected Reader getReader() throws IOException
    {
        // Hmmm - not sure what the "natural" ordering of these is
        // 

        String _encoding  = getEncodingFromFile();

        if (_encoding==null)
        {
            _log.debug("No encoding detected - using default");
            _encoding = getDefaultEncoding();
        }
        _log.debug("Using encoding "+_encoding);

        if (_encoding.equals("native_ascii"))
        {
            return new NativeAsciiReader(
                new InputStreamReader(_url.openStream(),"ASCII"));
        }
        else
        {
            return new BufferedReader(
                new InputStreamReader(_url.openStream(),_encoding));
        }
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
}
