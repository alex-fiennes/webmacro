/**
 *  The contents of this file are subject to the Mozilla Public License Version
 *  1.1 (the "License"); you may not use this file except in compliance with the
 *  License. You may obtain a copy of the License at http://www.mozilla.org/MPL/
 *  Software distributed under the License is distributed on an "AS IS" basis,
 *  WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 *  the specific language governing rights and limitations under the License.
 *  The Original Code is Wiki. The Initial Developer of the Original Code is
 *  Technology Concepts and Design, Inc. Copyright (C) 2000 Technology Concepts
 *  and Design, Inc. All Rights Reserved. Contributor(s): Lane Sharman
 *  (OpenDoors Software) Justin Wells (Semiotek Inc.) Eric B. Ridge (Technology
 *  Concepts and Design, Inc.) Alternatively, the contents of this file may be
 *  used under the terms of the GNU General Public License Version 2 or later
 *  (the "GPL"), in which case the provisions of the GPL are applicable instead
 *  of those above. If you wish to allow use of your version of this file only
 *  under the terms of the GPL and not to allow others to use your version of
 *  this file under the MPL, indicate your decision by deleting the provisions
 *  above and replace them with the notice and other provisions required by the
 *  GPL. If you do not delete the provisions above, a recipient may use your
 *  version of this file under either the MPL or the GPL. This product includes
 *  sofware developed by OpenDoors Software. This product includes software
 *  developed by Justin Wells and Semiotek Inc. for use in the WebMacro
 *  ServletFramework (http://www.webmacro.org).
 */
package org.tcdi.opensource.wiki;

import org.opendoors.instant.vlh.VLHProvider;
import org.opendoors.intf.QueueListener;
import org.tcdi.opensource.wiki.builder.WikiPageBuilder;
import org.tcdi.opensource.wiki.parser.WikiParser;
import org.tcdi.opensource.wiki.renderer.HTMLPageRenderer;
import org.tcdi.opensource.wiki.renderer.HTMLURLRenderer;
import org.tcdi.opensource.wiki.renderer.WikiPageRenderer;
import org.tcdi.opensource.wiki.search.LuceneIndexer;
import org.tcdi.opensource.wiki.search.WikiPageIndexer;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 *  The main WikiSystem Implementation. This implementation is "Powered by VLH".
 *
 *@author     Eric B. Ridge
 *@created    1. September 2002
 *@see        org.opendoors.instant.vlh.VLHProxy
 */
public final class Wiki implements WikiSystem, QueueListener {
	/**
	 *  Description of the Field
	 */
	public final static String PAGE_properties = "PageStore";
	/**
	 *  Description of the Field
	 */
	public final static String USER_properties = "UserStore";

	private String _version = null;
	private Properties _properties;
	private String _propsFilename;
	private Date _dateStarted;

	private Hashtable _pageStore;
	private Hashtable _userStore;

	private String baseDir = null;

	/**
	 *  array of all valid page names
	 */
	private volatile String[] _pageNames;
	private volatile Map _pageLookup;

	/**
	 *  the page renderer this Wiki is configured to use
	 */
	private WikiPageRenderer _pageRenderer;

	/**
	 *  the page builder class we should use
	 */
	private Class _pageBuilderClass;

	private WikiPageIndexer _indexer = new LuceneIndexer();


	/**
	 *  This constructor will load the specified .properties file from the <b>local
	 *  filesystem</b>
	 *
	 *@param  propertiesFilename  absolute path to a .properties file for the Wiki
	 *@exception  Exception       Description of the Exception
	 */
	public Wiki(String propertiesFilename) throws Exception {
		create(propertiesFilename);
		_dateStarted = new Date();
	}


	/**
	 *  Constructor for the Wiki object
	 *
	 *@param  baseDir             Description of the Parameter
	 *@param  propertiesFilename  Description of the Parameter
	 *@exception  Exception       Description of the Exception
	 */
	public Wiki(String baseDir, String propertiesFilename) throws Exception {
		this.baseDir = baseDir;
		create(propertiesFilename);
		_dateStarted = new Date();
	}


	/**
	 *  Gets the baseDir attribute of the Wiki object
	 *
	 *@return    The baseDir value
	 */
	public String getBaseDir() {
		return baseDir;
	}


	/**
	 *  Gets the page attribute of the Wiki object
	 *
	 *@param  title  Description of the Parameter
	 *@return        The page value
	 */
	public WikiPage getPage(String title) {
		return (WikiPage) _pageStore.get(title);
	}


	/**
	 *  Gets the pageNames attribute of the Wiki object
	 *
	 *@return    The pageNames value
	 */
	public Enumeration getPageNames() {
		return _pageStore.keys();
	}


	/**
	 *  Gets the currentPageNames attribute of the Wiki object
	 *
	 *@return    The currentPageNames value
	 */
	public String[] getCurrentPageNames() {
		return _pageNames;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  page  Description of the Parameter
	 */
	public void savePage(WikiPage page) {
		savePage(page, page.getTitle());
	}


	/**
	 *  Description of the Method
	 *
	 *@param  page   Description of the Parameter
	 *@param  title  Description of the Parameter
	 */
	public void savePage(WikiPage page, String title) {
		WikiPage oldVersion = (WikiPage) _pageStore.get(title);
		boolean isnew = false;
		if (oldVersion != null) {
			// backup old version of this page
			oldVersion.setTitle(oldVersion.getTitle() + "." + oldVersion.getVersion());
			_pageStore.put(oldVersion.getTitle() + "." + oldVersion.getVersion(), page);
		} else {
			// this is a brand new page
			isnew = true;
		}

		// save this page
		_pageStore.put(title, page);

		if (isnew) {
			populatePageNames();
		}

		try {
			indexPage(page);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public void indexCurrentPages() throws Exception {
		for (int x = 0; x < _pageNames.length; x++) {
			indexPage(getPage(_pageNames[x]));
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@param  page           Description of the Parameter
	 *@exception  Exception  Description of the Exception
	 */
	private void indexPage(WikiPage page) throws Exception {
		if (page == null) {
			return;
		}
		_indexer.index(this, page);
	}


	/**
	 *  Description of the Method
	 *
	 *@param  title  Description of the Parameter
	 */
	public void deletePage(String title) {
		// backup page we're about to remove
		WikiPage oldPage = getPage(title);
		if (oldPage != null) {
			oldPage.setTitle(".DELETED-" + oldPage.getTitle());
			_pageStore.put(".DELETED-" + oldPage.getTitle(), oldPage);
		}

		// remove the page
		_pageStore.remove(title);
		populatePageNames();
	}


	/**
	 *  Description of the Method
	 *
	 *@param  oldName  Description of the Parameter
	 *@param  newName  Description of the Parameter
	 */
	public void renamePage(String oldName, String newName) {
		// TODO: implement this
	}


	/**
	 *  Description of the Method
	 *
	 *@param  title  Description of the Parameter
	 *@return        Description of the Return Value
	 */
	public boolean containsPage(String title) {
		return _pageLookup.containsKey(title);
	}


	/**
	 *  Gets the user attribute of the Wiki object
	 *
	 *@param  uid  Description of the Parameter
	 *@return      The user value
	 */
	public WikiUser getUser(String uid) {
		return (WikiUser) _userStore.get(uid);
	}


	/**
	 *  Gets the userNames attribute of the Wiki object
	 *
	 *@return    The userNames value
	 */
	public Enumeration getUserNames() {
		return _userStore.keys();
	}


	/**
	 *  Description of the Method
	 *
	 *@param  uid  Description of the Parameter
	 */
	public void deleteUser(String uid) {
		_userStore.remove(uid);
	}


	/**
	 *  Description of the Method
	 *
	 *@param  uid            Description of the Parameter
	 *@param  fullname       Description of the Parameter
	 *@param  password       Description of the Parameter
	 *@param  attributes     Description of the Parameter
	 *@return                Description of the Return Value
	 *@exception  Exception  Description of the Exception
	 */
	public WikiUser registerUser(String uid, String fullname, String password, Hashtable attributes) throws Exception {
		if (getUser(uid) != null) {
			throw new Exception("Username already exists");
		}

		WikiUser newUser = new WikiUser(fullname, password);
		newUser.setIdentifier(uid);
		// TODO: need another VLH store for a UID<-->username mapping
		newUser.setIsModerator(isModerator(newUser));

		if (attributes != null) {
			Enumeration enum = attributes.keys();
			while (enum.hasMoreElements()) {
				String key = enum.nextElement().toString();
				newUser.setAttribute(key, attributes.get(key).toString());
			}
		}

		_userStore.put(uid, newUser);

		return newUser;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  user  Description of the Parameter
	 */
	public void updateUser(WikiUser user) {
		_userStore.put(user.getIdentifier(), user);
	}


	/**
	 *  Gets the administrator attribute of the Wiki object
	 *
	 *@param  user  Description of the Parameter
	 *@return       The administrator value
	 */
	public boolean isAdministrator(WikiUser user) {
		if (user == null) {
			return false;
		}

		return _properties.getProperty("Administrators").indexOf(user.getIdentifier() + ";") > -1;
	}


	/**
	 *  Gets the moderator attribute of the Wiki object
	 *
	 *@param  user  Description of the Parameter
	 *@return       The moderator value
	 */
	public boolean isModerator(WikiUser user) {
		if (user == null) {
			return false;
		}

		return _properties.getProperty("Moderators").indexOf(user.getIdentifier() + ";") > -1;
	}


	/**
	 *  Gets the properties attribute of the Wiki object
	 *
	 *@return    The properties value
	 */
	public Properties getProperties() {
		return _properties;
	}


	/**
	 *  Description of the Method
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public void reloadProperties() throws Exception {
		InputStream in;
		_properties = new Properties();

		in = new FileInputStream(_propsFilename);

		_properties.load(in);
		in.close();
	}


	/**
	 *  Gets the version attribute of the Wiki object
	 *
	 *@return    The version value
	 */
	public String getVersion() {
		return _version;
	}


	/**
	 *  Gets the dateStarted attribute of the Wiki object
	 *
	 *@return    The dateStarted value
	 */
	public Date getDateStarted() {
		return _dateStarted;
	}


	/**
	 *  The exception handler for VLH
	 *
	 *@param  exception  Description of the Parameter
	 */
	public void readQueue(Object exception) {
		((Exception) exception).printStackTrace();
	}



	//
	// private methods
	//
	/**
	 *  does necessary up-front configurations for the Wiki system.
	 *
	 *@param  propertiesFilename  the location of the properties file
	 *@exception  Exception       Description of the Exception
	 */
	private void create(String propertiesFilename) throws Exception {
		if (baseDir != null) {
			_propsFilename = baseDir + propertiesFilename;
		} else {
			_propsFilename = propertiesFilename;
		}

		reloadProperties();

		_version = _properties.getProperty("Version");

		try {
			_pageStore = createStore(PAGE_properties);
		} catch (Exception e) {
			System.err.println("Error creating " + PAGE_properties);
			e.printStackTrace();
		}

		try {
			_userStore = createStore(USER_properties);
		} catch (Exception e) {
			System.err.println("Error creating " + USER_properties);
			e.printStackTrace();
		}

		try {
			_pageBuilderClass = Class.forName(_properties.getProperty("PageBuilder"));
		} catch (Exception e) {
			System.err.println("Cannot load page builder class");
			e.printStackTrace();
		}

		// the page renderer
		_pageRenderer = new HTMLPageRenderer(new HTMLURLRenderer(this), this);

		// cache our internal list of page names, for fast lookup
		populatePageNames();
	}


	/**
	 *  populate our internal array of non-deleted, current version page names
	 */
	private void populatePageNames() {
		Map pageLookup = new HashMap();
		Enumeration enum = getPageNames();
		List l = new ArrayList();
		while (enum.hasMoreElements()) {
			String pageName = (String) enum.nextElement();
			if (pageName.indexOf('.') == -1) {
				l.add(pageName);
				// only add current pages (ie, not deleted or old versions)
				pageLookup.put(pageName, pageName);
			}
		}

		_pageNames = (String[]) l.toArray(new String[0]);
		_pageLookup = pageLookup;
	}


	/**
	 *  Creates a store of the specified name, getting properties from our
	 *  properties file
	 *
	 *@param  name           Description of the Parameter
	 *@return                Description of the Return Value
	 *@exception  Exception  Description of the Exception
	 */
	private Hashtable createStore(String name) throws Exception {
		Properties props = new Properties();
		props.put("ProxyImplementation", (String) _properties.get(name + ".ProxyImplementation"));
		props.put("PartitionKey", (String) _properties.get(name + ".PartitionKey"));
		props.put("ImmutableCertificate", (String) _properties.get(name + ".ImmutableCertificate"));
		props.put("MutableCertificate", (String) _properties.get(name + ".MutableCertificate"));
		props.put("Creator", (String) _properties.get(name + ".Creator"));
		props.put("Server", baseDir != null ? baseDir.concat((String) _properties.get(name + ".Server")) : (String) _properties.get(name + ".Server"));
		props.put("Port", (String) _properties.get(name + ".Port"));

		VLHProvider provider = VLHProvider.getInstance();
		try {
			provider.createPartition(props);
		} catch (Exception e) {
			if (e.toString() != null && e.toString().indexOf("Partition exists") == -1) {
				e.printStackTrace();
			}
		}

		return provider.getAgentAsHashtable(props, this);
	}


	/**
	 *  Create a new WikiPage object (but not add it to the page store). Caller is
	 *  responsible for setting the list of related pages
	 *
	 *@param  title          Description of the Parameter
	 *@param  author         Description of the Parameter
	 *@param  data           Description of the Parameter
	 *@return                Description of the Return Value
	 *@exception  Exception  Description of the Exception
	 */
	public WikiPage createPage(String title, String author, String data) throws Exception {
		WikiPageBuilder builder = (WikiPageBuilder) this._pageBuilderClass.newInstance();
		builder.setWikiTermMatcher(this);
		WikiParser parser = new WikiParser(new ByteArrayInputStream(data.getBytes()));

		WikiPage page = parser.parse(builder);
		page.setTitle(title);
		page.setAuthor(author);
		page.setUnparsedData(data);
		return page;
	}


	/**
	 *  parse page
	 *
	 *@param  page           Description of the Parameter
	 *@exception  Exception  Description of the Exception
	 */
	public void parsePage(WikiPage page) throws Exception {
		WikiPage tmp = createPage(page.getTitle(), page.getAuthor(), page.getUnparsedData());
		page.setWikiData(tmp.getData());
	}


	/**
	 *  reparse a page
	 *
	 *@param  page           Description of the Parameter
	 *@exception  Exception  Description of the Exception
	 */
	private void reparsePage(WikiPage page) throws Exception {
		WikiPageBuilder builder = (WikiPageBuilder) this._pageBuilderClass.newInstance();
		builder.setWikiTermMatcher(this);
		WikiParser parser = new WikiParser(new ByteArrayInputStream(page.getUnparsedData().getBytes()));
		WikiPage tmp = parser.parse(builder);

		page.setWikiData(tmp.getData());
		this.savePage(page);
	}


	/**
	 *  Is the specified String a Wiki page reference?
	 *
	 *@param  word  Description of the Parameter
	 *@return       The wikiTermReference value
	 */
	public boolean isWikiTermReference(String word) {
		char[] chars = word.toCharArray();
		int ucase = 0;
		int lcase = 0;

		// does the word end in a backwards tick?
		if (chars[chars.length - 1] == '`') {
			return true;
		}

		// else, make sure we have atleast 2 Upper and 2 Lower characters
		// non-alphanumerics cause us to immediately return false
		for (int x = 0; x < chars.length; x++) {
			char c = chars[x];
			if (c >= 'A' && c <= 'Z') {
				ucase++;
			} else if (c >= 'a' && c <= 'z') {
				lcase++;
			} else if (c >= '0' && c <= '9') {
				lcase++;
			} else {
				return false;
			}
		}
		return ucase >= 2 && lcase >= 3;
	}


	/**
	 *  What page renderer should be used?
	 *
	 *@return    The pageRenderer value
	 */
	public WikiPageRenderer getPageRenderer() {
		return _pageRenderer;
	}


	/**
	 *  What is the name of the "Start Page" for this Wiki?
	 *
	 *@return    The startPage value
	 */
	public String getStartPage() {
		return _properties.getProperty("StartPage").trim();
	}
}

