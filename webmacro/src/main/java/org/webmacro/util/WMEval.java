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

package org.webmacro.util;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.webmacro.Context;
import org.webmacro.Template;
import org.webmacro.WM;
import org.webmacro.WMConstants;
import org.webmacro.WebMacro;
import org.webmacro.engine.StreamTemplate;
import org.webmacro.servlet.WebContext;


/**
 * WMEval encapsulates an instance of WebMacro for reuse in any java application.
 * <p>
 * It's main benefits are a number of convenience methods for evaluating a template
 * and directing output either to a supplied output stream or to a file.
 * <p>
 * It can parse a single template stream and then evaluate that template
 * over a number of different contexts. And, it can maintain a single context
 * and evaluate different templates over the same context. Each time a context
 * or a template is provided, it is retained as state.
 * <p>
 * The context can therefore be preserved over multiple "writes" of different
 * templates.
 * <p>
 * The template stream can be any text stream but is often a rule stream containing
 * wm script directives.
 * <p>
 * This helper class is useful for evaluating WebMacro templates for which
 * flexibility in managing the evaluation options is key.
 * 
 * @author Lane Sharman
 * @version 3.0
 */
public class WMEval
{

    //-------public members-----

    //-------private and protected members-----
    private WebMacro wm;

    static Logger _log =  LoggerFactory.getLogger(WMEval.class);

    private Template currentTemplate;

    //private OutputStream out = System.out;

    private Context context;

    /**
     * If an output file is not specified as an argument, it
     * must be found in the context under this key.
     */
    public static final String outputContextKey = "OutputFileName";

    //-------constructor(s)-----
    /**
     * Constructor.
     * Build a WebMacro environment for currentTemplate execution.
     */
    public WMEval()
    {
        try {
            wm = new WM();
            init();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Build a WebMacro environment to evaluate a template given it's name.
     */
    public WMEval(String templateName)
    {
        try {
            wm = new WM();
            init();
            parseLocalTemplate(templateName);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * The constructor for WebMacro decorator in a servlet context.
     * Build a WebMacro environment for currentTemplate execution.
     */
    public WMEval(Servlet servlet)
    {
        try {
            if (servlet == null)
                wm = new WM();
            else
                wm = new WM(servlet);
            init();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private void init() { 
        context = wm.getContext();
    }
    /**
     * Return the settings associated with this WebMacro instance.
     */
    public Settings getSettings ()
    {
        return wm.getBroker().getSettings();
    }

    /**
     * Return the log associated with this instance of WMEval.
     */
    public Logger getLog ()
    {
        return _log;
    }

    //-------public initializers/destroyers-----
    /**
     * Initializes WMEval so that it can perform currentTemplate evaluation
     * on multiple contexts. Init parses the currentTemplate supplied.
     * <p>
     * The argument to init() is the currentTemplate as a stream allowing the 
     * currentTemplate to come from pretty much anywhere such as a url, 
     * a file, or a db field.
     * <p>
     * Care must be given to the fact that in parsing the currentTemplate, 
     * the current vm is able to resolve locations of other currentTemplates 
     * referenced within the supplied currentTemplate.
     * <p>
     * Note, once this is complete, the parsed currentTemplate can be applied 
     * to successive new object contexts. In other words, the application context
     * can assert new objects for currentTemplate application and remove others.
     * 
     * @param template The stream containing the top-level, unparsed currentTemplate.
     *
     */
    public Template init (InputStream template) throws Exception
    {
        Template t = new StreamTemplate(wm.getBroker(),
                template);
        t.parse();
        this.currentTemplate = t;
        return t;
    }

    public void error (String msg, Exception e)
    {
        _log.error(msg, e);
    }

    /**
     * Provides for a new context to be established.
     */
    public Context getNewContext ()
    {
        Context c = wm.getContext();
        this.context = c;
        return c;
    }

    public WebContext getNewContext (HttpServletRequest req,
            HttpServletResponse resp)
    {
        WebContext c = wm.getWebContext(req, resp);
        this.context = c;
        return c;
    }

    /**
     * Gets the current context.
     */
    public Context getCurrentContext ()
    {
        return this.context;
    }

    /**
     * Gets the current template.
     */
    public Template getCurrentTemplate ()
    {
        return this.currentTemplate;
    }

    /**
     * A convenience method to find and parse a template in the local template path.
     * FIXME Do not set currentTemplate here
     */
    public Template parseLocalTemplate (String templateName) throws Exception
    {
        Template t = wm.getTemplate(templateName);
        this.currentTemplate = t;
        return t;
    }

    /**
     * Supplies the parsed currentTemplate directly.
     * @param parsedTemplate The currentTemplate parsed possibly from a previous run.
     */
    public void setCurrentTemplate (Template parsedTemplate)
    {
        this.currentTemplate = parsedTemplate;
    }

    /**
     * Supplies a context to be  parsed currentTemplate directly.
     * @param c The context to be used for the evaluation.
     */
    public void setCurrentContext (Context c)
    {
        this.context = c;
    }

    /**
     * Sets the output stream to be different than the default, System.out.
     * @param out The new output stream for any output during currentTemplate evaluation.
     */
    //public void setOutputStream (OutputStream out)
    //{
    //    this.out = out;
    //}

    /**
     * Evaluates the context of this instance and the instance's
     * current template and current output stream using UTF8.
     */
    public String eval () throws Exception
    {
        return eval(context, currentTemplate);
    }

    /**
     * Evaluate the context supplied against the current template.
     * @param context The WebMacro context.
     */
    public String eval (Context context) throws Exception
    {
        return eval(context, currentTemplate);
    }

    /**
     * Evaluate the supplied template against the supplied context and 
     * return the result as a string.
     */
    public String eval (Context context, Template template) throws Exception
    {
        return template.evaluateAsString(context);
    }

    /**
     * Evaluates the string template against the current context
     * and returns the value.
     * @param templateName The name of the template.
     * @return The output from the evaluated template
     */
    public String eval (String templateName) throws Exception
    {
        return eval(context, templateName, null, null);
    }

    /**
     * Evaluate the named template against the given context
     * and return the result as a String. 
     * If an output stream is specified, the return value is 
     * also written out to the stream.
     * 
     * @param templateName The name of the template.
     * @param out An optional output stream.
     * @return The output from the evaluated template
     */
    public String eval (Context context, String templateName, OutputStream out)
            throws Exception
    {
        return eval(context, templateName, out, null);
    }

    /**
     * Evaluates the context using a file template sending the output to a disk file.
     * <p>
     * This method is the preferred method when an output stream is to be written
     * as well as the value of the string is to be returned.
     * 
     * @param context The context to use.
     * @param templateName The input template file in the resource path.
     * @param out The output stream. If null, an attempt will be made to locate 
     *   the outputstream in the context using the output stream key if
     *   in the context. If no output stream can be resolved, the method does not
     *   throw an exception.
     * @param encoding If null, the platform's encoding will be used.
     * @return The output from the evaluation of the template.
     */
    public String eval (Context context, String templateName, OutputStream out,
            String encoding) throws Exception
    {
        Template t = wm.getTemplate(templateName);
        String value = t.evaluateAsString(context);
        // output the file
        if (out == null) {
            String outputFileName = (String) context.get(outputContextKey);
            if (outputFileName != null) {
                out = new FileOutputStream(outputFileName);
            }
        }
        if (out != null) // write it to out
        {
            if (encoding == null) {
                out.write(value.getBytes());
            } else {
                out.write(value.getBytes(encoding));
            }
            out.close();
        }
        this.currentTemplate = t;
        this.context = context;
        return value;
    }
    /**
     * Evaluates the string template against a new context and writes
     * it to the http Response output stream using the proper encoding.
     * <p>
     * This is an exceptionally useful method for a servlet to use to
     * write out a template.
     * <p>
     * @param context The WM context to use.
     * @param templateName The name of the template.
     * @param resp The servlet response from which the encoding will be derived.
     * @return The output from the evaluated template.
     */
    public String eval (WebContext context, String templateName,
            HttpServletResponse resp) throws ServletException
    {
        String value = null;
        try {
            resp.setContentType("text/html");
            String encoding = wm
                    .getConfig(WMConstants.TEMPLATE_OUTPUT_ENCODING);
            value = eval(context, templateName, resp.getOutputStream(),
                    encoding);
        } catch (Exception e) {
            throw new ServletException(e);
        }
        return value;
    }


    /**
     *  Evaluate the named template against the given context.
     *  Writes the output to the given output file.
     *  
     * @param context the context to evaluate the template against
     * @param templateName  the name of the template to evaluate
     * @param outputFileName name of file to output to
     * @param append whether to apppend output
     * @param encoding the encoding to use, may be null
     * @return the String resulting from evaluating the template against the context
     */
    public String eval (Context context, String templateName,
            String outputFileName, boolean append, String encoding)
        throws Exception
    {
        OutputStream out = new FileOutputStream(outputFileName, append);
        return eval(context, templateName, out, encoding);
    }

    /**
     * Free up resources when no longer needed.
     */
    public void destroy ()
    {
        wm = null;
        currentTemplate = null;
        context = null;
    }
}
