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
 * Allows new users to register with Wiki
 * @author  e_ridge
 */
public class RegisterNewUserAction implements PageAction {

    /**
     * if "?register=true", we accept
     */
    public boolean accept(WikiSystem wiki, WebContext wc, WikiUser user) {
        String register = wc.getForm ("register");
        return register != null && register.equals ("true");
    }
    
    /**
     * If it's a GET request, we simply display the register template.  If it's
     * a POST request, do process the registration request, create the user, 
     * and display the register template again.  The register template should be
     * smart enough to detect $User, and if so, display some kind of "thank you"
     * message.
     */
    public void perform(WikiSystem wiki, WebContext wc, WikiUser user, WikiPage page) throws PageActionException {
        String method = wc.getRequest().getMethod();
        if (method.equalsIgnoreCase ("GET")) {
            return;
        } else if (method.equalsIgnoreCase ("POST")) {
            try {
                // register the user and store him in the context
                user = registerUser (wiki, wc);
                wc.put ("User", user);
                
                // and log him in... which creates his cookie
                LoginAction.loginUser(wiki, wc);
                
            } catch (Exception e) {
                throw new PageActionException (e.getMessage());
            }
        } else {
            throw new PageActionException ("Unknown method: " + method);
        }
    }
    
    /**
     * template is the "RegisterUserAction.Template" configuration option
     */
    public String getTemplateName(WikiSystem wiki, WikiPage page) {
        return wiki.getProperties().getProperty ("RegisterUserAction.Template");
    }
    
    /**
     * no page name for this action
     */
    public String getWikiPageName(WikiSystem wiki, WebContext wc) {
        return null;
    }
    
    
    /**
     * register the user based on info in the context, and create his cookie
     */
    private WikiUser registerUser(WikiSystem wiki, WebContext wc) throws Exception {
        String first = wc.getForm ("FIRST_NAME").trim();
        String last = wc.getForm ("LAST_NAME").trim();
        String fullName = first + " " + last;
        String email = wc.getForm ("EMAIL").trim();
        String uid = wc.getForm ("username").trim();
        String upw = wc.getForm ("password").trim();
        String upw2 = wc.getForm ("password2").trim();
        
        if (!upw.equals (upw2))
            throw new Exception ("Passwords do not match");
        if (first.length() == 0 || last.length() == 0 || email.length() == 0
            || uid.length() == 0)
            throw new Exception ("All fields are required");
        if (uid.indexOf (' ') > -1)
            throw new Exception ("Username cannot contiain spaces");
        
        Hashtable attributes = new Hashtable ();
        attributes.put ("email", email);
        attributes.put ("firstname", first);
        attributes.put ("lastname", last);
        
        WikiUser user = wiki.registerUser(uid, fullName, WikiUtil.getMD5(upw), attributes);
        
        return user;
    }
    
    /** 
     * create a cookie for the specified WikiUser
     */
    private void createCookie(WikiSystem wiki, WebContext wc, WikiUser user)  {
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