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

import java.io.*;
import java.util.*;

import com.lucene.analysis.Analyzer;
import com.lucene.analysis.StopAnalyzer;
import com.lucene.document.Document;
import com.lucene.search.Searcher;
import com.lucene.search.IndexSearcher;
import com.lucene.search.Query;
import com.lucene.search.Hits;
import com.lucene.queryParser.QueryParser;

import org.tcdi.opensource.wiki.*;

/**
 * The LuceneFinder uses the Lucene search engine (www.lucene.org) to
 * search the index created by a LuceneIndexer.<p>
 *
 * LuceneFinder requires 1 WikiSystem configuration property: LuceneIndexer.IndexDirectory<br>
 * This should point to a directory that contains the Lucene index that should
 * be searched.
 *
 * @author  e_ridge
 */
public class LuceneFinder implements WikiPageFinder {
    
    /**
     * return an array of pages that match the <code>query</code>.  The query
     * is implementation specific
     */
    public WikiPageFinder.FindResult[] findPages(WikiSystem wiki, String query) throws WikiPageFinder.FinderException {
        try {
            Searcher searcher = null;
            Analyzer analyzer = new StopAnalyzer();
            Query q = QueryParser.parse(query, "text", analyzer);
            Hits hits = null;
            
            synchronized (LuceneIndexer._lock) {
                searcher = new IndexSearcher(wiki.getProperties().getProperty("LuceneIndexer.IndexDirectory"));
                hits = searcher.search(q);
            }

            List pages = new ArrayList ();

            int size = hits.length();
            for (int x=0; x<size; x++) {
                String title = hits.doc(x).get("title");
                if (title != null) {
                    WikiPage page = wiki.getPage(title);
                    if (page == null)
                        continue;
                    
                    FindResult result = new FindResult ();
                    result.page = page;
                    result.score = hits.score(x);
                    result.preview = hits.doc(x).get ("text");
                    
                    boolean skip = false;
                    for (int y=0; y<pages.size(); y++) {
                        FindResult fr = (FindResult) pages.get(y);
                        String tmp = fr.page.getTitle();
                        if (tmp.equals(title)) {
                            fr.score+=1;
                            skip = true;
                            break;
                        }
                    }
                    if (skip)
                        continue;
                    pages.add(result);
                }
            }

            synchronized (LuceneIndexer._lock) {
                searcher.close ();
            }
            WikiPageFinder.FindResult[] results = (FindResult[]) pages.toArray (new WikiPageFinder.FindResult[0]);

            Arrays.sort (results, new Comparator () {
                            public int compare (Object o1, Object o2) {
                                FindResult fr1 = (FindResult) o1;
                                FindResult fr2 = (FindResult) o2;
                                return (int) (fr2.score*1000 - fr1.score*1000);
                            }
                        });            
            
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            throw new WikiPageFinder.FinderException (e.toString());
        }
    }
}