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

import org.tcdi.opensource.wiki.*;

/**
 * WikiPageRenderers can transform a WikiPage into a certain text format.
 * Default implementes for rendering to Text and HTML are provided.
 *
 * @see com.tcdi.opensource.wiki.renderer.TextPageRenderer
 * @see com.tcdi.opensource.wiki.renderer.HTMLPageRenderer
 * @author  e_ridge
 */
public interface WikiPageRenderer {
    
    /**
     * A WikiURLRenderer takes a URL (String) and generates a rendered version 
     * of it.  PageRenderer's are encouraged to use their own URL Renderer
     *
     * @author  e_ridge
     */
    public static interface WikiURLRenderer {

        public String render (String url);
    }    
    
    /** 
     * Exception thrown when a WikiPage cannot be rendered
     */
    public static class RenderException extends Exception {
        public RenderException (String msg) {
            super (msg);
        }
    }
    
    /**
     * Initialize this WikiPageRenderer for use with the specified
     * WikiSystem. 
     */
    public void init (WikiSystem wiki);
    
    /**
     * Render the specified WikiPage and return the rendered results
     * as a String
     */
    public String render (WikiPage page) throws RenderException;
    
    /**
     * Render the specified WikiPage to the specified output stream.<p>
     *
     * This method should <b>not</b> close the output stream.
     */
    public void render (WikiPage page, OutputStream out) throws IOException, RenderException;
}