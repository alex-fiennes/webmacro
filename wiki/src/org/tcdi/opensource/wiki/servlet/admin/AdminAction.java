/*
 * Created by IntelliJ IDEA.
 * User: e_ridge
 * Date: Nov 22, 2002
 * Time: 1:15:50 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.tcdi.opensource.wiki.servlet.admin;

import org.tcdi.opensource.wiki.WikiSystem;
import org.tcdi.opensource.wiki.WikiUser;
import org.tcdi.opensource.wiki.servlet.PageAction;
import org.webmacro.servlet.WebContext;

abstract class AdminAction implements PageAction {

    protected abstract boolean internalAccept (WikiSystem wiki, WebContext wc, WikiUser user);

    public final boolean accept(WikiSystem wiki, WebContext wc, WikiUser user) {
        return wc.getRequest().getRequestURI().indexOf("/Admin") > -1 & wiki.isAdministrator(user) & internalAccept (wiki, wc, user);
    }
}
