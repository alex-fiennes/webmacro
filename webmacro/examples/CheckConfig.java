import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.webmacro.Broker;
import org.webmacro.Log;
import org.webmacro.Template;
import org.webmacro.servlet.HandlerException;
import org.webmacro.servlet.WMServlet;
import org.webmacro.servlet.WebContext;

/**
 *
 * @author Keats Kirsch
 * @version 1.0
 * @since 11-Sep-01
 */
public class CheckConfig extends WMServlet {
    private static final long serialVersionUID = -1844295645621234018L;
    
    final static public boolean DEBUG = true;
    final static public String DEFAULT_TEMPLATE = "default.wm";
    private Log _log = null;
    
    public Template handle(WebContext context) throws HandlerException {
        try {
            //TODO: 
            /* Get template path
             * Get env info (OS, JVM, classpath, ...)
             * 
             */
            // get WM properties, put sorted entry set into context
            Properties wmProps = getBroker().getSettings().getAsProperties();
            TreeMap settings = new TreeMap(wmProps);
            context.put("Settings", settings.entrySet());
            
            // get system properties, put sorted entry set into context
            Properties sysProps = System.getProperties();
            TreeMap sysPropMap = new TreeMap(sysProps);
            context.put("SysProps", sysPropMap.entrySet());
            
            javax.servlet.ServletConfig sconf = getServletConfig();

            TreeMap servletParms = new TreeMap();
            Enumeration e = sconf.getInitParameterNames();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                servletParms.put(key, sconf.getInitParameter(key));
                servletParms.put(key, getInitParameter(key));
            }
            context.put("ServletParms", servletParms.entrySet());

            javax.servlet.ServletContext sc = this.getServletContext(); 
            
            TreeMap scAttribs = new TreeMap();
            Enumeration e2 = sc.getAttributeNames();
            while (e2.hasMoreElements()){
                String key = (String)e2.nextElement();
                scAttribs.put(key, sc.getAttribute(key));
            }
            context.put("ServletContextAttributes", scAttribs.entrySet());
            
            String jsdkVer = "2.0";
            try {
                jsdkVer = sc.getMajorVersion() + "." + sc.getMinorVersion();
            }
            catch (NoSuchMethodError nsme) {
            }
            context.put("JSDK_Version", jsdkVer);
            
            java.net.URL url = getBroker().getResource(Broker.WEBMACRO_DEFAULTS);
            context.put("WEBMACRO_DEFAULTS", url.toExternalForm());
            
            // find the WebMacro.properties file if any
            url = null;
            if (!"2.0".equals(jsdkVer)){
                url = getBroker().getResource("WEB-INF/" + Broker.WEBMACRO_PROPERTIES);
            }
            if (url == null){
                url = getBroker().getResource(Broker.WEBMACRO_PROPERTIES);
            }

            if (url != null){
                context.put("WEBMACRO_PROPERTIES", url.toExternalForm());
            }
            else {
                context.put("WEBMACRO_PROPERTIES", "[none]");
            }
                      
            Template t = getTemplate("CheckConfig.wm");
            return t;
        }
        catch (Exception e){
            context.put("stackTrace", getStackTrace(e));
            throw new HandlerException(e.getMessage());
        }
    }

    /**
     * This is called when the servlet environment initializes
     * the servlet for use via the init() method.
     * @exception ServletException to indicate initialization failed
     */
    protected void start() throws ServletException {
        // get the log
        //_log = org.webmacro.util.LogSystem.getInstance().getLog("WMTest", "WMTest log");
        _log = org.webmacro.util.LogSystem.getInstance().getLog(null);
        _log.info(this.getClass().getName() + ".start() invoked.");
    } // start()
    
    /**
     * This is called when the servlet environment shuts down the servlet
     * via the shutdown() method. The default implementation does nothing.
     */
    protected void stop() {
    }

    /** Debugging utility - dumps list of request headers from a WebContext. */
    static public void printRequestHeaders(WebContext context){
        HttpServletRequest req = context.getRequest();
        java.util.Enumeration hdrs = req.getHeaderNames();
        int hdrCnt = 0;
        while (hdrs.hasMoreElements()){
            hdrCnt++;
            String hdr = (String)hdrs.nextElement();
            String val = (String)req.getHeader(hdr);
            System.out.println("Req header " + hdrCnt + ", " + hdr + "=" + val);
        }
    }
    
    /** Write the stack trace from an exception into a string. */
    static public String getStackTrace(Throwable e){
        if (e == null) return null;
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    public class KeyValuePair implements java.lang.Comparable {
        public String key = null;
        public String value = null;
        public KeyValuePair(String key, String value){
            this.key = key;
            this.value = value;
        }
        public String getKey(){ return key; }
        public String getValue(){ return value; }
        
        public int compareTo(java.lang.Object obj) {
            return key.compareTo(((KeyValuePair)obj).getKey());
        }        
        
    }
}
