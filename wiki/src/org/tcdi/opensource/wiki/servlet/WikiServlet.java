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

import javax.servlet.http.*;
import javax.servlet.*;

import java.util.*;
import java.io.*;

import org.webmacro.*;
import org.webmacro.servlet.*;

import org.tcdi.opensource.wiki.*;
import org.tcdi.opensource.util.*;

/**
 * The main servlet for Wiki
 *
 * @author Eric B. Ridge
 */
public class WikiServlet extends WMServlet {

    private static String COOKIE_NAME;
    private static long COOKIE_TIMEOUT;
    
    /** a log we can use */
    private Log _log;

    /** the Wiki instance we're to use */
    private WikiSystem _wiki;
    
    /** the PageActionManager we should use for each page request */
    private PageActionManager _actionManager;
    

    /**
     * do necessary statup work like creating a Log and configuring
     * the various options of WikiServet
     */
    public void start () throws ServletException {
        super.start ();
        _log = this.getLog ("WikiServlet");

        try {
            configure();
        } catch (Exception e) {
            throw new ServletException (e);
        }      
    }
    
    
    /**
     * Respond to a request by getting the proper PageAction from
     * our PageActionManager
     */
    public final Template handle(WebContext wc) throws HandlerException {
        String pageName = null;
        WikiPage wikiPage = null;

        // who is trying to do something?
        WikiUser user = getUser (wc);
        
        // which action wants to respond to this request?
        PageAction action = _actionManager.getAction (wc, user);
        
        
        // use the action to determine which WikiPage
        // we should be dealing with
        if (action != null)
            pageName = action.getWikiPageName (_wiki, wc);
        if (pageName != null)
            wikiPage = _wiki.getPage (pageName);

        // stuff the webcontext with useful stuff
        stuffContext (wc, wikiPage, user);
        
        if (action == null)
            throw new HandlerException ("Unable to find a PageAction to handle"
                                      + " this request.");
        try {
            // attempt to perform the action against the page
            action.perform (_wiki, wc, user, wikiPage);
        } catch (PageAction.RedirectException re) {
            // action wants us to redirect somewhere else
            try {
                wc.getResponse().sendRedirect (re.getURL());
            } catch (IOException ioe) {
                throw new HandlerException ("Cannot redirect to " 
                                          + re.getURL(), ioe);
            }
            return null;
        } catch (Exception e) {
            // something bad happened while performing the action
            // TODO: Handle error and error template ourselves
            _log.error ("Could not perform action", e);
            throw new HandlerException (e.toString());
        }           


        // the action performed successfully, so now return 
        // the template it wants us to use
        try {
            // determine the template name and return
            String templateName = action.getTemplateName(_wiki, wikiPage);
            return getTemplate (templateName);

        } catch (ResourceException re) {
            throw new HandlerException ("Could not get template", re);
        }
    }
    
    
    /**
     * @return a WikiUser object from cookie.  null if no cookie or if user not found
     */
    private WikiUser getUser (WebContext wc) {
        try {
            Cookie cookie = wc.getCookie(COOKIE_NAME);
            String uid = null;
            String password = null;
            
            if (cookie != null) {
                String val = cookie.getValue();
                int idx = val.indexOf('|');
                uid = val.substring(0, idx);
                password = val.substring(idx+1);
            }
            
            WikiUser user = (WikiUser) ((uid == null) ? null : _wiki.getUser(uid));
            if (user != null) {
                // udate the last accessed attribute for this user
                user.setAttribute("LastAccessed", new Date().toString());
                user.setAttribute("IPAddress", wc.getRequest().getRemoteAddr());
                _wiki.updateUser(user);
            }
            
            return user;
        }
        catch (Exception e) {
            return null;
        }
    }
    
    /**
     * stuff the context with objects required by most templates
     */
    private void stuffContext (WebContext wc, WikiPage page, WikiUser user) {
        wc.put ("Wiki", _wiki);
        wc.put ("WikiUtil", WikiUtil.getInstance());
        wc.put ("Renderer", _wiki.getPageRenderer());
        wc.put ("Page", page);
        wc.put ("User", user);
    }
    
    /**
     * do one-time intialization/configuration
     */
    private void configure () throws Exception {
        _wiki = new Wiki (this.getInitParameter ("properties"));
        
        _actionManager = new PageActionManager (_wiki, _log);
        COOKIE_NAME = _wiki.getProperties().getProperty ("CookieName").trim();
        COOKIE_TIMEOUT = Long.parseLong (_wiki.getProperties().getProperty ("CookieTimeout").trim());
        _log.notice ("Wiki configured successfully");
        
        boolean reindex = _wiki.getProperties().getProperty ("ReIndex") != null
                       && _wiki.getProperties().getProperty ("ReIndex").equalsIgnoreCase ("true");
        if (reindex) 
            _wiki.indexCurrentPages();        
    }
}