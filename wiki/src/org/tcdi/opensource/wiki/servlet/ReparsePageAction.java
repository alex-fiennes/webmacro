package org.tcdi.opensource.wiki.servlet;

import java.util.*;
import javax.servlet.http.Cookie;
import org.webmacro.servlet.WebContext;

import org.tcdi.opensource.wiki.*;

/**
 *  User administration<p>
 *
 *
 *
 *@author     ChristianAust
 *@created    12. September 2002
 */
public class ReparsePageAction implements PageAction {

	/**
	 *  can only log in if "?user_admin=true" is specified in the request
	 *
	 *@param  wiki  Description of the Parameter
	 *@param  wc    Description of the Parameter
	 *@param  user  Description of the Parameter
	 *@return       Description of the Return Value
	 */
	public boolean accept(WikiSystem wiki, WebContext wc, WikiUser user) {
		String pagename = wc.getForm("reparse");
		return pagename != null && wiki.containsPage(pagename);
	}


	/**
	 *  based on the specified WikiSystem and WebContext, which WikiPage is this
	 *  action going to be dealing with?
	 *
	 *@param  wiki  Description of the Parameter
	 *@param  wc    Description of the Parameter
	 *@return       The wikiPageName value
	 */
	public String getWikiPageName(WikiSystem wiki, WebContext wc) {
		return wc.getForm("reparse");
	}


	/**
	 *  do whatever this action is supposed to do to the specified page
	 *
	 *@param  wiki                                Description of the Parameter
	 *@param  wc                                  Description of the Parameter
	 *@param  user                                Description of the Parameter
	 *@param  page                                Description of the Parameter
	 *@exception  PageAction.PageActionException  Description of the Exception
	 */
	public void perform(WikiSystem wiki, WebContext wc, WikiUser user, WikiPage page) throws PageAction.PageActionException {
		try {
			wiki.parsePage(wiki.getPage(wc.getForm("reparse")));
		} catch (Exception e) {
			//throw new PageAction.PageActionException(e.toString());
		} finally {
			throw new PageAction.RedirectException(wc.getForm("reparse"));
		}
	}


	/**
	 *  which WebMacro template does this action use?
	 *
	 *@param  wiki  Description of the Parameter
	 *@param  page  Description of the Parameter
	 *@return       The templateName value
	 */
	public String getTemplateName(WikiSystem wiki, WikiPage page) {
		Properties props = wiki.getProperties();
		String template = props.getProperty("ViewPageAction.Template");
		if (page != null) {
			if (props.getProperty(page.getTitle()) != null) {
				template = props.getProperty(page.getTitle());
			}
		}

		return template;
	}

}

