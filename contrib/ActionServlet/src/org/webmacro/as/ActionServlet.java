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
import java.text.ParseException;
import java.lang.reflect.*;
import java.util.*;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.webmacro.engine.*;
import org.webmacro.servlet.*;
import org.webmacro.*;
import org.webmacro.util.*;

/**
 * Highly configurable servlet that enables mapping of HTTP requests to methods of
 * components.
 *
 * <OL><OL><A NAME="start"></A>
 * <FONT FACE="Arial" COLOR="green"><LI><H2>Definitions of terms</H2></FONT>
 *
 * <H3>1.1 Components</H3>
 * are objects which <A HREF="#acm">action methods</A> are invoked by HTTP requests.
 * Example:
 *
 * <P><IMG SRC="../../../../images/auth.gif" BORDER="0"><P>
 *
 * Each component is referenced by the <A HREF="#config">configuration file</A>, where
 * it has specified Java <B>class</B> and <B>persistence</B>.<P>
 *
 * Persistence determines durability of a component object and may be of three types:<P>
 *
 * <UL>
 * <LI><TT>"application"</TT> persistence - the component object exists in only in one
 *     copy per servlet. This is useful for implementing "global" methods that manipulate
 *     with common application data.
 * <LI><TT>"session"</TT> persistence - each HTTP session owns a single copy of the
 *     componenent object. This is useful for implementing "shopping carts" etc.
 * <LI><TT>"request"</TT> persistence - for each HTTP request is created a new component
 *     object (except for page reload, which uses the same component).
 * </UL>
 *
 * <P>The component class must have a public constructor with a single parameter of type
 * <TT>org.webmacro.as.ActionServlet</TT>, which is used to create the component. If
 * the component wants to be informed when it is no longer used by ActionServlet,
 * it must implement {@link Destroyed Destroyed} interface.
 *
 * <A NAME="acm"></A>
 * <H3>1.2 Action methods</H3>
 * implement the behaviour of a component.<P>
 *
 * All HTTP requests must have defined a value of HTTP parameter named '<B>action</B>' and,
 * optionally, '<B>form</B>' (if omitted, it has a default empty value: ""). After combination
 * of these two parameters values is chosen a component which action method is invoked.
 * For example, "login" action would invoke <TT>someComponent.login(...)</TT> method.<P>
 *
 * Minimum requirements for each action method are:
 *
 * <P><UL>
 * <LI>it must be defined as <TT>public</TT>,
 * <LI>it must have the return type of <TT>org.webmacro.Template</TT> (this
 *     means that all action methods react to HTTP request by returning a
 *     <A HREF="#template">template</A>),
 * <LI>it may throw only <TT>org.webmacro.as.ActionException</TT>,
 * <LI>it must have its first parameter of type <TT>org.webmacro.servlet.WebContext</TT>.
 * </UL><P>
 *
 * Optionally, action methods may have additional parameters, to which are passed values
 * of appropriate HTTP parameters. Values of HTTP parameters are converted automatically
 * to Java types by <A HREF="#tha">type handlers</A>}.
 *
 * <A NAME="template"></A>
 * <H3>1.3 Templates</H3>
 * You should be familiar with <A HREF="http://www.webmacro.org">WebMacro</A> templates
 * already. The new thing is that you can pass HTTP parameter value(s) directly to the
 * component's action method.<P>
 *
 * <B>Example:</B> the following HTML form defines a parameter named <TT>userName</TT>
 * and <TT>password</TT> (and of course, the mandatory 'action'):
 *
 * <PRE>
 * &lt;FORM METHOD="POST"&gt;
 *    User name: &lt;INPUT TYPE="Text" <B>NAME="userName"</B>&gt;
 *    Password:  &lt;INPUT TYPE="Password" <B>NAME="password"</B>&gt;
 *    &lt;INPUT TYPE="Submit" <B>NAME="action"</B> VALUE="Login"&gt;
 * &lt;/FORM&gt;
 * </PRE>
 *
 * Values of these parameters can be easily passed to an action method:<P>
 *
 * <PRE>
 * public Template login(WebContext context, String <B>userName</B>, String <B>password</B>)
 * </PRE>
 *
 * Note: A similar effect can be achieved by GET method - using a link like:<BR>
 * <TT>&lt;A&nbsp;HREF="LoginServlet?action=Login&amp;userName=John+password=18x79Z"&gt;
 * login as John&lt;/A&gt; (hint: password=18x79Z)</TT><P>
 *
 * <A NAME="tha"></A>
 * <H3>1.4 Type handlers</H3>
 *
 * See {@link TypeHandler TypeHandler}, {@link SimpleTypeHandler SimpleTypeHandler} and
 * {@link CompositeTypeHandler CompositeTypeHandler} for more information.
 *
 * <A NAME="config"></A>
 * <H3>1.5 Configuration file</H3>
 * has a simple XML format and is divided into three main parts -
 * <A HREF="#el_templates"><TT>&lt;templates&gt;</TT></A> (optional),
 * <A HREF="#el_handlers"><TT>&lt;type-handlers&gt;</TT></A> (optional) and
 * <A HREF="#el_components"><TT>&lt;components&gt;</TT></A> (mandatory):<P>
 *
 * <PRE>
 * &lt;?xml version="1.0"?&gt;
 * &lt;!DOCTYPE application SYSTEM
 *           "http://dione.zcu.cz/~toman40/ActionServlet/dtd/ActionServlet_0_5.dtd"&gt;
 *
 * &lt;application&gt;
 *    &lt;templates&gt;
 *    ...
 *    &lt;/templates&gt;
 *
 *    &lt;type-handlers&gt;
 *    ...
 *    &lt;/type-handlers&gt;
 *
 *    &lt;components&gt;
 *    ...
 *    &lt;/components&gt;
 * &lt;/application&gt;
 * </PRE>
 *
 * <B>1.5.1 The root <TT>&lt;application&gt;</TT> element</B><P> may have optional <TT>repository</TT>
 * attribute, which specifies a directory where servlet components and other classes are stored
 * [TODO: future versions may accept also .jar repositories]. This attribute can be used only if you
 * don't subclass ActionServlet, otherwise all classes are expected to be loaded from <TT>CLASSPATH</TT>
 * by the servlet runtime, as usually. Subelements of <TT>&lt;application&gt;</TT> follow.<P>
 *
 * <B>Example:</B>
 * <PRE>
 * &lt;application repository="c:/servlets/App1/classes"&gt;
 *    ...
 * &lt;/application&gt;
 * </PRE>
 *
 * <B>1.5.2 <A NAME="el_templates"><TT>&lt;templates&gt;</TT></A> element</B><P> contains one or more
 * <TT>&lt;template&gt;</TT> subelements used to specify:
 *
 * <UL>
 * <LI>which template is shown upon a new session - via <TT>is-new-session</TT> attribute (if this
 *     attribute never set to "true", the {@link #newSession(WebContext) newSession()} method is
 *     expected to do the job).
 * <LI>which <A HREF="#el_outvar"><TT>&lt;output-variable&gt;</TT></A>s will be set when the template
 *     is shown (optional).
 * </UL><P>
 *
 * Each <TT>&lt;template&gt;</TT> element must have a <TT>name</TT> attribute specifying the name of
 * the template.<P>
 *
 * <B>Example 1:</B >
 * <PRE>
 * &lt;templates&gt;
 *    &lt;template name="Login.wm" is-new-session="true"/&gt;
 * &lt;/templates&gt;
 * </PRE>
 *
 * <B>Example 2:</B>
 * <PRE>
 * &lt;templates&gt;
 *    &lt;template name="Login.wm"/&gt;
 *
 *    &lt;template name="Main.wm" is-new-session="true"&gt;
 *       &lt;output-variable name="isAuth" component="Authenticator" value="isAuthenticated()"/&gt;
 *       &lt;output-variable name="user" component="Authenticator" value="getUser()"/&gt;
 *    &lt;/template&gt;
 * &lt;/templates&gt;
 * </PRE>
 *
 * <B>1.5.3 <A NAME="el_handlers"><TT>&lt;type-handlers&gt;</TT></A> element</B><P> contains one
 * or more <TT>&lt;type-handler&gt;</TT> subelements, that bind <A HREF="#tha">type handlers</A> to
 * appropriate Java types.<P>
 *
 * <B>Example:</B>
 * <PRE>
 * &lt;type-handlers&gt;
 *    &lt;type-handler type="my.types.Email" class="my.handlers.EmailHandler"/&gt;
 *    &lt;type-handler type="java.util.Date" class="my.handlers.DateHandler"/&gt;
 * &lt;/type-handlers&gt;
 * </PRE>
 *
 * <B>1.5.4 <A NAME="el_components"><TT>&lt;components&gt;</TT></A> element</B><P> contains one or
 * more <TT>&lt;component&gt;</TT> elements (with attributes <TT>class</TT> and <TT>persistence</TT>),
 * which may contain one or more <A HREF="#el_action"><TT>&lt;action&gt;</TT></A> subelements.<P>
 *
 * <B>Example:</B>
 * <PRE>
 * &lt;components&gt;
 *    &lt;component class="Authenticator" persistence="application"&gt;
 *       &lt;action name="Login" method="login(String userName, String password)"/&gt;
 *    &lt;/component&gt;
 *
 *    &lt;component class="SearchEngine" persistence="application"&gt;
 *       &lt;action name="Search" method="search(String str)"/&gt;
 *       &lt;action form="Status" name="OK" method="getStatus()"/&gt;
 *    &lt;/component&gt;
 * &lt;/components&gt;
 * </PRE>
 *
 * <B>1.5.4.1 One or more <A NAME="el_action"><TT>&lt;action&gt;</TT></A> elements</B>,<P> which bind
 * actions to components' action methods, can be specified inside each <TT>&lt;component&gt;</TT>
 * element. Element <TT>&lt;action&gt;</TT> has attributes:<P>
 *
 * <UL>
 * <LI><TT>name</TT> - specifies the name of 'action' (i.e. value of HTTP parameter
 *     'action'), which will be bound to this component's action <TT>method</TT>,
 * <LI>optional <TT>form</TT> attribute - helps distinguish between different forms
 *     with same actions (for example, if two forms contain same <TT>"OK"</TT> submit
 *     buttons),
 * <LI><TT>method</TT> - contains action method declaration, which specifies
 *     method name and its parameters (except for the first one, which is always <TT>
 *     WebContext</TT>). The return type and thrown exceptions must NOT be specified
 *     here (reasons - see <A HREF="#acm">above</A>). The types of parameters must be
 *     either primitive or fully qualified class names (except for classes from
 *     <TT>java.lang</TT> package). Names of the parameters from the <TT>method</TT>
 *     attribute correspond to HTTP parameter names!!! (Names of formal action method
 *     parameters in the source code may be different).
 * </UL>
 *
 * <P><B>Example:</B>
 * <PRE>
 * &lt;component ...&gt;
 *    &lt;action form="Search" name="OK" method="search(String str)"/&gt;
 *    &lt;action form="Submit" name="OK" method="newURL(java.net.URL url)"/&gt;
 * &lt;/component&gt;
 * </PRE>
 *
 * Element <TT>&lt;action&gt;</TT> can optionally contain  one or more
 * <A HREF="#el_outvar"><TT>&lt;output-variable&gt;</TT></A> subelements, which put additional
 * variables to WebContext of the action.<P>
 *
 * <B>Example:</B>
 * <PRE>
 * &lt;action name="Login" method="login(String userName, String password)"&gt;
 *    &lt;output-variable name="user" component="Authenticator" value="getUser()"/&gt;
 * &lt;/action&gt;
 * </PRE>
 * </UL>
 *
 * <B>1.5.5 One or more <A NAME="el_outvar"><TT>&lt;output-variable&gt;</TT></A> elements</B><P>
 * can be specified inside <TT>&lt;template&gt;</TT> and/or <TT>&lt;component&gt;</TT> elements. It
 * has three attributes:<P>
 *
 * <UL>
 * <LI><TT>name</TT> - determines name of the template $variable,
 * <LI><TT>component</TT> - is optional and specifies the component's class from which the
 *     <TT>value</TT> taken (it is instantiated if it doesn't yet exist),
 * <LI><TT>value</TT> - operator applied to the <TT>component</TT> (a method call, for example)
 *     or to a $variable.
 * </UL><P>
 *
 * Note 1: Output variables are evaluated in the specified order.<BR>
 * Note 2: First are set output variables of <TT>&lt;action&gt;</TT> and at last of
 *         <TT>&lt;template&gt;</TT>.
 *
 * <P><B>Example:</B> set $user to Authenticator.getUser() and $name to $user.getName()
 * <PRE>
 * &lt;output-variable name="user" component="Authenticator" value="getUser()"/&gt;
 * &lt;output-variable name="name" value="$user.Name"/&gt;
 * </PRE>
 *
 * <B>1.5.6 All-in-one example</B><P>
 *
 * An example of XML configuration file may look like this:
 *
 * <PRE>
 * &lt;?xml version="1.0"?&gt;
 * &lt;!DOCTYPE application SYSTEM
 *           "http://dione.zcu.cz/~toman40/ActionServlet/dtd/ActionServlet_0_5.dtd""&gt;
 *
 * &lt;application repository="<B>C:/servlets/shop/classes</B>"&gt;
 *    &lt;templates&gt;
 *       &lt;template name="Login.wm"/&gt;
 *
 *       &lt;template name="Main.wm" is-new-session="true"&gt;
 *          &lt;output-variable name="isAuth" component="Authenticator" value="isAuthenticated()"/&gt;
 *          &lt;output-variable name="user" component="Authenticator" value="getUser()"/&gt;
 *       &lt;/template&gt;
 *    &lt;/templates&gt;
 *
 *    &lt;type-handlers&gt;
 *       &lt;type-handler name="<B>shop.types.Email</B>"
 *                     class="<B>shop.handlers.EmailHandler</B>"/&gt;
 *    &lt;/type-handlers&gt;
 *
 *    &lt;components&gt;
 *       &lt;component class="<B>shop.Main</B>" persistence="<B>application</B>"&gt;
 *          &lt;action name="<B>showMain</B>" method="</B>showMain()</B>"/&gt;
 *
 *          &lt;action name="<B>showGoods</B>" method="<B>showGoods()</B>"&gt;
 *             &lt;output-variable name="<B>goods</B>" value="<B>getGoods()</B>"/&gt;
 *          &lt;/action&gt;
 *       &lt;/component&gt;
 *
 *       &lt;component class="<B>shop.Toolbar</B>" persistence="<B>session</B>"/&gt;
 *    &lt;/components&gt;
 * &lt;/application&gt;
 * </PRE>
 *
 * Note: A XML configuration filename must be passed to ActionServlet via a servlet init
 *       parameter named <TT>"ActionConfig"</TT> (consult your servlet runtime
 *       documentation how to do it).
 *
 * <P><FONT FACE="Arial" COLOR="green"><LI><H2>HTTP request processing</H2></FONT>
 *
 * HTTP requests are handled as follows:<P>
 *
 * <OL>
 * <LI>A HTTP request is passed to {@link #handle(WebContext) handle()} method.
 *     ActionServlet requires that 'action' (and optional 'form') HTTP parameters
 *     are defined in the request.
 * <LI>A component, which action method is bound to the 'action', is created (with respect
 *     to its persistance), if it doesn't yet exist.
 * <LI>{@link #beforeConversion(WebContext,String,String) beforeConversion()} method of
 *     ActionServlet is called.
 * <LI>HTTP parameter values are converted to the values of Java types via type handlers.
 * <LI>If overriden, {@link #beforeInvoke(WebContext,String,String,Object[])
 *     beforeInvoke()} method is called.
 * <LI>Appropriate action method is invoked.
 * <LI>Output variables for action and returned template are set.
 * <LI>If overriden, {@link #afterInvoke(Template,WebContext,String,String,Object[])
 *     afterInvoke()} method is called.
 * </OL>
 *
 * <P>Exceptions to this schema happen if:<P>
 *
 * <UL>
 * <LI>conversion error occurrs - then {@link
 *     #conversionError(WebContext,String, String,ConversionException) conversionError()}
 *     method is called,
 * <LI>'action' parameter is not defined in the HTTP request or is not defined for
 *     specified 'form' - then {@link #unassignedAction(WebContext,String, String)
 *     unassignedAction()} is called.
 * </UL><P>
 *
 * ActionServlet informs components when they are no longer used - see {@link Destroyed
 * Destroyed} interface.
 *
 * <P><FONT FACE="Arial" COLOR="green"><LI><H2>ActionServlet usage</H2></FONT>
 *
 * The development work is divided into several (quite) separate tasks:<P>
 *
 * <UL>
 * <LI>Design of WebMacro templates - they provide the look and feel of your application.
 * <LI>Programming of components - they control the aplication flow.
 * <LI>Programming of type handlers - they enable custom HTTP parameter type conversions.
 * <LI>Creation of the configuration file - it binds actions to components methods and
 *     Java types to type handlers.
 * <LI>Deploying of the application - depends on your servlet runtime.
 * <LI>Setting up runtime environment - servlet init parameter <TT>"ActionConfig"</TT>
 *     must be passed to ActionServlet and also 'WebMacro.properties' file must set up
 *     (see WebMacro installation documentation).
 * </UL><P>
 *
 * Note: Because ActionServlet is no longer abstract class, subclassing is not necessary
 * unless you need to modify some of its functionality.
 * </OL>
 *
 * @see TypeHandler
 */
public class ActionServlet extends WMServlet {
    /**
     * Table of HTML forms: key = form name, value = hashtable of actions.
     */
    private final Hashtable forms = new Hashtable();

    /**
     * Table of components with "application" persistence:
     * key = component class, value = component object.
     */
    private final Hashtable applicationComponents = new Hashtable();

    /**
     * Table of components with "session" persistence:
     * key = http session, value = hashtable of component objects
     *                            (key = "component class", value = component object).
     */
    private final Hashtable sessionComponents = new Hashtable();

    /**
     * Table of all components classes:
     * key = component class name, value = instance of org.webmacro.as.ComponentData
     */
    private final Hashtable componentClasses = new Hashtable();

    /**
     * Table of output-variables of templates.
     * key = template.toString(), value = vector of output-variables
     */
    final Hashtable templateOutputVariables = new Hashtable();

    /**
     * Table of threads using sessions (uses {@link #getComponent(Class,boolean) getComponent()}.
     * key = thread hashcode, value = http session
     */
    final Hashtable threadSessions = new Hashtable();

    /**
     * Used for initialization of components.
     */
    private final Object[] _this = {this};

    /**
     * Page shown upon a new session.
     */
    private Template newSessionTemplate;

    /**
     * Indicates incorrect initialization - null means OK.
     */
    private String _problem = "ActionServlet was not initialized by calling the start() method!";

    /**
     * ActionServlet log.
     */
    Log log;

    /**
     * Saved parameters of the last actions for each session:
     * key = http session, value = lastActionData.
     *
     * Used by {@link #handle(WebContext) handle()} and
     * Action.invoke(...) methods.
     */
    Hashtable lastActions = new Hashtable();

    /**
     * Sleeping period of session data reaper in miliseconds (default: 1 minute)
     */
    protected int sessionReaperTimeout = 60*1000;

    /**
     * Thread that periodically removes unused session data.
     */
    private SessionReaper sessionReaper = new SessionReaper();

    /**
     * Housekeeping thread.
     */
    private class SessionReaper extends Thread {
        /** Flag telling sessionReaper to stop. */
        private boolean finish;

        public void run() {
            Enumeration keys1, keys2;
            HttpSession session = null;
            String id = null;

            finish = false;

            for (keys1 = lastActions.keys(), keys2 = sessionComponents.keys();;) {
                if (finish) return;

                // clean last action data
                try {
                    session = (HttpSession) keys1.nextElement();
                    id = session.getId();
                } catch(IllegalStateException e) {
                    LastActionData la = (LastActionData)lastActions.get(session);
                    if (la.action.componentData.persistence == ComponentData.PERSISTENCE_REQUEST &&
                        la.component instanceof Destroyed) ((Destroyed)la.component).destroy();

                    lastActions.remove(session);
                    log.debug("Removed lastActionData of invalid session '" + id + "'");
                } catch(NoSuchElementException e) {
                    keys1 = lastActions.keys();
                }

                // clean session components
                try {
                    session = (HttpSession) keys2.nextElement();
                    session.getId();
                } catch(IllegalStateException e) {
                    Object component = sessionComponents.get(session);
                    if (component instanceof Destroyed) ((Destroyed)component).destroy();

                    sessionComponents.remove(session);
                    log.debug("Removed session component of invalid session '" + id + "'");
                } catch(NoSuchElementException e) {
                    keys2 = sessionComponents.keys();
                }

                try {
                    sleep(sessionReaperTimeout);
                } catch (InterruptedException e) {}
            }
        }
    };

    /**
     * Component and type handler class loader.
     */
    private class ASClassLoader extends ClassLoader {
        private Hashtable cache = new Hashtable();
        private File repository;

        private ASClassLoader(File repository) {
            this.repository = repository;
        }

        /**
         * Loads a file from 'repository'.
         */
        private byte[] loadClassData(String className) throws ClassNotFoundException {
            File f = new File(repository, className.replace('.',File.separatorChar)+".class");
            byte[] b = new byte[(int)f.length()];

            try {
                FileInputStream fin = new FileInputStream(f);
                fin.read(b);
                fin.close();
            } catch(FileNotFoundException e) {
                throw new ClassNotFoundException(e.getMessage());
            } catch(IOException e) {
                _problem = "ActionServlet class loader error: " + e.getMessage();
                log.error(_problem, e);
            }
            return b;
        }

        /**
         * Loads a class from 'repository'.
         */
        public synchronized Class loadClass(String name, boolean resolve)
        throws ClassNotFoundException {
            Class c;

            try {
                // non-repository class loading
                c = Class.forName(name);
            } catch (ClassNotFoundException e) {
                if ("boolean".equals(name)) c = boolean.class;
                else if ("byte".equals(name)) c = byte.class;
                else if ("double".equals(name)) c = double.class;
                else if ("float".equals(name)) c = float.class;
                else if ("int".equals(name)) c = int.class;
                else if ("long".equals(name)) c = long.class;
                else if ("short".equals(name)) c = short.class;
                else if ("char".equals(name)) c = char.class;
                else if ("void".equals(name)) c = void.class;
                else
                    if ((c = (Class) cache.get(name)) == null) {
                        byte[] data = loadClassData(name);
                        c = defineClass(name, data, 0, data.length);
                        cache.put(name, c);
                        if (resolve) resolveClass(c);
                    }
            }

            return c;
        }
    }

    /**
     * Used in the initialization process.
     */
    ClassLoader loader;

    /**
     * Loaded {@link TypeHandler TypeHandlers}.
     */
    private final Hashtable typeHandlers = new Hashtable();

    /**
     * Type handlers of String and primitive types.
     */
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
                throw new ConversionException("Cannot convert '" + parameterValue + "' to boolean");
            }
        });

        typeHandlers.put("byte", new SimpleTypeHandler() {
            public Object convert(WebContext context, String parameterValue)
            throws ConversionException {
                try {
                    return Byte.valueOf(parameterValue);
                } catch(NumberFormatException e) {
                    throw new ConversionException("Cannot convert '" + parameterValue + "' to byte", e);
                }
            }
        });

        typeHandlers.put("double", new SimpleTypeHandler() {
            public Object convert(WebContext context, String parameterValue)
            throws ConversionException {
                try {
                    return Double.valueOf(parameterValue);
                } catch(NumberFormatException e) {
                    throw new ConversionException("Cannot convert '" + parameterValue + "' to double", e);
                }
            }
        });

        typeHandlers.put("float", new SimpleTypeHandler() {
            public Object convert(WebContext context, String parameterValue)
            throws ConversionException {
                try {
                    return Float.valueOf(parameterValue);
                } catch(NumberFormatException e) {
                    throw new ConversionException("Cannot convert '" + parameterValue + "' to float", e);
                }
            }
        });

        typeHandlers.put("int", new SimpleTypeHandler() {
            public Object convert(WebContext context, String parameterValue)
            throws ConversionException {
                try {
                    return Integer.valueOf(parameterValue);
                } catch(NumberFormatException e) {
                    throw new ConversionException("Cannot convert '" + parameterValue + "' to int", e);
                }
            }
        });

        typeHandlers.put("long", new SimpleTypeHandler() {
            public Object convert(WebContext context, String parameterValue)
            throws ConversionException {
                try {
                    return Long.valueOf(parameterValue);
                } catch(NumberFormatException e) {
                    throw new ConversionException("Cannot convert '" + parameterValue + "' to long", e);
                }
            }
        });

        typeHandlers.put("short", new SimpleTypeHandler() {
            public Object convert(WebContext context, String parameterValue)
            throws ConversionException {
                try {
                    return Short.valueOf(parameterValue);
                } catch(NumberFormatException e) {
                    throw new ConversionException("Cannot convert '" + parameterValue + "' to short", e);
                }
            }
        });

        typeHandlers.put("char", new SimpleTypeHandler() {
            public Object convert(WebContext context, String parameterValue)
            throws ConversionException {
                if (parameterValue.length() != 1)
                    throw new ConversionException("Cannot convert '" + parameterValue + "' to char");
                return new Character(parameterValue.charAt(0));
            }
        });
    }

    /**
     * Initialization of ActionServlet. Must be called as <TT>super.start()</TT>,
     * if overriden.
     */
    public void start() {
        log = getLog("ActionServlet");
        log.info("ActionServlet initialization startup");

        String configFilename = "&lt;unknown&gt;",
               template = "&lt;unknown&gt;";

        try {
            // XML config filename is taken from servlet init parameter 'ActionConfig'
            if ((configFilename = getInitParameter("ActionConfig")) == null)
                throw new InitException("Servlet init parameter 'config' must specify" +
                                        " fully qualified name to XML configuration file" +
                                        " (c:/servlets/MyServlet.xml, for example)");

            // parse XML configuration file
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            docBuilderFactory.setValidating(true);
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

            docBuilder.setEntityResolver(new EntityResolver() {
                private String DTD = "http://dione.zcu.cz/~toman40/ActionServlet/dtd/ActionServlet_0_5.dtd";

                public InputSource resolveEntity (String publicId, String systemId) {
                    try {
                        if (DTD.equals(systemId)) {
                            InputStream is = getClass().getResourceAsStream("/ActionServlet_0_5.dtd");
                            return new InputSource(new InputStreamReader(is));
                        } else log.warning("SYSTEM attribute of <!DOCTYPE> should be \"" + DTD + "\"");
                    } catch (Exception e) {
                        log.warning("Error while loading 'ActionServlet.dtd'" + ": " + e.getMessage());
                    }
                    return null;
                }
            });

            docBuilder.setErrorHandler(new org.xml.sax.ErrorHandler() {
                public void fatalError(SAXParseException e) throws SAXException {
                    throw e;
                }

                public void error (SAXParseException e) throws SAXParseException {
                    throw e;
                }

                public void warning (SAXParseException e) throws SAXParseException {}
            });

            Document doc = docBuilder.parse(new FileInputStream(configFilename));

            // <application>
            Element root = doc.getDocumentElement();
            root.normalize();

            // 'repository' attribute
            String rep = root.getAttribute("repository");

            if (ActionServlet.class == getClass()) {  // is ActionServlet subclassed?
                if ("".equals(rep))
                    throw new InitException("Attribute 'repository' of element"+
                              " <application> in the configuration file must be"+
                              " used if ActionServlet is NOT subclassed by your servlet");
                loader = new ASClassLoader(new File(rep));
            } else {
                if (!"".equals(rep))
                    throw new InitException("Attribute 'repository' of element"+
                              " <application> in the configuration file cannot be"+
                              " used if ActionServlet is subclassed by your servlet");
                loader = getClass().getClassLoader();
            }

            final NodeList list = root.getChildNodes();
            NodeList componentList = null;

            // pre-parse <components> (fills 'componentClasses')
            for(int i=0; i < list.getLength(); i++) {
                Node node = list.item(i);

                if (node.getNodeName().equals("components")) {
                    componentList = node.getChildNodes();

                    for(int j=0; j < componentList.getLength(); j++) {
                        node = componentList.item(j);
                        if (node.getNodeType() == Node.ELEMENT_NODE)    // <component>
                            parseComponent((Element)node);
                    }
                }
            }

            // parse <templates>, <type-handlers>
            for(int i=0; i < list.getLength(); i++) {
                Node node = list.item(i);

                if (node.getNodeName().equals("type-handlers")) {
                    NodeList list2 = node.getChildNodes();

                    for(int j=0; j < list2.getLength(); j++) {
                        node = list2.item(j);
                        if (node.getNodeType() == Node.ELEMENT_NODE)    // <type-handler>
                            parseTypeHandler((Element)node);
                    }
                } else if (node.getNodeName().equals("templates")) {
                    NodeList list2 = node.getChildNodes();

                    for(int j=0; j < list2.getLength(); j++) {
                        node = list2.item(j);

                        if (node.getNodeType() == Node.ELEMENT_NODE) {  // <template>
                            Template t = parseTemplate((Element)node);

                            // new session template (1)
                            if (t != null)
                                if (newSessionTemplate != null)
                                    throw new InitException("Attribute 'is-new-session' of" +
                                    "<template> element can be \"true\" only once");
                                else newSessionTemplate = t;
                        }
                    }
                }
            }

            // new session template (2)
            if (newSessionTemplate == null) {
                try {
                    getClass().getMethod("newSession", new Class[] {WebContext.class});
                } catch (NoSuchMethodException e) {
                    throw new InitException("Attribute 'is-new-session' of element" +
                          " <template> in the configuration file '" + configFilename + "' was" +
                          " not specified nor the 'newSession()' method was overriden by your servlet");
                }
            }

            // parse <component>s subelements
            for(int i=0; i < componentList.getLength(); i++) {
                Node node = componentList.item(i);

                if (node.getNodeName().equals("component"))
                    parseActions((ComponentData)componentClasses.get(((Element)node).getAttribute("class")),
                                 node.getChildNodes());
            }

            // start cleaning thread
            sessionReaper.start();

            _problem = null;
            log.info("ActionServlet initialization OK");
        } catch (IOException e) {
            _problem = "Error while reading the configuration file '" + configFilename + "'";
            log.error(_problem, e);
        } catch (InitException e) {
            StringTokenizer st = new StringTokenizer(e.getMessage(), "<>", true);
            String s;
            _problem = "";

            while (st.hasMoreTokens()) {
                if("<".equals(s = st.nextToken())) _problem += "&lt;";
                    else if(">".equals(s)) _problem += "&gt;";
                        else _problem += s;
            }

            log.error(_problem, e);
        } catch (SAXException e) {
            if (e instanceof SAXParseException)
                _problem = "Bad format of the configuration file '" + configFilename +
                           "' [line " + ((SAXParseException)e).getLineNumber() + "]: ";
            else _problem = "Bad format of the configuration file '" + configFilename + "': ";

            StringTokenizer st = new StringTokenizer(e.getMessage(), "<>", true);
            String s;

            while (st.hasMoreTokens()) {
                if("<".equals(s = st.nextToken())) _problem += "&lt;";
                    else if(">".equals(s)) _problem += "&gt;";
                        else _problem += s;
            }

            log.error(e.toString());
        } catch (ParserConfigurationException e) {
            _problem = "XML parser exception: " + e.getMessage();
            log.error(_problem, e);
        } catch (Exception e) {
            _problem = "Unexpected initialization error: " + e.getMessage();
            log.error(_problem, e);
        }
    }

    /**
     * Parses &lt;template&gt; element.
     */
    private Template parseTemplate(Element element) throws InitException {
        Template template = getWMTemplate(element.getAttribute("name"));
        boolean isNewSession = "true".equals(element.getAttribute("is-new-session"));

        NodeList list = element.getChildNodes();
        Vector outputVariables = new Vector();

        for(int i=0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node.getNodeName().equals("output-variable"))
                parseOutputVariable(outputVariables, (Element)node);
        }

        if (!outputVariables.isEmpty()) {
            if (templateOutputVariables.get(template.toString()) != null)
                throw new InitException("Template of the name '" + template + "' defined more then" +
                                        " once in the configuration file");

            templateOutputVariables.put(template.toString(), outputVariables);
        }

        if (isNewSession) return template;
        return null;
    }

    /**
     * Parses &lt;type-handler&gt; element.
     */
    private void parseTypeHandler(Element element) throws InitException {
        String type = element.getAttribute("type"),
               className = element.getAttribute("class");

        if (typeHandlers.containsKey(type))
            throw new InitException("Duplicit definition of handler for type '" + type + "'");

        try {
            TypeHandler handler = (TypeHandler) loader.loadClass(className).newInstance();

            // supported handlers: SimpleTypeHandler and CompositeTypeHandler
            if (!(handler instanceof SimpleTypeHandler) &&
                !(handler instanceof CompositeTypeHandler))
                throw new InitException("Type handler '" + className +
                                        "' must implement interface" +
                                        " org.webmacro.as.SimpleTypeHandler or " +
                                        " org.webmacro.as.CompositeTypeHandler");

            typeHandlers.put(type, handler);
            log.info("Type handler '" + className + "' for type '" + type + "' loaded.");
        } catch (ClassNotFoundException e) {
            throw new InitException("Type handler '" + className + "' not found" +
                                    (loader instanceof ASClassLoader? (" in the repository '"
                                    + ((ASClassLoader)loader).repository + "'"):""));
        } catch (ClassCastException e) {
            throw new InitException("Type handler '" + className +
                                    "' does not implement interface" +
                                    " org.webmacro.as.TypeHandler");
        } catch (InstantiationException e) {
            throw new InitException("Cannot instantiate type handler '" + className + "'");
        } catch (IllegalAccessException e) {
            throw new InitException("Type handler '" + className +
                                    "' perhaps does not have a public constructor" +
                                    " with no parameters");
        }
    }

    /**
     * Parses &lt;component&gt; element (but not its subelements!!!)
     */
    private void parseComponent(Element element)
    throws InitException {
        String componentClassName = element.getAttribute("class");
        String persistenceStr     = element.getAttribute("persistence");
        Class componentClass;
        Constructor constructor;
        int persistence;

        try {
            componentClass = loader.loadClass(componentClassName);
            constructor = componentClass.getConstructor(new Class[]{ActionServlet.class});
        } catch(ClassNotFoundException e) {
            throw new InitException("Cannot find component class '" + componentClassName +
                      (loader instanceof ASClassLoader? ("' in the repository '" +
                      ((ASClassLoader)loader).repository + "'"):"'"));
        } catch(NoSuchMethodException e) {
            throw new InitException("Component class '" + componentClassName +
                      "' doesn't have a public constructor with a single parameter " +
                      "of type 'org.webmacro.as.ActionServlet'");
        }

        if (persistenceStr.equals("application")) persistence = ComponentData.PERSISTENCE_APPLICATION;
            else if (persistenceStr.equals("session")) persistence = ComponentData.PERSISTENCE_SESSION;
               else persistence = ComponentData.PERSISTENCE_REQUEST;

        // component 'class' name must be unique in the application
        if (componentClasses.containsKey(componentClassName))
            throw new InitException("More than one component of &lt;class&gt; name '"+
                                    componentClassName + "' defined in the configuration file");

        componentClasses.put(componentClassName,
                             new ComponentData(componentClass, constructor, persistence));
    }

    /**
     * Parses &lt;action&gt;s node list.
     */
    private void parseActions(ComponentData componentData, NodeList list) throws InitException {
        String form, action, method;

        for(int i=0; i < list.getLength(); i++) {
            Node node = list.item(i);

            // <action>
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                form = ((Element)node).getAttribute("form");
                action = ((Element)node).getAttribute("name");
                method = ((Element)node).getAttribute("method");

                Vector outputVariables = new Vector();
                NodeList list2 = node.getChildNodes();

                // <output-variable>s
                for(int j=0; j < list2.getLength(); j++) {
                    node = list2.item(j);

                    if (node.getNodeType() == Node.ELEMENT_NODE)
                        parseOutputVariable(outputVariables, (Element)node);
                }

                // assign 'form'.'action' to 'method'
                Hashtable actions = (Hashtable) forms.get(form);
                if (actions == null) forms.put(form, actions = new Hashtable());

                if (actions.containsKey(action))
                    throw new InitException("Attempt to assing action '" + action +
                            "' of component '" + componentData.componentClass.getName() +
                            "' more than once");

                if ("".equals(form)) form = null;

                actions.put(action, new Action(this, form, action, componentData,
                                               typeHandlers, method, outputVariables));

                log.info("Action '" + (form == null?"": (form + "'.'")) + action +
                         "' bound to method '" + componentData.componentClass.getName() +
                         "." + method + "'");
            }
        }
    }

    /**
     * Parses &lt;output-variable&gt; element.
     */
    private void parseOutputVariable(Vector outputVariables, Element element)
    throws InitException {
        String className = element.getAttribute("component");
        ComponentData componentData = null;

        if (!"".equals(className)) {
            componentData = (ComponentData) componentClasses.get(className);

            if (componentData == null)
                throw new InitException("Value '" + className + "' of attribute 'component' in the" +
                                        " &lt;output-variable&gt; element must be a" +
                                        " component class name from the configuration file");
        }

        String value = element.getAttribute("value");

        try {
            outputVariables.addElement(new OutputVariable(this, componentData, element.getAttribute("name"), value));
        } catch (org.webmacro.engine.ParseException e) {
            throw new InitException("Error while parsing '" + value + "' (value of 'value' attribute" +
                                    " in the &lt;output-variable&gt; element):" + e.getMessage());
        }
    }

    /**
     * Called when the servlet is destroyed. Must be called as <TT>super.stop()</TT>,
     * if overriden.
     */
    protected void stop() {
        sessionReaper.finish = true;

        // destroy components with "request" persistance
        for (Enumeration e = lastActions.elements() ; e.hasMoreElements() ;) {
            LastActionData la = (LastActionData)e.nextElement();
            if (la.action.componentData.persistence == ComponentData.PERSISTENCE_REQUEST &&
                la.component instanceof Destroyed) ((Destroyed)la.component).destroy();
        }
        lastActions.clear();

        // destroy components with "session" persistance
        for (Enumeration e = sessionComponents.elements() ; e.hasMoreElements() ;) {
            Object component = e.nextElement();
            if (component instanceof Destroyed) ((Destroyed)component).destroy();
        }
        sessionComponents.clear();

        // destroy components with "application" persistance
        for (Enumeration e = applicationComponents.elements() ; e.hasMoreElements() ;) {
            Object component = e.nextElement();
            if (component instanceof Destroyed) ((Destroyed)component).destroy();
        }
        applicationComponents.clear();
    }

    /**
     * Handles HTTP request. This method controls invocation of methods according
     * to the configuration file using these rules:<P>
     *
     * <OL>
     * <LI>If the value of 'action' parameter is not defined in the HTTP reqest of a new
     * session, 'new-session-template' template from the configuration file is returned.
     *
     * <LI>If the value of 'action' parameter is defined in the HTTP reqest of a valid
     * session, last action is reinvoked - using same parameter values (this happens when
     * browser reloads the same page).
     *
     * <LI> If the value of 'action' parameter is defined, the assigned action method is
     * called. If a conversion error occurs (before the action method is invoked), {@link
     * #conversionError(WebContext,String,String,ConversionException) conversionError()}
     * is called.
     *
     * <LI> If the value of 'action' parameter is not assigned to any action method,
     * {@link #unassignedAction(WebContext, String, String) unassignedAction()} is called.
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

        threadSessions.put(new Integer(System.identityHashCode(Thread.currentThread())), session);

        try {
            // get actions hashtable from 'forms'
            String formName = context.getForm("form");
            Hashtable actions = (Hashtable) forms.get(formName == null? "": formName.trim());

            String actionName;
            Action action;

            // assign action
            if ((actionName = context.getForm("action")) == null) {
                // 'action' parameter not defined

                // new session -> show default page
                if (session.isNew()) return newSession(context);

                // page reload -> reinvoke last method
                LastActionData la;
                if ((la = (LastActionData) lastActions.get(session)) != null)
                    return la.reinvokeLastAction(context);

                // reload of the default page
                return newSession(context);
            } else {
                actionName = actionName.trim();

                if (actions == null ||
                    (action = (Action) actions.get(actionName)) == null)
                    return unassignedAction(context, formName, actionName);
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
                        throw new ConversionException("HTTP parameter '" +
                                  action.paramNames[i] + "' does not exist");
                    else if (vals.length == 1) params[i] = vals[0];
                        else params[i] = vals.clone();
                }

                // object of which method will be invoked
                Object component = getComponent(action.componentData.componentClass, true);

                return action.invoke(context, component, params);
            } catch (ConversionException e) {
                return conversionError(context, formName, actionName, e);
            }
        } finally {
            threadSessions.remove(new Integer(System.identityHashCode(Thread.currentThread())));
        }
    }

    /**
     * Retrieves a template from the template provider - this method replaces
     * {@link org.webmacro.servlet.WMServlet#getTemplate(String) getTemplate()} method.
     */
    public Template getWMTemplate(String key) {
        try {
            return getTemplate(key);
        } catch(WebMacroException e) {
            log.error("Template '" + key + "' not found!");
            log.error(e.toString());
            return null;
        }
    }

    /**
     * Returns component of a given class.
     *
     * @param componentClass corresponds to the component <TT>class</TT> attribute from
     *                  the configuration file
     * @param create true = the component will be created if doesn't yet exist
     * @return null if component class name is unknown or an instantiation error occurs
     * @exception IllegalStateException if the method is called from a non-session thread
     *            (only for components with <TT>"session"</TT> persistence)
     */
    public final Object getComponent(Class componentClass, boolean create) {
        ComponentData componentData = (ComponentData) componentClasses.get(componentClass.getName());

        // wrong class name?
        if (componentData == null) {
            log.error("Component class '" + componentClass.getName() + "' passed to" +
                      " ActionServlet.getComponent(Class,boolean) method" +
                      " must be specified via <component> element in the" +
                      " configuration file");
            return null;
        }

        Object component = null;

        try {
            // get component instance
            switch(componentData.persistence) {
                case ComponentData.PERSISTENCE_APPLICATION:
                    component = applicationComponents.get(componentData.componentClass);

                    if (create && component == null)
                        applicationComponents.put(componentData.componentClass,
                                                component = componentData.constructor.newInstance(_this));
                break;

                case ComponentData.PERSISTENCE_SESSION:
                    HttpSession session = (HttpSession) threadSessions.get(new Integer(System.identityHashCode(Thread.currentThread())));

                    if (session == null)
                        throw new IllegalStateException("Cannot call 'ActionServlet.getComponent()'" +
                              " from a non-session thread");

                    Hashtable components = (Hashtable) sessionComponents.get(session);

                    if (components == null)
                        sessionComponents.put(session, components = new Hashtable());

                    component = components.get(componentData.componentClass);

                    if (create && component == null)
                        components.put(componentData.componentClass,
                                    component = componentData.constructor.newInstance(_this));
                break;

                default:    // ComponentData.PERSISTENCE_REQUEST
                    component = componentData.constructor.newInstance(_this);
            }
        } catch(InvocationTargetException e) {
            log.error("Cannot access component '" +
                      componentData.componentClass.getName() + "'");
        } catch(InstantiationException e) {
            log.error("Cannot instantiate component '" +
                      componentData.componentClass.getName() + "'");
        } catch(IllegalAccessException e) {
            log.error("Cannot invoke constructor of component '" +
                      componentData.componentClass.getName() + "'");
        }

        return component;
    }

    /**
     * Method called upon a new session. By default returns a template, which has specified
     * <A HREF="#el_templates"><TT>new-session</TT></A> attribute.
     *
     * @return template to be displayed
     */
    protected Template newSession(WebContext context) {
        Vector templateOutputVariables = (Vector) this.templateOutputVariables.get(newSessionTemplate.toString());

        // set <output-variable>s of template
        if (templateOutputVariables != null)
            for (Enumeration e = templateOutputVariables.elements(); e.hasMoreElements(); )
                ((OutputVariable) e.nextElement()).evaluate(context);

        return newSessionTemplate;
    }

    /**
     * Method called before any parameter type handler is invoked.
     * Does nothing by default.
     *
     * @param context context from {@link #handle(WebContext) handle()} method
     * @param form form that 'action' belongs to (null if not specified)
     * @param action action to be invoked
     */
    protected void beforeConversion(WebContext context, String form, String action) {}

    /**
     * Method called right before each action method. If not overriden - does nothing and
     * returns null.
     *
     * @param context context from {@link #handle(WebContext) handle()} method
     * @param form form that 'action' belongs to (null if not specified)
     * @param action action to be invoked
     * @param convertedParams converted parameteters, which will be passed to the
     *                        action method. <B>Note:</B> 'convertedParams[0]' is context
     *                        from {@link #handle(WebContext) handle()} (is of type {@link
     *                        org.webmacro.servlet.WebContext WebContext}).
     * @return template to be displayed (if not null, the action 'actionName' will
     *         be invoked)
     */
    protected Template beforeInvoke(WebContext context,
                                    String form,
                                    String action,
                                    Object[] convertedParams) throws ActionException {
        return null;
    }

    /**
     * Method called right after each action method invocation. If not overriden - does
     * nothing and returns the passed 'template'.
     *
     * @param context context from {@link #handle(WebContext) handle()} method
     * @param template template returned be action method
     * @param form form that 'action' belongs to (null if not specified)
     * @param action invoked action name
     * @param convertedParams converted parameteters, which have been passed to the
     *                        action method. <B>Note:</B> 'convertedParams[0]' is context
     *                        from {@link #handle(WebContext) handle()} (is of type {@link
     *                        org.webmacro.servlet.WebContext WebContext}).
     * @return template to be displayed
     */
    protected Template afterInvoke(Template template,
                                   WebContext context,
                                   String form,
                                   String action,
                                   Object[] convertedParams) throws ActionException {
        return template;
    }

    /**
     * Method called if 'action' parameter is defined but not assigned to any method.
     * Throws new ActionException by default.
     *
     * @param context context from {@link #handle(WebContext) handle()} method
     * @param form form that 'action' belongs to (null if not specified)
     * @param action unassigned action name
     * @return template to be displayed
     * @exception ActionException on error of action method
     */
    protected Template unassignedAction(WebContext context,
                                        String form,
                                        String action) throws ActionException {
        throw new ActionException("Unassigned action '" +
                                 (form == null?"": (form + "'.'")) + action + "'");
    }

    /**
     * Method called on parameter conversion error or when a HTTP parameter is
     * missing or has a wrong name. Throws new ActionException by default.
     *
     * @param context context from {@link #handle(WebContext) handle()} method
     * @param form form that 'action' belongs to (null if not specified)
     * @param action action which cannot be called due to the error
     * @param e exception which caused the error
     * @return template to be displayed
     */
    protected Template conversionError(WebContext context,
                                       String form,
                                       String action,
                                       ConversionException e) throws ActionException {
        throw new ActionException ("Conversion error when handling action '" +
                  (form == null?"": (form + "'.'")) + action +
                  "': " + e.getMessage());
    }
}
