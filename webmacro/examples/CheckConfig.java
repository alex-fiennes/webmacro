import org.webmacro.servlet.*;
import org.webmacro.*;
import java.util.*;
import java.io.StringWriter;
import java.io.PrintWriter;
import javax.servlet.http.*;
import javax.servlet.ServletException;

/**
 *
 * @author Keats Kirsch
 * @version 1.0
 * @since 11-Sep-01
 */
public class CheckConfig extends WMServlet {
    final static public boolean DEBUG = true;
    final static public String DEFAULT_TEMPLATE = "default.wm";
    private Log _log = null;
    
    public Template handle(WebContext context) throws HandlerException {
        HttpSession sess = null;
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
            
            javax.servlet.ServletContext sc = this.getServletContext(); 

            TreeMap servletParms = new TreeMap();
            Enumeration e = sc.getInitParameterNames();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                servletParms.put(key, sc.getInitParameter(key));
            }
            context.put("ServletParms", servletParms.entrySet());
            
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
            url = getBroker().getResource(Broker.WEBMACRO_PROPERTIES);
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

    /** debugging utility - dumps list of request headers from a WebContext */
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
    
    /** write the stack trace from an exception into a string */
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
} // class CheckConfig
