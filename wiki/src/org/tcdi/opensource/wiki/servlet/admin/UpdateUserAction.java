/*
 * Created by IntelliJ IDEA.
 * User: e_ridge
 * Date: Nov 22, 2002
 * Time: 1:15:38 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.tcdi.opensource.wiki.servlet.admin;

import org.tcdi.opensource.wiki.WikiSystem;
import org.tcdi.opensource.wiki.WikiUser;
import org.tcdi.opensource.wiki.WikiPage;
import org.tcdi.opensource.wiki.WikiUtil;
import org.tcdi.opensource.wiki.servlet.PageAction;
import org.webmacro.servlet.WebContext;

public class UpdateUserAction extends AdminAction {
    protected boolean internalAccept(WikiSystem wiki, WebContext wc, WikiUser user) {
        return wc.getForm("user") != null;
    }

    public String getWikiPageName(WikiSystem wiki, WebContext wc) {
        return "AdminUpdateUser";
    }

    public void perform(WikiSystem wiki, WebContext wc, WikiUser user, WikiPage page) throws PageAction.PageActionException {
        String action = wc.getForm("ACTION");
        String username = wc.getForm("user");
        WikiUser userToUpdate = wiki.getUser(username);
        if (userToUpdate == null)
            throw new PageAction.PageActionException (username + " is not a valid user");

        if ("Update".equals(action)) {
            String password = wc.getForm("password").trim();
            String email = wc.getForm("email");
            String name = wc.getForm("name");
            boolean isModerator = wc.getForm("moderator") != null;

            userToUpdate.setName(name);
            userToUpdate.setIsModerator(isModerator);
            if (password.length() > 0)
                userToUpdate.setPassword(WikiUtil.getMD5(password));
            userToUpdate.setAttribute("email", email);

            wiki.updateUser(userToUpdate);
            wc.put ("Saved", Boolean.TRUE);
        } else if ("Delete".equals(action)) {
            wiki.deleteUser(userToUpdate.getIdentifier());
            throw new PageAction.RedirectException("AdminUserList");
        }

        wc.put("UserToUpdate", userToUpdate);
    }

    public String getTemplateName(WikiSystem wiki, WikiPage page) {
        return "admin/update_user.wm";
    }
}
