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

import org.webmacro.*;
import org.webmacro.engine.StreamTemplate;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;


/**
 * WMEval encapsulates an instance of WebMacro for reuse in any java application.
 * <p>
 * Its main benefit are a number of convenience methods for evaluating a template
 * and directing output either to a supplied output stream or to a file.
 * <p>
 * It can parse a single template stream and then evaluate that rule
 * over a number of different contexts. And, it can maintain a single context
 * and evaluate different templates over the same context.
 * <p>
 * The context can therefore be preserved over multiple "writes" of different
 * templates.
 * <p>
 * The template stream can be any text stream but is often a rule stream containing
 * wm script directives.
 * <p>
 * A template, once parsed, can be preserved and made available using setRule()
 * and getRule().
 * <p>
 * This helper class is useful for evaluating WebMacro templates for which
 * flexibility in managing the evaluation options is key.
 * <p>
 * <b>Note</b>: All uses of the method assert(argList) should be converted
 * to eval(argList) in anticipation of jdk 1.4.
 * @author Lane Sharman
 * @version 2.0
 */
public class WMEval
{

    //-------public members-----

    //-------private and protected members-----
    private WebMacro wm;
    private Template rule;
    private OutputStream out = System.out;
    private Context context;
    /**
     * If an output file is not specified as an argument, it
     * must be found in the context under this key.
     */
    public static final String outputContextKey = "OutputFileName";

    //-------constructor(s)-----
    /**
     * The constructor for WebMacro decorator in a servlet context.
     */
    public WMEval (Servlet servlet)
    {
        // Build a web macro environment for rule execution.
        try
        {
            if (servlet == null)
                wm = new WM();
            else
                wm = new WM(servlet);
            context = wm.getContext();
        }
        catch (Exception e)
        {
            e.printStackTrace(System.err);
            throw new IllegalStateException(e.toString());
        }
    }

    public WMEval ()
    {
        this(null);
    }


    //-------public initializers/destroyers-----
    /**
     * Initializes WMEval so that it can perform rule evaluation
     * on multiple contexts. Init parses the rule supplied.
     * <p>
     * The argument to init() is the rule as a stream allowing the rule
     * to come from pretty much anywhere such as a url, a file, or a db field.
     * <p>
     * Care must be given to the fact that in parsing the rule, th current vm is able
     * to resolve locations of other rules referenced within the supplied rule.
     * <p>
     * Note, once this is complete, the parsed rule can be applied to successive
     * new object contexts. In other words, the application context
     * can assert new objects for rule application and remove others.
     * @param unparsedRule The stream containing the top-level, unparsed rule.
     *
     */
    public Template init (InputStream unparsedRule) throws Exception
    {
        //
        rule = new StreamTemplate(wm.getBroker(), new InputStreamReader(unparsedRule));
        rule.parse();
        return rule;
    }

    public void error (String msg, Exception e)
    {
        wm.getLog("ERROR").error(msg, e);
    }

    /**
     * Provides for a new context to be established.
     */
    public Context getNewContext ()
    {
        context = wm.getContext();
        return context;
    }

    /**
     * Gets the current context.
     */
    public Context getCurrentContext ()
    {
        return context;
    }

    /**
     * A convenience method to find and parse a template in the local template path.
     */
    public Template parseLocalTemplate (String templateName) throws Exception
    {
        rule = wm.getTemplate(templateName);
        return rule;
    }

    /**
     * Supplies the parsed rule directly.
     * @param parsedTemplate The rule parsed possibly from a previous run.
     */
    public void setParsedTemplate (Template parsedTemplate)
    {
        rule = parsedTemplate;
    }

    /**
     * Obtain the parsed rule possibly for reuse in another run.
     * @return The rule parsed which can be resupplied in another run.
     */
    public Template getRule ()
    {
        return rule;
    }

    /**
     * Sets the output stream to be different than the default, System.out.
     * @param out The new output stream for any output during rule evaluation.
     */
    public void setOutputStream (OutputStream out)
    {
        this.out = out;
    }

    /**
     * Evaluates the context of this instance and the instance's
     * current template and current output stream using UTF8.
     * @deprecated
     */
    public void eval () throws Exception
    {
        eval(context, rule, out, "UTF8");
    }


    /**
     * Evaluate the context supplied against the current rule.
     * @param context The map containing the referents to assertable, rule-driven objects.
     */
    public void eval (Context context) throws Exception
    {
        eval(context, rule, out, "UTF8");
    }

    /**
     * Evaluate the supplied context and template to the provided output.
     * @ rule the template provided.
     * @ out an output stream.
     * @ encoding the encoding for the output.
     */
    public void eval (Context context, Template rule, OutputStream out, String encoding) throws Exception
    {
        // context.put("FastWriter", w); // allow template writers to access the output stream!
        rule.write(out, encoding, context);
    }

    /**
     * Evaluates the string template against the current context
     * and returns the value. If an output file is specified, the value
     * is written out as well.
     * @param templateName The name of the template.
     * @param out An optional output stream.
     * @return The output from the evaluated template
     */
    public String eval (String templateName, OutputStream out) throws Exception
    {
        Template t = wm.getTemplate(templateName);
        String val = t.evaluateAsString(context);
        if (out != null)
        {
            out.write(val.getBytes());
        }
        return val;
    }

    /**
     * Evaluates the string template against the current context
     * and returns the value.
     * @param templateName The name of the template.
     * @return The output from the evaluated template
     */
    public String eval (String templateName) throws Exception
    {
        return eval(templateName, null);
    }

    /**
     * Evaluates the string template against a new context and writes
     * it to the http Response output stream using the proper encoding.
     * <p>
     * This is an exceptionally useful method for a servlet to use to
     * write out a template.
     * <p>
     * <b>Note:</b> This method places "FastWriter" in the context
     * so you can write out to the stream.
     * @param templateName The name of the template.
     * @param resp The servlet response object and its output stream
     */
    public void eval (String templateName, HttpServletRequest req,
                      HttpServletResponse resp) throws ServletException
    {
        try
        {
            context = wm.getWebContext(req, resp);
            String encoding = wm.getConfig(WMConstants.TEMPLATE_OUTPUT_ENCODING);
            if (encoding == null)
            {
                encoding = resp.getCharacterEncoding();
            }
            Template t = wm.getTemplate(templateName);
            // context.put("FastWriter", w); // allow template writers to access the output stream!
            t.write(resp.getOutputStream(), encoding, context);
        }
        catch (Exception e)
        {
            e.printStackTrace(System.err);
            throw new ServletException(e.toString());
        }
    }

    /**
     * Evaluate the supplied context and template and return the result as a
     * as a string.
     */
    public String eval (Context context, Template rule) throws Exception
    {
        return rule.evaluateAsString(context);
    }

    /**
     * Evaluates the context using a file template sending the output to a disk file.
     * @param context The context to use.
     * @param templateResourceFile The input template file in the resource path.
     * @param outputFileName The absolute path to a file. If null, the context
     * key OutputFileName must be present.
     * @param append If true, the file will be opened for appending the output.
     * @param encoding If null, the platform's encoding will be used.
     * @return The output is also returned as a convenience.
     */
    public String eval (Context context, String templateResourceFile,
                        String outputFileName, boolean append, String encoding) throws Exception
    {
        Template rule = wm.getTemplate(templateResourceFile);
        String value = rule.evaluateAsString(context);
        // output the file
        if (outputFileName == null)
            outputFileName = (String) context.get(outputContextKey);
        OutputStream out = new FileOutputStream(outputFileName, append);
        if (encoding == null)
            out.write(value.getBytes());
        else
            out.write(value.getBytes(encoding));
        out.close();
        return value;
    }


    /**
     * Free up resources when no longer needed.
     */
    public void destroy ()
    {
        wm = null;
        rule = null;
    }
}
