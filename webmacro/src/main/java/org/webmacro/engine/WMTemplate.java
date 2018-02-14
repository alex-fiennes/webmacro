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

package org.webmacro.engine;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.webmacro.Broker;
import org.webmacro.Context;
import org.webmacro.FastWriter;
import org.webmacro.PropertyException;
import org.webmacro.Template;
import org.webmacro.TemplateException;
import org.webmacro.TemplateVisitor;
import org.webmacro.WMConstants;

/**
 * Template objects represent the user defined layout into which the webmacro package will
 * substitute values. It is a very simple kind of interpreted language containing text, blocks, and
 * directives. Text is to be passed through verbatim. LList group text and directives into linear
 * lists and sublists. Directives determine how subsequent blocks are to be processed and constitute
 * the commands of the language.
 * <p>
 * The Template is lazily evaluated: it does not parse or open the supplied filename until it is
 * used. Once it has parsed the file, it never alters its data. The intent is to allow a Template to
 * be parsed once (somewhat expensive) and then used many times; and also not to incur any parsing
 * costs at all if the Template is never actually used.
 * <p>
 * CONCURRENCY: You must parse() the template before making it available to other threads. After a
 * template has been parsed it is immutable and can be shared between threads without
 * synchronization. This class performs no synchronization itself but instead relies on the
 * TemplateProvider/Broker to synchronize access.
 */
abstract public class WMTemplate
  implements Template
{

  static Logger _log = LoggerFactory.getLogger(WMTemplate.class);
  /**
   * The resource broker used to resolve things in this template.
   */
  final protected Broker __broker;

  /**
   * Whether template has already been parsed.
   */
  private boolean _parsed;

  /**
   * What this template contains is a top level block.
   */
  protected Block _content;

  /**
   * Which parser (grammar) is used to parse this template, typically "wm".
   */
  private String _parserName;

  /**
   * Template parameters.
   */
  private Map<Object, Object> _parameters;

  /**
   * Template Macros.
   */
  private Map<String, MacroDefinition> _macros;

  /**
   * Create a new Template. Constructors must supply a broker.
   */
  protected WMTemplate(Broker broker)
  {
    this("wm",
         broker);
  }

  /**
   * Create a new Template specifying both the broker and the parsing language.
   */
  protected WMTemplate(String parserName,
                       Broker broker)
  {
    __broker = broker;
    _parserName = parserName;
  }

  /**
   * Get the stream the template should be read from. Parse will call this method in order to locate
   * a stream.
   * 
   * @exception IOException
   *              if unable to read template
   */
  protected abstract Reader getReader()
      throws IOException;

  /**
   * Return a name for this template. For example, if the template reads from a file you might want
   * to mention which it is--will be used to produce error messages describing which template had a
   * problem.
   */
  @Override
  public abstract String toString();

  /**
   * Return a name for this template. If not overridden, uses toString().
   */
  @Override
  public String getName()
  {
    return toString();
  }

  /**
   * Set the name for this template. Default implementation does nothing.
   */
  @Override
  public void setName(String name)
  {
  }

  /**
   * Subclasses can override this if they wish to invoke a parser other than the WM parser, or
   * choose the parser on some more complicated condition. This method will be called by parse();
   */
  protected Parser getParser()
      throws TemplateException
  {
    try {
      return (Parser) __broker.get("parser", "wm");
    } catch (Exception e) {
      throw new TemplateException("Could not load parser type " + _parserName, e);
    }
  }

  /**
   * Template API.
   */
  @Override
  public void parse()
      throws IOException, TemplateException
  {
    if (!_parsed) {
      Block newContent = null;
      Map<Object, Object> newParameters = null;
      Map<String, MacroDefinition> newMacros = null;
      Reader in = null;
      BuildContext bc = null;
      try {
        Parser parser = getParser();
        in = getReader();
        BlockBuilder bb = parser.parseBlock(getName(), in);
        in.close();
        bc = new BuildContext(__broker);
        // put global macros from Broker into the BuildContext
        Map<String, MacroDefinition> globalMacros = __broker.getMacros();
        for (Iterator<Map.Entry<String, MacroDefinition>> i = globalMacros.entrySet().iterator(); i.hasNext();) {
          Map.Entry<String, MacroDefinition> entry = i.next();
          bc.putMacro(entry.getKey(), entry.getValue());
        }
        newParameters = bc.getMap();
        newMacros = bc.getMacros();
        newContent = (Block) bb.build(bc);
      } catch (BuildException be) {
        if (bc != null)
          be.setContextLocation(bc.getCurrentLocation());
        _log.error("Template contained invalid data", be);
        throw be;
      } catch (IOException e) {
        _log.error("Template: Could not read template: " + this);
        throw e;
      } catch (Exception e) {
        _log.error("Error parsing template: " + this, e);
        BuildException be = new BuildException("Error parsing template: " + this, e);
        if (bc != null)
          be.setContextLocation(bc.getCurrentLocation());
        throw be;
      } finally {
        try {
          if (in != null)
            in.close();
        } catch (IOException e) {
          e = null; // Real error reported above
        }
        _parameters = newParameters;
        _content = newContent;
        _macros = newMacros;
        _parsed = true;
      }
    } else {
      _log.debug("Ignoring parse request on already parsed template " + this);
    }
  }

  /**
   * Get the #macros' defined for this template.
   * 
   * @return the #macro's defined for this template, or null if this template has not yet been <code>parse()'d</code>
   *         .
   */
  @Override
  public Map<String, MacroDefinition> getMacros()
  {
    return _macros;
  }

  /**
   * Parse the Template against the supplied context data and return it as a string. If the
   * operation fails for some reason, such as unable to read template or unable to introspect the
   * context then this method will return a null string.
   */
  @Override
  public final String evaluateAsString(Context context)
      throws PropertyException
  {
    try {
      FastWriter fw = FastWriter.getInstance(__broker);
      write(fw, context);
      String ret = fw.toString();
      fw.close();
      return ret;
    } catch (IOException e) {
      _log.error("Template: Could not write to ByteArrayOutputStream!", e);
      return null;
    }
  }

  /**
   * Parse the Template against the supplied context data and return it as a byte array. If the
   * operation fails for some reason, such as unable to read template or unable to introspect the
   * context then this method will return a null string.
   */
  @Override
  public final byte[] evaluateAsBytes(String encoding,
                                      Context context)
      throws PropertyException
  {
    try {
      FastWriter fw = FastWriter.getInstance(__broker, encoding);
      write(fw, context);
      byte[] ret = fw.toByteArray();
      fw.close();
      return ret;
    } catch (IOException e) {
      _log.error("Template: Could not write to ByteArrayOutputStream!", e);
      return null;
    }
  }

  @Override
  public void write(OutputStream out,
                    Context context)
      throws PropertyException, IOException
  {
    FastWriter fw = FastWriter.getInstance(__broker, out);
    write(fw, context);
    fw.flush();
    fw.close();
  }

  @Override
  public void write(OutputStream out,
                    String encoding,
                    Context context)
      throws PropertyException, IOException
  {
    FastWriter fw = FastWriter.getInstance(__broker, out, encoding);
    write(fw, context);
    fw.flush();
    fw.close();
  }

  @Override
  public final void write(FastWriter out,
                          Context context)
      throws IOException, PropertyException
  {
    try {
      if (!_parsed) {
        parse();
      }
    } catch (TemplateException e) {
      _log.error("Template: Unable to parse template: " + this, e);
      out.write(context.getEvaluationExceptionHandler()
                       .errorString("Template failed to parse. Reason: \n" + e.toString(), e));
    }

    try {
      _content.write(out, context);
    } catch (PropertyException e) {
      e.setContextLocation(context.getCurrentLocation());
      throw e;
    } catch (IOException ioe) {
      throw ioe;
    } catch (Exception e) {
      String warning = "Template: Exception evaluating template " + this;
      _log.warn(warning, e);

      out.write(context.getEvaluationExceptionHandler()
                       .warningString("Could not interpret template. Reason: \n" + warning + "\n"
                                          + e.toString(),
                                      e));
    }
  }

  @Override
  public void accept(TemplateVisitor v)
  {
    _content.accept(v);
  }

  /**
   * return the default encoding either from the WebMacro config or the JVM settings. Note for Unix
   * users: you may need to set the environmental variable LC_ALL=[locale] to get the default one
   * set up.
   */

  protected final String getDefaultEncoding()
  {
    try {
      return (String) __broker.get("config", WMConstants.TEMPLATE_INPUT_ENCODING);
    } catch (Exception e) {
      return System.getProperty("file.encoding");
    }
  }

  /**
   * Template API.
   */
  @Override
  public Object getParam(String key)
      throws IOException, TemplateException
  {
    try {
      return _parameters.get(key);
    } catch (NullPointerException e) {
      parse();
      return _parameters.get(key);
    }
  }

  @Override
  public Map<Object, Object> getParameters()
  {
    return _parameters;
  }

  @Override
  public void setParam(String key,
                       Object value)
  {
    _parameters.put(key, value);
  }

}
