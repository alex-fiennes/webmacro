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

import java.io.*;
import java.util.*;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import org.webmacro.broker.ResourceUnavailableException;
import org.webmacro.engine.*;
import org.webmacro.broker.*;
import org.webmacro.util.*;

/**
 * Highly configurable servlet that maps HTTP requests to servlet methods and provides 
 * automatic conversion of HTTP parameters to values of Java types.
 * 
 * <P>Example of a simple <TT>LoginServlet</TT> demonstrates how to make ActionServlet work.
 *
 * <OL>
 * <LI><H3>Specify the 'action' in HTML or template file</H3>
 * In the most simple case, ActionSevlet requires only one mandatory parameter named 'action' 
 * to be defined in the HTTP request. For our example, we will also have to define 
 * <TT>userName</TT> and <TT>password</TT> fields in the HTML form:<P>
 * 
 * <P><PRE>
 * &lt;FORM ACTION="LoginServlet" METHOD="POST"&gt;
 * User name: &lt;INPUT TYPE="Text" <B>NAME="userName"</B>&gt;
 * Password:  &lt;INPUT TYPE="Password" <B>NAME="password"</B>&gt;
 * &lt;INPUT TYPE="Submit" <B>NAME="action"</B> VALUE="Login"&gt;
 * &lt;/FORM&gt;
 * </PRE>
 *
 * Note: A similar effect can be achieved with GET method - using a link like:<BR>
 * <TT>&lt;A&nbsp;HREF="LoginServlet?action=Login+userName=John+password=18x79Z"&gt;
 * login John&lt;/A&gt;(password=18x79Z)</TT><P> 
 *
 * Note: The parameter names are case-and-encoding sensitive and their names must 
 * correspond to appropriate action definitions in the config file.
 *
 * <P><LI><H3>Create the config file</H3>
 * The config file must be named <TT>YourServletClassName.properties</TT>, i.e.
 * <TT>LoginServlet.properties</TT> in our example. This file must be put somewhere 
 * into your CLASSPATH.<P>
 * 
 * The file has the Properties-style file format:<P>
 *
 * <PRE>
 * # hash starts comments
 * newSession.template = templateShownUponNewSession.wm
 *
 * # action definitions
 * action.0 = "actionName0" methodImplementingAction0(parameters1)
 * action.1 = "actionName1" methodImplementingAction1(parameters2)
 *   ...
 * action.N = "actionNameN" methodImplementingActionN(parametersN)
 * </PRE>
 *
 * <UL>
 * <LI><TT>newSession.template</TT> parameter specifies the template displayed
 * by the servlet upon a new HTTP session, for example when the browser loads the servlet 
 * page for the first time.
 *
 * <LI>An action definition specifies the <EM>action name</EM> and the <EM>implementing 
 * method</EM> which is invoked by the action. The parameters types must be either 
 * primitive types or fully qualified class names (except for classes from java.lang 
 * package) or arrays of them. The return type and thrown exceptions must not be specfyied 
 * here. Do not mistake 'method definition' part in config with the real method definition 
 * in Java code!
 * </UL>
 *
 * <P>Continuing in our example, the <TT>LoginServer.properties</TT> file will contain these two
 * lines:<P>
 *
 * <PRE>
 * newSession.template = Login.wm
 * action.0 = "Login" login(String userName, String password)
 * </PRE>
 *
 * <LI><H3>Implement the servlet</H3>
 * Simply program your servlet as a usual Java application. Just keep in mind that the 
 * methods implementing actions:<P>
 *
 * <OL>
 * <LI>must be declared public,
 * <LI>their return type must be <TT>org.webmacro.engine.Template</TT>,
 * <LI>their first parameter must be of type <TT>org.webmacro.servlet.WebContext</TT>,
 * <LI>may declare only <TT>org.webmacro.servlet.ActionException</TT> and/or 
 *     <TT>org.webmacro.broker.ResourceUnavailableException</TT> in its throws clause,
 * <LI>the parameter names defined in config must be equal to the parameter names in the 
 *     HTTP request (i.e. in HTML/template file) and need not to have same names as in Java 
 *     source code (for more info on how parameter types are converted see {@link 
 *     TypeHandler TypeHandler}).
 * </OL><P>
 *
 * To create the servlet do following:<P>
 *
 * <OL>
 * <LI>Subclass ActionServlet and define methods implementing actions,
 * <LI>Put some startup code (if any) into {@link #start() start()} method (don't forget
 *     to call <TT>super.start()</TT>),
 * <LI>Specify what to do if conversion error occurs and when required action is not implemented
 *     by overriding {@link #conversionError(WebContext,String,ConversionException) 
 *     conversionError()} and {@link #unassignedAction(WebContext,String) unassignedAction()} 
 *     methods.
 * <LI>Optionally, define methods ivoked before and after each action - {@link 
 *     #beforeInvoke(WebContext,String,Object[]) beforeInvoke()} and {@link 
 *     #afterInvoke(Template,WebContext,String,Object[]) afterInvoke()}.
 * <LI>Optionally, define {@link #beforeConversion(WebContext, String) beforeConversion()} 
 *     method, which is called before any conversion handler is invoked.
 * </OL>
 *
 * <P>The <TT>LoginServlet</TT> implementation from our example comes here:
 *
 * <PRE>
 * <B>public class LoginServlet extends ActionServlet</B> {
 *
 *     // implements 'Login' action
 *     <B>public login(WebContext context, String userName, String password) 
 *     throws ResourceUnavailableException</B> {
 *         // only John may log in...
 *         if ("John".equals(userName) && "18x79Z".equals(password)) 
 *             return getTemplate(SuccessfulLogin.wm);
 *         else {
 *             // others will get 'Bad login' mesage back on the login page
 *             context.put("badLogin", Boolean.TRUE);
 *             return Login.wm;
 *         }
 *     }
 *
 *     // can't be invoked in this simple example, so...
 *     <B>protected Template conversionError(WebContext context, 
 *                                        String actionName,
 *                                        ConversionException e)</B> {
 *         return null;   // ... do nothing in this method
 *     }
 *
 *     // just display the login page, if action is not equal to 'login'
 *     <B>protected Template unassignedAction(WebContext context, String actionName) 
 *     throws ResourceUnavailableException </B>{
 *         return getTemplate("Login.wm");
 *     }     
 * }
 * </PRE>
 *
 * <LI><H3>Desing servlet templates</H3>
 * To finish our example we need two templates:
 *
 * <UL>
 * <LI><TT>Login.wm</TT>, which contains all the HTML text from above:
 *
 * <P><PRE>
 * &lt;HTML&gt;
 * &lt;BODY&gt;
 * #if ($badLogin){
 *     &lt;P&gt;&lt;FONT COLOR="red"&gt;Bad user name or password. 
 *     Try again.&lt;/FONT&gt;&lt;P&gt;
 * }
 * &lt;FORM ACTION="LoginServlet" METHOD="POST"&gt;
 * User name: &lt;INPUT TYPE="Text" <B>NAME="userName"</B>&gt;
 * Password:  &lt;INPUT TYPE="Password" <B>NAME="password"</B>&gt;
 * &lt;INPUT TYPE="Submit" <B>NAME="action"</B> VALUE="Login"&gt;
 * &lt;/FORM&gt;
 * &lt;/BODY&gt;
 * &lt;/HTML&gt;
 * </PRE>
 *
 * <LI><TT>SuccessfulLogin.wm</TT>, which contains:
 *
 * <P><PRE>
 * &lt;HTML&gt;
 * &lt;BODY&gt;
 * &lt;P&gt;&lt;CENTER&gt;The user $userName has been 
 * successfully logged in.&lt;/CENTER&gt;&lt;P&gt;
 * &lt;/BODY&gt;
 * &lt;/HTML&gt;
 * </PRE>
 *
 * </UL>
 *
 * <LI><H3>Compile, setup you servlet runner, and enjoy!</H3>
 * </OL>
 *
 * <H4>Notes on synchronization</H4> 
 * Action methods and the following methods inherited from ActionServlet:
 * <UL>
 * <LI>{@link #conversionError(WebContext,String,ConversionException) conversionError()},
 * <LI>{@link #beforeInvoke(WebContext,String,Object[]) beforeInvoke()},
 * <LI>{@link #afterInvoke(Template,WebContext,String,Object[]) afterInvoke()},
 * <LI>{@link #beforeConversion(WebContext,String) beforeConversion()}
 * </UL>
 * are not synchronized by default. This means that two or more of these methods can be 
 * called at a time. All the synchronization needed it is left up to the developer.
 * </UL>
 *
 * @see javax.servlet.http.HttpSession
 * @see org.webmacro.engine.Template
 * @see org.webmacro.servlet.WebContext
 * @see TypeHandler
 */
public abstract class ActionServlet extends WMServlet {
    /** 
     * Table of this servlet's actions.
     * @see Action
     */
    private final Hashtable actions = new Hashtable();
    
    /** 
     * Table of action names (used by {@link #getActionName(int) getActionName}).
     */
    private final Vector actionNamesByIndex = new Vector();

    /** Page shown upon a new `session */
    private Template newSessionTemplate;

    /**
     * Indicates correct initialization - null means OK.
     */
    private String _problem = "ActionServlet was not initialized by calling start() method!";
    
    /** ActionServlet log. */
    private final Log log = new Log("ActionServlet", "ActionServlet");

    /**
     * Saved parameters of the last actions for each session. Used by {@link 
     * #handle(WebContext) handle()} and {@link Action#invoke(WebContext, Object[])} methods.
     */
    Hashtable lastActions = new Hashtable();
    
    /** Sleeping period of lastActionData reaper in miliseconds (default: 1 minute) */
    protected int actionDataReaperTimeout = 60*1000;

    /** 
     * Thread that periodically removes unused lastActionData from {@link #lastActions 
     * lastActions} table (= data from invalidated sessions).
     */
    private Thread lastActionDataCleaner = new Thread() {
        /**
         * Once per actionDataReaperTimeout removes unused lastActionData.
         */
        public void run() {
            for (;;)
                for (Enumeration keys = lastActions.keys(); keys.hasMoreElements(); ) {
                    HttpSession session = null;
                    
                    try {
                        session = (HttpSession) keys.nextElement();
                        session.getId();
                        sleep(actionDataReaperTimeout);
                    } catch(IllegalStateException e) {
                        lastActions.remove(session);
                        log.debug("Removed lastActionData of invalid session - current number of sessions:" + lastActions.size());
                    } catch(NoSuchElementException e) {
                        keys = lastActions.keys();
                    } catch (InterruptedException e) {}
                }
        }
    };

    /** Loaded {@link TypeHandler TypeHandlers}. */
    private final Hashtable typeHandlers = new Hashtable();
    
    /** Type handlers of String and primitive types. */
    {
        TypeHandler stringHandler = new SimpleTypeHandler() {
            public Object convert(WebContext context, String parameterValue) 
            throws ConversionException {
                return parameterValue;
            }
        };
        typeHandlers.put("String", stringHandler);
        typeHandlers.put("java.lang.String", stringHandler);

        typeHandlers.put("boolean", new SimpleTypeHandler() {
            public Object convert(WebContext context, String parameterValue) 
            throws ConversionException {
                if ("true".equals(parameterValue)) return new Boolean(true);
                if ("false".equals(parameterValue)) return new Boolean(false);
                throw new ConversionException("Cannot convert to boolean");
            }
        });

        typeHandlers.put("byte", new SimpleTypeHandler() {
            public Object convert(WebContext context, String parameterValue) 
            throws ConversionException {
                try {
                    return Byte.valueOf(parameterValue);
                } catch(NumberFormatException e) {
                    throw new ConversionException("Cannot convert to byte", e);
                }
            }
        });

        typeHandlers.put("double", new SimpleTypeHandler() {
            public Object convert(WebContext context, String parameterValue) 
            throws ConversionException {
                try {
                    return Double.valueOf(parameterValue);
                } catch(NumberFormatException e) {
                    throw new ConversionException("Cannot convert to double", e);
                }
            }
        });

        typeHandlers.put("float", new SimpleTypeHandler() {
            public Object convert(WebContext context, String parameterValue) 
            throws ConversionException {
                try {
                    return Float.valueOf(parameterValue);
                } catch(NumberFormatException e) {
                    throw new ConversionException("Cannot convert to float", e);
                }
            }
        });

        typeHandlers.put("int", new SimpleTypeHandler() {
            public Object convert(WebContext context, String parameterValue) 
            throws ConversionException {
                try {
                    return Integer.valueOf(parameterValue);
                } catch(NumberFormatException e) {
                    throw new ConversionException("Cannot convert to int", e);
                }
            }
        });

        typeHandlers.put("long", new SimpleTypeHandler() {
            public Object convert(WebContext context, String parameterValue) 
            throws ConversionException {
                try {
                    return Long.valueOf(parameterValue);
                } catch(NumberFormatException e) {
                    throw new ConversionException("Cannot convert to long", e);
                }
            }
        });

        typeHandlers.put("short", new SimpleTypeHandler() {
            public Object convert(WebContext context, String parameterValue) 
            throws ConversionException {
                try {
                    return Short.valueOf(parameterValue);
                } catch(NumberFormatException e) {
                    throw new ConversionException("Cannot convert to short", e);
                }
            }
        });

        typeHandlers.put("char", new SimpleTypeHandler() {
            public Object convert(WebContext context, String parameterValue) 
            throws ConversionException {
                if (parameterValue.length() != 1)
                    throw new ConversionException("Cannot convert to char");
                return new Character(parameterValue.charAt(0));
            }
        });
    }

    /**
     * Initialization of ActionServletu. Must be called as <TT>super.start()</TT>
     * if overriden.
     */
    public void start() {
        String configName = null;
        int actionIndex = -1;

        log.info("Initialization startup");
            
        try {
            // action config - filename is taken from class name and .property suffix
            String className = getClass().getName();
            configName = className.substring(className.lastIndexOf(".") + 1) + "_handlers.properties";

            // read ActionServlet config (type handlers)
            Properties handlersProperties = new Properties();
            InputStream in = ClassLoader.getSystemResourceAsStream(configName);
            if (in != null) {
                handlersProperties.load(in);
                in.close();
            }
            
            configName = className.substring(className.lastIndexOf(".") + 1) + ".properties";
            in = ClassLoader.getSystemResourceAsStream(configName);
            PropertyResourceBundle config = new PropertyResourceBundle(in);
            in.close();
            
            newSessionTemplate = getTemplate(config.getString("newSession.template"));

            // assing methods to actions
            for (actionIndex=0;;actionIndex++) {
                String str;
                
                try {
                    str = config.getString("action." + actionIndex);
                } catch (MissingResourceException e) {
                    // at least one action must exist
                    if (actionIndex > 0) break;
                    throw e;
                }
                
                Action action = new Action(this, typeHandlers, handlersProperties, str);
                String key = action.getActionName();
                    
                // put action to table
                if (actions.put(key, action) != null) 
                    throw new ConfigException("Action '" + key + "' is defined more than once");

                actionNamesByIndex.addElement(key);
            }

            // start cleaning thread
            lastActionDataCleaner.start();

            _problem = null;
            log.info("Initialization OK");
        } catch (ResourceUnavailableException e) {
            _problem = "Cannot get template of the key 'newSession.template' " +
                       "from configuration file '" + configName + "'";
            log.exception(e);
            log.error(_problem);
        } catch (NullPointerException e) {
            _problem = "Cannot read configuration file '" + configName + "'";
            log.exception(e);
            log.error(_problem);
        } catch (IOException e) {
            _problem = "Error while reading configuration file '" + configName + "'";
            log.exception(e);
            log.error(_problem);
        } catch (MissingResourceException e) { 
            _problem = "Value 'action." + actionIndex + "' is not defined in configuration " +
                       " file '" + configName + "'";
            log.exception(e);
            log.error(_problem);
        } catch (ConfigException e) { 
            _problem = e.getMessage();
            log.exception(e);
            log.error(_problem);
        } catch (InitException e) { 
            System.err.println("** " + e.getMessage() + "\n\n" + e);
            _problem = e.getMessage();
            log.exception(e);
            log.error(_problem);
        } catch (Exception e) {
            _problem = "Unexpected initialization error: " + e.getMessage();
            log.exception(e);
        }
    }

    /**
     * Handles HTTP request. This method controls invocation of methods according
     * to the configuration file using these rules:<P>
     *
     * <OL>
     * <LI>If the value of 'action' parameter is not defined in the HTTP reqest of a new 
     * session, template of key 'newSession.template' from configuration file is returned.
     *
     * <LI>If the value of 'action' parameter is not defined in the HTTP reqest of a valid
     * session, last action is reinvoked - using same parameter values (this happens when
     * browser reloads the same page).
     *
     * <LI> If the value of 'action' parameter is defined, the assigned action method is 
     * called. If a conversion error occurs (before the action method is called), {@link 
     * #conversionError(WebContext,String,ConversionException) conversionError()} method.
     *
     * <LI> If the value of 'action' parameter is not assigned to any action method,
     * {@link #unassignedAction(WebContext, String) unassignedAction()} is called.
     * </OL>
     *
     * @return template to be displayed
     * @exception ActionException on error of action method
     * @exception HandlerException on other error
     * @see TypeHandler
     */
    public final Template handle(WebContext context) throws HandlerException {
        if (_problem != null) return error(context, _problem);
        HttpSession session = context.getSession();

        try {
            String actionName;
            Action action;

            // assign action
            try {
                actionName = context.getForm("action").trim();

                if ((action = (Action) actions.get(actionName)) == null)
                    return unassignedAction(context, actionName);

            } catch (NullPointerException e) {
                // 'action' parameter not defined
                
                // new session -> show default page
                if (session.isNew()) return newSession(context);

                // page reload -> reinvoke last method
                LastActionData la;
                if ((la = (LastActionData) lastActions.get(context.getSession().getId())) != null) 
                    return la.reinvokeLastAction(context);
                else return newSession(context);
            }

            // parameters passed to action method
            Object[] params = new Object[action.paramNames.length];

            // get required parameters from HTTP request
            HttpServletRequest request = context.getRequest();
            params[0] = context;
            
            try {
                for (int i=1; i<params.length; i++) {
                    String[] vals = request.getParameterValues(action.paramNames[i]);

                    if (vals == null)
                        throw new ConversionException("Parameter '" + action.paramNames[i] 
                                                      + "' does not exist");
                    else if (vals.length == 1) params[i] = vals[0];
                        else params[i] = vals.clone();
                }

                return action.invoke(context, params);
            } catch (ConversionException e) {
                return conversionError(context, actionName, e);
            }
        } catch (ResourceUnavailableException e) {
            log.exception(e);
            throw new HandlerException(e.getMessage());
        } catch (RuntimeException e) {
            log.exception(e);
            throw e;
        }
    }
    
    /**
     * Returns the name of action as defined in configuration file.
     * @param index index of action (starts by 0)
     * @return null action of this index does not exist or initialization did not
     *         finished sucessfuly
     */
    protected final String getActionName(int index) {
        try {
            return (String) actionNamesByIndex.elementAt(index);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Returns all action names from configuration file.
     */
    protected final Enumeration getActionNames() {
        Vector v = new Vector();

        String name = "";
        for (int i=0; name != null; i++) {
            if ((name = getActionName(i)) != null) v.addElement(name);
        }

        return v.elements();
    }

    /**
     * Method called upon a new session. Returns 'newSessionTemplate' if not overrriden.
     */
    protected Template newSession(WebContext context) {
        return newSessionTemplate;
    }

    /**
     * Method called before any parameter type handler is invoked. If not overriden -
     * does nothing.
     *
     * @param context context from {@link #handle(WebContext) handle()} method
     * @param actionName action to be invoked
     */
    protected void beforeConversion(WebContext context, String actionName) {}
    
    /**
     * Method called right before each action method. If not overriden - does nothing and 
     * returns null.
     *
     * @param context context from {@link #handle(WebContext) handle()} method
     * @param actionName action to be invoked
     * @param convertedParams converted parameteters, which will be passed to the
     *                        action method. <B>Note:</B> 'convertedParams[0]' is context 
     *                        from {@link #handle(WebContext) handle()} (is of type {@link 
     *                        org.webmacro.servlet.WebContext WebContext}).
     * @return template to be displayed (only if not null, the action 'actionName' will 
     *         be invoked)
     */
    protected Template beforeInvoke(WebContext context, 
                                    String actionName,
                                    Object[] convertedParams)
    throws ResourceUnavailableException, ActionException {
        return null;
    }

    /**
     * Method called right after each action method invocation. If not overriden - does 
     * nothing and returns the passed 'template'.
     *
     * @param context context from {@link #handle(WebContext) handle()} method
     * @param template template returned be action method
     * @param actionName invoked action name
     * @param convertedParams converted parameteters, which have been passed to the
     *                        action method. <B>Note:</B> 'convertedParams[0]' is context 
     *                        from {@link #handle(WebContext) handle()} (is of type {@link 
     *                        org.webmacro.servlet.WebContext WebContext}).
     * @return template to be displayed
     */
    protected Template afterInvoke(Template template,
                                   WebContext context, 
                                   String actionName,
                                   Object[] convertedParams)
    throws ResourceUnavailableException, ActionException {
        return template;
    }

    /**
     * Method called if 'action' parameter is defined but not assigned to any method.
     *
     * @param context context from {@link #handle(WebContext) handle()} method
     * @param actionName unassigned action name
     * @return template to be displayed
     * @exception ActionException on error of action method
     * @exception ResourceUnavailableException on error of action method
     */
    protected abstract Template unassignedAction(WebContext context, String actionName) 
    throws ResourceUnavailableException, ActionException;
    
    /**
     * Method called on parameter conversion error.
     *
     * @param context context from {@link #handle(WebContext) handle()} method
     * @param actionName action which cannot be called due to the error
     * @param e exception which caused the error
     * @return template to be displayed
     */
    protected abstract Template conversionError(WebContext context, 
                                                String actionName,
                                                ConversionException e) 
    throws ResourceUnavailableException, ActionException;
}
