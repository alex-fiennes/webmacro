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
 * components.<P>
 *
 * Note: Because ActionServlet is no longer abstract class, subclassing is not necessary
 * unless you need to modify some of its functionality. This means the application can
 * be build only by components and as a main servlet is use directly ActionServlet.
 *
 * @see TypeHandler
 */
public class ActionServlet extends WMServlet {
    /**
     * Table &lt;properties&gt;: key = property name, value = property value.
     */
    private final Hashtable properties = new Hashtable();

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
     * Table of sessions: key = http session id, value = http session
     */
    private final Hashtable sessions = new Hashtable();

    /**
     * Table of components with "session" persistence:
     * key = http session id, value = hashtable of component objects
     *                               (key = component name, value = component object).
     */
    private final Hashtable sessionComponents = new Hashtable();

    /**
     * Table of all components:
     * key = component name, value = instance of org.webmacro.as.ComponentData
     */
    final Hashtable componentClasses = new Hashtable();

    /**
     * Table of output-variables of templates.
     * key = template instance, value = template name
     */
    final WeakHashMap templatesNames = new WeakHashMap();

    /**
     * Table of output-variables of templates.
     * key = template name from 'templatesNames', value = vector of output-variables
     */
    final Hashtable templateOutputVariables = new Hashtable();

    /**
     * Table of threads using sessions (uses {@link #getComponent(String,boolean) getComponent()}.
     * key = thread hashcode, value = http session id
     */
    private final Hashtable threadSessions = new Hashtable();

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
     * key = http session id, value = lastActionData.
     *
     * Used by {@link #handle(WebContext) handle()} and
     * Action.invoke(...) methods.
     */
    final Hashtable lastActions = new Hashtable();

    /**
     * Used for initialization of components.
     */
    private Object[] _this = new Object[] {this};

    /**
     * Sleeping period of session data reaper in miliseconds (default: 1 minute)
     */
    protected int sessionReaperTimeout = 60*1000;

    /**
     * Thread that periodically removes unused session data.
     */
    private final SessionReaper sessionReaper = new SessionReaper();

    /**
     * Housekeeping thread.
     */
    private class SessionReaper extends Thread {
        /** Flag telling sessionReaper to stop. */
        private boolean finish;

        public void run() {
            Enumeration keys1, keys2;
            String id = null;

            finish = false;

            for (keys1 = lastActions.keys(), keys2 = sessionComponents.keys(); !finish; ) {
                // clean last action data
                try {
                    id = (String) keys1.nextElement();
                    ((HttpSession) sessions.get(id)).getId();
                } catch(NullPointerException e) {
                    LastActionData la = (LastActionData)lastActions.get(id);
                    if (la.action.componentData.persistence == ComponentData.PERSISTENCE_REQUEST &&
                        la.component instanceof Destroyed) ((Destroyed)la.component).destroy();

                    lastActions.remove(id);
                    log.debug("Removed lastActionData of invalid session");
                } catch(IllegalStateException e) {
                    LastActionData la = (LastActionData)lastActions.get(id);
                    if (la.action.componentData.persistence == ComponentData.PERSISTENCE_REQUEST &&
                        la.component instanceof Destroyed) ((Destroyed)la.component).destroy();

                    lastActions.remove(id);
                    log.debug("Removed lastActionData of invalid session");
                } catch(NoSuchElementException e) {
                    keys1 = lastActions.keys();
                }

                // clean session components
                try {
                    id = (String) keys2.nextElement();
                    ((HttpSession) sessions.get(id)).getId();
                } catch(NullPointerException e) {
                    for (Enumeration en = ((Hashtable) sessionComponents.get(id)).elements(); en.hasMoreElements(); ) {
                        Object component = en.nextElement();
                        if (component instanceof Destroyed) ((Destroyed)component).destroy();
                    }

                    sessionComponents.remove(id);
                    log.debug("Removed session component of invalid session");
                } catch(IllegalStateException e) {
                    for (Enumeration en = ((Hashtable) sessionComponents.get(id)).elements(); en.hasMoreElements(); ) {
                        Object component = en.nextElement();
                        if (component instanceof Destroyed) ((Destroyed)component).destroy();
                    }

                    sessionComponents.remove(id);
                    log.debug("Removed session component of invalid session");
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
        typeHandlers.put("java.lang.String", new SimpleTypeHandler() {
            public Object convert(WebContext context, String parameterValue)
            throws ConversionException {
                return parameterValue;
            }
        });

        typeHandlers.put("org.webmacro.servlet.WebContext", new SimpleTypeHandler() {
            public Object convert(WebContext context, String parameterValue)
            throws ConversionException {
                return context;     // ignores parameterValue!!!
            }
        });

        typeHandlers.put("boolean", new SimpleTypeHandler() {
            public Object convert(WebContext context, String parameterValue)
            throws ConversionException {
                if ("true".equals(parameterValue.trim())) return new Boolean(true);
                if ("false".equals(parameterValue.trim())) return new Boolean(false);
                throw new ConversionException("Cannot convert '" + parameterValue + "' to boolean");
            }
        });

        typeHandlers.put("byte", new SimpleTypeHandler() {
            public Object convert(WebContext context, String parameterValue)
            throws ConversionException {
                try {
                    return Byte.valueOf(parameterValue.trim());
                } catch(NumberFormatException e) {
                    throw new ConversionException("Cannot convert '" + parameterValue + "' to byte", e);
                }
            }
        });

        typeHandlers.put("double", new SimpleTypeHandler() {
            public Object convert(WebContext context, String parameterValue)
            throws ConversionException {
                try {
                    return Double.valueOf(parameterValue.trim());
                } catch(NumberFormatException e) {
                    throw new ConversionException("Cannot convert '" + parameterValue + "' to double", e);
                }
            }
        });

        typeHandlers.put("float", new SimpleTypeHandler() {
            public Object convert(WebContext context, String parameterValue)
            throws ConversionException {
                try {
                    return Float.valueOf(parameterValue.trim());
                } catch(NumberFormatException e) {
                    throw new ConversionException("Cannot convert '" + parameterValue + "' to float", e);
                }
            }
        });

        typeHandlers.put("int", new SimpleTypeHandler() {
            public Object convert(WebContext context, String parameterValue)
            throws ConversionException {
                try {
                    return Integer.valueOf(parameterValue.trim());
                } catch(NumberFormatException e) {
                    throw new ConversionException("Cannot convert '" + parameterValue + "' to int", e);
                }
            }
        });

        typeHandlers.put("long", new SimpleTypeHandler() {
            public Object convert(WebContext context, String parameterValue)
            throws ConversionException {
                try {
                    return Long.valueOf(parameterValue.trim());
                } catch(NumberFormatException e) {
                    throw new ConversionException("Cannot convert '" + parameterValue + "' to long", e);
                }
            }
        });

        typeHandlers.put("short", new SimpleTypeHandler() {
            public Object convert(WebContext context, String parameterValue)
            throws ConversionException {
                try {
                    return Short.valueOf(parameterValue.trim());
                } catch(NumberFormatException e) {
                    throw new ConversionException("Cannot convert '" + parameterValue + "' to short", e);
                }
            }
        });

        typeHandlers.put("char", new SimpleTypeHandler() {
            public Object convert(WebContext context, String parameterValue)
            throws ConversionException {
                if (parameterValue.trim().length() != 1)
                    throw new ConversionException("Cannot convert '" + parameterValue + "' to char");
                return new Character(parameterValue.trim().charAt(0));
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
                throw new InitException("Servlet init parameter 'ActionConfig' must specify" +
                                        " name of the XML configuration file " +
                                        " (c:/servlets/MyServlet.xml, for example)");

            // parse ActionConfig
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            docBuilderFactory.setValidating(true);
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

            docBuilder.setEntityResolver(new EntityResolver() {
                private String DTD = "http://dione.zcu.cz/~toman40/ActionServlet/dtd/ActionServlet_0_7.dtd";

                public InputSource resolveEntity (String publicId, String systemId) {
                    try {
                        if (DTD.equals(systemId)) {
                            InputStream is = getClass().getResourceAsStream("/ActionServlet_0_7.dtd");
                            return new InputSource(new InputStreamReader(is));
                        } else log.warning("SYSTEM attribute of <!DOCTYPE> should be \"" + DTD + "\"");
                    } catch (Exception e) {
                        log.warning("Error while loading 'ActionServlet_0_7.dtd'" + ": " + e.getMessage());
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


            InputStream in = null;

            // try to load config from classpath if not found
            try {
                in = new FileInputStream(configFilename);
            } catch (FileNotFoundException e) {
                in = getClass().getResourceAsStream("/" + configFilename);

                if (in == null)
                    throw new FileNotFoundException("ActionConfig '" + configFilename + "' not found" +
                                                    " even in classpath");
            }

            Document doc = docBuilder.parse(in);

            // <application>
            Element root = doc.getDocumentElement();
            root.normalize();

            // 'repository' attribute
            String rep = root.getAttribute("repository");

            if (ActionServlet.class == getClass()) {  // is ActionServlet subclassed?
                if ("".equals(rep)) loader = getClass().getClassLoader();
                    else loader = new ASClassLoader(new File(rep));
            } else {
                if (!"".equals(rep))
                    throw new InitException("Attribute 'repository' of element"+
                              " <application> in ActionConfig cannot be"+
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

                if (node.getNodeName().equals("properties")) {
                    NodeList list2 = node.getChildNodes();

                    for(int j=0; j < list2.getLength(); j++) {
                        node = list2.item(j);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {   // <property>
                            String name = ((Element)node).getAttribute("name");
                            String value = ((Element)node).getAttribute("value");
                            String component = ((Element)node).getAttribute("component");

                            if ("".equals(component)) {
                                properties.put(name, value);
                                log.debug("Found global property: name='"+name+"' value='"+value+"'");
                            } else {
                                ComponentData componentData = (ComponentData) componentClasses.get(component);
                                if (componentData == null)
                                    throw new InitException("Name of '"+component+"' used in attribute "+
                                    " \"component\" of <property> must be reffered by <component> attribute \"name\"");
                                componentData.properties.put(name, value);
                                log.debug("Found property: name='"+name+"' value='"+value+"' for component '"+component+"'");
                            }
                        }
                    }
                } else if (node.getNodeName().equals("type-handlers")) {
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
                                    throw new InitException("Attribute 'is-new-session' of " +
                                    "<template> element can be \"true\" only once");
                                else newSessionTemplate = t;
                        }
                    }
                }
            }

            // new session template (2)
            if (newSessionTemplate == null) {
                try {
                    getClass().getDeclaredMethod("newSession", new Class[] {WebContext.class});
                } catch (NoSuchMethodException e) {
                    throw new InitException("Attribute 'is-new-session' of element" +
                          " <template> in ActionConfig '" + configFilename + "' was" +
                          " not specified nor the 'newSession()' method was overriden by your servlet");
                }
            } else if (getClass() != ActionServlet.class) {
                try {
                    getClass().getDeclaredMethod("newSession", new Class[] {WebContext.class});
                    throw new InitException("Attribute 'is-new-session' of element" +
                          " <template> in ActionConfig '" + configFilename + "' cannot" +
                          " be specified when 'newSession()' method is overriden by your servlet");
                } catch (NoSuchMethodException e) {}
            }

            // parse <component>s subelements
            for(int i=0; i < componentList.getLength(); i++) {
                Node node = componentList.item(i);

                if (node.getNodeName().equals("component")) {
                    String componentName = ((Element)node).getAttribute("name");
                    ComponentData componentData = (ComponentData)componentClasses.get(componentName);
                    parseActions(componentName, componentData, node.getChildNodes());
                    parseOnReturns(componentData, node.getChildNodes());
                }
            }

            // start cleaning thread
            sessionReaper.start();

            _problem = null;
            log.info("ActionServlet initialization OK");
        } catch (IOException e) {
            _problem = "Error while reading ActionConfig '" + configFilename + "'";
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
                _problem = "Bad format of ActionConfig '" + configFilename +
                           "' [line " + ((SAXParseException)e).getLineNumber() + "]: ";
            else _problem = "Bad format of ActionConfig '" + configFilename + "': ";

            StringTokenizer st = new StringTokenizer(e.getMessage(), "<>", true);
            String s;

            while (st.hasMoreTokens()) {
                if("<".equals(s = st.nextToken())) _problem += "&lt;";
                    else if(">".equals(s)) _problem += "&gt;";
                        else _problem += s;
            }

            log.error(e.toString(), e);
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
        String name = element.getAttribute("name");
        Template template = getWMTemplate(name);

        if (template == null)
            throw new InitException("Cannot find template '" + name + "'");

        boolean isNewSession = "true".equals(element.getAttribute("is-new-session"));

        NodeList list = element.getChildNodes();
        Vector outputVariables = new Vector();

        for(int i=0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node.getNodeName().equals("output-variable"))
                parseOutputVariable(outputVariables, (Element)node);
        }

        if (!outputVariables.isEmpty()) {
            if (templateOutputVariables.get(name) != null)
                throw new InitException("Template of the name '" + name + "' defined more than" +
                                        " once in ActionConfig");

            templateOutputVariables.put(name, outputVariables);
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
        String componentName = element.getAttribute("name");
        String componentClassName = element.getAttribute("class");
        String persistenceStr     = element.getAttribute("persistence");
        Class componentClass = null;
        Constructor constructor;
        int persistence;
        boolean consOne = true;

        try {
            componentClass = loader.loadClass(componentClassName);
            constructor = componentClass.getConstructor(new Class[]{ActionServlet.class});
        } catch(ClassNotFoundException e) {
            throw new InitException("Cannot find component class '" + componentClassName +
                      (loader instanceof ASClassLoader? ("' in the repository '" +
                      ((ASClassLoader)loader).repository + "'"):"'"));
        } catch(NoSuchMethodException e) {
            try {
                constructor = componentClass.getConstructor(new Class[]{ActionServlet.class,String.class});
                consOne = false;
            } catch (NoSuchMethodException ex) {
                throw new InitException("Component class '" + componentClassName +
                          "' does not have a public constructor with a parameter " +
                          "of type 'org.webmacro.as.ActionServlet' and (optionally) " +
                          "second of type 'java.lang.String'");
            }
        }

        if (persistenceStr.equals("application")) persistence = ComponentData.PERSISTENCE_APPLICATION;
            else if (persistenceStr.equals("session")) persistence = ComponentData.PERSISTENCE_SESSION;
               else persistence = ComponentData.PERSISTENCE_REQUEST;

        // component 'class' name must be unique in the application
        if (componentClasses.containsKey(componentName))
            throw new InitException("More than one component of &lt;class&gt; name '"+
                                    componentClassName + "' defined in ActionConfig");

        componentClasses.put(componentName, new ComponentData(componentName, componentClass,
                                            constructor, consOne, persistence));
    }

    /**
     * Parses &lt;action&gt;s node list.
     */
    private void parseActions(String componentName, ComponentData componentData, NodeList list) throws InitException {
        String form, action, method;

        for(int i=0; i < list.getLength(); i++) {
            Node node = list.item(i);

            // <action>
            if ("action".equals(node.getNodeName())) {
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
                         "' bound to method '" + method + "' of '" +
                         componentData.componentName + "'");
            }
        }
    }

    /**
     * Parses &lt;on-return&gt;s node list.
     */
    private void parseOnReturns(ComponentData componentData, NodeList list) throws InitException {
        String value, showTemplate;

        for(int i=0; i < list.getLength(); i++) {
            Node node = list.item(i);

            // <on-return>
            if ("on-return".equals(node.getNodeName())) {
                value = ((Element)node).getAttribute("value");
                showTemplate = ((Element)node).getAttribute("show-template");

                if (componentData.onReturns.containsKey(value))
                   throw new InitException("Attribute 'value' must be unique among <on-return> " +
                                           "elements of a single component");

                Vector outputVariables = new Vector();
                NodeList list2 = node.getChildNodes();

                // <output-variable>s
                for(int j=0; j < list2.getLength(); j++) {
                    node = list2.item(j);

                    if (node.getNodeType() == Node.ELEMENT_NODE)
                        parseOutputVariable(outputVariables, (Element)node);
                }

                if ("void".equals(value)) {
                    componentData.onReturns.put("void", showTemplate);

                    if (!outputVariables.isEmpty())
                        componentData.onReturnsOutputVars.put("void", outputVariables);
                } else try {
                    Field f = componentData.componentClass.getField(value);
                    if (!Modifier.isPublic(f.getModifiers()) ||
                        !Modifier.isStatic(f.getModifiers()) ||
                        !Modifier.isFinal(f.getModifiers()))
                        throw new NoSuchFieldException();

                    componentData.onReturns.put(f.get(null), showTemplate);

                    if (!outputVariables.isEmpty())
                        componentData.onReturnsOutputVars.put(f.get(null), outputVariables);
                } catch(NoSuchFieldException e) {
                    throw new InitException("No \"public static final\" field of name '" + value + "' found in component " +
                    "class '" + componentData.componentClass.getName() + "' (error while parsing <on-return> element)");
                } catch(IllegalAccessException e) {/* can't happen */}
            }
        }
    }

    /**
     * Parses &lt;output-variable&gt; element.
     */
    private void parseOutputVariable(Vector outputVariables, Element element)
    throws InitException {
        String componentName = element.getAttribute("component");
        ComponentData componentData = null;

        if (!"".equals(componentName)) {
            componentData = (ComponentData) componentClasses.get(componentName);

            if (componentData == null)
                throw new InitException("Value '" + componentName + "' of attribute 'component' in the" +
                                        " &lt;output-variable&gt; element must be a" +
                                        " component \"name\" from ActionConfig");
        }

        String value = element.getAttribute("value");

        try {
            outputVariables.addElement(new OutputVariable(this, componentData,
            element.getAttribute("name"), value, element.getAttribute("if")));
        } catch (org.webmacro.engine.ParseException e) {
            throw new InitException("Error while parsing &lt;output-variable&gt; element: " + e.getMessage(), e);
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
        for (Enumeration e = sessionComponents.elements(); e.hasMoreElements(); )
            for (Enumeration en = ((Hashtable) e.nextElement()).elements(); en.hasMoreElements(); ) {
                Object component = en.nextElement();
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
     * to ActionConfig using these rules:<P>
     *
     * <OL>
     * <LI>If the value of 'action' parameter is not defined in the HTTP reqest of a new
     * session, 'new-session-template' template from ActionConfig is returned.
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

        sessions.put(session.getId(), session);
        threadSessions.put(new Integer(System.identityHashCode(Thread.currentThread())), session.getId());

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
                if (session.isNew()) return evalOutputVars(newSession(context), context);

                // page reload -> reinvoke last method
                LastActionData la;
                if ((la = (LastActionData) lastActions.get(session)) != null)
                    return la.reinvokeLastAction(context);

                // reload of the default page
                return evalOutputVars(newSession(context), context);
            } else {
                actionName = actionName.trim();

                if (actions == null ||
                    (action = (Action) actions.get(actionName)) == null)
                    return evalOutputVars(unassignedAction(context, formName, actionName), context);
            }

            // parameters passed to action method
            Object[] params = new Object[action.paramNames.length];

            // get required parameters from HTTP request
            HttpServletRequest request = context.getRequest();

            try {
                for (int i=0; i<params.length; i++) {
                    String[] vals = request.getParameterValues(action.paramNames[i]);

                    if (vals == null) {
                        if (action.paramTypes[i] == WebContext.class) params[i] = "*WebContext*";
                        else throw new ConversionException("HTTP parameter '" +
                                       action.paramNames[i] + "' does not exist " +
                                       "(required by action method)");
                    } else if (vals.length == 1) params[i] = vals[0];
                        else params[i] = vals.clone();
                }

                // object of which method will be invoked
                Object component = getComponent(action.componentData.componentName, true);

                return action.invoke(context, action.componentData.componentName, component, params);
            } catch (ConversionException e) {
                return evalOutputVars(conversionError(context, formName, actionName, e), context);
            } catch (ActionException e) {
                log.error(e.getMessage(), e);
                return evalOutputVars(onException(context, formName, actionName, e), context);
            }
        } finally {
            threadSessions.remove(new Integer(System.identityHashCode(Thread.currentThread())));
        }
    }

    /**
     * Returns the value of global <TT>&lt;property&gt;</TT> from ActionConfig.
     *
     * @param name property name
     * @exception MissingResourceException if the property is not defined
     */
    public String getProperty(String name) throws MissingResourceException {
        String property = (String) properties.get(name);
        if (property == null)
            throw new MissingResourceException("Value of global property '" + name +
                  "' not defined in ActionConfig", "ActionServlet", name);
        return property;
    }

    /**
     * Returns the value of component <TT>&lt;property&gt;</TT> from ActionConfig.
     *
     * @param componentName component name
     * @param propertyName property name
     * @exception MissingResourceException if the property is not defined
     */
    public String getProperty(String componentName, String propertyName)
    throws MissingResourceException {
        ComponentData componentData = (ComponentData) componentClasses.get(componentName);
        if (componentData == null) return null;

        String property = (String) componentData.properties.get(propertyName);
        if (property == null)
            throw new MissingResourceException("Value of property '" + propertyName+
                      "' for component '" + componentName + "' not defined in ActionConfig",
                      "ActionServlet", propertyName);
        return property;
    }

    /**
     * Retrieves a template from the template provider - this method replaces
     * {@link org.webmacro.servlet.WMServlet#getTemplate(String) getTemplate()} method.
     */
    public Template getWMTemplate(String key) {
        Template t = null;

        try {
            t = getTemplate(key);
        } catch(WebMacroException e) {
            log.error("Template '" + key + "' not found!", e);
            return null;
        }

        if (t != null) templatesNames.put(t, key);
        return t;
    }

    /**
     * Returns component of a given class.
     *
     * @param componentName corresponds to the component <TT>name</TT> attribute from ActionConfig
     * @param create true = the component will be created if doesn't yet exist
     * @return null if component name is unknown or an instantiation error occurs
     * @exception IllegalStateException if the method is called from a non-session thread
     *            (only for components with <TT>"session"</TT> persistence)
     */
    public final Object getComponent(String componentName, boolean create) {
        ComponentData componentData = (ComponentData) componentClasses.get(componentName);

        // wrong component name?
        if (componentData == null) {
            log.error("Component name '" + componentName + "' passed to" +
                      " ActionServlet.getComponent(String,boolean) method" +
                      " must be specified via <component> element in" +
                      " ActionConfig");
            return null;
        }

        Object component = null;

        try {
            // get component instance
            switch(componentData.persistence) {
                case ComponentData.PERSISTENCE_APPLICATION:
                    component = applicationComponents.get(componentData.componentName);

                    if (create && component == null) {
                        log.debug("Creating component '" + componentData.componentName + "' (" +
                                  componentData.componentClass.getName() + ".class)");
                        if (componentData.consOne) component = componentData.constructor.newInstance(_this);
                           else component = componentData.constructor.newInstance(new Object[] {this, componentData.componentName});
                        applicationComponents.put(componentData.componentName, component);
                    }
                break;

                case ComponentData.PERSISTENCE_SESSION:
                    String id = (String) threadSessions.get(new Integer(System.identityHashCode(Thread.currentThread())));

                    if (id == null)
                        throw new IllegalStateException("Cannot call 'ActionServlet.getComponent()'" +
                              " from a non-session thread");

                    Hashtable components = (Hashtable) sessionComponents.get(id);

                    if (components == null)
                        sessionComponents.put(id, components = new Hashtable());

                    component = components.get(componentData.componentName);

                    if (create && component == null) {
                        log.debug("Creating component '" + componentData.componentName + "' (" +
                                  componentData.componentClass.getName() + ".class)");
                        if (componentData.consOne) component = componentData.constructor.newInstance(_this);
                            else component = componentData.constructor.newInstance(new Object[] {this, componentData.componentName});
                        components.put(componentData.componentName, component);
                    }
                break;

                default:    // ComponentData.PERSISTENCE_REQUEST
                    log.debug("Creating component '" + componentData.componentName + "' (" +
                              componentData.componentClass.getName() + ".class)");
                    if (componentData.consOne) component = componentData.constructor.newInstance(_this);
                        else component = componentData.constructor.newInstance(new Object[] {this, componentData.componentName});
            }
        } catch(InvocationTargetException e) {
            log.error("Cannot access component '" +
                      componentData.componentName + "'", e);
        } catch(InstantiationException e) {
            log.error("Cannot instantiate component '" +
                      componentData.componentName + "'", e);
        } catch(IllegalAccessException e) {
            log.error("Cannot invoke constructor of component '" +
                      componentData.componentName + "'", e);
        }

        return component;
    }

    /**
     * Evaluates <TT>&lt;output-variables&gt;</TT> for template.
     */
    private Template evalOutputVars(Template t, WebContext context) {
        if (t != null) {
            String name = "error.wm".equals(t.getName()) || t.getName().endsWith(File.separator+"error.wm")?
                          "error.wm": (String) templatesNames.get(t);

            Vector templateOutputVariables = (Vector) this.templateOutputVariables.get(name);

            // set <output-variable>s of template
            if (templateOutputVariables != null)
                for (Enumeration e = templateOutputVariables.elements(); e.hasMoreElements(); )
                    ((OutputVariable) e.nextElement()).evaluate(context);
        } else log.warning("Cannot evaluate <output-variable>s for null template");

        return t;
    }

    /**
     * Method called upon a new session. By default returns a template, which has specified
     * <A HREF="#el_templates"><TT>new-session</TT></A> attribute.
     *
     * @return template to be displayed
     */
    protected Template newSession(WebContext context) {
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
     * @return error code or template to be displayed (if not null, the action 'actionName'
     *         should be invoked)
	 * @exception ActionException never (by default)
     */
    protected Object beforeInvoke(WebContext context,
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
     * @param retValue value returned by action method
     * @param form form that 'action' belongs to (null if not specified)
     * @param action invoked action name
     * @param convertedParams converted parameteters, which have been passed to the
     *                        action method. <B>Note:</B> 'convertedParams[0]' is context
     *                        from {@link #handle(WebContext) handle()} (is of type {@link
     *                        org.webmacro.servlet.WebContext WebContext}).
     * @return error code or template to be displayed
	 * @exception ActionException never (by default)
     */
    protected Object afterInvoke(Object retValue,
                                 WebContext context,
                                 String form,
                                 String action,
                                 Object[] convertedParams) throws ActionException {
        return retValue;
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
     * @exception ActionException on conversion error
     */
    protected Template conversionError(WebContext context,
                                       String form,
                                       String action,
                                       ConversionException e) throws ActionException {
        return error(context, "Conversion error when handling action '" +
                    (form == null?"": (form + "'.'")) + action +
                    "': " + e.getMessage());
    }

    /**
     * Method called if action method throws an exception. Calls error() by default.
     *
     * @param context context from {@link #handle(WebContext) handle()} method
     * @param form form that 'action' belongs to (null if not specified)
     * @param action action which cannot be called due to the error
     * @param e exception which caused the error
     * @return template to be displayed
     */
    protected Template onException(WebContext context,
                                   String form,
                                   String action,
                                   ActionException e) {
        return error(context, e.getMessage());
    }

    /**
     * Method called for unmatched values from <TT>&lt;on-return&gt;</TT>
     * tags. Assumes <TT>value</TT> is a template or template name, by default.
     *
     * @param context context from {@link #handle(WebContext) handle()} method
     * @param form form that 'action' belongs to (null if not specified)
     * @param action action which cannot be called due to the error
     * @param value value returned by action method (null for void return type)
     * @exception ActionException if no template of the name value cannot be found
     * @return template to be displayed
     */
    protected Template onReturn(WebContext context,
                                String form,
                                String action,
                                Object value) throws ActionException {
        if (value == null)
           throw new ActionException("Don't know how to handle unmapped \"void\" " +
                                     "return value of action '" + (form==null?"":
                                      form+"'.'") + action + "'");

        Template template;

        if (value instanceof String) template = getWMTemplate((String)value);
            else if (value instanceof Template) return (Template)value;
                else template = getWMTemplate(value.toString());

        if (template == null)
           throw new ActionException("Cannot find template of name '" + value + "' " +
                                     "returned by action '" + (form==null?"":
                                      form+"'.'") + action + "'");
        return template;
    }
}
