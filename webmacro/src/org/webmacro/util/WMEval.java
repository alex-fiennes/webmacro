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
import java.util.*;
import java.io.*;
import org.webmacro.*;
import org.webmacro.engine.StreamTemplate;
import org.webmacro.engine.StringTemplate;


/**
 * WMEval encapsulates an instance of WebMacro for reuse.
 * <p>
 * It parses a template stream consisting of rules and directives
 * and then makes the parsed entity available
 * for evaluation and execution multiple times against an object context.
 * <p>
 * The context can be preserved over multiple "writes" of different
 * templates.
 * <p>
 * The directive stream can be anything but is often a rule stream.
 * <p>
 * The template, once parsed, is preserved and available using getRule().
 * This helper class is useful for evaluating WebMacro templates which
 * are mostly rule streams with some output.
 * @author Lane Sharman
 * @version 2.0
 */
public class WMEval {

	//-------public members-----

	//-------private and protected members-----
	private WebMacro wm;
	private Template rule;
	private OutputStream out;
	private Context context;

	//-------constructor(s)-----
	/**
	 * The constructor which creates the environment for evaluating a rule.
	 */
	public WMEval() {
		// Build a web macro environment for rule execution.
		try {
			wm = new WM();
			context = wm.getContext();
		}	
		catch (Exception e) {
			e.printStackTrace();
		}
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
	public Template init(InputStream unparsedRule) throws Exception {
		//
		rule = new StreamTemplate(wm.getBroker(), new InputStreamReader(unparsedRule));
		rule.parse();
		return rule;
	}

	/**
	 * Provides for a new context to be established.
	 */
	public Context getNewContext() {
		context = wm.getContext();
		return context;
	}

	/**
	 * Gets the current context.
	 */
	public Context getCurrentContext() {
		return context;
	}

	/**
	 * A convenience method to find and parse a template in the local template path.
	 */
	public Template parseLocalTemplate(String templateName) throws Exception {
    rule = wm.getTemplate(templateName);
    return rule;
	}

	/**
	 * A convenience method to parse a string using the encoding supplied.
	 */
	public Template parseStringTemplate(String template, String encoding) {
	  rule = new StringTemplate(wm.getBroker(), template, encoding);
	  return rule;
	}

	/**
	 * Supplies the parsed rule directly.
	 * @param parsedRule The rule parsed possibly from a previous run.
	 */
	public void setParsedTemplate(Template parsedTemplate) {
		rule = parsedTemplate;
	}

	/**
	 * Obtain the parsed rule possibly for reuse in another run.
	 * @return The rule parsed which can be resupplied in another run.
	 */
	public Template getRule() {
		return rule;
	}

	/**
	 * Sets the output stream to be different than the default, System.out.
	 * @param out The new output stream for any output during rule evaluation.
	 */
	public void setOutputStream(OutputStream out) {
		this.out = out;
	}

	/**
	 * Evaluates the context belong to this instance.
	 */
	public void assert() throws Exception {
		assert(context, rule, out, "UTF8");
	}

	/**
	 * Evaluate the context supplied against the current rule.
	 * @param context The map containing the referents to assertable, rule-driven objects.
	 */
	public void assert(Context context) throws Exception {
		assert(context, rule, out, "UTF8");
	}

	/**
	 * Evaluate the supplied context and template to the provided output.
	 */
	public void assert(Context context, Template rule, OutputStream out, String encoding) throws Exception {
		FastWriter w;
		if (out == null)
			w = new FastWriter(context.getBroker(), System.out, "UTF8");
		else
			w = new FastWriter(context.getBroker(), out, encoding);
		context.put("FastWriter", w); // allow template writers to access the output stream!
		rule.write(w, context);
		w.flush();
	}
	
	/**
	 * Free up resources when no longer needed.
	 */
	public void destroy() {
		wm.destroy();
		wm = null;
		rule = null;
	}
}
