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
public class UserAdminAction implements PageAction {

	/**
	 *  can only log in if "?user_admin=true" is specified in the request
	 *
	 *@param  wiki  Description of the Parameter
	 *@param  wc    Description of the Parameter
	 *@param  user  Description of the Parameter
	 *@return       Description of the Return Value
	 */
	public boolean accept(WikiSystem wiki, WebContext wc, WikiUser user) {
		String useradmin = wc.getForm("user_admin");
		return useradmin != null && useradmin.equals("true") && wiki.isAdministrator(user);
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
		if (wc.getForm("username") != null) {
			//display user details
			WikiUser currentuser = wiki.getUser(wc.getForm("username"));
			wc.put("currentuser", currentuser);
		}
		if (wc.getForm("update_user") != null) {
			//save new user data
			WikiUser currentuser = wiki.getUser(wc.getForm("username"));
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
		return wiki.getProperties().getProperty("UserAdminAction.Template");
	}

}

