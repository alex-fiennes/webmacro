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
package org.tcdi.opensource.wiki.search;

import java.util.List;

import org.tcdi.opensource.wiki.*;

/**
 * A simplified interface into a search backend.  Allows one to execute
 * a "query" and receive a set of scored results that meet the search criteria.<p>
 *
 * Typically, WikiPageFinders and WikiPageIndexers go hand-in-hand.
 *
 * @author  e_ridge
 */
public interface WikiPageFinder {

    /** 
     * Thrown when a WikiPageFinder encounters errors while searching
     * for pages
     */
    public static class FinderException extends Exception {
        public FinderException (String msg) {
            super (msg);
        }
    }
 
    /** 
     * a simple structure that represents a single matching result from
     * a findPages query
     */
    public static class FindResult {
        public WikiPage page;
        public float score;
        public String preview;
    }
    
    /**
     * return a list of pages that match the <code>query</code>.  The query
     * is implementation specific
     */
    public FindResult[] findPages (WikiSystem wiki, String query) throws FinderException;
    
}