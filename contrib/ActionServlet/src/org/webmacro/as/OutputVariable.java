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
import org.webmacro.*;
import org.webmacro.directive.Directive;
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
    private final Variable variable;
    private final ComponentData componentData;
    private final String name;
    private Object value;
    private Object condition;

    /**
     * Creates an output-variable.
     */
    OutputVariable(ActionServlet servlet, ComponentData componentData, 
                   String name, String definition, String condition)
    throws ParseException {
        this.servlet = servlet;
        this.componentData = componentData;
        this.name = name;
        definition = definition.trim();
        condition = condition.trim();

        Broker broker = servlet.getBroker();
        BuildContext context = new BuildContext(broker);

        // prepare 'if' condition
        if (!"".equals(condition))
            try {
                this.condition = ((Builder) new WMParser(broker).parseBlock("string",
                                 new StringReader("#if("+condition+"){true}")).elementAt(0)).build(context);

            } catch (Exception e ) {
                throw new ParseException("Bad definition of attribute 'if'=\"" +
                                         condition + "\"", e);
            }

        // prepare definition for evaluation
        try {
            if (componentData != null) {
               variable = (Variable) ((VariableBuilder) new WMParser(broker).parseBlock("string",
               new StringReader("$"+TMP_VAR_NAME+"." + definition)).elementAt(0)).build(context);
            } else if (definition.startsWith("$")) {
               variable = (Variable) ((VariableBuilder) new WMParser(broker).parseBlock("string",
               new StringReader(definition)).elementAt(0)).build(context);

               if (variable == null)
                   throw new ParseException("Bad definition of attribute 'value'=\"" +
                                            definition + "\" (maybe missing 'component' attribute)");
            } else {
               variable = null;

               // definition will be considered a value: int, double or String
               try {
                   value = new Integer(definition);
               } catch(NumberFormatException e) {
                   try {
                       value = new Double(definition);
                   } catch(NumberFormatException ee) {
                       if (definition.equalsIgnoreCase("true")) value = Boolean.TRUE;
                           else if (definition.equalsIgnoreCase("false")) value = Boolean.FALSE;
                               else value = definition;
                   }
               }
            }
        } catch (ParseException e ) {
            throw e;
        } catch (Exception e ) {
            throw new ParseException("Bad definition of 'value' attribute=\"" +
                                     definition + "\" (maybe missing 'component' attribute)", e);
        }
    }

    /**
     * Evaluates &lt;output-variable&gt;.
     */
    void evaluate(WebContext context) {
        if (!evalCondition(context)) {
            servlet.log.debug("<output-variable> $" + name + " not evaluated - 'if' condition is false");
            return;
        }

        Object val = null;

        if (value != null) val = value;
        else {
            if (componentData != null)
                context.put(TMP_VAR_NAME, servlet.getComponent(componentData.componentName, true));

            try {
                val = variable.getValue(context);
            } catch(Exception e) {
                servlet.log.error("Error while evaluating <output-variable> $" + name, e);
            }

            if (componentData != null)
                context.remove(TMP_VAR_NAME);
        }

        context.put(name, val);
        servlet.log.debug("Evaluated <output-variable> $" + name + " = " + val);
    }

    /**
     * Evaluates 'if' attribute.
     */
    private boolean evalCondition(WebContext context) {
       if (condition == null) return true;

       if (condition instanceof Directive)
           try {
               return !"".equals(((Directive) condition).evaluate(context));
           } catch(PropertyException e) {
               servlet.log.debug("Error while evaluating <output-variable> $" + name, e);
               return false;
           }

       return !(condition instanceof String);
    }
}