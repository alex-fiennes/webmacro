/*
 * WikiTermMatcher.java
 *
 * Created on October 18, 2001, 2:30 AM
 */

package org.tcdi.opensource.wiki;

/**
 * The <code>WikiTermMatcher</code> (for lack of a more creative name), 
 * decides if a particular word matches a defined pattern for a WikiTerm.
 *
 * @author  e_ridge
 */
public interface WikiTermMatcher {

    /** 
     * Does the specified word meet the criteria for a WikiTerm,
     * as defined by this WikiTermMatcher?
     */
    public boolean isWikiTermReference (String word);
}