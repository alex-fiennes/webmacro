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
import org.webmacro.Log;
import org.tcdi.opensource.wiki.*;

/**
 * PageActionManager maintains a list of configured <code>PageActions</i>
 * for a specified <code>WikiSystem</code>.  The <code>getAction</code> method
 * is used to find the PageAction that knows how to handle a request.
 *
 * @author  e_ridge
 */
public class PageActionManager {
    private final List _actions = new ArrayList ();
    private int _action_count;
    private final WikiSystem _wiki;
    private final Log _log;
    
    public PageActionManager (WikiSystem wiki, Log log) {
        _wiki = wiki;
        _log = log;
        loadActions ();
    }

    public final PageAction getAction (WebContext wc, WikiUser user) {
        for (int x=0; x<_action_count; x++) {
            PageAction action = (PageAction) _actions.get (x);
            if (action.accept (_wiki, wc, user)) {
                wc.getLog ("Wiki").notice ((user == null ? "anonymous" : user.getName()) 
                                         + ": " + action.getClass().getName() + ": "
                                         + wc.getRequest().getRequestURI());
                return action;
            }
        }
        return null;
    }
    
    public final void loadActions () {
        _actions.clear ();
        
        StringTokenizer st = new StringTokenizer (_wiki.getProperties().getProperty ("Actions"));
        while (st.hasMoreTokens()) {
            String classname = st.nextToken ();
            try {
                PageAction action = (PageAction) Class.forName (classname).newInstance();
                _actions.add (action);
            } catch (Exception e) {
                _log.error ("Cannot load action " + classname, e);
            }
        }
        
        _action_count = _actions.size ();
        _log.info (_action_count + " actions load");
    }
}
