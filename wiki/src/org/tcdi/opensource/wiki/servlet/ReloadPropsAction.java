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
public class ReloadPropsAction implements PageAction {

	/**
	 *  can only log in if "?user_admin=true" is specified in the request
	 *
	 *@param  wiki  Description of the Parameter
	 *@param  wc    Description of the Parameter
	 *@param  user  Description of the Parameter
	 *@return       Description of the Return Value
	 */
	public boolean accept(WikiSystem wiki, WebContext wc, WikiUser user) {
		String actiontag = wc.getForm("reload_props");
		return actiontag != null && actiontag.equals("true") && wiki.isAdministrator(user);
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
		return null;
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
			wiki.reloadProperties();
		} catch (Exception e) {
			throw new PageAction.PageActionException(e.getMessage());
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
		return "reloadprops.wm";
	}

}

