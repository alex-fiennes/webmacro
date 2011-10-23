/*
 * Copyright (C) 1998-2000 Semiotek Inc. All Rights Reserved. Redistribution and use in source and
 * binary forms, with or without modification, are permitted under the terms of either of the
 * following Open Source licenses: The GNU General Public License, version 2, or any later version,
 * as published by the Free Software Foundation (http://www.fsf.org/copyleft/gpl.html); or The
 * Semiotek Public License (http://webmacro.org/LICENSE.) This software is provided "as is", with NO
 * WARRANTY, not even the implied warranties of fitness to purpose, or merchantability. You assume
 * all risks and liabilities associated with its use. See www.webmacro.org for more information on
 * the WebMacro project.
 */

package org.webmacro.resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.webmacro.Broker;
import org.webmacro.InitException;
import org.webmacro.NotFoundException;
import org.webmacro.ResourceException;
import org.webmacro.Template;
import org.webmacro.util.Settings;

/**
 * This is a "drop-in" replacement for the standard TemplateProvider in the WebMacro distribution.
 * The primary benefit is to allow a template to be loaded by a variety of means, without requiring
 * an absolute path. This should make applications more portable. <h3>TemplatePath</h3>
 * <i>TemplatePath=path1[;path2;....]</i>
 * <p>
 * Each path should be a full URL specification or one of the special cases listed below. If the
 * path cannot be interpreted, it will be tried as file:path
 * <h3>Special cases</h3>
 * <ul>
 * <li><i>TemplatePath=classpath:/templates</i> Template will be loaded from the classpath. Each
 * directory on the classpath will be tried in turn to locate the template.
 * <li><i>TemplatePath=ignore:</i> Don't use the template path. All templates will be referenced
 * explicitly as full URL's. A common case would be to generate a base URL at runtime (e.g., from
 * ServletContext.getResource("/") in JSDK 2.2+) and to prepend this to the template path.
 * <li><i>TemplatePath=context:</i> reserved for future use (specifically for finding templates with
 * a servlet context)
 * </ul>
 * <h3>Locale support</h3> There is a limited locale based implemention here. Template paths can
 * contain a string of the form {_aaa_bbb....} which will usually correspond to a locale such as
 * en_GB. Thus <i>load("template{_en_GB}.wm")</i> will look for
 * <ul>
 * <li>template_en_GB.wm</li>
 * <li>template_en.wm</li>
 * <li>template.wm</li>
 * </ul>
 * in that order, returning the first one that exists. This is implemented so that requests for
 * template_en_GB.wm & template_en_US.wm will both return the same template template_en.wm assuming
 * only the latter exists. In other words, the template is only parsed once.
 * 
 * @see org.webmacro.resource.CachingProvider
 * @see org.webmacro.resource.TemplateProvider
 * @since before 0.96
 * @author fergus
 */
final public class URLTemplateProvider
  extends CachingProvider
{
  static Logger _log = LoggerFactory.getLogger(URLTemplateProvider.class);

  /** CVS Revision tag. */
  public static final String RCS = "@(#) $Id$";

  // INITIALIZATION

  private Broker _broker = null;

  /**
   * The default separator for TemplathPath
   */
  private static String _pathSeparator = ";";

  private String[] _templateDirectory = null;

  /**
   * URLs can contain strings like "{_AAA_BBB...}" to mimic locale handling in ResourceBundles. I
   * think this can be improved upon - one idea would be to fix the wm extension and then ask for
   * (resource)+(locale_string).wm We still have the problem of how to pass in the locale info, just
   * as to pass in the encoding.
   */

  private static final String _OPEN = "{";
  private static final String _CLOSE = "}";

  private static final String CLASSPATH_PREFIX = "classpath:";
  private static final String CONTEXT_PREFIX = "context:";
  private static final String IGNORE_PREFIX = "ignore:";

  private static final int CLASSPATH_PREFIX_LENGTH = CLASSPATH_PREFIX.length();
  // private static final int CONTEXT_PREFIX_LENGTH
  // = CONTEXT_PREFIX.length();

  private static final String _TYPE = "template";

  /**
   * _baseURL is just a placeholder for the moment. My hope is that we can pass in a context base at
   * construction time (e.g., the servlet context base)
   */
  private URL _baseURL = null;

  private final HashMap<String, Template> templateNameCache = new HashMap<String, Template>();

  /**
   * The value of TemplatePath in WebMacro inititialization file. This is a semicolon separated
   * string with individual values like
   * <ol>
   * <li>ignore: - ignore completely. All request will be given by a full URL</li>
   * <li>classpath:[path]. Look for values on the system classpath, optionally with some relative
   * path. E.g., classpath:/templates/</li>
   * <li>*TODO* context:[path] load relative to some path in the current context (typically a
   * ServletContext)</li>
   * <li>[url] - look for templates relative to this location</li>
   * <li>[path] - Equivalent to file:[path]</li>
   * </ol>
   */

  private String _templatePath;

  /**
   * Supports the "template" type. This is a straight replacement for the default TemplateProvider
   * 
   * @return the template type. Always the String "template"
   */

  final public String getType()
  {
    return _TYPE;
  }

  /**
   * Create a new TemplateProvider that uses the specified directory as the source for Template
   * objects that it will return.
   * 
   * @param b
   *          A broker
   * @param config
   *          Settings from the webmacro initialization file
   * @exception InitException
   *              thrown when the provider fails to initialize
   */

  @Override
  public void init(Broker b,
                   Settings config)
      throws InitException
  {
    super.init(b, config);
    _broker = b;

    try {
      _templatePath = config.getSetting("TemplatePath");
      StringTokenizer st = new StringTokenizer(_templatePath, _pathSeparator);
      _templateDirectory = new String[st.countTokens()];
      int i;
      for (i = 0; i < _templateDirectory.length; i++) {
        String dir = st.nextToken();
        _templateDirectory[i] = dir;
      }

    } catch (Exception e) {
      throw new InitException("Could not initialize", e);
    }
  }

  /**
   * Grab a template based on its name, setting the request event to contain it if we found it.
   * 
   * @param name
   *          The name of the template to load
   * @throws NotFoundException
   *           if no matching template can be found
   * @throws ResourceException
   *           if template cannot be loaded
   * @return the requested resource
   */

  final public Object load(String name,
                           CacheElement ce)
      throws ResourceException
  {
    return load(name, _baseURL);
  }

  /**
   * Find the specified template in the directory managed by this template store. Any path specified
   * in the filename is relative to the directory managed by the template store.
   * <p>
   * 
   * @param name
   *          relative to the current directory fo the store
   * @return a template matching that name, or null if one cannot be found
   */

  final public Object load(String name,
                           URL base)
      throws ResourceException
  {
    _log.debug("Load URLTemplate: (" + base + "," + name + ")");
    try {
      Template _tmpl = null;

      if (base == null) {
        _tmpl = getTemplate(name);
      } else {
        _tmpl = getTemplate(new URL(base, name));
      }

      if (_tmpl == null) {
        throw new NotFoundException(this + " could not locate " + name + " on path "
                                    + _templatePath);
      }
      templateNameCache.put(name, _tmpl);
      return _tmpl;
    } catch (IOException e) {
      _log.debug(e.getClass().getName() + " " + e.getMessage());
      throw new ResourceException(e.getMessage());
    }
  }

  /**
   * Always return false. It is not possible to decide if an object fetched from a URL should be
   * reloaded or not. Returning false will cause the CachingProvider to load() only when it's cache
   * has expired.
   * 
   * @param name
   *          The name of the template to test
   * @return always returns false. ** TO DO ** Can do better than this for file: and jar: URLs jar
   *         urls are of the form jar:<url-of-jar>!<path-within-jar> e.g.,
   *         jar:file:/path/my.jar!/templates/test.wm However, this might be an expensive operation.
   */

  // IMPLEMENTATION

  /**
     *
     */

  final public boolean shouldReload(String name)
  {
    URLTemplate tmpl = (URLTemplate) templateNameCache.get(name);
    return (tmpl == null) ? false : tmpl.shouldReload();
  }

  private final boolean exists(URL url)
  {
    InputStream _is = null;
    try {
      _is = url.openStream();
      System.out.println(url + " exists");
      return true;
    } catch (IOException e) {
      System.out.println(url + " does not exist");
      return false;
    } finally {
      if (_is != null) {
        try {
          _is.close();
        } catch (Exception ignore) {
        }
      }
    }
  }

  /**
   * Load a template relative to the base.
   * 
   * @param path
   *          the relative or absolute URL-path of the template
   */

  final private Template getTemplate(URL path)
  {
    _log.debug("get:" + path);
    URLTemplate t;
    String pre = null;
    String mid = null;
    String post = null;
    String pathStr = path.toExternalForm();
    try {
      String[] parts = parseLocalePath(pathStr);

      String urlPath = null;
      URL url = path;
      if (parts == null) {
        urlPath = pathStr;
      } else {
        pre = parts[0];
        mid = parts[1];
        post = parts[2];
        urlPath = pre + ((mid == null) ? "" : mid) + post;
        url = new URL(urlPath);
      }

      if (urlPath.length() > 512) {
        throw new IllegalArgumentException("URL path too long: " + urlPath);
      }
      _log.debug("URLTemplateProvider: loading " + url + "(" + path + "," + urlPath + ")");

      t = new URLTemplate(_broker, url);
      _log.debug("**PARSING " + url);
      t.parse();
      return t;
    } catch (IOException e) {
      _log.debug(e.getClass().getName() + " " + e.getMessage());
      // try the next locale
      if (mid != null) {
        try {
          String p = buildPath(pre, mid, post);
          return (Template) _broker.get(_TYPE, p);
        } catch (Exception ex) {
          _log.debug(ex.getClass().getName() + " " + ex.getMessage());
          // ignore
        }
      }
      _log.error("URLTemplateProvider(1): Could not load template: " + path, e);
    } catch (Exception e) {
      _log.error("URLTemplateProvider(2): Could not load template: " + path, e);
    }
    _log.debug("URLTemplateProvider: " + path + " not found.");
    return null;
  }

  /**
   * Get the URL for a specified template.
   */

  private Template getTemplate(String path)
      throws IOException
  {
    _log.debug("getTemplate: " + path);
    for (int i = 0; i < _templateDirectory.length; i++) {
      String tPart = _templateDirectory[i];
      URL url = null;
      if (tPart.startsWith(CLASSPATH_PREFIX)) {
        tPart = tPart.substring(CLASSPATH_PREFIX_LENGTH);
        url = searchClasspath(join(tPart, path));

        if (url == null) {
          throw new FileNotFoundException("Unable to locate " + path + " on classpath");
        }
      } else if (tPart.startsWith(CONTEXT_PREFIX)) {
        throw new IllegalStateException("Not implemented");
      } else if (tPart.startsWith(IGNORE_PREFIX)) {
        url = new URL(path);
      } else {
        String s = join(tPart, path);
        try {
          url = new URL(s);
        } catch (MalformedURLException e) {
          url = new URL("file", null, s);
        }
      }
      if (exists(url)) {
        return getTemplate(url);
      }
    }
    return null;
  }

  // Utility Methods

  /**
   * The URL path separator. Used by join().
   */
  private static final String _SEP = "/";

  /**
   * Join two parts of a URL string together, without duplicating any "/" between them;
   */

  private final String join(String pre,
                            String post)
  {
    _log.debug("Joining <" + pre + "> + <" + post + ">");
    if ((pre == null) || (pre.length() == 0))
      return post;
    if ((post == null) || (post.length() == 0))
      return pre;
    boolean first = pre.endsWith(_SEP);
    boolean second = post.startsWith(_SEP);
    if (first ^ second)
      return pre + post;
    if (first)
      return pre.substring(0, pre.length() - 1) + post;
    if (second)
      return first + post.substring(1);
    return pre + _SEP + post;
  }

  /**
   * Searches the SYSTEM classpath for a resource. Probably Class.getSytemResource() might be
   * better.
   * <p>
   * Ideally would like to be able to search the application classpath, which is not necessarily the
   * same (e.g., in servlet 2.2+) so could pass in the application classloader. But we have the same
   * problem as in other places where there is no easy way to pass extra information with the
   * request.
   */
  private final URL searchClasspath(String resource)
  {
    _log.debug("Searching classpath for " + resource);
    URL url = null;
    ClassLoader cl = this.getClass().getClassLoader();
    if (cl != null) {
      url = cl.getResource(resource);
    } else {
      url = ClassLoader.getSystemResource(resource);
    }

    if (url != null) {
      return url;
    }
    /*
     * look for locale specific resources AAAA{_xxxx_yyyyy_....}BBBBB
     */
    String[] parts = parseLocalePath(resource);
    if (parts != null) {
      if (parts[1] != null) {
        resource = buildPath(parts[0], parts[1], parts[2]);
        return searchClasspath(resource);
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
    if (s == null)
      return null;
    int p = s.lastIndexOf("_");
    if (p < 0) {
      return null;
    }
    String ret = s.substring(0, p);
    return ("".equals(ret)) ? null : ret;
  }

  /**
   * Builds up a new path from the form AAAAA{_B1_B2...._Bn-1}CCCCC when given arguments AAAA,
   * _B1_B2...._Bn, CCCC
   * <p>
   * e.g., "path/myfile","_en_GB",".wm" => "path/myfile{_en}.wm"
   */
  private final String buildPath(String pre,
                                 String mid,
                                 String post)
  {
    StringBuffer sb = new StringBuffer(pre);
    String stripped = stripLast(mid);
    if (stripped != null) {
      sb.append(_OPEN);
      sb.append(stripped);
      sb.append(_CLOSE);
    }
    sb.append(post);
    return sb.toString();
  }

  /**
   * Looks for a string of the form AAA{BBB}CCC. If found, returns [AAA,BBB,CCC], null otherwise
   * This is used to strip out the "Locale" part of a resource name
   */
  private String[] parseLocalePath(String path)
  {
    int p1 = path.indexOf(_OPEN);
    int p2 = path.indexOf(_CLOSE);
    if ((p1 < 0) || (p2 < 0) || (p2 < p1)) {
      return null;
    }
    String pre = path.substring(0, p1);
    String mid = path.substring(p1 + 1, p2);
    String post = path.substring(p2 + 1);
    return new String[] { pre, mid, post };
  }
}
