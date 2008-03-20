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


package org.webmacro.engine;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Iterator;
import java.util.Map;

import org.webmacro.Broker;
import org.webmacro.Context;
import org.webmacro.FastWriter;
import org.webmacro.Log;
import org.webmacro.PropertyException;
import org.webmacro.Template;
import org.webmacro.TemplateException;
import org.webmacro.TemplateVisitor;
import org.webmacro.WMConstants;

/**
 * Template objects represent the user defined layout into which the
 * webmacro package will substitute values. It is a very simple kind of
 * interpreted language containing text, blocks, and directives. Text is
 * to be passed through verbatim. LList group text and directives into
 * linear lists and sublists. Directives determine how subsequent blocks
 * are to be processed and constitute the commands of the language.
 * <p>
 * The Template is lazily evaluated: it does not parse or open the
 * supplied filename until it is used. Once it has parsed the file, it
 * never alters its data. The intent is to allow a Template to be parsed
 * once (somewhat expensive) and then used many times; and also not to
 * incur any parsing costs at all if the Template is never actually used.
 * <p>
 * CONCURRENCY: You must parse() the template before making it available
 * to other threads. After a template has been parsed it is immutable and
 * can be shared between threads without synchronization. This class
 * performs no synchronization itself but instead relies on the
 * TemplateProvider/Broker to synchronize access.
 */
abstract public class WMTemplate implements Template
{

    /**
     * The resource broker used to resolve things in this template.
     */
    final protected Broker _broker;

    /**
     * Where we log our errors.
     */
    final protected Log _log;

    /**
     * Whether template has already been parsed.
     */
    private boolean _parsed;

    /**
     * What this template contains is a top level block.
     */
    protected Block _content;

    /**
     * Which parser (grammar) is used to parse this template,
     * typically "wm".
     */
    private String _parserName;

    /**
     * Template parameters.
     */
    private Map _parameters;

    /**
     * Template Macros.
     */
    private Map _macros;


    /**
     * Create a new Template. Constructors must supply a broker.
     */
    protected WMTemplate (Broker broker)
    {
        this("wm", broker);
    }

    /**
     * Create a new Template specifying both the broker and the
     * parsing language.
     */
    protected WMTemplate (String parserName, Broker broker)
    {
        _broker = broker;
        _parserName = parserName;
        _log = broker.getLog("template", "template lifecycle");
    }

    /**
     * Get the stream the template should be read from. Parse will
     * call this method in order to locate a stream.
     * @exception IOException if unable to read template
     */
    protected abstract Reader getReader () throws IOException;

    /**
     * Return a name for this template. For example, if the template reads
     * from a file you might want to mention which it is--will be used to
     * produce error messages describing which template had a problem.
     */
    public abstract String toString ();

    /**
     * Return a name for this template. If not overridden, uses toString().
     */
    public String getName ()
    {
        return toString();
    }

    /**
     * Set the name for this template.  Default implementation does nothing.
     */
    public void setName (String name)
    {
    }

    /**
     * Subclasses can override this if they wish to invoke a parser
     * other than the WM parser, or choose the parser on some more
     * complicated condition. This method will be called by parse();
     */
    protected Parser getParser () throws TemplateException
    {
        try
        {
            return (Parser) _broker.get("parser", "wm");
        }
        catch (Exception e)
        {
            throw new TemplateException("Could not load parser type " +
                    _parserName, e);
        }
    }

    /**
     * Template API.
     */
    public void parse () throws IOException, TemplateException
    {
        if (!_parsed)
        {
            Block newContent = null;
            Map newParameters = null;
            Map newMacros = null;
            Reader in = null;
            BuildContext bc = null;
            try
            {
                Parser parser = getParser();
                in = getReader();
                BlockBuilder bb = parser.parseBlock(getName(), in);
                in.close();
                bc = new BuildContext(_broker);
                // put global macros from Broker into the BuildContext
                Map globalMacros = _broker.getMacros();
                for (Iterator i=globalMacros.entrySet().iterator(); i.hasNext(); ) {
                    Map.Entry entry = (Map.Entry)i.next();
                    bc.putMacro((String)entry.getKey(),(MacroDefinition) entry.getValue());
                }
                newParameters = bc.getMap();
                newMacros = bc.getMacros();
                newContent = (Block) bb.build(bc);
            }
            catch (BuildException be)
            {
                if (bc != null)
                    be.setContextLocation(bc.getCurrentLocation());
                newContent = null;
                _log.error("Template contained invalid data", be);
                throw be;
            }
            catch (IOException e)
            {
                newContent = null; // don't let the old one survive
                _log.error("Template: Could not read template: " + this);
                throw e;
            }
            catch (Exception e)
            {
                _log.error("Error parsing template: " + this, e);
                BuildException be = new BuildException("Error parsing template: " + this, e);
                if (bc != null)
                    be.setContextLocation(bc.getCurrentLocation());
                throw be;
            }
            finally
            {
                try
                {
                    if (in != null)
                        in.close();
                }
                catch (IOException e)
                {
                    e = null; // Real error reported above
                }
                _parameters = newParameters;
                _content = newContent;
                _macros = newMacros;
                _parsed = true;
            }
        }
        else
        {
            _log.debug("Ignoring parse request on already parsed template " + this);
        }
    }

    /**
     * Get the #macros' defined for this template.
     *
     * @return the #macro's defined for this template, or null if this template has
     * not yet beed <code>parse()'d</code>.
     */
    public Map getMacros ()
    {
        return _macros;
    }

    /**
     * Parse the Template against the supplied context data and
     * return it as a string. If the operation fails for some reason,
     * such as unable to read template or unable to introspect the context
     * then this method will return a null string.
     */
    public final String evaluateAsString (Context context) throws PropertyException
    {
        try
        {
            FastWriter fw = FastWriter.getInstance(_broker);
            write(fw, context);
            String ret = fw.toString();
            fw.close();
            return ret;
        }
        catch (IOException e)
        {
            _log.error("Template: Could not write to ByteArrayOutputStream!", e);
            return null;
        }
    }

    /**
     * Parse the Template against the supplied context data and
     * return it as a byte array. If the operation fails for some reason,
     * such as unable to read template or unable to introspect the context
     * then this method will return a null string.
     */
    public final byte[] evaluateAsBytes (String encoding, Context context) throws PropertyException
    {
        try
        {
            FastWriter fw = FastWriter.getInstance(_broker, encoding);
            write(fw, context);
            byte[] ret = fw.toByteArray();
            fw.close();
            return ret;
        }
        catch (IOException e)
        {
            _log.error("Template: Could not write to ByteArrayOutputStream!", e);
            return null;
        }
    }

    public void write(OutputStream out, Context context)
            throws PropertyException, IOException {
        FastWriter fw = FastWriter.getInstance(_broker, out);
        write(fw, context);
        fw.flush();
        fw.close();
    }

    public void write(OutputStream out, String encoding, Context context)
            throws PropertyException, IOException {
        FastWriter fw = FastWriter.getInstance(_broker, out, encoding);
        write(fw, context);
        fw.flush();
        fw.close();
    }

    public final void write (FastWriter out, Context context)
            throws IOException, PropertyException
    {
        try
        {
            if (!_parsed)
            {
                parse();
            }
        }
        catch (TemplateException e)
        {
            _log.error("Template: Unable to parse template: " + this, e);
            out.write(context.getEvaluationExceptionHandler()
                    .errorString("Template failed to parse. Reason: \n"
                    + e.toString()));
        }

        try
        {
            _content.write(out, context);
        }
        catch (PropertyException e)
        {
            e.setContextLocation(context.getCurrentLocation());
            throw e;
        }
        catch (IOException ioe)
        {
            throw ioe;
        }
        catch (Exception e)
        {
            String warning =
                    "Template: Exception evaluating template " + this;
            _log.warning(warning, e);

            out.write(context.getEvaluationExceptionHandler()
                    .warningString("Could not interpret template. Reason: \n"
                    + warning + "\n" + e.toString()));
        }
    }

    public void accept (TemplateVisitor v)
    {
        _content.accept(v);
    }


    /**
     * return the default encoding either from the WebMacro config
     * or the JVM settings.
     *
     * Note for Unix users: you may need to set the environmental variable
     * LC_ALL=[locale] to get the default one set up.
     */

    protected final String getDefaultEncoding ()
    {
        try
        {
            return (String) _broker.get("config", WMConstants.TEMPLATE_INPUT_ENCODING);
        }
        catch (Exception e)
        {
            return System.getProperty("file.encoding");
        }
    }


    /**
     * Template API.
     */
    public Object getParam (String key)
            throws IOException, TemplateException
    {
        try
        {
            return _parameters.get(key);
        }
        catch (NullPointerException e)
        {
            parse();
            return _parameters.get(key);
        }
    }

    public Map getParameters ()
    {
        return _parameters;
    }

    public void setParam (String key, Object value)
    {
        _parameters.put(key, value);
    }


}

