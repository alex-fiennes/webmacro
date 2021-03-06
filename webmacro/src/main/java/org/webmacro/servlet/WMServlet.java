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

package org.webmacro.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Locale;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webmacro.Broker;
import org.webmacro.Context;
import org.webmacro.InitException;
import org.webmacro.NotFoundException;
import org.webmacro.PropertyException;
import org.webmacro.ResourceException;
import org.webmacro.Template;
import org.webmacro.WM;
import org.webmacro.WMConstants;
import org.webmacro.WebMacro;

/**
 * This is an abstract base class which can be used to implement a kind of WebMacro servlet. You can
 * either subclass from it directly, or make use of one of the generic subclasses provided.
 * <p>
 * It's primary function is to create a WebContext and manage a Broker. It also provides a couple of
 * convenience functions that access the Broker and/or WebContext to make some commonly accessed
 * services more readily available.
 * <p>
 * 
 * @see org.webmacro.Broker
 */
abstract public class WMServlet
  extends HttpServlet
  implements WebMacro
{

  private static final long serialVersionUID = -4903102471116106113L;
  static Logger _log = LoggerFactory.getLogger(WMServlet.class);

  private WebMacro _wm = null;
  private Broker _broker = null;
  private boolean _started = false;
  /**
   * The name of the config entry we look for to find out what to call the variable used in the
   * ERROR_TEMPLATE.
   */
  final static String ERROR_VARIABLE = "ErrorVariable";

  /**
   * The name of the error template we will use if something goes wrong.
   */
  final static String ERROR_TEMPLATE = "ErrorTemplate";

  /**
   * Defaults for error variable and error template.
   */
  final static String ERROR_TEMPLATE_DEFAULT = "error.wm";
  final static String ERROR_VARIABLE_DEFAULT = "error";

  /**
   * Null means all OK.
   */
  private String _problem =
      "Not yet initialized: "
          + "Your servlet API tried to access WebMacro without first calling init()!!!";

  /**
   * This is the old-style init method, it just calls init(), after handing the ServletConfig object
   * to the superclass.
   * 
   * @exception ServletException
   *              if it failed to initialize
   */
  @Override
  public synchronized void init(ServletConfig sc)
      throws ServletException
  {
    super.init(sc);
    init();
  }

  /**
   * This method is called by the servlet runner--do not call it. It must not be overidden because
   * it manages a shared instance of the broker--you can overide the start() method instead, which
   * is called just after the broker is initialized.
   */
  @Override
  public synchronized void init()
  {

    if (_started) {
      return;
    }

    // locate a Broker

    if (_wm == null) {
      try {
        _wm = initWebMacro();
        _broker = _wm.getBroker();
      } catch (InitException e) {
        _problem =
            "Could not initialize the broker!\n\n"
                + "*** Check that WebMacro.properties was in your servlet\n"
                + "*** classpath, in a similar place to webmacro.jar \n"
                + "*** and that all values were set correctly.\n\n" + e.getMessage();
        _log.error(_problem, e);
        return;
      }
    }

    try {
      if (_log.isDebugEnabled()) {
        java.net.URL url = getBroker().getResource(Broker.WEBMACRO_PROPERTIES);
        if (url != null)
          _log.debug("Using properties from " + url.toExternalForm());
        else
          _log.debug("No WebMacro.properties file was found.");
      }
      start();
      _problem = null;
    } catch (ServletException e) {
      _problem =
          "WebMacro application code failed to initialize: \n" + e + "\n"
              + "This error is the result of a failure in the\n"
              + "code supplied by the application programmer.\n";
      _log.error(_problem, e);
    }
    _log.info("started: " + this);
    _started = true;

  }

  /**
   * This method is called by the servlet runner--do not call it. It must not be overidden because
   * it manages a shared instance of the broker--you can overide the stop() method instead, which
   * will be called just before the broker is shut down. Once the stop() method has been called, the
   * nested WM will be destroyed and then the parent destroy() method will be invoked.
   * 
   * @see WM#destroy()
   */
  @Override
  public synchronized void destroy()
  {
    stop();
    _wm.destroy();
    _log.info("stopped: " + this);
    _wm = null;
    _started = false;
    super.destroy();
  }

  // SERVLET API METHODS

  /**
   * Process an incoming GET request: Builds a WebContext up and then passes it to the handle()
   * method. You can overide this if you want, though for most purposes you are expected to overide
   * handle() instead.
   * <p>
   * 
   * @param req
   *          the request we got
   * @param resp
   *          the response we are generating
   * @exception ServletException
   *              if we can't get our configuration
   * @exception IOException
   *              if we can't write to the output stream
   */
  @Override
  protected void doGet(HttpServletRequest req,
                       HttpServletResponse resp)
      throws ServletException, IOException
  {
    doRequest(req, resp);
  }

  /**
   * Behaves exactly like doGet() except that it reads data from POST before doing exactly the same
   * thing. This means that you can use GET and POST interchangeably with WebMacro. You can overide
   * this if you want, though for most purposes you are expected to overide handle() instead.
   * <p>
   * 
   * @param req
   *          the request we got
   * @param resp
   *          the response we are generating
   * @exception ServletException
   *              if we can't get our configuration
   * @exception IOException
   *              if we can't read/write to the streams we got
   */
  @Override
  protected void doPost(HttpServletRequest req,
                        HttpServletResponse resp)
      throws ServletException, IOException
  {
    doRequest(req, resp);
  }

  final private void doRequest(HttpServletRequest req,
                               HttpServletResponse resp)
      throws IOException
  {

    WebContext context = null;

    if (_problem != null) {
      init();
      if (_problem != null) {
        try {
          resp.setContentType("text/html");
          Writer out = resp.getWriter();

          out.write("<html><head><title>WebMacro Error</title></head>");
          out.write("<body><h1><font color=\"red\">WebMacro Error: ");
          out.write("</font></h1><pre>");
          out.write(_problem);
          out.write("</pre>");
          out.write("Please contact the server administrator");
          out.flush();
          out.close();
        } catch (Exception e) {
          _log.error(_problem, e);
        }
        return;
      }
    }

    preDoRequest(req, resp);
    context = newWebContext(req, resp);
    try {
      Template t;
      t = handle(context);

      if (t != null) {
        execute(t, context);
      }
      destroyContext(context);
    } catch (HandlerException e) {
      _log.error("Your handler failed to handle the request:" + this, e);
      Template tmpl =
          error(context, "Your handler was unable to process the request successfully "
                         + "for some reason. Here are the details:<p>" + "<pre>" + e + "</pre>");
      execute(tmpl, context);
    } catch (Exception e) {
      _log.error("Your handler failed to handle the request:" + this, e);
      Template tmpl =
          error(context, "The handler WebMacro used to handle this request failed for "
                         + "some reason. This is likely a bug in the handler written "
                         + "for this application. Here are the details:<p>" + "<pre>" + e
                         + "</pre>");
      execute(tmpl, context);
    } finally {
      postDoRequest(req, resp);
    }
  }

  protected void preDoRequest(HttpServletRequest req,
                              HttpServletResponse resp)
  {
  }

  protected void postDoRequest(HttpServletRequest req,
                               HttpServletResponse resp)
  {
  }

  // CONVENIENCE METHODS & ACCESS TO THE BROKER

  /**
   * Create an error template using the built in error handler. This is useful for returning error
   * messages on failure; it is used by WMServlet to display errors resulting from any exception
   * that you may throw from the handle() method.
   * 
   * @param context
   *          will add error variable to context (see Config)
   * @param error
   *          a string explaining what went wrong
   */
  protected Template error(WebContext context,
                           String error)
  {
    Template tmpl = null;
    // Handler hand = new ErrorHandler();
    try {
      context.put(getErrorVariableName(), error);
      // tmpl = hand.accept(context);
      tmpl = getErrorTemplate();
    } catch (Exception e2) {
      _log.error("Unable to use ErrorHandler", e2);
    }
    return tmpl;
  }

  /**
   * Returns the name of the error variable, as per the config.
   * 
   * @return Name to use for the error variable in templates
   */
  protected String getErrorVariableName()
  {
    return getConfig(ERROR_VARIABLE, ERROR_VARIABLE_DEFAULT);
  }

  /**
   * This object is used to access components that have been plugged into WebMacro; it is shared
   * between all instances of this class and its subclasses. It is created when the first instance
   * is initialized, and deleted when the last instance is shut down. If you attempt to access it
   * after the last servlet has been shutdown, it will either be in a shutdown state or else null.
   */
  @Override
  public Broker getBroker()
  {
    // this method can be unsynch. because the broker manages its own
    // state, plus the only time the _broker will be shutdown or null
    // is after the last servlet has shutdown--so why would anyone be
    // accessing us then? if they do the _broker will throw exceptions
    // complaining that it has been shut down, or they'll get a null here.
    return _broker;
  }

  /**
   * Retrieve a template from the "template" provider. Equivalent to
   * getBroker().get(TemplateProvider.TYPE,key)
   * 
   * @exception NotFoundException
   *              if the template was not found
   * @exception ResourceException
   *              if the template coult not be loaded
   */
  @Override
  public Template getTemplate(String key)
      throws ResourceException
  {
    return _wm.getTemplate(key);
  }

  /**
   * Retrieve a URL. This is largely equivalent to creating a URL object and requesting its content,
   * though it will sit in WebMacro's cache rather than re-requesting each time. The content will be
   * returned as an Object.
   */
  @Override
  public String getURL(String url)
      throws ResourceException
  {
    return _wm.getURL(url);
  }

  /**
   * Retrieve configuration information from the "config" provider. Equivalent to
   * getBroker().get(Config.TYPE,key)
   * 
   * @exception NotFoundException
   *              could not locate requested information
   */
  @Override
  public String getConfig(String key)
      throws NotFoundException
  {
    return _wm.getConfig(key);
  }

  /**
   * Retrieve configuration information from the "config" provider. Return specified default if key
   * could not be found
   */
  public String getConfig(String key,
                          String defaultValue)
  {
    try {
      return _wm.getConfig(key);
    } catch (NotFoundException e) {
      return defaultValue;
    }
  }

  /**
   * Create a new Context object.
   */
  @Override
  public Context getContext()
  {
    return _wm.getContext();
  }

  /**
   * Create a new WebContext object; can be overridden.
   */
  @Override
  public WebContext getWebContext(HttpServletRequest req,
                                  HttpServletResponse res)
  {
    return _wm.getWebContext(req, res);
  }

  /**
   * Convenience method for writing a template to an OutputStream. This method takes care of all the
   * typical work involved in writing a template.
   * <p>
   * This method uses the default <code>TemplateOutputEncoding</code> specified in WebMacro.defaults
   * or your custom WebMacro.properties.
   * 
   * @param templateName
   *          name of Template to write. Must be accessible via TemplatePath
   * @param out
   *          where the output of the template should go
   * @param context
   *          The Context (can be a WebContext too) used during the template evaluation phase
   * @throws java.io.IOException
   *           if the template cannot be written to the specified output stream
   * @throws ResourceException
   *           if the template name specified cannot be found
   * @throws PropertyException
   *           if a fatal error occured during the Template evaluation phase
   */
  @Override
  public void writeTemplate(String templateName,
                            java.io.OutputStream out,
                            Context context)
      throws java.io.IOException, ResourceException, PropertyException
  {

    writeTemplate(templateName, out, getConfig(WMConstants.TEMPLATE_OUTPUT_ENCODING), context);
  }

  /**
   * Convienence method for writing a template to an OutputStream. This method takes care of all the
   * typical work involved in writing a template.
   * 
   * @param templateName
   *          name of Template to write. Must be accessible via TemplatePath
   * @param out
   *          where the output of the template should go
   * @param encoding
   *          character encoding to use when writing the template if the encoding is
   *          <code>null</code>, the default <code>TemplateOutputEncoding</code> is used
   * @param context
   *          The Context (can be a WebContext too) used during the template evaluation phase
   * @throws java.io.IOException
   *           if the template cannot be written to the specified output stream
   * @throws ResourceException
   *           if the template name specified cannot be found
   * @throws PropertyException
   *           if a fatal error occured during the Template evaluation phase
   */
  @Override
  public void writeTemplate(String templateName,
                            java.io.OutputStream out,
                            String encoding,
                            Context context)
      throws java.io.IOException, ResourceException, PropertyException
  {

    if (encoding == null)
      encoding = getConfig(WMConstants.TEMPLATE_OUTPUT_ENCODING);

    Template tmpl = getTemplate(templateName);
    tmpl.write(out, encoding, context);
  }

  // DELEGATE-TO METHODS -- COMMON THINGS MADE EASIER

  /**
   * This method takes a populated context and a template and writes out the interpreted template to
   * the context's output stream.
   */
  protected void execute(Template tmpl,
                         WebContext c)
      throws IOException
  {
    try {
      HttpServletResponse resp = c.getResponse();

      Locale locale = (Locale) tmpl.getParam(WMConstants.TEMPLATE_LOCALE);
      _log.debug("TemplateLocale=" + locale);
      if (locale != null) {
        setLocale(resp, locale);
      }

      String encoding = (String) tmpl.getParam(WMConstants.TEMPLATE_OUTPUT_ENCODING);
      if (encoding == null) {
        encoding = resp.getCharacterEncoding();
      }

      _log.debug("Using output encoding " + encoding);

      // get the bytes before calling getOutputStream
      // this is necessary to be compatible with JSDK 2.3
      // where you can't call setContentType() after getOutputStream(),
      // which could be happening during the template evaluation
      byte[] bytes = tmpl.evaluateAsBytes(encoding, c);

      // now write the FW buffer to the response output stream
      writeResponseBytes(resp, bytes, encoding);
    } catch (UnsupportedEncodingException e) {
      // can be thrown by FastWriter.getInstance
      // rethrow it, because otherwise it would be ignored
      // as an IOException
      _log.error("tried to use an unsupported encoding", e);
      throw e;
    } catch (IOException e) {
      // ignore disconnect
    } catch (Exception e) {
      String error =
          "WebMacro encountered an error while executing a template:\n"
              + ((tmpl != null)
                  ? (tmpl + ": " + e + "\n")
                  : ("The template failed to load; double check the "
                     + "TemplatePath in your webmacro.properties file."));
      _log.error(error, e);
      try {
        Template errorTemplate =
            error(c, "WebMacro encountered an error while executing a template:\n"
                     + ((tmpl != null)
                         ? (tmpl + ": ")
                         : ("The template failed to load; double check the "
                            + "TemplatePath in your webmacro.properties file.")) + "\n<pre>" + noHtml(e.toString())
                     + "</pre>\n");

        String err = errorTemplate.evaluateAsString(c);
        c.getResponse().getWriter().write(err);
      } catch (Exception errExcept) {
        _log.error("Error writing error template!", errExcept);
      }
    }
  }
  
  public static String noHtml(String source)
  {
    return source.replace("<", "&lt;").replace(">", "&gt;");
  }

  /**
   * Helper method to write out a FastWriter (that has bufferd the response) to a ServletResponse.
   * This method will try to use the response's OutputStream first and if this fails, fall back to
   * its Writer.
   * 
   * @param response
   *          where to write fast writer to
   * @param bytes
   *          the bytes to write
   */
  private void writeResponseBytes(HttpServletResponse response,
                                  byte[] bytes,
                                  String encoding)
      throws IOException
  {
    OutputStream out;
    // We'll check, if the OutputStream is available
    try {
      out = response.getOutputStream();
    } catch (IllegalStateException e) {
      // Here comes a quick hack, we need a cleaner
      // solution in a future release. (skanthak)

      // this means, that the ServletOutputStream is
      // not available, because the Writer has already
      // be used. We have to use it, although its
      // much slower, especially, because we need to
      // revert the encoding process now
      out = null;
      _log.debug("Using Writer instead of OutputStream");
    }
    response.setContentLength(bytes.length);
    if (out != null) {
      out.write(bytes);
    } else {
      response.getWriter().write(new String(bytes, encoding));
    }
  }

  // FRAMEWORK TEMPLATE METHODS--PLUG YOUR CODE IN HERE

  /**
   * This method is called at the beginning of a request and is responsible for providing a Context
   * for the request. The default implementation calls WebContext.newInstance(req,resp) on the
   * WebContext prototype returned by the initWebContext() method. This is probably suitable for
   * most servlets, though you can override it and do something different if you like. You can throw
   * a HandlerException if something goes wrong.
   */
  public WebContext newContext(HttpServletRequest req,
                               HttpServletResponse resp)
      throws HandlerException
  {
    return _wm.getWebContext(req, resp);
    // return _wcPrototype.newInstance(req, resp);
  }

  /**
   * This method is called to handle the processing of a request. It should analyze the data in the
   * request, put whatever values are required into the context, and return the appropriate view.
   * 
   * @param context
   *          contains all relevant data structures, incl builtins.
   * @return the template to be rendered by the WebMacro engine
   * @throws HandlerException
   *           throw this to produce vanilla error messages
   */
  public abstract Template handle(WebContext context)
      throws HandlerException;

  /**
   * This method is called at the end of a request and is responsible for cleaning up the Context at
   * the end of the request. You may not need to do anything here, but it is sometimes important if
   * you have an open database connection in your context that you need to close. The default
   * implementation calls wc.clear().
   */
  public void destroyContext(WebContext wc)
      throws HandlerException
  {
  }

  /**
   * Override this method to implement any startup/init code you require. The broker will have been
   * created before this method is called; the default implementation does nothing. This is called
   * when the servlet environment initializes the servlet for use via the init() method.
   * 
   * @exception ServletException
   *              to indicate initialization failed
   */
  protected void start()
      throws ServletException
  {
  }

  /**
   * Override this method to implement any shutdown code you require. The broker may be destroyed
   * just after this method exits. This is called when the servlet environment shuts down the
   * servlet via the shutdown() method. The default implementation does nothing.
   */
  protected void stop()
  {
  }

  /**
   * This method returns the WebMacro object which will be used to load, access, and manage the
   * Broker. The default implementation is to return a new WM() object. You could override it and
   * return a WM object constructed with a particular configuration file, or some other
   * implementation of the WebMacro interface.
   */
  public WebMacro initWebMacro()
      throws InitException
  {
    return new WM(this);
  }

  public WebContext newWebContext(HttpServletRequest req,
                                  HttpServletResponse resp)
  {
    return new WebContext(_broker, req, resp);
  }

  /**
   * Set the locale on the response. The reflection trickery is because this is only defined for
   * JSDK 2.2+
   */
  protected void setLocale(HttpServletResponse resp,
                           Locale locale)
  {
    try {
      Method m = HttpServletResponse.class.getMethod("setLocale", new Class[] { Locale.class });
      m.invoke(resp, (Object[]) new Locale[] { locale });
      _log.debug("Successfully set locale to " + locale);
    } catch (Exception e) {
      _log.debug("Error set locale to " + locale + ": " + e.getClass());
    }
  }

  private static final String DEFAULT_ERROR_TEXT =
      "<HTML><HEAD><TITLE>Error</TITLE></HEAD>\n" + "#set $Response.ContentType = \"text/html\"\n"
          + "<BODY><H1>Error</H1>" + "<HR>$error</BODY></HTML>";

  private Template _errorTemplate = null;

  /**
   * Gets a template for displaying an error message. Tries to get configured template, then default
   * template and lastly constructs a string template.
   * 
   * @return A Template which can be used to format an error message
   */
  public Template getErrorTemplate()
  {
    String templateName = getErrorTemplateName();

    try {
      _errorTemplate = (Template) _broker.get("template", templateName);
    } catch (ResourceException e) {
      _errorTemplate =
          new org.webmacro.engine.StringTemplate(_broker,
                                                 DEFAULT_ERROR_TEXT,
                                                 "WebMacro default error template");
    }
    return _errorTemplate;
  }

  protected String getErrorTemplateName()
  {
    String templateName;

    try {
      templateName = (String) _broker.get("config", ERROR_TEMPLATE);
    } catch (ResourceException e) {
      templateName = ERROR_TEMPLATE_DEFAULT;
    }
    return templateName;
  }

}
