/*
 * Created by IntelliJ IDEA.
 * User: e_ridge
 * Date: Nov 22, 2002
 * Time: 1:11:38 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.tcdi.opensource.wiki.servlet.admin;

import org.tcdi.opensource.wiki.servlet.PageAction;
import org.tcdi.opensource.wiki.WikiSystem;
import org.tcdi.opensource.wiki.WikiUser;
import org.tcdi.opensource.wiki.WikiPage;
import org.webmacro.servlet.WebContext;

public class UserListAction extends AdminAction {

    public boolean internalAccept(WikiSystem wiki, WebContext wc, WikiUser user) {
        return true;
    }

    public String getWikiPageName(WikiSystem wiki, WebContext wc) {
        return "AdminUserList";
    }

    public void perform(WikiSystem wiki, WebContext wc, WikiUser user, WikiPage page) throws PageAction.PageActionException {
        // nothing to do here
        String prefix = wc.getForm("PREFIX");
        if (prefix != null)
            user.setAttribute("LastUserListLetter", prefix);
    }

    public String getTemplateName(WikiSystem wiki, WikiPage page) {
        return "admin/user_list.wm";
    }
}
