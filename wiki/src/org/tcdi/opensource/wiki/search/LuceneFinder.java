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
package org.tcdi.opensource.wiki.search;

import java.io.*;
import java.util.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Hits;
import org.apache.lucene.queryParser.QueryParser;

import org.tcdi.opensource.wiki.*;

/**
 *  The LuceneFinder uses the Lucene search engine (www.lucene.org) to search
 *  the index created by a LuceneIndexer.<p>
 *
 *  LuceneFinder requires 1 WikiSystem configuration property:
 *  LuceneIndexer.IndexDirectory<br>
 *  This should point to a directory that contains the Lucene index that should
 *  be searched.
 *
 *@author     e_ridge
 *@created    8. September 2002
 */
public class LuceneFinder implements WikiPageFinder {
	static Analyzer analyzer = new GermanAnalyzer();


	/**
	 *  return an array of pages that match the <code>query</code>. The query is
	 *  implementation specific
	 *
	 *@param  wiki                                Description of the Parameter
	 *@param  query                               Description of the Parameter
	 *@return                                     Description of the Return Value
	 *@exception  WikiPageFinder.FinderException  Description of the Exception
	 */
	public WikiPageFinder.FindResult[] findPages(WikiSystem wiki, String query) throws WikiPageFinder.FinderException {
		try {
			Searcher searcher = null;
			//Analyzer analyzer = new StopAnalyzer();

			String _query = "title:" + query + " keywords:" + query + "^2 " + query + "^3";
			Query q = QueryParser.parse(_query, "text", analyzer);
			/*
			 *  QueryParser qp = new QueryParser("text", analyzer);
			 *  Query q = qp.parse("(title:" + query + ")^4 OR (keywords:" + query + ")^2 OR (text:" + query + ")");
			 *  System.out.println("query: " + q);
			 */
			Hits hits = null;

			if (wiki.getBaseDir() != null) {
				searcher = new IndexSearcher(wiki.getBaseDir() + wiki.getProperties().getProperty("LuceneIndexer.IndexDirectory"));
			} else {
				searcher = new IndexSearcher(wiki.getProperties().getProperty("LuceneIndexer.IndexDirectory"));
			}
			hits = searcher.search(q);

			List pages = new ArrayList();

			int size = hits.length();
			for (int x = 0; x < size; x++) {
				String title = hits.doc(x).get("title");
				if (title != null) {
					WikiPage page = wiki.getPage(title);
					if (page == null) {
						continue;
					}

					FindResult result = new FindResult();
					result.page = page;
					result.score = Math.round(hits.score(x) * 100);
					result.preview = org.webmacro.util.HTMLEscaper.escape(hits.doc(x).get("text"));

					/*
					 *  boolean skip = false;
					 *  for (int y = 0; y < pages.size(); y++) {
					 *  FindResult fr = (FindResult) pages.get(y);
					 *  String tmp = fr.page.getTitle();
					 *  if (tmp.equals(title)) {
					 *  fr.score += 1;
					 *  skip = true;
					 *  break;
					 *  }
					 *  }
					 *  if (skip) {
					 *  continue;
					 *  }
					 */
					pages.add(result);
				}
			}

			searcher.close();
			WikiPageFinder.FindResult[] results = (FindResult[]) pages.toArray(new WikiPageFinder.FindResult[0]);

			/*
			 *  Arrays.sort(results,
			 *  new Comparator() {
			 *  public int compare(Object o1, Object o2) {
			 *  FindResult fr1 = (FindResult) o1;
			 *  FindResult fr2 = (FindResult) o2;
			 *  return (int) (fr2.score * 1000 - fr1.score * 1000);
			 *  }
			 *  });
			 */
			return results;
		} catch (Exception e) {
			e.printStackTrace();
			throw new WikiPageFinder.FinderException(e.toString());
		}
	}
}

