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
 * TextPageRenderer can render a WikiPage as ASCII Text.  Aside from
 * newlines, paragraphs, and indents, doesn't support much formatting
 *
 * @author  e_ridge
 */
public class TextPageRenderer implements WikiPageRenderer {

    private WikiSystem _wiki;
 
    /**
     * Initialize this WikiPageRenderer for use with the specified
     * WikiSystem.  It should also use the specified URLRenderer for
     * rendering URL's contained within a page
     */
    public void init(WikiSystem wiki) {
        _wiki = wiki;
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
            Object obj = wikidata.getData();
            String data = (obj == null) ? "" : obj.toString();
            StringBuffer sb = new StringBuffer();
            int type = wikidata.getType();
            switch (type) {
                case WikiDataTypes.PLAIN_TEXT:
                    sb.append(data);
                    break;
                    
                case WikiDataTypes.PAGE_REFERENCE:
                        sb.append(data);
                    break;
                    
                case WikiDataTypes.INDENT:
                    int many = (data != null && data.length() > 0) ? Integer.parseInt(data) : 1;
                    for (int x=0; x<=many; x++)
                        sb.append(" ");
                    break;
                    
                case WikiDataTypes.START_NAMED_HEADER:
                case WikiDataTypes.END_NAMED_HEADER:
                    break;
                    
                case WikiDataTypes.JAVADOC:
                case WikiDataTypes.IMAGE:
                case WikiDataTypes.URL:
                    sb.append (data);
                    break;
                    
                case WikiDataTypes.START_COLOR:
                case WikiDataTypes.END_COLOR:

                case WikiDataTypes.EMAIL:
                    sb.append(data);
                    break;
                    
                case WikiDataTypes.QUOTED_BLOCK:
                    sb.append(org.webmacro.servlet.TextTool.HTMLEncode (data));
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
                    break;
            }
            return sb.toString();
        }
        catch (Exception e) {
            throw new RenderException (e.toString());
        }
    }
 }