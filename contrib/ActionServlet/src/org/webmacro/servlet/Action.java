/*
 *    Action Servlet, an extension of the WebMacro library, which enables easy 
 *    mapping of user 'actions' to servlet methods.
 *
 *    Copyright (C) 1999-2000  Petr Toman
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
 *    along with this library; see the file COPYING.  If not, write to
 *    the Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 *    Boston, MA 02111-1307, USA.
 */
package org.webmacro.servlet;

import java.lang.reflect.*;
import java.util.MissingResourceException;
import java.text.ParseException;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.webmacro.broker.ResourceUnavailableException;
import org.webmacro.engine.Template;
import org.webmacro.util.InitException;
import org.webmacro.util.Log;

/**
 * Represents a servlet action invoked by HTTP request.
 *
 * @see ActionServlet
 */
final class Action {
    /** Action log. */
    private static final Log log = new Log("Action", "Action of ActionServlet");
    
    /** Servlet that defines method implementing this action. */
    private final ActionServlet servlet;
    
    /** Action name (equal to the 'action' parameter from HTTP request. */
    private final String actionName;
    
    /** Method implementing the action. */
    private final Method method;
    
    /** Method parameter types (except the first one, which is always WebContext). */
    private final Class[] paramTypes;
    
    /** Indicates, if paramTypes[i] is array. */
    private final boolean[] isParamArray;

    /** Type handlers for paramTypes. */
    private final TypeHandler[] typeHandlers;

    /** HTTP parameter names which are mapped to method parameters. */
    final String[] paramNames;

    /** 
     * Set to true if servlet overrides 
     * {@link ActionServlet#afterInvoke(Template,WebContext,String,Object[]) afterInvoke()}
     * method. */
    private final boolean callInvokeAfter;
    
    /** 
     * Set to true if servlet overrides 
     * {@link ActionServlet#beforeInvoke(WebContext,String,Object[]) beforeInvoke()}
     * method. */
    private final boolean callInvokeBefore;

    /**
     * Creates a new action..
     *
     * @param servlet object that defines action methods
     * @param typeHandlers table of type handlers
     * @param handlersProperties maps types to type hadlers
     * @param str method declaration of the format:
     *        <TT>methodName ( Type1 parameter1 , Type2 parameter2 , ... TypeN parameterN )</TT>
     * @exception InitException on incorrect method definition
     */
    Action(ActionServlet servlet, 
           Hashtable typeHandlers, 
           Properties handlersProperties, 
           final String str) 
    throws InitException {
        this.servlet = servlet;

        final Vector paramTypes = new Vector();
        final Vector isParamArray = new Vector();
        final Vector paramNames = new Vector();
        
        // 1st parameter must be of typeWebContext
        paramTypes.addElement(WebContext.class);
        isParamArray.addElement(Boolean.FALSE);
        paramNames.addElement("context");
        
        class MethodParser {
            private StringTokenizer st = new StringTokenizer(str.trim(), " \t\"[(,;)]", true);
            private String token;
            private int tokenNum = 0;
     
            /** Return trimmed action name: "&lt;jméno akce&gt;". */
            String getActionName() throws ParseException {
                StringBuffer sb = new StringBuffer();
                int lookAhead;
                
                if (getToken(false) != '"') throw new ParseException(str + ": '" + token + "'", tokenNum);

                while ((lookAhead = getToken(true)) != '"') {
                    sb.append(token);
                    if (lookAhead == -1) throw new ParseException(str + ": '" + token + "'", tokenNum);
                }
                   
                return sb.toString().trim();
            }
            
            /** 
             * Parsing of method definition: &lt;methodName&gt; '(' parseParam() ')'
             * @return method name
             */
            String parseMethod() throws ParseException {
                if (getToken(false) != 's') throw new ParseException(str + ": '" + token + "'", tokenNum);
                String methodName = token;

                if (getToken(false) != '(') throw new ParseException(str + ": '" + token + "'", tokenNum);

                int t;
                Out:{
                    do 
                        if (!parseParam()) {t = ')'; break Out;}  // kludge
                    while((t = getToken(false)) == ',' && t != -1);
                    
                    if (t != ')') throw new ParseException(str + ": '" + token + "'", tokenNum);
                }
                
                if ((t = getToken(false)) != ';' && t != -1) 
                    throw new ParseException(str + ": '" + token + "'", tokenNum);

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
                    throw new ParseException(str + ": '" + token + "'", tokenNum);
                
                // check for valid Java type
                Class c;

                try {
                    c = Class.forName(token);
                } catch (ClassNotFoundException e) {
                    try {
                        c = Class.forName("java.lang." + token);
                    } catch (ClassNotFoundException ee) {
                        if ("boolean".equals(token)) c = boolean.class;
                        else if ("byte".equals(token)) c = byte.class;
                        else if ("double".equals(token)) c = double.class;
                        else if ("float".equals(token)) c = float.class;
                        else if ("int".equals(token)) c = int.class;
                        else if ("long".equals(token)) c = long.class;
                        else if ("short".equals(token)) c = short.class;
                        else if ("char".equals(token)) c = char.class;
                        else throw new ParseException("Unknown parameter type '" + token + "' in action definition: " + str, tokenNum);
                    }
                }
                
                paramTypes.addElement(c);
                
                lookAhead = getToken(false);

                if (lookAhead != 's' && lookAhead != '[') 
                    throw new ParseException(str + ": '" + token + "'", tokenNum);
                
                // is parameter array?
                if (lookAhead == '[') {
                    if (getToken(false) != ']') 
                        throw new ParseException(str + ": '" + token + "'", tokenNum);
                        
                    isParamArray.addElement(Boolean.TRUE);
                    lookAhead = getToken(false);
                } else isParamArray.addElement(Boolean.FALSE);

                if (lookAhead != 's') throw new ParseException(str + ": '" + token + "'", tokenNum);
                paramNames.addElement(token);
                
                return true;
            }

            /** Returns token. */
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
                                                    else tok = 's';
                }
                
                return tok;
            }
        }

        MethodParser parser = new MethodParser();

        try {
            actionName = parser.getActionName();
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

            bindParamTypesToHandlers(typeHandlers, handlersProperties);
            method = getServletActionMethod(actionName, actionMethod);
        } catch (InitException e) {
            throw e;
        } catch (ParseException e) {
            throw new InitException("Error while parsing definition: " + e.getMessage());
        } catch (Exception e) {
            log.exception(e);
            throw new InitException(e.getMessage());
        }
        
        // set callInvokeAfter flag
        try {
            Class[] typeClasses = {Template.class,
                                   WebContext.class,
                                   String.class,
                                   Object[].class};
            // check whether servlet overrides 'afterInvoke()' method
            servlet.getClass().getDeclaredMethod("afterInvoke", typeClasses);
            callInvokeAfter = true;
        } catch (NoSuchMethodException e) {
            callInvokeAfter = false;
        }
        
        // set callInvokeBefore flag
        try {
            Class[] typeClasses = {WebContext.class, 
                                   String.class,
                                   Object[].class};
            // check whether servlet overrides 'beforeInvoke()' method
            servlet.getClass().getDeclaredMethod("beforeInvoke", typeClasses);
            callInvokeBefore = true;
        } catch (NoSuchMethodException e) {
            callInvokeBefore = false;
        }
    }
    
    /**
     * Binds appropriate type handlers to parameter types.
     * Before calling this method must be filled <TT>paramTypes</TT>.
     */
    private void bindParamTypesToHandlers(Hashtable typeHandlers, 
                                          Properties handlersProperties) 
    throws InitException {
        String handlerName = null;

        // start by 1 because of WebContext
        for (int i=1; i < paramTypes.length; i++) {
            TypeHandler handler;
            if ((handler = (TypeHandler) typeHandlers.get(paramTypes[i].getName())) != null)
                this.typeHandlers[i] = handler;
            else
                try {
                    if ((handlerName = handlersProperties.getProperty(paramTypes[i].getName())) == null)
                        throw new InitException("Type handler for type '" + paramTypes[i] + 
                                                "' not defined in '<servlet>_handlers.properties'");

                    handler = (TypeHandler) Class.forName(handlerName).newInstance();

                    // supported handlers: SimpleTypeHandler and CompositeTypeHandler
                    if (!(handler instanceof SimpleTypeHandler) &&
                        !(handler instanceof CompositeTypeHandler)) 
                        throw new InitException("Type handler '" + handlerName + 
                                                "' must implement interface" +
                                                " org.webmacro.servlet.SimpleTypeHandler" +
                                                " or" + 
                                                " org.webmacro.servlet.CompositeTypeHandler");
                                                
                    if (handler instanceof CompositeTypeHandler && isParamArray[i]) 
                        throw new InitException("Type '" + paramTypes[i] + "' cannot be" +
                                                " array, because it is handled by composite" +
                                                " type handler '" + handler + "'");
                            
                    typeHandlers.put(paramTypes[i].getName(), handler);
                    this.typeHandlers[i] = handler;
                } catch (ClassNotFoundException e) {
                    throw new InitException("Type handler '" + handlerName + "' not found");
                } catch (ClassCastException e) {
                    throw new InitException("Type handler '" + handlerName + 
                                            "' does not implement interface" +
                                            " org.webmacro.servlet.TypeHandler");
                } catch (InstantiationException e) {
                    throw new InitException("Cannot instantiate type handler " + handlerName);
                } catch (IllegalAccessException e) {
                    throw new InitException("Type handler " + handlerName + 
                                            " perhaps does not have a public konstruktor" +
                                            " with no parameters");
                }
        }
    }
    
    /**
     * Returns action servlet method.
     * Before calling this method must be filled <TT>paramTypes</TT>.
     *
     * @param actionName action name
     * @param actionMethod name of the method implementing this action
     * @exception InitException if action method is not properfly defined or not found at all
     */
    private Method getServletActionMethod(String actionName, String actionMethod) 
    throws InitException {
        Method[] methods = servlet.getClass().getMethods();
        
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
                    if (exceptions[k] != ActionException.class &&
                        exceptions[k] != ResourceUnavailableException.class)
                        continue Out;

                return methods[i];
            }

        throw new InitException("Undefined method '" + actionMethod + "' of action '" + 
                                actionName + "' or configuration error. Note: each method" + 
                                " implementing action must have its first parameter of type" +
                                " WebContext!");
    }
    
    /**
     * Returns action name.
     */
    String getActionName() {
        return actionName;
    }
    
    /**
     * Returns the name of method implementing this action.
     */
    String getMethodName() {
        return method.getName();
    }

    /**
     * Invokes the servlet method, which implements this action:<BR>
     * <TT>&lt;servlet&gt;.&lt;method.getName()&gt;(converted rawParams)</TT>.
     *
     * @param context context from {@link ActionServlet#handle(WebContext) handle()} method
     * @param rawParams parameters to be converted
     * @return template to be displayed
     * @exception ConversionException on conversion error
     * @exception ActionException if action method throws exception
     * @exception ResourceUnavailableException propagated exception
     */
    Template invoke(WebContext context, Object[] rawParams) 
    throws ConversionException, ActionException, ResourceUnavailableException {
        if (rawParams.length != paramTypes.length) 
            throw new IllegalArgumentException("Incorrect number of parameters of method '" + 
                                                getMethodName() + "'");

        // servlet method called before any parameter type handler is invoked
        servlet.beforeConversion(context, actionName);

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

        // action invocation - if successful, save parameters
        Template view = reinvoke(context, convertedParams);
        servlet.lastActions.put(context.getSession(), 
                                new LastActionData(this, convertedParams));
        return view;
    }

    /**
     * Calls a simple type handler.
     *
     * @param context context from {@link ActionServlet#handle(WebContext) handle()} method
     * @param rawParamValues parmeter values
     * @param isParamArray indicates whether rawParamValues is array
     * @param paramType type of action metody parameter
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
     * Calls a composite type handler. [TODO: check for null HTTP parameter values]
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
     * Invokes a servlet action method. Called by {@link #invoke(WebContext,Object[])}.
     *
     * @param context context from {@link ActionServlet#handle(WebContext) handle()} method
     * @param convertedParams converted parameters
     * @exception ActionException if action method throws exception
     * @exception ResourceUnavailableException propagated exception
     * @return template to be displayed
     */
    Template reinvoke(WebContext context, Object[] convertedParams) 
    throws ActionException, ResourceUnavailableException {
        try {
            convertedParams[0] = context;
            Template template;

            if (callInvokeBefore) {
                // servlet method called before each action
                log.debug("Invoking 'beforeInvoke()' method of action '" + actionName + "'");
                    
                if ((template = servlet.beforeInvoke(context, actionName, convertedParams)) != null) {
                    log.debug("Method 'beforeInvoke()' returns a non null value -> action" +
                                " '" + actionName + (callInvokeAfter?"' and 'afterInvoke()' ": "'") + 
                                " won't be invoked");
                    return template;   // !!!
                }
            }

            // invoke action method
            log.debug("Invoking method '" + getMethodName() + 
                        "' implementing action '" + actionName + "'");
            template = (Template) method.invoke(servlet, convertedParams);

            if (callInvokeAfter) {
                // servlet method called after each action
                log.debug("Invoking 'afterInvoke()' method of action '" + actionName + "'");
                    
                if ((template = servlet.afterInvoke(template, context, actionName, convertedParams)) == null)
                    throw new ActionException("Method 'afterInvoke()' returns null");
            }

            if (template == null)
                throw new ActionException("Method '" + getMethodName() + 
                                            "' implementing action '" + actionName + 
                                            "' returns null");
            return template;
        } catch (InvocationTargetException e) {
            Throwable target = e.getTargetException();
            
            log.exception(target);
            if (target instanceof ActionException) throw (ActionException) target;
            if (target instanceof ResourceUnavailableException) throw (ResourceUnavailableException) target;

            throw new ActionException("Unexpected exception " + target.toString() +
                                      " thrown by of invoked method '" + 
                                      getMethodName() + "()' implementing action '" + 
                                      getActionName() + "': " + target.getMessage());
        } catch (IllegalAccessException e) {
            log.exception(e);
            throw new ActionException(e.getMessage());
        }
    }
}
