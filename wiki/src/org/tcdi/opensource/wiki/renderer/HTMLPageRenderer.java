/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Wiki.
 *
 * The Initial Developer of the Original Code is Technology Concepts
 * and Design, Inc.
 * Copyright (C) 2000 Technology Concepts and Design, Inc.  All
 * Rights Reserved.
 *
 * Contributor(s): Lane Sharman (OpenDoors Software)
 *                 Justin Wells (Semiotek Inc.)
 *                 Eric B. Ridge (Technology Concepts and Design, Inc.)
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the GNU General Public License Version 2 or later (the
 * "GPL"), in which case the provisions of the GPL are applicable
 * instead of those above.  If you wish to allow use of your
 * version of this file only under the terms of the GPL and not to
 * allow others to use your version of this file under the MPL,
 * indicate your decision by deleting the provisions above and
 * replace them with the notice and other provisions required by
 * the GPL.  If you do not delete the provisions above, a recipient
 * may use your version of this file under either the MPL or the
 * GPL.
 *
 *
 * This product includes sofware developed by OpenDoors Software.
 *
 * This product includes software developed by Justin Wells and Semiotek Inc.
 * for use in the WebMacro ServletFramework (http://www.webmacro.org).
 */

package org.tcdi.opensource.wiki.renderer;

import java.io.*;
import java.util.*;

import org.tcdi.opensource.wiki.*;
import org.tcdi.opensource.wiki.renderer.*;

/**
 * HTMLPageRenderer can render a WikiPage to HTML.  Supports all the
 * various formatting options parsed by the WikiParser.
 *
 * @author  e_ridge
 */
public class HTMLPageRenderer implements WikiPageRenderer {

    private static final Map _renderDataTypeLookup = new HashMap ();
    static {
        _renderDataTypeLookup.put("" + WikiDataTypes.START_BOLD, "<b>");
        _renderDataTypeLookup.put("" + WikiDataTypes.END_BOLD,   "</b>");
        
        _renderDataTypeLookup.put("" + WikiDataTypes.START_UNDERLINE, "<u>");
        _renderDataTypeLookup.put("" + WikiDataTypes.END_UNDERLINE, "</u>");
        
        _renderDataTypeLookup.put("" + WikiDataTypes.START_ITALIC, "<i>");
        _renderDataTypeLookup.put("" + WikiDataTypes.END_ITALIC, "</i>");
        
        _renderDataTypeLookup.put("" + WikiDataTypes.PARAGRAPH_BREAK, "<p>\n\n");
        _renderDataTypeLookup.put("" + WikiDataTypes.LINE_BREAK, "<br>\n");
        
        _renderDataTypeLookup.put("" + WikiDataTypes.HORIZ_LINE, "<hr>\n");
    }

    private WikiSystem _wiki;
    private WikiURLRenderer _urlRenderer;

    /**
     * Initialize this WikiPageRenderer for use with the specified
     * WikiSystem.  It should also use the specified URLRenderer for
     * rendering URL's contained within a page
     */
    public void init(WikiSystem wiki) {
        _wiki = wiki;
        _urlRenderer = new HTMLURLRenderer ();
    }
    
    
    /**
     * Render the specified WikiPage and return the rendered results
     * as a String
     */
    public String render(WikiPage page) throws RenderException {
        if (page == null)
            throw new RenderException ("Page is null");
        
        WikiData[] data = page.getData ();
        StringBuffer sb = new StringBuffer (4096);
        String title = page.getTitle ();
        for (int x=0; x<data.length; x++) {
            sb.append (renderWikiData (data[x], title));
        }
        
        return sb.toString ();
    }
    
    /**
     * Render the specified WikiPage to the specified output stream.<p>
     *
     * This method should <b>not</b> close the output stream.
     */
    public void render(WikiPage page, OutputStream out) throws IOException, RenderException {
        out.write (render (page).getBytes());
    }
    
    
    /**
     * render a specific WikiData object as a string
     */
    private final String renderWikiData(WikiData wikidata, String title) throws RenderException {
        try {
            int type = wikidata.getType();
            String output = (String) _renderDataTypeLookup.get("" + type);
            
            if (output != null)
                return output;
            
            Object obj = wikidata.getData();
            String data = (obj == null) ? "" : obj.toString();
            StringBuffer sb = new StringBuffer();
            
            switch (type) {
                case WikiDataTypes.PLAIN_TEXT:
                    sb.append(data);
                    break;
                    
                case WikiDataTypes.PAGE_REFERENCE:
                    if (title.equals(data) || data.equals("WebMacro"))
                        sb.append(data);
                    else if (_wiki.containsPage(data)) {
                        sb.append("<a href=\"");
                        sb.append(data);
                        sb.append("\">");
                        sb.append(data);
                        sb.append("</a>");
                    }
                    else {
                        sb.append(data);
                        sb.append("<a href=\"");
                        sb.append("ControllerPage?edit=");
                        sb.append(data);
                        sb.append("\" TITLE=\"Define " + data + "\">?</a>");
                    }
                    break;
                    
                case WikiDataTypes.INDENT:
                    int many = (data != null && data.length() > 0) ? Integer.parseInt(data) : 1;
                    for (int x=0; x<=many; x++)
                        sb.append("&nbsp;");
                    break;
                    
                case WikiDataTypes.START_NAMED_HEADER:
                    String header = _wiki.getProperties().getProperty("Headers." + data + "_Start");
                    if (header == null)
                        header = _wiki.getProperties().getProperty("Headers._Start") + data + " ";
                    sb.append(header);
                    break;
                    
                case WikiDataTypes.END_NAMED_HEADER:
                    String end = _wiki.getProperties().getProperty("Headers." + data + "_End");
                    if (end == null)
                        end = _wiki.getProperties().getProperty("Headers._End");
                    sb.append(end);
                    break;
                    
                case WikiDataTypes.URL:
                    sb.append (_urlRenderer.render (data));
                    break;
                    
                // not used anymore.  is not a URL type.  remains for backwards
                // compatibility with existing serialized WikiPages
                case WikiDataTypes.IMAGE:
                    sb.append("<img src=\"");
                    sb.append(data);
                    sb.append("\">");
                    break;
                    
                // not used anymore.  is not a URL type.  remains for backwards
                // compatibility with existing serialized WikiPages
                case WikiDataTypes.JAVADOC:
                    sb.append("<a target=javadoc href=\"");
                    sb.append(_wiki.getProperties().getProperty("JavaDocRoot"));
                    sb.append(data.replace('.', '/'));
                    sb.append(".html\">");
                    sb.append("<font color=\"");
                    sb.append(_wiki.getProperties().getProperty("Color.JavadocLink"));
                    sb.append("\">");
                    sb.append(data);
                    sb.append("</font></a>");
                    break;
                    
                case WikiDataTypes.START_COLOR:
                    sb.append("<font color=\"");
                    sb.append(data);
                    sb.append("\">");
                    break;
                    
                case WikiDataTypes.END_COLOR:
                    sb.append("</font>");
                    break;
                    
                case WikiDataTypes.EMAIL:
                    sb.append("<a href=\"mailto:");
                    sb.append(data);
                    sb.append("\">");
                    sb.append(data);
                    sb.append("</a>");
                    break;
                    
                case WikiDataTypes.QUOTED_BLOCK:
                    sb.append("<pre>");
                    sb.append(org.webmacro.servlet.TextTool.HTMLEncode (data));
                    sb.append("</pre>");
                    break;
               
                case WikiDataTypes.SPACE:
                    sb.append (" ");
                    break;
                    
                case WikiDataTypes.LT:
                    sb.append ("&lt;");
                    break;
                    
                case WikiDataTypes.GT:
                    sb.append ("&gt;");
                    break;
                    
                default:
                    sb.append("<!-- ");
                    sb.append(type);
                    sb.append(" is an unrecognized data type. -->");
            }
            return sb.toString();
        }
        catch (Exception e) {
            throw new RenderException (e.toString());
        }
    }
 
    
    class HTMLURLRenderer implements WikiPageRenderer.WikiURLRenderer {
        public String render(String url) {
            if (url == null)
                return null;

            int l_paren = url.indexOf ('(');
            int r_paren = url.indexOf (')');
            String label;
            if (r_paren > l_paren && l_paren != -1) {
                label = url.substring (l_paren+1, r_paren);
                url = url.substring (0, l_paren);
            } else {
                label = url;
            }

            int idx = url.indexOf(':');
            String protocol = url.substring (0, idx);
            String location = url.substring (idx+1);
            
            StringBuffer sb = new StringBuffer ();

            if (protocol.equalsIgnoreCase ("http") 
             || protocol.equalsIgnoreCase ("ftp")
             || protocol.equalsIgnoreCase ("gopher")) {
                sb.append ("<a href=\"")
                  .append (url)
                  .append ("\" target=_blank>")
                  .append (label)
                  .append ("</a>");
            } else if (protocol.equalsIgnoreCase ("mailto")) {
                sb.append ("<a href=\"")
                  .append (url)
                  .append ("\">")
                  .append (label)
                  .append ("</a>");
            } else if (protocol.equalsIgnoreCase ("javadoc")) {
                sb.append("<a target=javadoc href=\"")
                  .append(_wiki.getProperties().getProperty("JavaDocRoot"))
                  .append(location.replace('.', '/'))
                  .append(".html\">")
                  .append(label)
                  .append("</a>");
            } else if (protocol.equalsIgnoreCase ("image")) {
                sb.append ("<img src=\"")
                  .append (location)
                  .append ("\" border=0>");
            } else {  // unrecognized protocol
                return url;
            }

            return sb.toString();
        }
    }    
}