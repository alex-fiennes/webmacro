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
package org.tcdi.opensource.wiki.servlet;

import java.util.*;

import org.webmacro.servlet.WebContext;

import org.tcdi.opensource.wiki.*;

/**
 * Displays a WikiPage via the "EditPageAction.Template".
 * @author  e_ridge
 */
public class DeletePageAction implements PageAction {

    /**
     * only moderators can delete a page
     */
    public boolean accept(WikiSystem wiki, WebContext wc, WikiUser user) {
        return user != null 
            && user.getIsModerator()
            && wc.getForm ("delete") != null;
    }
    
    /**
     * if the request type is GET, display the DeletePageAction.Template.  If
     * it's POST and the "?ACTION" parameter equals "Yes", delete the page
     */
    public void perform(WikiSystem wiki, WebContext wc, WikiUser user, WikiPage page) throws PageAction.PageActionException {
        if (page != null && !user.getIsModerator())
            throw new PageActionException ("Only moderators can delete pages");
        
        String method = wc.getRequest().getMethod();
        String pageName = wc.getForm ("delete");
        String action = wc.getForm ("ACTION");
        
        if (method.equalsIgnoreCase ("POST")) {
            if (action != null && action.equals ("Yes")) {
                wiki.deletePage (pageName);
                throw new PageAction.RedirectException (wiki.getStartPage());
            } else {
                throw new PageAction.RedirectException (pageName);
            }
        }
        
        wc.put ("PageName", pageName);
    }
    
    /**
     * which WebMacro template does this action use?
     */
    public String getTemplateName(WikiSystem wiki, WikiPage page) {
        return wiki.getProperties().getProperty ("DeletePageAction.Template");
    }
    
    /**
     * based on the specified WikiSystem and WebContext, which WikiPage is
     * this action going to be dealing with?
     */
    public String getWikiPageName(WikiSystem wiki, WebContext wc) {
        return wc.getForm ("delete");
    }
}
