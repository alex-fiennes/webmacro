package org.webmacro.resource;

import org.webmacro.*;
import org.webmacro.util.*;
import org.webmacro.servlet.*;
import org.webmacro.resource.*;
import java.util.*;
import java.io.*;
import java.net.*;

/**
 *
 * <H2>URLTemplateProvider<H2>
 *
 * This is a "drop-in" replacement for the standard TemplateProvider in the
 * WebMacro distribution.  The primary benefit is to allow template to be loaded
 * by a variety of means, without requiring an absolute path.  This should make
 * applications more portable.
 *
 * <h3>TemplatePath</h3>
 *
 *     <i>TemplatePath=path1[;path2;....]</i>
 * <p>
 * Each path should be a full URL specification or one of the special cases 
 * listed below.  If the path cannot be interpreted, it will be tried as file:path
 *
 * <h3>Special cases</h3>
 *
 * <ul>
 *     <li><i>TemplatePath=classpath:/templates</i>
 *         Template will be loaded from the classpath.   Each directory on the
 * classpath will be tried in turn to locate the template.
 *
 *     <li><i>TemplatePath=ignore:</i>
 * Don't use the template path. All templates will be referenced
 * explicitly as full URL's.  A common case would be to generate a base URL
 * at runtime (e.g., from ServletContext.getResource("/") in JSDK 2.2+) and to 
 * prepend this to the template path.
 *          
 *     <li><i>TemplatePath=context:</i>
 *         reserved for future use (specifically for finding templates with a
 *        servlet context)
 * </ul>
 * 
 * <h3>Locale support</h3>
 *
 * There is a limited locale based implemention here.  Template paths can contain
 * a string of the form {_aaa_bbb....} which will usually correspond to a 
 * locale such as en_GB. 
 * Thus <i>load("template{_en_GB}.wm")</i> will look for
 * <ul>
 *  <li>template_en_GB.wm</li>
 *  <li>template_en.wm</li>
 *  <li>template.wm</li>
 * </ul>
 *
 * in that order, returning the first one that exists.  This is implemented so that
 * requests for template_en_GB.wm & template_en_US.wm will both return the same template
 * template_en.wm assuming only the latter exists.  In other words, the template is 
 * only parsed once.
 * 
 * @see CachingProvider
 * @see TemplateProvider
 */
final public class URLTemplateProvider extends CachingProvider
{
    public static final String RCS = "@(#) $Id$";

    // INITIALIZATION

    private Broker _broker = null;

    private static String _pathSeparator = ";";
    private String[] _templateDirectory = null;

    /**
     * URLs can contain strings like "{_AAA_BBB...}" to mimic
     * locale handling in ResourceBundles.   
     * I think this can be improved upon - one idea would be to 
     * fix the wm extension and then ask for (resource)+(locale_string).wm
     * We still have the problem of how to pass in the locale info, just as to 
     * pass in the encoding.
     * 
     */

    private static final String _OPEN = "{";
    private static final String _CLOSE = "}";

    private static final String CLASSPATH_PREFIX = "classpath:";
    private static final String CONTEXT_PREFIX   = "context:";
    private static final String IGNORE_PREFIX    = "ignore:";

    private static final String _TYPE    = "template";

    /**
     *  _baseURL is just a placeholder for the moment.
     *  My hope is that we can pass in a context base at construction
     * time (e.g., the servlet context base)
     */
    private URL _baseURL = null;

    /**
     * How long to keep templates cached for (in milliseconds)
     */
    private int _cacheDuration;

    /**
     * The value of TemplatePath in WebMacro inititialization file.
     * This is a semicolon separated string with individual values like
     * <ol>
     * <li> ignore: - ignore completely.  All request will be given by a full URL</li>
     * <li> classpath:[path].  Look for values on the system classpath, optionally 
     * with some relative path. E.g., classpath:/templates/</li>
     * <li> *TODO* context:[path] load relative to some </li>
     * <li> [url] - look for templates relative to this location</li>
     * <li> [path] - Equivalent to file:[path]</li>
     * </ol>
     */
    private String _templatePath;

    /**
      * Where we write our log messages 
      */
    private Log _log;

    /**
     * Supports the "template" type.  This is a straight replacement for the
     * default TemplateProvider
     */
    final public String getType() {
        return _TYPE;
    }


   /**
    * Create a new TemplateProvider that uses the specified directory
    * as the source for Template objects that it will return
    * 
    * @param b A broker
    * @param config Settings from the webmacro initialization file
    * @exception InitException thrown when the provider fails to initialize
    */
   public void init(Broker b, Settings config) throws InitException
   {
      super.init(b,config);
      _broker = b;
      _log = b.getLog("resource", "general object loading");

      try {
         try {
            String cacheStr = config.getSetting("TemplateExpireTime");
            _cacheDuration = Integer.valueOf(cacheStr).intValue();
         } catch (Exception ee) {
            // use default
         }
         _templatePath = config.getSetting("TemplatePath");
         StringTokenizer st = 
            new StringTokenizer(_templatePath, _pathSeparator);
         _templateDirectory = new String[ st.countTokens() ];
         int i;
         for (i=0; i < _templateDirectory.length; i++) 
         {
            String dir = st.nextToken(); 
            _templateDirectory[i] = dir;
         }

      } catch(Exception e) {
         throw new InitException("Could not initialize: " + e);
      }
   }


   /**
     * Grab a template based on its name, setting the request event to 
     * contain it if we found it.
     */

    final public TimedReference load(String name) throws NotFoundException 
    {
        _log.debug("Load URLTemplate: "+name+ " duration="+_cacheDuration);
        try {
            Template t = loadTemplate(name,_baseURL);
            if (t == null) {
                throw new NotFoundException(
                this + " could not locate " + name + " on path " + _templatePath);
            }
            return new TimedReference(t, _cacheDuration);   
        }
        catch (IOException e) {
            _log.debug(e.getClass().getName()+" "+e.getMessage());
            throw new NotFoundException(e.getMessage());
        }
    }
	 
    /**
      * Always return false.  It is not possible to decide if an object
      * fetched from a URL should be reloaded or not.  Returning false
      * will cause the CachingProvider to load() only when it's cache
      * has expired.
      */
    final public boolean shouldReload (String name) {
    	return false;
    }

    // IMPLEMENTATION


    /**
     * Find the specified template in the directory managed by this
     * template store. Any path specified in the filename is relative
     * to the directory managed by the template store.
     * <p>
     * @param path relative to the current directory fo the store
     * @return a template matching that name, or null if one cannot be found
     */

    final private Template loadTemplate(String path, URL base) throws IOException
    {
        _log.debug("loadTemplate");
        Template _tmpl = null;
        Object _key = null;

        String encoding = System.getProperty("file.encoding");
        if (path.charAt(0) == ':') {
            int fstart = path.indexOf(':', 1);
            encoding = path.substring(1,fstart);
            path = path.substring(fstart + 1);
        }

        if (base == null)
        {
            _key = path;
            _tmpl = getTemplate(path,encoding);
        }
        else
        {
            _key = new URL(base,path);
            _tmpl = getTemplate(path,base,encoding);
        }

        return _tmpl;
    }

    /**
     * load a template relative to the base.
     * @param path the relative or absolute URL-path of the template
     * @param base URL to load path relative to
     * @param encoding the character encoding to use
     */

    final private Template getTemplate(String path, URL base, String encoding)
    {
        _log.debug("get:"+path+"@"+base);
        URLTemplate t;
        String pre = null;
        String mid = null;
        String post = null;
        try
        {
            String[] parts = parseLocalePath(path);
            String urlPath = null;
            if (parts == null)
            {
                urlPath = path;
            }
            else
            {
                pre = parts[0];
                mid = parts[1];
                post = parts[2];
                urlPath = pre+((mid == null) ? "" : mid)+post;
            }
            // try to parse as-is, or relative to the base URL
            // N.B. URL() accepts a null first argument
            URL url = new URL(base,urlPath);

            _log.debug("URLTemplateProvider: loading " + url);
            /*
             * Seems a bit wasteful to check the URL here but t.parse()
             * generates "spurious" errors if the template doesn't exist
             */
            InputStream _is = null;
            try
            {
                _is = url.openStream();
                t = new URLTemplate(_broker,url,encoding);
                _log.debug("**PARSING "+url);
                t.parse();
                return t;
            }
            finally
            {
                if (_is != null) _is.close();
            }
        }
        catch (IOException e)
        {
            _log.debug(e.getClass().getName()+" "+e.getMessage());
            // try the next locale 
            if (mid != null)
            {
                try
                {
                    String p = buildPath(pre,mid,post);
                    if (base != null) {
                        return (Template) _broker.get(
                            _TYPE,
                            join(base.toString(),p));
                    }
                    else
                    {
                        return (Template) _broker.get(_TYPE,p);
                    }
                }
                catch (Exception ex)
                {
                    _log.debug(ex.getClass().getName()+" "+ex.getMessage());
                	// ignore
                }
            }
            _log.error("URLTemplateProvider(1): Could not load template: "+ path,e);
        }
        catch (Exception e)
        {
            _log.error("URLTemplateProvider(2): Could not load template: "+ path,e);
        }
        _log.debug("URLTemplateProvider: " + path + " not found.");
        return null;
    }

	/**
	 * load a template based on one of the prefixes in TemplatePath
	 *
	 */

    private Template getTemplate(String path, String encoding) throws IOException
    {
        _log.debug("getTemplate:"+path);
        for (int i=0; i< _templateDirectory.length; i++)
        {
            String base = _templateDirectory[i];
            URL url = null;
            if (base.startsWith(CLASSPATH_PREFIX))
            {
                base = base.substring(CLASSPATH_PREFIX.length());
                base = searchClasspath(base);
                if (base != null)
                {
                    url = new URL("file",null,join(base,path));
                }
                else
                {
                    throw new FileNotFoundException("Unable to locate "+path+" on classpath");
                }
            }
            else if (base.startsWith(CONTEXT_PREFIX))
            {
                base = base.substring(CONTEXT_PREFIX.length());
                // no way to get hold of the servletcontext yet
                // url = servletContext.getResource(base+"/"+urlPath);
                throw new IllegalArgumentException("Can't deal with "+CONTEXT_PREFIX+" yet");
            }
            else if (base.startsWith(IGNORE_PREFIX))
            {
                url = null;
            }
            else
            {
                try
                {
                    url = new URL(base);
                }
                catch (MalformedURLException e)
                {
                    url = new URL("file",null,base);
                }
            }

            Template _tmpl =  getTemplate(path,url, encoding);
            if (_tmpl != null) return _tmpl;
        }
        return null;
    }

    // Utility Methods

	/**
	 * The URL path separator.  Used by join()
	 */
	private static final String _SEP = "/";
	
    /**
     * Join two parts of a URL string together, without duplicating 
     * any "/" between them;
     */
     
    private static final String join(String pre, String post)
    {
        boolean first = pre.endsWith(_SEP);
        boolean second = post.startsWith(_SEP);
        if (first ^ second) return pre+post;
        if (first) return pre.substring(0,pre.length()-1)+post;
        if (second) return first+post.substring(1);
        return pre+_SEP+post;
    }

    /**
     * Searches the SYSTEM classpath for a resource.
     * Probably Class.getSytemResource() might be better.
     * <p>
     * Ideally would like to be able to search the application
     * classpath, which is not necessarily the same (e.g., in servlet 2.2+)
     * so could pass in the application classloader.  But we have the same 
     * problem as in other places where there is no easy way to pass extra 
     * information with the request.
     */

    private final String searchClasspath(String resource)
    {
        String classpath = System.getProperty("java.class.path");
        String pathSep = System.getProperty("path.separator");
        _log.debug("Searching classpath: "+classpath);
        StringTokenizer st = new StringTokenizer(classpath, pathSep);
        while (st.hasMoreTokens())
        {
            String entry = st.nextToken();
            File f = new File(entry);

            if (f.isDirectory())
            {
                String path = f.getAbsolutePath();
                _log.debug("Testing "+path);
                path = join(path,resource);
                File _rf = new File(path);
                if (_rf.exists()) return path;
            }
        }
        return null;
    }

    /**
     * Removes the last locale part from a string
     * <p>
     * e.g. "_en_GB" => "_en"
     */

    private final String stripLast(String s)
    {
        if (s==null) return null;
        int p = s.lastIndexOf("_");
        if (p < 0)
        {
            return null;
        }
        String ret = s.substring(0,p);
        return ("".equals(ret)) ? null : ret;
    }

    /**
     * Builds up a new path from the form AAAAA{_B1_B2...._Bn-1}CCCCC
     * when given arguments AAAA, _B1_B2...._Bn, CCCC
     * <p>
     * e.g., "path/myfile","_en_GB",".wm" => "path/myfile{_en}.wm"
     */

    private final String buildPath(String pre, String mid, String post)
    {
        StringBuffer sb = new StringBuffer(pre);
        String stripped = stripLast(mid);
        if (stripped != null)
        {
            sb.append(_OPEN);
            sb.append(stripped);
            sb.append(_CLOSE);
        }
        sb.append(post);
        return sb.toString();
    }

    /**
     * parseLocalePath
     * Looks for a string of the form AAA{BBB}CCC
     * If found, returns [AAA,BBB,CCC], null otherwise
     *
     * This is used to strip out the "Locale" part of a resource name
     */

    private String[] parseLocalePath(String path)
    {
        int p1 = path.indexOf(_OPEN);
        int p2 = path.indexOf(_CLOSE);
        if ((p1 < 0) || (p2 < 0) || (p2 < p1))
        {
            return null;
        }
        String pre = path.substring(0,p1);
        String mid = path.substring(p1+1,p2);
        String post = path.substring(p2+1);
        return new String[] {pre,mid,post};
    }


}
