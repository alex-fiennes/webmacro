package org.webmacro.util.contextdebugger;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Array;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Iterator;
import org.webmacro.servlet.WebContext;
import org.webmacro.PropertyException;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */

public final class ContextDebugger {
    WebContext context;

    public ContextDebugger(WebContext context) {
        this.context = context;
    }

    public final WebContext getWebContext() {
        return context;
    }

    public final String toString() {
        StringBuffer    sb = new StringBuffer();
        sb.append(displayContext(context));

        sb.append(displaySessionObjects(context));

        sb.append(ServletDebugUtil.debugRequestedURL( context.getRequest() ));

        sb.append(ServletDebugUtil.debugParameters( context.getRequest() ));

        sb.append(ServletDebugUtil.debugCookieInfo( context.getRequest() ));

        sb.append(ServletDebugUtil.debugRequestInfo( context.getRequest() ));

        sb.append(ServletDebugUtil.debugHeaderNames( context.getRequest() ));

        return (sb.toString());
    }

    public final String contextInfo() {
        return displayContext(context);
    }
    public final String sessionInfo() {
        return displaySessionObjects(context);
    }
    public final String requestURLInfo() {
        return ServletDebugUtil.debugRequestedURL( context.getRequest() );
    }
    public final String formInfo() {
        return ServletDebugUtil.debugParameters( context.getRequest() );
    }
    public final String cookieInfo() {
        return ServletDebugUtil.debugCookieInfo( context.getRequest() );
    }
    public final String requestInfo() {
        return ServletDebugUtil.debugRequestInfo( context.getRequest() );
    }
    public final String headerInfo() {
        return ServletDebugUtil.debugHeaderNames( context.getRequest() );
    }
    public final String systemInfo() {
        return ServletDebugUtil.debugSystemProperties();
    }

    /**
     * Method declaration
     *
     *
     * @param key
     *
     * @return
     *
     * @see
     */

    public static final String getValue(WebContext context, Object key) {
        if ( key == null) return "NULL VALUE";
        Object  value = context.get(key);

        if ( value == null) return "NULL VALUE";



        if (value instanceof Vector ) return "Vector.size() ="+ ((Vector)value).size();
        if (value instanceof Hashtable ) return "Hashtable.size() ="+ ((Hashtable)value).size();
        if (!value.getClass().isArray()) return value.toString();

        StringBuffer    sb = new StringBuffer();
        sb.append("[ ");

        int arrLen = Array.getLength(value);

        for (int i = 0; i < arrLen; i++) {
            sb.append(Array.get(value, i));
            if (i < (arrLen - 1)) sb.append(", ");
        }

        sb.append(" ]");

        return sb.toString();
    }



    public static final String getValueType(WebContext context, Object key) {
        if (key == null) return "NULL VALUE";
        Object  value = context.get(key);
        if (value == null) return "NULL VALUE";

        if (!value.getClass().isArray()) return value.getClass().getName();

        StringBuffer    sb = new StringBuffer();
        sb.append(value.getClass().getComponentType().getName());
        sb.append("[");
        sb.append(Array.getLength(value) + "");
        sb.append("]");

        return sb.toString();
    }


    public static final String getSessionValueType(HttpSession httpSession, String key) {
        if (key == null) return "NULL VALUE";
        Object  value = httpSession.getValue(key);
        if (value == null) return "NULL VALUE";

        if (!value.getClass().isArray()) return value.getClass().getName();

        StringBuffer    sb = new StringBuffer();
        sb.append(value.getClass().getComponentType().getName());
        sb.append("[");
        sb.append(Array.getLength(value) + "");
        sb.append("]");

        return sb.toString();
    }

    public static final String getSessionValue(HttpSession httpSession, String name) {
        Object  obj = httpSession.getValue(name);
        if (obj!=null) return obj.toString();
        else return "";
    }

    public static final String displayContext(WebContext context ) {
        StringBuffer    sb = new StringBuffer();
        sb.append("<h1>Context Variables:</h1>\n");
        sb.append("<table border=\"1\" width=\"100%\">\n");
        sb.append("    <tbody>\n");
        sb.append("        <tr>\n");
        sb.append("             <th style=\"BACKGROUND-COLOR: #008080\" width=\"33%\"><font color=\"#ffffff\"><b>Key</b></font></th>\n");
        sb.append("             <th style=\"BACKGROUND-COLOR: #008080\" width=\"33%\"><font color=\"#ffffff\"><b>Value</b></font></th>\n");
        sb.append("             <th style=\"BACKGROUND-COLOR: #008080\" width=\"33%\"><font color=\"#ffffff\"><b>Value Type</b></font></th>\n");
        sb.append("        </tr>\n");
        Iterator keyIterator = context.getMap().keySet().iterator();
        while ( keyIterator.hasNext() ) {

            Object  key = keyIterator.next();
            Object wmObj = context.get(key);
            sb.append("        <tr>\n");
            sb.append("            <td width=\"33%\">\n");

            if (key!=null) {
                sb.append(key.toString());
            } else {
                sb.append("NULL KEY");
            }
            sb.append("            </td>\n");
            sb.append("            <td width=\"33%\">\n");
            sb.append(getValue( context, key));
            sb.append("            </td>\n");

            sb.append("            <td width=\"33%\">\n");
            sb.append(getValueType( context, key));
            sb.append("            </td>\n");
            sb.append("        </tr>\n");

        }

        sb.append("    </tbody>\n");
        sb.append("</table>\n");
        return (sb.toString());
    }

    public static final String displaySessionObjects(WebContext context) {
        StringBuffer    sb = new StringBuffer();
        try {
            HttpSession httpSession = (HttpSession) context.getProperty("Session");
            String[]  sessionVars= httpSession.getValueNames();

            sb.append("<h1>Session Objects Information:</h1>\n");
            sb.append("<table border=\"1\" width=\"100%\">\n");
            sb.append("    <tbody>\n");
            sb.append("        <tr>\n");
            sb.append("             <th style=\"BACKGROUND-COLOR: #008080\" width=\"33%\"><font color=\"#ffffff\"><b>Key</b></font></th>\n");
            sb.append("             <th style=\"BACKGROUND-COLOR: #008080\" width=\"33%\"><font color=\"#ffffff\"><b>Value</b></font></th>\n");
            sb.append("             <th style=\"BACKGROUND-COLOR: #008080\" width=\"33%\"><font color=\"#ffffff\"><b>Value Type</b></font></th>\n");
            sb.append("        </tr>\n");
            for (int i=0; i< sessionVars.length; i++){

                String  key = sessionVars[i];
                sb.append("        <tr>\n");
                sb.append("            <td width=\"33%\">\n");

                if (key!=null) {
                    sb.append(key.toString());
                } else {
                    sb.append("NULL KEY");
                }
                sb.append("            </td>\n");
                sb.append("            <td width=\"33%\">\n");
                sb.append(getSessionValue( httpSession, key));
                sb.append("            </td>\n");

                sb.append("            <td width=\"33%\">\n");
                sb.append(getSessionValueType(httpSession, key ));
                sb.append("            </td>\n");
                sb.append("        </tr>\n");
            }

            sb.append("    </tbody>\n");
            sb.append("</table>\n");

        }
        catch (PropertyException e) {
            sb.append("<H1>Unable to access Session</H1>");
        }
        return sb.toString();
    }

    /*
    public static final String displaySessionObjects(WebContext context) {
        StringBuffer    sb = new StringBuffer();
        HttpSession httpSession = context.getSession();
        Enumeration enumAttributes= httpSession.getAttributeNames();

        sb.append("<h1>Session Objects Information:</h1>\n");
        sb.append("<table border=\"1\" width=\"100%\">\n");
        sb.append("    <tbody>\n");
        sb.append("        <tr>\n");
        sb.append("             <th style=\"BACKGROUND-COLOR: #008080\" width=\"33%\"><font color=\"#ffffff\"><b>Key</b></font></th>\n");
        sb.append("             <th style=\"BACKGROUND-COLOR: #008080\" width=\"33%\"><font color=\"#ffffff\"><b>Value</b></font></th>\n");
        sb.append("             <th style=\"BACKGROUND-COLOR: #008080\" width=\"33%\"><font color=\"#ffffff\"><b>Value Type</b></font></th>\n");
        sb.append("        </tr>\n");

        while (enumAttributes.hasMoreElements() ){


            String  key = (String) enumAttributes.nextElement();
            sb.append("        <tr>\n");
            sb.append("            <td width=\"33%\">\n");

            if (key!=null) {
                sb.append(key.toString());
            } else {
                sb.append("NULL KEY");
            }
            sb.append("            </td>\n");
            sb.append("            <td width=\"33%\">\n");
            sb.append(getSessionValue( httpSession, key));
            sb.append("            </td>\n");

            sb.append("            <td width=\"33%\">\n");
            sb.append(getSessionValueType(httpSession, key ));
            sb.append("            </td>\n");
            sb.append("        </tr>\n");
        }

        sb.append("    </tbody>\n");
        sb.append("</table>\n");

        return (sb.toString());
    }
    */
}

/*--- formatting done in "CIBC Java Application Frameworks Coding Style" style on 05-10-2000 ---*/