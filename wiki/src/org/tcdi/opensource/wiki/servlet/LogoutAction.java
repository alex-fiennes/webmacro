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
import javax.servlet.http.Cookie;
import org.webmacro.servlet.WebContext;

import org.tcdi.opensource.wiki.*;

/**
 * Log out the current user by removing his Cookie
 * @author  e_ridge
 */
public class LogoutAction implements PageAction {

    /**
     * can only log out if "?logout=true" is specified in the request
     */
    public boolean accept(WikiSystem wiki, WebContext wc, WikiUser user) {
        String logout = wc.getForm ("logout");
        return logout != null && logout.equals ("true");
    }
    
    /**
     * we redirect to the start page of the specified wiki, after
     * removing any cookie associed with this request
     */
    public void perform(WikiSystem wiki, WebContext wc, WikiUser user, WikiPage page) throws PageAction.PageActionException {
        String cookieName = wiki.getProperties().getProperty ("CookieName");
        try {
            // create and add a null, expired cookie
            Cookie c = new Cookie(cookieName, "");
            c.setPath("/");
            c.setMaxAge(0); 

            wc.getResponse().addCookie(c);
        } catch (Exception e) {
            // should never happen
            throw new PageAction.PageActionException (e.toString());
        }
        
        throw new PageAction.RedirectException (wiki.getStartPage ());
    }
    
    /**
     * no template for this action, because we do a redirect during perform()
     */
    public String getTemplateName(WikiSystem wiki, WikiPage page) {
        return null;
    }
    
    /**
     * no page name for this action
     */
    public String getWikiPageName(WikiSystem wiki, WebContext wc) {
        return null;
    }
    
}
