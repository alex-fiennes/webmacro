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

import java.lang.reflect.*;
import java.util.MissingResourceException;
import java.text.ParseException;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.webmacro.Template;
import org.webmacro.servlet.WebContext;
import org.webmacro.InitException;
import org.webmacro.Log;

/**
 * Represents action invoked by HTTP request.
 *
 * @see ActionServlet
 */
final class Action {
    /** Parent servlet. */
    final ActionServlet servlet;

    /** Component that this action belongs to. */
    final ComponentData componentData;

    /** Form name (equal to the 'form' parameter from HTTP request). */
    final String formName;

    /** Action name (equal to the 'action' parameter from HTTP request). */
    final String actionName;

    /** Method implementing the action. */
    final Method method;

    /** Declaration of method implementing the action (for debug use only). */
    private final String methodDecl;

    /** Method parameter types (except the first one, which is always WebContext). */
    private final Class[] paramTypes;

    /** Indicates, if paramTypes[i] is array. */
    private final boolean[] isParamArray;

    /** Type handlers for paramTypes[]. */
    private final TypeHandler[] typeHandlers;

    /** HTTP parameter names which are mapped to 'method' parameters. */
    final String[] paramNames;

    /**
     * Set to true if servlet overrides
     * {@link ActionServlet#afterInvoke(Template,WebContext,String,String,Object[]) afterInvoke()}
     * method. */
    private final boolean callAfterInvoke;

    /**
     * Set to true if servlet overrides
     * {@link ActionServlet#beforeInvoke(WebContext,String,String,Object[]) beforeInvoke()}
     * method. */
    private final boolean callBeforeInvoke;

    /**
     * Vector of <TT>&lt;output-variable&gt;</TT>s from the configuration file.
     */
    private Vector outputVariables;

    /**
     * Creates a new action (note: all actions are statically created at startup).
     *
     * @param servlet parent servlet
     * @param formName HTTP parameter named 'form'
     * @param actionName HTTP parameter named 'action'
     * @param componentData component that defines action methods
     * @param typeHandlers table of type handlers
     * @param methodDecl method declaration of the format:
     *        <TT>methodName(Type1 parameter1, Type2 parameter2, ... TypeN parameterN)</TT>
     * @param outputVariables table of <TT>&lt;output-variable&gt;</TT>s from the
     *        configuration file
     * @exception InitException on incorrect method definition
     */
    Action(final ActionServlet servlet,
           String formName,
           String actionName,
           ComponentData componentData,
           Hashtable typeHandlers,
           final String methodDecl,
           Vector outputVariables)
    throws InitException {
        this.servlet = servlet;
        this.formName = formName;
        this.actionName = actionName;
        this.componentData = componentData;
        this.methodDecl = methodDecl;
        this.outputVariables = outputVariables;

        final Vector paramTypes = new Vector();
        final Vector isParamArray = new Vector();
        final Vector paramNames = new Vector();

        // 1st parameter must be of typeWebContext
        paramTypes.addElement(WebContext.class);
        isParamArray.addElement(Boolean.FALSE);
        paramNames.addElement("context");

        /**
         * Method definition parser.
         */
        class MethodDefinitionParser {
            private StringTokenizer st = new StringTokenizer(methodDecl.trim(), " \t\"[(,;)]", true);
            private String token;
            private int tokenNum = 0;

            /**
            * Parsing of method definition: &lt;methodName&gt; '(' parseParam() ')'
            * @return method name
            */
            String parseMethod() throws ParseException {
                if (getToken(false) != 's') throw new ParseException(methodDecl + ": '" + token + "'", tokenNum);
                String methodName = token;

                if (getToken(false) != '(') throw new ParseException(methodDecl + ": '" + token + "'", tokenNum);

                int t;
                Out:{
                    do
                        if (!parseParam()) {t = ')'; break Out;}  // kludge
                    while((t = getToken(false)) == ',' && t != -1);

                    if (t != ')') throw new ParseException(methodDecl + ": '" + token + "'", tokenNum);
                }

                if ((t = getToken(false)) != ';' && t != -1)
                    throw new ParseException(methodDecl + ": '" + token + "'", tokenNum);

                return methodName;
            }

            /**
            * Parsing of parameter: &lt;type&gt; &lt;name&gt; nebo &lt;typ&gt;[] jméno
            * @return false method has no parameters
            */
            private boolean parseParam() throws ParseException {
                int lookAhead = getToken(false);

                if (lookAhead == ')') return false;

                if (lookAhead != 's')
                    throw new ParseException(methodDecl + ": '" + token + "'", tokenNum);

                // check for valid Java type
                Class c;

                try {
                    c = servlet.loader.loadClass(token);
                } catch (ClassNotFoundException e) {
                    if ("boolean".equals(token)) c = boolean.class;
                    else if ("byte".equals(token)) c = byte.class;
                    else if ("double".equals(token)) c = double.class;
                    else if ("float".equals(token)) c = float.class;
                    else if ("int".equals(token)) c = int.class;
                    else if ("long".equals(token)) c = long.class;
                    else if ("short".equals(token)) c = short.class;
                    else if ("char".equals(token)) c = char.class;
                    else if ("void".equals(token)) c = void.class;
                    else
                        try {
                            c = Class.forName("java.lang." + token);
                        } catch (ClassNotFoundException ee) {
                            throw new ParseException("Unknown parameter type '" + token +
                                    "' in action definition: " + methodDecl, tokenNum);
                        }
                }

                paramTypes.addElement(c);

                lookAhead = getToken(false);

                if (lookAhead != 's' && lookAhead != '[')
                    throw new ParseException(methodDecl + ": '" + token + "'", tokenNum);

                // is parameter array?
                if (lookAhead == '[') {
                    if (getToken(false) != ']')
                        throw new ParseException(methodDecl + ": '" + token + "'", tokenNum);

                    isParamArray.addElement(Boolean.TRUE);
                    lookAhead = getToken(false);
                } else isParamArray.addElement(Boolean.FALSE);

                if (lookAhead != 's') throw new ParseException(methodDecl + ": '" + token + "'", tokenNum);
                paramNames.addElement(token);

                return true;
            }

            /**
            * Stores next token to 'token'.
            * @return token type of token
            */
            private int getToken(boolean returnSpaces) {
                int tok = -1;
                tokenNum++;

                Out: {
                    do
                        try {
                            token = st.nextToken();
                            if (!returnSpaces) token = token.trim();
                        } catch (NoSuchElementException e) {
                            break Out;
                        }
                    while (token.length() == 0);

                    if ("(".equals(token)) {tok = '('; break Out; }
                        else if (")".equals(token)) {tok = ')'; break Out; }
                            else if (",".equals(token)) {tok = ','; break Out; }
                                else if ("[".equals(token)) {tok = '['; break Out; }
                                    else if ("]".equals(token)) {tok = ']'; break Out; }
                                        else if (";".equals(token)) {tok = ';'; break Out; }
                                            else if ("\"".equals(token)) {tok = '"'; break Out; }
                                                if (" ".equals(token) || "\t".equals(token)) {tok = ' '; break Out; }
                                                    else tok = 's';     // 's' for string
                }

                return tok;
            }
        }

        MethodDefinitionParser parser = new MethodDefinitionParser();

        try {
            String actionMethod = parser.parseMethod();
            int size = paramNames.size();

            this.paramTypes = new Class[size];
            this.isParamArray = new boolean[size];
            this.paramNames = new String[size];
            this.typeHandlers = new TypeHandler[size];

            paramTypes.copyInto(this.paramTypes);
            paramNames.copyInto(this.paramNames);

            for (int i=0; i < size; i++)
                this.isParamArray[i] = ((Boolean)isParamArray.elementAt(i)).booleanValue();

            bindParamTypesToHandlers(typeHandlers);
            method = getComponentActionMethod(actionName, actionMethod);
        } catch (InitException e) {
            throw e;
        } catch (ParseException e) {
            throw new InitException("Error while parsing definition: " + e.getMessage());
        } catch (Exception e) {
            servlet.log.error(e.toString());
            throw new InitException(e.getMessage());
        }

        Class sc = servlet.getClass();

        // set callAfterInvoke flag
	boolean callAfterInvoke;
        try {
            Class[] typeClasses = {Template.class,
                                   WebContext.class,
                                   String.class,
                                   String.class,
                                   Object[].class};
            // check whether servlet overrides 'afterInvoke()' method
            if (sc != ActionServlet.class) {
                sc.getDeclaredMethod("afterInvoke", typeClasses);
                callAfterInvoke = true;
            } else callAfterInvoke = false;
        } catch (NoSuchMethodException e) {
            callAfterInvoke = false;
        }
	this.callAfterInvoke = callAfterInvoke;

        // set callBeforeInvoke flag
	boolean callBeforeInvoke;
        try {
            Class[] typeClasses = {WebContext.class,
                                   String.class,
                                   String.class,
                                   Object[].class};
            // check whether servlet overrides 'beforeInvoke()' method
            if (sc != ActionServlet.class) {
                sc.getDeclaredMethod("beforeInvoke", typeClasses);
                callBeforeInvoke = true;
            } else callBeforeInvoke = false;
        } catch (NoSuchMethodException e) {
            callBeforeInvoke = false;
        }
	this.callBeforeInvoke = callBeforeInvoke;
    }

    /**
     * Binds appropriate type handlers to parameter types.
     * Before calling this method must be filled <TT>paramTypes</TT>.
     */
    private void bindParamTypesToHandlers(Hashtable typeHandlers) throws InitException {
        String handlerName = null;

        // start by 1 because of WebContext parameter
        for (int i=1; i < paramTypes.length; i++) {
            TypeHandler handler;

            if ((handler = (TypeHandler) typeHandlers.get(paramTypes[i].getName())) != null) {
                if (handler instanceof CompositeTypeHandler && isParamArray[i])
                    throw new InitException("Type '" + paramTypes[i] + "' cannot be" +
                                            " array, because it is handled by composite" +
                                            " type handler '" + handler + "'");
                this.typeHandlers[i] = handler;
            } else
                throw new InitException("Type handler for type '" + paramTypes[i] +
                                        "' not defined in the configuration file");
        }
    }

    /**
     * Returns component action method.
     * Before calling this method must be filled <TT>paramTypes</TT>.
     *
     * @param actionName action name
     * @param actionMethod name of the method implementing this action
     * @exception InitException if action method is not properfly defined or not found at all
     */
    private Method getComponentActionMethod(String actionName, String actionMethod)
    throws InitException {
        Method[] methods = componentData.componentClass.getMethods();

        Out: for (int i=0; i < methods.length; i++)
            // search for method of given name
            if (actionMethod.equals(methods[i].getName())) {
                Class[] types = methods[i].getParameterTypes();

                // check parameter types
                if (paramTypes.length != types.length) continue Out;

                for (int j=0; j < paramTypes.length; j++)
                    if (isParamArray[j]) {
                        if (paramTypes[j] != types[j].getComponentType()) continue Out;
                    } else if (paramTypes[j] != types[j]) continue Out;

                // check return type
                if (methods[i].getReturnType() != Template.class) continue Out;

                // check thrown exception types
                Class[] exceptions = methods[i].getExceptionTypes();

                for (int k=0; k < exceptions.length; k++)
                    if (exceptions[k] != ActionException.class) continue Out;

                return methods[i];
            }

        throw new InitException("Action method '" + methodDecl + "' not found in '" +
                                componentData.componentClass.getName() + "' while " +
                                "binding action '" + (formName == null?"": (formName + "'.'")) +
                                actionName + "' or configuration error. Note: Method " +
                                "must return org.webmacro.Template, its first parameter " +
                                "must be of type org.webmacro.servlet.WebContext and may throw " +
                                "only org.webmacro.as.ActionException!");
    }

    /**
     * Invokes component method, which implements this action:<BR>
     * <TT>&lt;component&gt;.&lt;method.getName()&gt;(converted rawParams)</TT>.
     *
     * @param context context from {@link ActionServlet#handle(WebContext) handle()} method
     * @param component target object
     * @param rawParams parameters to be converted
     * @return template to be displayed
     * @exception ConversionException on conversion error
     * @exception ActionException if action method throws exception
     */
    Template invoke(WebContext context, Object component, Object[] rawParams)
    throws ConversionException, ActionException {
        if (rawParams.length != paramTypes.length)
            throw new IllegalArgumentException("Incorrect number of parameters of method '" +
                                                method.getName() + "'");

        // servlet method called before any parameter type handler is invoked
        servlet.beforeConversion(context, formName, actionName);

        Object[] convertedParams = new Object[paramTypes.length];
        convertedParams[0] = context;

        // parameter conversion
        int i = -1;

        try {
            for (i=1; i<paramTypes.length; i++)
                if (typeHandlers[i] instanceof SimpleTypeHandler) {
                    convertedParams[i] = execSimpleTypeHandler(context,
                                                               rawParams[i],
                                                               isParamArray[i],
                                                               paramTypes[i],
                                                               (SimpleTypeHandler)typeHandlers[i]);
                } else if (typeHandlers[i] instanceof CompositeTypeHandler) {
                    if (rawParams[i].getClass().isArray())
                        throw new ConversionException("More than one definition of composite type '" +
                                                      paramNames[i] + "' in HTTP request");

                    convertedParams[i] = execCompositeTypeHandler(context,
                                                                 (String)rawParams[i],
                                                                 (CompositeTypeHandler)typeHandlers[i]);
                } else {
                    // cannot happen, but...
                    throw new ConversionException("Type handler '" +
                                                  typeHandlers[i] + "' does not implement" +
                                                  " appropriate interface");
                }
        } catch (ConversionException e) {
            e.setParameterName(paramNames[i]);
            throw e;
        }

        // action invocation
        Template view = reinvoke(context, component, convertedParams);

        // handle components with "request" persistence
        HttpSession session = context.getSession();
        LastActionData la = (LastActionData) servlet.lastActions.get(session);
        if (la != null &&
            la.action.componentData.persistence == ComponentData.PERSISTENCE_REQUEST &&
            la.component instanceof Destroyed) ((Destroyed)la.component).destroy();

        // save last astion's parameters
        servlet.lastActions.put(session, new LastActionData(this, component, convertedParams));
        return view;
    }

    /**
     * Calls a simple type handler.
     *
     * @param context context from {@link ActionServlet#handle(WebContext) handle()} method
     * @param rawParamValues parmeter values
     * @param isParamArray indicates whether rawParamValues is array
     * @param paramType type of action method parameter
     * @param handler type handler
     * @return converted value
     */
    private Object execSimpleTypeHandler(WebContext context,
                                         Object rawParamValues,
                                         boolean isParamArray,
                                         Class paramType,
                                         SimpleTypeHandler handler)
    throws ConversionException {
        String paramValue = null;

        try {
            if (rawParamValues == null)
                return handler.convert(context, paramValue = null);

            if (isParamArray) {
                Object array;

                if (!rawParamValues.getClass().isArray()) {
                    array = Array.newInstance(paramType, 1);
                    Array.set(array, 0, handler.convert(context, paramValue = (String)rawParamValues));
                } else {
                    array = Array.newInstance(paramType, Array.getLength(rawParamValues));
                    for (int j=0; j < Array.getLength(array); j++)
                        Array.set(array, j, handler.convert(context, paramValue = (String)Array.get(rawParamValues, j)));
                }

                return array;
            }

            if (rawParamValues.getClass().isArray())
                throw new ConversionException("Parameter is not array but has more values defined");
            return handler.convert(context, paramValue = (String)rawParamValues);
        } catch (ConversionException e) {
            e.setExceptionOrigin(handler);
            e.setParameterValue(paramValue);
            e.setWasThrownInComposite(false);
            throw e;
        }
    }

    /**
     * Calls a composite type handler. [TODO: check for null HTTP parameter values?]
     *
     * @param context context from {@link ActionServlet#handle(WebContext) handle()} method
     * @param compositeDefinition see {@link CompositeTypeHandler CompositeTypeHandler}
     * @param handler composite type handler
     * @return converted value
     */
    private Object execCompositeTypeHandler(WebContext context,
                                            String compositeDefinition,
                                            CompositeTypeHandler handler)
    throws ConversionException {
        try {
            StringTokenizer st = new StringTokenizer(compositeDefinition, " \t,[]", true);
            int numTokens = st.countTokens();
            String[] componentNames = new String[numTokens];
            String[][] componentValues = new String[numTokens][];
            HttpServletRequest request = context.getRequest();

            // get parameter values from HTTP request
            boolean wasPart = false, isArray;
            for(int i=0; st.hasMoreTokens();) {
                componentNames[i] = st.nextToken();

                if (" ".equals(componentNames[i]) || "\t".equals(componentNames[i])) continue;

                if (",".equals(componentNames[i]))
                    if (wasPart) {
                        wasPart = false;
                        continue;
                    } else throw new ConversionException("Definition of composite handler '" +
                                                         compositeDefinition + "'is incorrect");
                if ("[".equals(componentNames[i])) {
                    if ("]".equals(st.nextToken())) isArray = true;
                        else throw new ConversionException("Definition of composite handler '" +
                                                           compositeDefinition + "'is incorrect");
                } else isArray = false;

                componentValues[i] = request.getParameterValues(componentNames[i]);

                if (componentValues[i] == null)
                    throw new ConversionException("Parameter component '" + componentNames[i] + "' does not exist");

                if (!isArray && componentValues[i].length > 1)
                    throw new ConversionException("Component '" + componentNames[i] + "' of " +
                                                  " composite type is not array but has more " +
                                                  " values defined");
                wasPart = true;
                i++;
            }

            return handler.convert(context, componentNames, componentValues);
        } catch(ConversionException e) {
            e.setExceptionOrigin(handler);
            e.setWasThrownInComposite(true);
            e.setParameterValue(compositeDefinition);
            throw e;
        }
    }

    /**
     * Invokes component action method. Called by {@link #invoke(WebContext,Object[])}.
     *
     * @param context context from {@link ActionServlet#handle(WebContext) handle()} method
     * @param convertedParams converted parameters
     * @exception ActionException if action method throws exception
     * @return template to be displayed
     */
    Template reinvoke(WebContext context, Object component, Object[] convertedParams)
    throws ActionException {
        try {
            convertedParams[0] = context;
            Template template;

            if (callBeforeInvoke) {
                // servlet method called before each action
                servlet.log.debug("Invoking 'beforeInvoke()' method of action '" + actionName + "'");

                if ((template = servlet.beforeInvoke(context, formName, actionName, convertedParams)) != null) {
                    servlet.log.debug("Method 'beforeInvoke()' returns a non null value -> action" +
                                " '" + actionName + (callAfterInvoke?"' and 'afterInvoke()' ": "'") +
                                " won't be invoked");
                    return template;   // !!!
                }
            }

            // invoke action method
            servlet.log.debug("Invoking method '" + componentData.componentClass.getName() +
                              "." + methodDecl +"' of action '" + (formName==null?"":
                              formName+"'.'")+ actionName + "'");
            template = (Template) method.invoke(component, convertedParams);

            if (callAfterInvoke) {
                // servlet method called after each action
                servlet.log.debug("Invoking 'afterInvoke()' method of action '" + actionName + "'");

                if ((template = servlet.afterInvoke(template, context, formName, actionName, convertedParams)) == null)
                    throw new ActionException("Method 'afterInvoke()' returns null");
            }

            if (template == null)
                throw new ActionException("Method '" + method.getName() +
                                          "' implementing action '" + actionName +
                                          "' returns null");

            // set <output-variable>s of action
            for (Enumeration e = outputVariables.elements(); e.hasMoreElements(); )
                ((OutputVariable) e.nextElement()).evaluate(context);

            // set <output-variable>s of template
            Vector templateOutputVariables = (Vector) servlet.templateOutputVariables.get(template.toString());

            if (templateOutputVariables != null)
                for (Enumeration e = templateOutputVariables.elements(); e.hasMoreElements(); )
                    ((OutputVariable) e.nextElement()).evaluate(context);

            return template;
        } catch (InvocationTargetException e) {
            Throwable target = e.getTargetException();
            servlet.log.error(target.toString());

            if (target instanceof ActionException) throw (ActionException) target;

            throw new ActionException("Unexpected exception " + target.toString() +
                                      " thrown by method '" + method.getName() +
                                      "()' implementing action '" +
                                      actionName + "': " + target.getMessage());
        } catch (IllegalAccessException e) {
            servlet.log.error(e.toString());
            throw new ActionException(e.getMessage());
        }
    }
}
