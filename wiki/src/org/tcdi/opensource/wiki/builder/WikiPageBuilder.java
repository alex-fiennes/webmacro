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
package org.tcdi.opensource.wiki.builder;

import org.tcdi.opensource.wiki.*;

/**
 * A WikiPageBuilder receives formatting instructions and blocks of text,
 * typically from a WikiParser, and builds a WikiPage object.<p>
 *
 * Call <code>getPage()</code> to get the page that was built.
 *
 * @author  e_ridge
 */
public interface WikiPageBuilder {

    /** initialize this WikiPageBuilder to use the specified WikiSystem.
     *  The WikiSystem is typically used for looking up other WikiPage's
     */
    public void init (WikiSystem wiki);
    
    /** return the completed WikiPage */
    public WikiPage getPage ();
    
    /** which WikiSystem instance were we initialized with? */
    public WikiSystem getWikiSystem ();
    
    /** toggle bolding of text */
    public void bold ();
    
    /** toggle underlining of text */
    public void underline ();
    
    /** toggle italicizing of text */
    public void italic ();
    
    /** start to colorize text */
    public void color (String color);
    
    /** start a new header */
    public void header (String headerName);
    
    /** end the current color (or header) formatting */
    public void endColorOrHeader ();
    
    /** indent <code>many</code> spaces */
    public void indent (int many);
    
    /** add a single space */
    public void space ();
    
    /** start a new line */
    public void newline ();
    
    /** start a new paragraph */
    public void paragraph ();
    
    /** add a horizontal ruler to the text */
    public void ruler ();
    
    /** add a word.  This word could be a WikiTerm */
    public void word (String word);
    
    /** add a WikiTerm */
    public void wikiTerm (String pageName);
    
    /** add the less-than (&lt;) symbol */
    public void lt ();
    
    /** add the greater-than (&gt;) symbol */
    public void gt ();
    
    /** add a URL */
    public void url (String url);
    
    /** add an email address */
    public void email (String email);
    
    /** add a block of quoted text */
    public void quotedBlock (String block);
}