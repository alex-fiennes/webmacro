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
package org.tcdi.opensource.wiki;

import java.util.*;

import org.tcdi.opensource.wiki.parser.*;
import org.tcdi.opensource.wiki.renderer.*;

/**
 * WikiSystem describes a WikiSystem.  Plain and Simple.
 *
 * @author Eric B. Ridge
 */
public interface WikiSystem extends WikiTermMatcher {

    /**
     * Represents a single entry in our Page Tree
     */ 
    public static class PageTreeEntry {
        private final WikiPage _page;
        private final int _depth;
        
        public PageTreeEntry (WikiPage page, int depth) {
            _page = page;
            _depth = depth;
        }        
        
        public WikiPage getPage() {
            return _page;
        }
        
        public int getDepth() {
            return _depth;
        }
    }

    /**
     * How many "current" pages do we have?
     */ 
    public int getPageCount();
    
    /**
     * @param title the title of the WikiPage to retrieve.  This should be a propertly formed WikiTerm
     * @return the WikiPage of the specified title
     */
    public WikiPage getPage(String title);
    
    /**
     * Return a list of all page names that begin with the specified string.  
     * The special string "*" is understood to mean <i>all pages</i>. 
     * So is <code>null</code> and the empty string.
     */ 
    public String[] getPages(String prefix);
    
    
    /**
     * return a list of all page names, including names of deleted pages
     * and old versions of pages.  User should take care to filter these
     * if their use is not required.
     *
     *
     * @return an Enumeration of all page names
     */
    public Enumeration getPageNames();
    
    /**
     * return an array of all wiki page names that are current
     * (ie, not deleted)
     */
    public String[] getCurrentPageNames();
    
    /**
     * saves the provided page to the wiki system<p>
     *
     * takes care to preserve existing versions
     *
     * @param page the WikiPage object to save into this wiki system
     */
    public void savePage(WikiPage page);
    
    
    /**
     * saves the provided page to the wiki system as <code>title</code>
     *
     * takes care to preserve existing versions
     *
     * @param page the WikiPage object to save into this wiki system
     * @param title the title to use when saving
     */
    public void savePage(WikiPage page, String title);
    
    /**
     * delete the page of the specified title<p>
     *
     * takes care to backup the page being deleted
     *
     * @param title the title of the WikiPage to delete.  This should be a properly form WikiTerm
     */
    public void deletePage(String title);
    
    /**
     * rename oldName to newName<p>
     *
     * this method also makes appropriate backups of oldName
     *
     * @param oldName the existing WikiPage
     * @param newName the new name for the old WikiPage
     */
    public void renamePage(String oldName, String newName);
    
    /**
     * does this wiki system contain the specified page?
     *
     * @param title this should be a properly formed WikiTerm
     * @return true if we contain this page
     */
    public boolean containsPage(String title);
    
    /** How many users do we have? */
    public int getUserCount();
    
    /**
     * @param uid the user id of the requested user
     * @return WikiUser object for the user of specified id
     */
    public WikiUser getUser(String uid);
    
    /**
     * get an enumeration of all user names
     *
     * @return an Enumeration of all user names
     */
    public Enumeration getUserNames();

    /**
     * get an enumeration of all users in the system, sorted by their usernames
     */
    public Enumeration getUsers ();

    /**
     * get an enumeration of all users in the system who's username begins with the
     * specified prefix.  Use <code>null</code> or <code>*</code> to get all users.
     */
    public Enumeration getUsers (String prefix);

    /**
     * Delete a user from this wiki system
     *
     * @param uid the user id to delete
     */
    public void deleteUser(String uid);

    /**
     * Register a new user in this wiki system
     *
     * @param uid the user id.  This should be unique
     * @param fullname the full name of the user
     * @param password the user password.  It is recommened this not be clear text
     * @param attributes a hashtable of attributes to apply to this user.  keys and values should be Strings
     */
    public WikiUser registerUser(String uid, String fullname, String password, Hashtable attributes) throws Exception;
    
    /**
     * Updates specified user in the user store
     *
     * @param user the user to update
     */
    public void updateUser(WikiUser user);
    
    /**
     * @param user WikiUser to check for administrator access
     * @return true is user is an administrator of this WikiSystem
     */
    public boolean isAdministrator(WikiUser user);
    
    /**
     * access to the Properties used by the WikiSystem
     */
    public Properties getProperties();
    
    /**
     * reload the Properties used by the WikiSytem
     */
    public void reloadProperties() throws Exception;
    
    /**
     * @return version of this WikiSystem
     */
    public String getVersion();
    
    /**
     * @return date/time this WikiSystem was started
     */
    public Date getDateStarted();
    
    /**
     * Create a new WikiPage object (but not add it to the page store).  Caller
     * is responsible for setting the list of related pages
     */
    public WikiPage createPage(String title, String author, String data) throws Exception;
    
    /**
     * (re)parse the contents of a WikiPage.  Should not modify other
     * internals of the WikiPage
     */
    public void parsePage (WikiPage page) throws Exception;
    
    /**
     * Is the specified String a WikiTerm (page name) reference?
     */
    public boolean isWikiTermReference (String word);
    
    /**
     * What page renderer should be used?
     */
    public WikiPageRenderer getPageRenderer ();
    
    /**
     * What is the name of the "Start Page" for this Wiki?
     */
    public String getStartPage ();
    
    /**
     * Index all the current (ie, non-deleted, current version) pages
     * in this wiki
     */
    public void indexCurrentPages () throws Exception;
    
    /**
     * Returns a tree-like structure ordered by page links.
     */ 
    public List getPageTree();
}