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

import org.webmacro.servlet.*;
import org.tcdi.opensource.wiki.*;

/**
 * A PageAction is a simple class that is used to respond to an http
 * request.  Each PageAction can decide for itself, via the <code>accept(...)</code>
 * method if it should respond to the current request.<p>
 *
 * If the PageAction accepts, it is responsible for providing the caller
 * with the Wiki page name and webmacro template to use.  It is also responsible
 * for doing any work required to handle the request.<p>
 *
 * PageActions are should be thread-safe.  PageActions are managed by the
 * PageActionManager, and only 1 PageAction exists per PageActionManager.  It
 * is very possible multiple requests will be using the same PageAction *at the
 * same time*.<p>
 *
 * If a PageAction determines it needs to issue an HTTP redirect to another
 * URL, its <code>perform</code> method can throw a 
 * <code>PageAction.RedirectException</code> with the new URL as the Exception
 * message.<p>
 *
 * If the PageAction cannot successfully perform, it should throw a new
 * <code>PageActionException</code>.  The Exception message may be displayed
 * to the user.
 *
 * @author  e_ridge
 */
public interface PageAction {
    /**
     * an exception thrown by page actions when they can't perform properly
     */
    public static class PageActionException extends Exception {
        public PageActionException (String str) {
            super (str);
        }
    }
    
    /**
     * an exception thrown by a page when it needs to redirect to another page
     */
    public static class RedirectException extends PageActionException {
        private final String _url;
        public RedirectException (String url) {
            super (url);
            _url = url;
        }
        
        /** the destination of the redirection */
        public String getURL () {
            return _url;
        }
    }

    /**
     * based on the specified WikiSystem and WebContext, should this action be performed?
     */
    public boolean accept (WikiSystem wiki, WebContext wc, WikiUser user);

    /**
     * based on the specified WikiSystem and WebContext, which WikiPage is
     * this action going to be dealing with?
     */
    public String getWikiPageName (WikiSystem wiki, WebContext wc);
    
    /**
     * do whatever this action is supposed to do to the specified page
     */
    public void perform (WikiSystem wiki, WebContext wc, WikiUser user, WikiPage page) throws PageActionException;
    
    /**
     * which WebMacro template does this action use?
     */
    public String getTemplateName (WikiSystem wiki, WikiPage page);
}