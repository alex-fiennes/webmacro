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
 * Login a user to a Wiki.  Also sets a cookie on the client's computer.<p>
 * @author  e_ridge
 */
public class LoginAction implements PageAction {

    /**
     * can only log in if "?login=true" is specified in the request
     */
    public boolean accept(WikiSystem wiki, WebContext wc, WikiUser user) {
        String login = wc.getForm ("login");
        return login != null && login.equals ("true");
    }
    
    /**
     * If it's a GET request, we simply display the login template.  If it's
     * a POST request, do process the login request, set the cookie, and
     * redirect to the start page.
     */
    public void perform(WikiSystem wiki, WebContext wc, WikiUser user, WikiPage page) throws PageAction.PageActionException {
        String method = wc.getRequest().getMethod();
        if (method.equalsIgnoreCase ("GET")) {
            return;
        } else if (method.equalsIgnoreCase ("POST")) {
            if (!loginUser (wiki, wc))
                throw new PageAction.PageActionException ("Authentication failed");
            else    // redirect to wiki start page
                throw new PageAction.RedirectException (wiki.getStartPage ());
        } else {
            throw new PageAction.PageActionException ("Unknown method: " + method);
        }
    }
    
    /**
     * template is the "LoginAction.Template" configuration option
     */
    public String getTemplateName(WikiSystem wiki, WikiPage page) {
        return wiki.getProperties().getProperty ("LoginAction.Template");
    }
    
    /**
     * no page name for this action
     */
    public String getWikiPageName(WikiSystem wiki, WebContext wc) {
        return null;
    }
    
    
    /**
     * logs user into site
     * Required "username" and "password" to exist in the webcontext as
     * form parameters (ie, via <code>wc.getForm(...)<code>)
     * @return true if login successful.
     */
    static boolean loginUser(WikiSystem wiki, WebContext wc) {
        String userid   = wc.getForm("username");
        String password = wc.getForm("password");

        if (userid == null || password == null) {
            createCookie(wiki, wc, null);
            return false;
        }
        
        WikiUser user = (WikiUser) wiki.getUser(userid);
        if (user == null)
            return false;   // user not found

        String userupw = user.getPassword().substring(0, 10);
        String authupw = WikiUtil.getMD5 (password).substring(0, 10);
        if (userupw.equals (authupw)) {
            createCookie(wiki, wc, user);
            if (user.getPassword().equals(password)) {  // support for old clear-text passwords
                // convert to new md5 form
                user.setPassword(WikiUtil.getMD5(password));
                // and save back into the user store
                wiki.updateUser(user);
            }
            
            return true;
        }

        return false;
    }
    
    /** 
     * create a cookie for the specified WikiUser
     */
    private static void createCookie(WikiSystem wiki, WebContext wc, WikiUser user)  {
        try {
            String cookieName = wiki.getProperties().getProperty ("CookieName");
            long cookieTimeout = Long.parseLong (wiki.getProperties().getProperty ("CookieTimeout").trim());
            Cookie c = new Cookie(cookieName, "" 
                            + ((user == null) ? "" : user.getIdentifier()) 
                            + ((user == null) ? "" : ("|" + user.getPassword()))
                                  );
            c.setPath("/");
            if (user == null)
                c.setMaxAge(0);  // clear it out
            else
                c.setMaxAge((int) cookieTimeout);

            wc.getResponse().addCookie(c);
        } catch (Exception e) {
            // should never happen
        }
    }     
}