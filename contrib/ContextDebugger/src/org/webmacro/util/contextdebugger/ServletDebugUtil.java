package org.webmacro.util.contextdebugger;


import java.util.Enumeration;
import java.util.StringTokenizer;
import java.io.File;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpUtils;
import javax.servlet.GenericServlet;


/**
 * Class Declaration.
 *
 *
 * @see
 *
 * @author
 * @version   %I%, %G%
 */

public class ServletDebugUtil {


    /**
     * Method declaration
     *
     *
     * @param MultipartRequest
     *
     * @return
     *
     * @see
     */

    public static final String debugMultiPartRequest(MultipartRequest multi) {
        StringBuffer    sb = new StringBuffer();

        sb.append("<h1>Multi Params:</h1>");
        sb.append("<table border=\"1\" width=\"100%\">");
        sb.append("    <tr>");
        sb.append("        <th width=\"50%\" style=\"background-color: #008080\"><font color=\"#FFFFFF\"><b>Param Name</b></font></th>");
        sb.append("        <th width=\"50%\" style=\"background-color: #008080\"><font color=\"#FFFFFF\"><b>Value</b></font></th>");
        sb.append("    </tr>");

        Enumeration params = multi.getParameterNames();

        while (params.hasMoreElements()) {
            String  name = (String) params.nextElement();
            String  value = multi.getParameter(name);

            sb.append("    <tr>");
            sb.append("        <td width=\"50%\">");
            sb.append(name);
            sb.append("                </td>");

            sb.append("        <td width=\"50%\">");
            sb.append(value);
            sb.append("                </td>");
            sb.append("    </tr>");
        }

        sb.append("</table>");


        sb.append("<b>");
        sb.append("<h1>Files:</h1>");

        Enumeration files = multi.getFileNames();


        sb.append("<table border=\"1\" width=\"100%\">");
        sb.append("    <tr>");
        sb.append("        <th width=\"30%\" style=\"background-color: #008080\"><font color=\"#FFFFFF\"><b>Name</b></font></th>");
        sb.append("        <th width=\"30%\" style=\"background-color: #008080\"><font color=\"#FFFFFF\"><b>filename</b></font></th>");
        sb.append("        <th width=\"30%\" style=\"background-color: #008080\"><font color=\"#FFFFFF\"><b>type</b></font></th>");

        sb.append("        <th width=\"30%\" style=\"background-color: #008080\"><font color=\"#FFFFFF\"><b>file</b></font></th>");
        sb.append("        <th width=\"30%\" style=\"background-color: #008080\"><font color=\"#FFFFFF\"><b>file.getName()</b></font></th>");
        sb.append("        <th width=\"30%\" style=\"background-color: #008080\"><font color=\"#FFFFFF\"><b>file.exists()</b></font></th>");
        sb.append("        <th width=\"30%\" style=\"background-color: #008080\"><font color=\"#FFFFFF\"><b>file.length()</b></font></th>");
        sb.append("    </tr>");


        while (files.hasMoreElements()) {
            String  name = (String) files.nextElement();
            String  filename = multi.getFilesystemName(name);
            String  type = multi.getContentType(name);
            File    f = multi.getFile(name);

            sb.append("    <tr>");

            sb.append("        <td width=\"30%\">");
            sb.append(name);
            sb.append("                </td>");

            sb.append("        <td width=\"30%\">");
            sb.append(filename);
            sb.append("                </td>");

            sb.append("        <td width=\"30%\">");
            sb.append(type);
            sb.append("                </td>");

            if (f != null) {
                sb.append("        <td width=\"30%\">");
                sb.append(f.toString());
                sb.append("                </td>");

                sb.append("        <td width=\"30%\">");
                sb.append(f.getName());
                sb.append("                </td>");

                sb.append("        <td width=\"30%\">");
                sb.append(f.exists());
                sb.append("                </td>");

                sb.append("        <td width=\"30%\">");
                sb.append(f.length());
                sb.append("                </td>");
            }

            sb.append("    </tr>");
        }

        sb.append("</table>");

        return (sb.toString());
    }






    /**
     * Method declaration
     *
     *
     * @param HttpServletRequest
     *
     * @return
     *
     * @see
     */

    public static final String debugParameters(HttpServletRequest request) {
        StringBuffer    sb = new StringBuffer();

        Enumeration     enum = request.getParameterNames();

        if (enum.hasMoreElements()) {
            sb.append("<h1>Servlet parameters (Single Value style):</h1>");


            sb.append("<table border=\"1\" width=\"100%\">");
            sb.append("    <tr>");
            sb.append("        <th width=\"30%\" style=\"background-color: #008080\"><font color=\"#FFFFFF\"><b>Param Name</b></font></th>");
            sb.append("        <th width=\"70%\" style=\"background-color: #008080\"><font color=\"#FFFFFF\"><b>Value</b></font></th>");
            sb.append("    </tr>");

            while (enum.hasMoreElements()) {
                String  name = (String) enum.nextElement();

                sb.append("    <tr>");
                sb.append("        <td width=\"30%\">");
                sb.append(name);
                sb.append("                </td>");

                sb.append("        <td width=\"70%\">");
                sb.append(request.getParameter(name));
                sb.append("                </td>");
                sb.append("    </tr>");
            }

            sb.append("</table>");
        }

        return (sb.toString());
    }




    /**
     * Method declaration
     *
     *
     * @param request
     *
     * @return
     *
     * @see
     */

    public static final String debugMultiParameters(HttpServletRequest request) {
        StringBuffer    sb = new StringBuffer();

        Enumeration     enum = request.getParameterNames();

        if (enum.hasMoreElements()) {
            sb.append("<h1>Servlet parameters (Multiple Value style):</h1>");


            sb.append("<table border=\"1\" width=\"100%\">");
            sb.append("    <tr>");
            sb.append("        <th width=\"30%\" style=\"background-color: #008080\"><font color=\"#FFFFFF\"><b>Param Name</b></font></th>");
            sb.append("        <th width=\"70%\" style=\"background-color: #008080\"><font color=\"#FFFFFF\"><b>Value</b></font></th>");
            sb.append("    </tr>");

            while (enum.hasMoreElements()) {
                String  name = (String) enum.nextElement();
                String  vals[] = (String[]) request.getParameterValues(name);

                sb.append("    <tr>");
                sb.append("        <td width=\"30%\">");
                sb.append(name);
                sb.append("                </td>");

                sb.append("        <td width=\"70%\">");

                for (int i = 0; i < vals.length; i++) {
                    sb.append("  val[" + i + "]=" + vals[i]);
                }

                sb.append("                </td>");
                sb.append("    </tr>");
            }

            sb.append("</table>");
        }

        return (sb.toString());
    }






    /**
     * Method declaration
     *
     *
     * @param HttpServletRequest
     *
     * @return
     *
     * @see
     */

    public static final String debugSystemProperties() {
        StringBuffer    sb = new StringBuffer();
        Enumeration     enum = System.getProperties().propertyNames();

        sb.append("<h1>System Properties:</h1>");
        sb.append("<table border=\"1\" width=\"800\">");
        sb.append("    <tr>");
        sb.append("        <th width=\"30%\" style=\"background-color: #008080\"><font color=\"#FFFFFF\"><b>System Property Name</b></font></th>");
        sb.append("        <th width=\"70%\" style=\"background-color: #008080\"><font color=\"#FFFFFF\"><b>Value</b></font></th>");
        sb.append("    </tr>");


        while (enum.hasMoreElements()) {
            String  key = (String) enum.nextElement();

            sb.append("    <tr>");
            sb.append("        <td width=\"30%\">");
            sb.append(key);
            sb.append("                </td>");

            sb.append("        <td width=\"70%\">");


            if ( key.indexOf("path") > 0 ) {
                StringTokenizer st = new StringTokenizer(  System.getProperty(key),  System.getProperty("path.separator") );
                while (st.hasMoreElements() ) {
                    sb.append("<p style=\"line-height: 1; margin-top: 1; margin-bottom: 1\">");
                    sb.append(st.nextElement() );
                    sb.append("</p>");
                }
            } else {
                sb.append(System.getProperty(key));
            }



            sb.append("                </td>");
            sb.append("    </tr>");
        }

        sb.append("</table>");

        return (sb.toString());
    }




    /**
     * Method declaration
     *
     *
     * @param res
     *
     * @return
     *
     * @see
     */

    public static final String debugResponse(HttpServletResponse res) {
        StringBuffer    sb = new StringBuffer();

        String          charset = res.getCharacterEncoding();

        sb.append("<h1>Response Information:</h1>");
        sb.append("<pre>");
        sb.append("MIME character encoding: " + charset);
        sb.append("</pre>");

        return sb.toString();
    }




    /**
     * Method declaration
     *
     *
     * @param req
     *
     * @return
     *
     * @see
     */

    public static final String debugRequestedURL(HttpServletRequest req) {
        StringBuffer    sb = new StringBuffer();

        sb.append("<h1>Requested URL:</h1>");
        sb.append("<pre>");
        sb.append(HttpUtils.getRequestURL(req).toString());
        sb.append("</pre>");

        return sb.toString();
    }



    /**
     * Method declaration
     *
     *
     * @param servlet
     *
     * @return
     *
     * @see
     */

    public static final String debugServletInitParams(GenericServlet servlet) {
        StringBuffer    sb = new StringBuffer();

        sb.append("<h1>Init Parameters</h1>");
        sb.append("<pre>");


        Enumeration enum = servlet.getServletConfig().getInitParameterNames();

        if (enum != null) {

            while (enum.hasMoreElements()) {
                String  param = (String) enum.nextElement();

                sb.append(" " + param + ": " + servlet.getInitParameter(param));
                sb.append("\n");
            }

            sb.append("</pre>");
        }

        return sb.toString();
    }










    /**
     * Method declaration
     *
     *
     * @param HttpServletRequest
     *
     * @return
     *
     * @see
     */

    public static final String debugCookieInfo(HttpServletRequest request) {
        StringBuffer    sb = new StringBuffer();


        sb.append("<h1>Cookie Information:</h1>");

        Cookie[]    cookieList = request.getCookies();

        if (cookieList != null) {
            sb.append("<table border=\"1\" width=\"100%\">");
            sb.append("    <tr>");
            sb.append("        <th width=\"20%\" style=\"background-color: #008080\"><font color=\"#FFFFFF\"><b>Cookie Name</b></font></th>");
            sb.append("        <th width=\"20%\" style=\"background-color: #008080\"><font color=\"#FFFFFF\"><b>Value</b></font></th>");
            sb.append("        <th width=\"20%\" style=\"background-color: #008080\"><font color=\"#FFFFFF\"><b>Path</b></font></th>");
            sb.append("        <th width=\"20%\" style=\"background-color: #008080\"><font color=\"#FFFFFF\"><b>Domain</b></font></th>");
            sb.append("        <th width=\"20%\" style=\"background-color: #008080\"><font color=\"#FFFFFF\"><b>Max Age</b></font></th>");
            sb.append("    </tr>");

            for (int i = 0; i < cookieList.length; i++) {

                sb.append("    <tr>");
                sb.append("        <td width=\"20%\">");
                sb.append(cookieList[i].getName());
                sb.append("                </td>");
                sb.append("        <td width=\"20%\">");
                sb.append(cookieList[i].getValue());
                sb.append("                </td>");
                sb.append("        <td width=\"20%\">");
                sb.append(cookieList[i].getPath());
                sb.append("                </td>");
                sb.append("        <td width=\"20%\">");
                sb.append(cookieList[i].getDomain());
                sb.append("                </td>");
                sb.append("        <td width=\"20%\">");
                sb.append(cookieList[i].getMaxAge());
                sb.append("                </td>");



                sb.append("    </tr>");
            }

            sb.append("</table>");
        } else {
            sb.append("<br>No Cookie Information Available");


        }

        sb.append("<br>");

        return sb.toString();
    }






    /**
     * Method declaration
     *
     *
     * @param HttpServletRequest
     *
     * @return
     *
     * @see
     */

    public static final String debugRequestInfo(HttpServletRequest request) {
        StringBuffer    sb = new StringBuffer();

        sb.append("<h1>Request information:</h1>");
        sb.append("<table border=\"1\" width=\"100%\">");

        sb.append("    <tr>");
        sb.append("        <td width=\"30%\" style=\"background-color: #008080\"><font color=\"#FFFFFF\"><b>");
        sb.append("Request method");
        sb.append("        </b></font></td>");
        sb.append("        <td width=\"70%\" style=\"background-color: #FFFFFF\"><font color=\"#000000\"><b>");
        sb.append(request.getMethod());
        sb.append("        </b></font></td>");
        sb.append("    </tr>");


        sb.append("    <tr>");
        sb.append("        <td width=\"30%\" style=\"background-color: #008080\"><font color=\"#FFFFFF\"><b>");
        sb.append("Request URI");
        sb.append("        </b></font></td>");
        sb.append("        <td width=\"70%\" style=\"background-color: #FFFFFF\"><font color=\"#000000\"><b>");
        sb.append(request.getRequestURI());
        sb.append("        </b></font></td>");
        sb.append("    </tr>");


        sb.append("    <tr>");
        sb.append("        <td width=\"30%\" style=\"background-color: #008080\"><font color=\"#FFFFFF\"><b>");
        sb.append("Request protocol");
        sb.append("        </b></font></td>");
        sb.append("        <td width=\"70%\" style=\"background-color: #FFFFFF\"><font color=\"#000000\"><b>");
        sb.append(request.getProtocol());
        sb.append("        </b></font></td>");
        sb.append("    </tr>");


        sb.append("    <tr>");
        sb.append("        <td width=\"30%\" style=\"background-color: #008080\"><font color=\"#FFFFFF\"><b>");
        sb.append("Servlet path");
        sb.append("        </b></font></td>");
        sb.append("        <td width=\"70%\" style=\"background-color: #FFFFFF\"><font color=\"#000000\"><b>");
        sb.append(request.getServletPath());
        sb.append("        </b></font></td>");
        sb.append("    </tr>");


        sb.append("    <tr>");
        sb.append("        <td width=\"30%\" style=\"background-color: #008080\"><font color=\"#FFFFFF\"><b>");
        sb.append("Path info");
        sb.append("        </b></font></td>");
        sb.append("        <td width=\"70%\" style=\"background-color: #FFFFFF\"><font color=\"#000000\"><b>");
        sb.append(request.getPathInfo());
        sb.append("        </b></font></td>");
        sb.append("    </tr>");


        sb.append("    <tr>");
        sb.append("        <td width=\"30%\" style=\"background-color: #008080\"><font color=\"#FFFFFF\"><b>");
        sb.append("Path translated");
        sb.append("        </b></font></td>");
        sb.append("        <td width=\"70%\" style=\"background-color: #FFFFFF\"><font color=\"#000000\"><b>");
        sb.append(request.getPathTranslated());
        sb.append("        </b></font></td>");
        sb.append("    </tr>");


        String  queryString = request.getQueryString();

        sb.append("    <tr>");
        sb.append("        <td width=\"30%\" style=\"background-color: #008080\"><font color=\"#FFFFFF\"><b>");
        sb.append("Query string");
        sb.append("        </b></font></td>");
        sb.append("        <td width=\"70%\" style=\"background-color: #FFFFFF\"><font color=\"#000000\"><b>");
        sb.append(queryString);
        sb.append("        </b></font></td>");
        sb.append("    </tr>");

        if (queryString != null) {
            sb.append("    <tr>");
            sb.append("        <td width=\"30%\" style=\"background-color: #008080\"><font color=\"#FFFFFF\"><b>");
            sb.append("Query string Length");
            sb.append("        </b></font></td>");
            sb.append("        <td width=\"70%\" style=\"background-color: #FFFFFF\"><font color=\"#000000\"><b>");
            sb.append(queryString.length());
            sb.append("        </b></font></td>");
            sb.append("    </tr>");
        }


        sb.append("    <tr>");
        sb.append("        <td width=\"30%\" style=\"background-color: #008080\"><font color=\"#FFFFFF\"><b>");
        sb.append("Content length");
        sb.append("        </b></font></td>");
        sb.append("        <td width=\"70%\" style=\"background-color: #FFFFFF\"><font color=\"#000000\"><b>");
        sb.append(request.getContentLength());
        sb.append("        </b></font></td>");
        sb.append("    </tr>");


        sb.append("    <tr>");
        sb.append("        <td width=\"30%\" style=\"background-color: #008080\"><font color=\"#FFFFFF\"><b>");
        sb.append("Content type");
        sb.append("        </b></font></td>");
        sb.append("        <td width=\"70%\" style=\"background-color: #FFFFFF\"><font color=\"#000000\"><b>");
        sb.append(request.getContentType());
        sb.append("        </b></font></td>");
        sb.append("    </tr>");


        sb.append("    <tr>");
        sb.append("        <td width=\"30%\" style=\"background-color: #008080\"><font color=\"#FFFFFF\"><b>");
        sb.append("Server name");
        sb.append("        </b></font></td>");
        sb.append("        <td width=\"70%\" style=\"background-color: #FFFFFF\"><font color=\"#000000\"><b>");
        sb.append(request.getServerName());
        sb.append("        </b></font></td>");
        sb.append("    </tr>");


        sb.append("    <tr>");
        sb.append("        <td width=\"30%\" style=\"background-color: #008080\"><font color=\"#FFFFFF\"><b>");
        sb.append("Server port");
        sb.append("        </b></font></td>");
        sb.append("        <td width=\"70%\" style=\"background-color: #FFFFFF\"><font color=\"#000000\"><b>");
        sb.append(request.getServerPort());
        sb.append("        </b></font></td>");
        sb.append("    </tr>");


        sb.append("    <tr>");
        sb.append("        <td width=\"30%\" style=\"background-color: #008080\"><font color=\"#FFFFFF\"><b>");
        sb.append("Remote user");
        sb.append("        </b></font></td>");
        sb.append("        <td width=\"70%\" style=\"background-color: #FFFFFF\"><font color=\"#000000\"><b>");
        sb.append(request.getRemoteUser());
        sb.append("        </b></font></td>");
        sb.append("    </tr>");

        sb.append("    <tr>");
        sb.append("        <td width=\"30%\" style=\"background-color: #008080\"><font color=\"#FFFFFF\"><b>");
        sb.append("Remote address");
        sb.append("        </b></font></td>");
        sb.append("        <td width=\"70%\" style=\"background-color: #FFFFFF\"><font color=\"#000000\"><b>");
        sb.append(request.getRemoteAddr());
        sb.append("        </b></font></td>");
        sb.append("    </tr>");

        sb.append("    <tr>");
        sb.append("        <td width=\"30%\" style=\"background-color: #008080\"><font color=\"#FFFFFF\"><b>");
        sb.append("Remote host");
        sb.append("        </b></font></td>");
        sb.append("        <td width=\"70%\" style=\"background-color: #FFFFFF\"><font color=\"#000000\"><b>");
        sb.append(request.getRemoteHost());
        sb.append("        </b></font></td>");
        sb.append("    </tr>");

        sb.append("    <tr>");
        sb.append("        <td width=\"30%\" style=\"background-color: #008080\"><font color=\"#FFFFFF\"><b>");
        sb.append("Authorization scheme");
        sb.append("        </b></font></td>");
        sb.append("        <td width=\"70%\" style=\"background-color: #FFFFFF\"><font color=\"#000000\"><b>");
        sb.append(request.getAuthType());
        sb.append("        </b></font></td>");
        sb.append("    </tr>");

        sb.append("</table>");

        return sb.toString();
    }


    /**
     * Method declaration
     *
     *
     * @param HttpServletRequest
     *
     * @return
     *
     * @see
     */

    public static final String debugHeaderNames(HttpServletRequest request) {
        StringBuffer    sb = new StringBuffer();

        Enumeration     enum = request.getHeaderNames();

        if (enum.hasMoreElements()) {
            sb.append("<h1>Request headers:</h1>");
            sb.append("<table border=\"1\" width=\"100%\">");
            sb.append("    <tr>");
            sb.append("        <th width=\"30%\" style=\"background-color: #008080\"><font color=\"#FFFFFF\"><b>Header Name</b></font></th>");
            sb.append("        <th width=\"70%\" style=\"background-color: #008080\"><font color=\"#FFFFFF\"><b>Value</b></font></th>");
            sb.append("    </tr>");


            while (enum.hasMoreElements()) {
                String  name = (String) enum.nextElement();

                sb.append("    <tr>");
                sb.append("        <td width=\"30%\">");
                sb.append(name);
                sb.append("                </td>");

                sb.append("        <td width=\"70%\">");
                sb.append(request.getHeader(name));
                sb.append("                </td>");
                sb.append("    </tr>");
            }

            sb.append("</table>");
        }

        return sb.toString();
    }


    /**
     * Method declaration
     * returns the String that has detailed request information
     *
     * @param context
     *
     * @return
     *
     *
     * @see
     */

    public static final String getRequestInfo(GenericServlet servlet, HttpServletRequest request, HttpServletResponse response) {

        String  content_type = request.getContentType();


        if (content_type != null && content_type.startsWith("multipart")) {
            try {
                MultipartRequest    multi = new MultipartRequest(request, ".");

                return getMultiPartRequestInfo(servlet, multi, request, response);
            } catch (Exception e) {
                return "Error reading multipart request Exception:" + e;
            }

        } else {
            return getStdRequestInfo(servlet, request, response);
        }
    }




    /**
     * Method declaration
     *
     *
     * @param context
     * @param errorMsg
     *
     * @return
     *
     *
     * @see
     */

    public static final String getStdRequestInfo(GenericServlet servlet, HttpServletRequest request, HttpServletResponse response) {
        StringBuffer    sb = new StringBuffer();

        sb.append(ServletDebugUtil.debugServletInitParams( servlet));

        sb.append(ServletDebugUtil.debugRequestedURL(request));
        sb.append(ServletDebugUtil.debugCookieInfo(request));
        sb.append(ServletDebugUtil.debugRequestInfo(request));
        sb.append(ServletDebugUtil.debugHeaderNames(request));
        sb.append(ServletDebugUtil.debugParameters(request));
        sb.append(ServletDebugUtil.debugMultiParameters(request));
        sb.append(ServletDebugUtil.debugSystemProperties());
        sb.append(ServletDebugUtil.debugResponse(response));

        return (sb.toString());
    }


    /**
     * Method declaration
     *
     *
     * @param context
     *
     * @return
     *
     * @throws Exception
     *
     * @see
     */

    public static final String getMultiPartRequestInfo(GenericServlet servlet, MultipartRequest multi, HttpServletRequest request, HttpServletResponse response) throws Exception {


        StringBuffer    sb = new StringBuffer();

        sb.append(ServletDebugUtil.debugServletInitParams( servlet));

        sb.append(ServletDebugUtil.debugRequestedURL(request));
        sb.append(ServletDebugUtil.debugCookieInfo(request));
        sb.append(ServletDebugUtil.debugRequestInfo(request));
        sb.append(ServletDebugUtil.debugHeaderNames(request));
        sb.append(ServletDebugUtil.debugParameters(request));
        sb.append(ServletDebugUtil.debugMultiParameters(request));
        sb.append(ServletDebugUtil.debugMultiPartRequest( multi));
        sb.append(ServletDebugUtil.debugSystemProperties());
        sb.append(ServletDebugUtil.debugResponse(response));

        return (sb.toString());
    }


}



/*--- formatting done in "CIBC Java Application Frameworks Coding Style" style on 05-16-2000 ---*/

