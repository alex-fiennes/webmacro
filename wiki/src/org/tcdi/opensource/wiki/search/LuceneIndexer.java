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

import com.lucene.document.Document;
import com.lucene.document.Field;
import com.lucene.document.DateField;
import com.lucene.analysis.StopAnalyzer;
import com.lucene.index.IndexWriter;

import org.tcdi.opensource.wiki.*;
import org.tcdi.opensource.wiki.renderer.*;

/**
 * The LuceneIndexer uses the Lucene (www.lucene.org) search engine
 * to index a WikiPage.  It indexes the author, keywords, date, and full text
 * of a WikiPage.<p>
 *
 * It requires 1 WikiSystem configuration property: LuceneIndexer.IndexDirectory<br>
 * This is where the physical page index is stored.  It must point to a directory
 * that is writeable by the owner of the running JVM.
 *
 * @author  e_ridge
 */
public class LuceneIndexer implements WikiPageIndexer {
    
    static Object _lock = new Object ();
    
    public synchronized void index(WikiSystem wiki, WikiPage page) throws WikiPageIndexer.IndexerException {
        try {
            Document doc = createDocument(wiki, page);

            if (doc != null) {
                synchronized (_lock) {
                    File f = new File (wiki.getProperties().getProperty("LuceneIndexer.IndexDirectory"));
                    IndexWriter writer = new IndexWriter(f, new StopAnalyzer(), 
                                                         !f.exists());
                    writer.mergeFactor = 20;
                    writer.addDocument(doc);
                    writer.optimize();
                    writer.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new WikiPageIndexer.IndexerException(e.toString());
        }
    }
    
    private static Document createDocument(WikiSystem wiki, WikiPage page) {
        Document doc = new Document();
        String tmp = null;
        
        // title
        tmp = page.getTitle();
        if (tmp != null)
            doc.add(Field.Keyword("title", tmp));
        
        // last modified date
        doc.add(Field.Keyword ("modified", DateField.dateToString(page.getDateLastModified())));
        
        // creation date
        doc.add(Field.Keyword ("created", DateField.dateToString(page.getDateCreated())));
        
        // author
        WikiUser user = wiki.getUser(page.getAuthor());
        if (user != null)
            doc.add(Field.Keyword("author", user.getName()));
        
        // keywords (related titles)
        StringBuffer sb = new StringBuffer(128);
        String[] keywords = page.getRelatedTitles();
        for (int x=0; x<keywords.length; x++) {
            if (sb.length() > 0)
                sb.append(" ");
            sb.append(keywords[x]);
        }
        doc.add(Field.Text("keywords", sb.toString()));
        
        // the actual text of the page
        tmp = page.getUnparsedData();
        if (tmp != null) {
            WikiPageRenderer renderer = new TextPageRenderer (new TextURLRenderer(), wiki);

            try {
                doc.add(Field.Text("text", renderer.render (page)));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        
        return doc;
    }
    
}