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

/**
 * Prepares the context so that all control files can be viewed.
 * 
 * @author Lane Sharman
 */
public class ControlFilesAction extends AdminAction {

    public boolean internalAccept(WikiSystem wiki, WebContext wc, WikiUser user) {
      //System.out.println("Testing=" + wc.getRequest().getRequestURI() + " " + wc.getRequest().getRequestURI().endsWith("AdminControlFiles"));
      return wc.getRequest().getRequestURI().endsWith("AdminControlFiles");
    }

    public String getWikiPageName(WikiSystem wiki, WebContext wc) {
        return "AdminControlFiles";
    }

    public void perform(WikiSystem wiki, WebContext wc, WikiUser user, WikiPage page) throws PageAction.PageActionException {
      String action = wc.getForm("ACTION");

      if ("Update".equals(action)) {
          String value = wc.getForm("contents");
          int index = Integer.parseInt(wc.getForm("index"));
          wiki.getControlFiles().getEntries()[index].setContents(value);
      }
    }

    public String getTemplateName(WikiSystem wiki, WikiPage page) {
        return "admin/controlfiles.wm";
    }
    
}
