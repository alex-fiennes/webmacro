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
import java.io.Serializable;

import org.tcdi.opensource.util.*;

/**
 * A WikiPage is the object that represents all information about
 * a page in the wiki system.  It contains the following information:
 * <code>
 *    <li>date it was created
 *    <li>author who created reated it
 *    <li>is it a moderated page
 *    <li>date it was last modified
 *    <li>an array of editors of this page in chronological order
 *    <li>a version.  The version is updated each time the page is parsed
 *
 *    <li>it's title
 *    <li>an array of keywords or related page titles
 *
 *    <li>the original, unparsed text of the page
 *    <li>a WikiData array, which is the parsed version of the above
 *
 *    <li>a WikiAttachment array.  Currently, this is not used
 * </code>
 *
 * @author Eric B. Ridge
 */
public class WikiPage implements Serializable {
    Date _dateCreated;
    String _authorCreated;
    
    boolean _isModerated = false;
    
    Date _dateLastModified;
    String[] _editors = new String[0];
    
    String _title;
    String[] _relatedTitles = new String[0];
    
    String _unparsedData;
    WikiData[] _parsedData = new WikiData[0];
    
    WikiAttachment[] _attachments = new WikiAttachment[0];
    
    static final long serialVersionUID = 0L;
    
    long _version = 0;
    
    
    //
    // constructors
    //
    
    /**
     * Default constructor.  If using the constructor, must remember to
     * set the metadata
     */
    public WikiPage() {
        _dateCreated = new Date();
    }
    
    /**
     * Constructor to set title and author who created the page
     * Sets dateCreated to current date/time.<p>
     *
     * @param title the title for this page
     * @param autorCreated name of person who initially authored this page
     */
    public WikiPage(String title, String authorCreated) {
        _title = title;
        _authorCreated = authorCreated;
        _dateCreated = new Date();
    }
    
    //
    // action methods
    //
    
    /**
     * simply here to show off the introspection power of WebMacro
     *
     * @return an Enumeration of WikiData objects that represent this page
     */
    public Enumeration elements() {
        return new Enumeration() {
            int _cursor = 0;
            public boolean hasMoreElements() { return _cursor != _parsedData.length; }
            public Object nextElement()      { return _parsedData[_cursor++]; }
        };
    }
    
    /**
     * Add a related title to this WikiPage
     *
     * @param title the title of a WikiPage to add as a relation
     */
    public void addRelatedTitle(String title) { _relatedTitles = (String[]) ArrayHelper.appendToArray(_relatedTitles, title); }
    
    /**
     * Remove a related title from this WikiPage
     *
     * @param title the title to remove
     *
     * @return true if the title was removed
     */
    public boolean removeRelatedTitle(String title) {
        for (int x=0; x<_relatedTitles.length; x++) {
            if (title.equalsIgnoreCase(_relatedTitles[x])) {
                _relatedTitles = (String[]) ArrayHelper.removeIndex(_relatedTitles, x);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Add an attachment reference to this WikiPage
     * @param attachment the attachment to add
     */
    public void addAttachment(WikiAttachment attachment) {
        _attachments = (WikiAttachment[]) ArrayHelper.appendToArray(_attachments, attachment);
    }
    
    /**
     * Remove an attachment reference from this WikiPage
     * @param attachment the attachment to remove
     * @return true if it was removed.  false if not
     */
    public boolean removeAttachment(WikiAttachment attachment) {
        for (int x=0; x<_attachments.length; x++) {
            if (attachment.equals(_attachments[x])) {
                _attachments = (WikiAttachment[]) ArrayHelper.removeIndex(_attachments, x);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Add an editor to the list of editors
     * @param editor the name of the editor
     */
    public void addEditor(String editor) {
        _editors = (String[]) ArrayHelper.appendToArray(_editors, editor);
    }
    
    //
    // get methods
    //
    
    /**
     * @return the version number of this page
     */
    public long getVersion()           { return _version; }
    
    /** increment the version number of this page.  You'll probably want to re-save it
     * to the page store after this
     */ 
    public void incrementVersion () {
        _version++;
    }
    
    public void setVersion (long version) {
        _version = version;
    }
    
    /**
     * @return Author of this page.
     */
    public String getAuthor()          {
        return _authorCreated;
    }
    
    
    /**
     * @return Title of this page
     */
    public String getTitle()           {
        return _title;
    }
    
    /**
     * @return Date this page was created
     */
    public Date getDateCreated()       {
        return _dateCreated;
    }
    
    /**
     * @return the date created as a long value.
     */
    public long getDateCreatedAsLong() {
        return _dateCreated.getTime();
    }
    
    
    /**
     * @return Date this page was last modified
     */
    public Date getDateLastModified()  {
        return _dateLastModified;
    }
    
    /**
     * set the date last modified
     * @param date the new last modified date
     */
    void setDateLastModified(Date date) {
        _dateLastModified = date;
    }
    
    /**
     * @return the date last modifed as a long value.
     */
    public long getDateLastModifiedAsLong() {
        return _dateLastModified.getTime();
    }
    
    /**
     * @return the text that was used to create this WikiPage
     */
    public String getUnparsedData()   {
        return _unparsedData;
    }
    
    /**
     * In the WebMacro world, this method is functionally equivilant to the
     * .elements() method.  The only difference lies in the WM syntax:
     * <pre>
     *    #foreach $data_block in $WikiPage   // .elements()
     *             v/s
     *    #foreach $data_block in $WikiPage.Data    // this method
     * </pre>
     * @return the WikiData[] representation of this WikiPage
     */
    public WikiData[] getData()        {
        return _parsedData;
    }
    
    /**
     * @return the array of related WikiPage titles
     */
    public String[] getRelatedTitles() {
        return _relatedTitles;
    }
    
    /**
     * @return an array of WikiAttachments
     */
    public WikiAttachment[] getAttachments() {
        return _attachments;
    }
    
    /**
     * @return an array of people who have edited this page
     */
    public String[] getEditors() {
        return _editors;
    }
    
    /**
     * @param idx the index of the editor to retrieve
     * @return editor name at specified index
     */
    public String getEditorAt(int idx) {
        if (idx >= _editors.length || _editors.length == 0)
            return null;
        return _editors[idx];
    }
    
    /**
     * @return last editor of this page
     */
    public String getLastEditor() {
        return getEditorAt(_editors.length-1);
    }
    
    /**
     * @return count of related titles
     */
    public int getRelatedTitleCount() {
        return _relatedTitles.length;
    }
    
    /**
     * @return count of attachments
     */
    public int getAttachmentCount()   {
        return _attachments.length;
    }
    
    /**
     * @return number of times this page has been edited
     */
    public int getEditCount() {
        return _editors.length;
    }
    
    /**
     * @return true if this page is moderated.
     */
    public boolean getIsModerated() {
        return _isModerated;
    }
    
    
    //
    // set methods
    //
    
    /**
     * set moderation status of this page
     */
    public void setIsModerated(boolean isModerated) {
        _isModerated = isModerated;
    }
    
    /**
     * Set the title of this page.  updates dateLastModified to current date/time.<p>
     *
     * Note: Changing the title will affect how it is stored in the PageStore
     *
     * @param title the new title
     */
    public void setTitle(String title) {
        _title = title;        
        _dateLastModified = new Date();
    }
    
    /**
     * Set the name of the author who created this page
     */
    public void setAuthor(String author) {
        _authorCreated = author;
    }
    
    /**
     * Set the unparsed text of this page
     */
    public void setUnparsedData(String data) {
        _unparsedData = data;
    }
    
    /**
     * Set the parsed WikiData array of this page
     */
    public void setWikiData (WikiData[] data) {
        _parsedData = data;
    }
    
    /**
     * sets the array of related titles
     *
     * @param relatedTitles a String[] of WikiPage titles that this WikiPage is related to
     */
    public void setRelatedTitles(String[] relatedTitles) {
        _relatedTitles = (String[]) relatedTitles.clone();
        
        _dateLastModified = new Date();
    }
    
    /**
     * sets the array of attachments
     *
     * @param attachments a WikiAttachment[] of attachments this page should have
     */
    public void setAttachments(WikiAttachment[] attachments) {
        _attachments = (WikiAttachment[]) attachments.clone();
    }
    
    
    /**
     * overloaded to return the Title of this page
     *
     * NOTE: This should either return the _unparsedData or the _cachedOutput,
     * not the title.  Comments anyone?
     */
    public String toString() {
        return _title;
    }
}

