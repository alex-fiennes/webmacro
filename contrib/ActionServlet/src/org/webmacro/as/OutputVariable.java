/*
 *    Action Servlet is an extension of the WebMacro servlet framework, which
 *    provides an easy mapping of HTTP requests to methods of Java components.
 *
 *    Copyright (C) 1999-2001  Petr Toman
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Library General Public
 *    License as published by the Free Software Foundation; either
 *    version 2 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Library General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this library.  If not, write to the Free Software Foundation,
 *    Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package org.webmacro.as;

import java.io.*;
import java.util.Hashtable;
import javax.servlet.http.HttpSession;
import org.webmacro.Macro;
import org.webmacro.PropertyException;
import org.webmacro.engine.*;
import org.webmacro.parser.WMParser;
import org.webmacro.servlet.WebContext;

/**
 * Represents &lt;output-variable&gt;.
 */
class OutputVariable {
    private static final String TMP_VAR_NAME = "ACTION_SERVLET_COMPONENT";
    private final ActionServlet servlet;
    private final Macro variable;
    private final ComponentData componentData;
    private final String name;

    /**
     * Creates an output-variable.
     */
    OutputVariable(ActionServlet servlet, ComponentData componentData, String name, String definition)
    throws ParseException {
        this.servlet = servlet;
        this.componentData = componentData;
        this.name = name;

        try {
            if (componentData != null)
                variable = (Macro) new WMParser(servlet.getBroker()).parseBlock("string",
                           new StringReader("$"+TMP_VAR_NAME+"." + definition)).build(new BuildContext(servlet.getBroker()));
            else
                variable = (Macro) new WMParser(servlet.getBroker()).parseBlock("string",
                           new StringReader(definition)).build(new BuildContext(servlet.getBroker()));

            if (variable == null)
                throw new ParseException("Bad definition of 'value' attribute=\"" +
                                         definition + "\" of <output-variable>" +
                                         " (maybe missing 'component' attribute)");
        } catch (Exception e ) {
            throw new ParseException("Bad definition of 'value' attribute=\"" +
                                     definition + "\" of <output-variable>" +
                                     " (maybe missing 'component' attribute)", e);
        }
    }

    /**
     * Evaluates &lt;output-variable&gt;.
     */
    void evaluate(WebContext context) {
        if (componentData != null)
            context.put(TMP_VAR_NAME, servlet.getComponent(componentData.componentClass, true));

        Object val = null;
        try {
            val = variable.evaluate(context);
        } catch(Exception e) {
            servlet.log.error("Error while evaluating output variable '" + name + "'", e);
        }

        if (componentData != null)
            context.remove(TMP_VAR_NAME);
        context.put(name, val);
    }
}