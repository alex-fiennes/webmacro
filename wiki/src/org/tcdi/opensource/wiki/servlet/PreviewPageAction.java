package org.tcdi.opensource.wiki.servlet;

import org.tcdi.opensource.wiki.WikiSystem;
import org.tcdi.opensource.wiki.WikiUser;
import org.tcdi.opensource.wiki.WikiPage;
import org.webmacro.servlet.WebContext;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: e_ridge
 * Date: Jul 2, 2003
 * Time: 11:34:53 PM
 * To change this template use Options | File Templates.
 */
public class PreviewPageAction implements PageAction {
    /**
     * can only save a page if the request is POST and "?save=<pagename>" is
     * in the request
     */
    public boolean accept(WikiSystem wiki, WebContext wc, WikiUser user) {
        // and then only accept if this is a get request
        return wc.getRequest().getMethod().equalsIgnoreCase("POST") 
            && wc.getForm ("preview") != null && wc.getForm("preview").equals("true");
    }
    
    /**
     * do the saving of the page.  When we're done, we redirect to the page
     * so the user can view his changes.
     */
    public void perform(WikiSystem wiki, WebContext wc, WikiUser user, WikiPage page) throws PageAction.PageActionException {
        if (page != null && page.getIsModerated() && !user.getIsModerator())
            throw new PageActionException ("This page can only be saved by moderators");
        
        try {
            String text = wc.getForm ("TEXT");
            String pageName = wc.getForm ("save");
            page = wiki.createPage(pageName, user.getIdentifier(), text);
            wiki.parsePage(page);
            wc.put ("Page", page);
        } catch (Exception e) {
            e.printStackTrace();
            throw new PageAction.PageActionException (e.toString());
        }
    }
    
    /**
     * no template for saving
     */
    public String getTemplateName(WikiSystem wiki, WikiPage page) {
        return "preview.wm";
    }
    
    /**
     * the page name is whatever is behind the "?save=<pagename>" request
     * parameter.
     */
    public String getWikiPageName(WikiSystem wiki, WebContext wc) {
        return null;
    }
}
