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
package org.tcdi.opensource.wiki.servlet;

import org.tcdi.opensource.wiki.*;
import org.tcdi.opensource.wiki.renderer.TextPageRenderer;
import org.tcdi.opensource.wiki.renderer.WikiPageRenderer;
import org.tcdi.opensource.wiki.renderer.TextURLRenderer;
import org.tcdi.opensource.wiki.util.Diff;
import org.webmacro.servlet.WebContext;


import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;


public class DiffPageAction implements PageAction {
    
    public static class DiffHelper {
        private List _changes;
        
        public DiffHelper (Diff.change changes) {
            _changes = new ArrayList();
            while (changes != null) {
                _changes.add (changes);
                changes = changes.link;
            }
        }
        
        public List getChanges() {
            return _changes;
        }
        
        public boolean isModified (int line_no) {
            for (Iterator itr = _changes.iterator(); itr.hasNext();) {
                Diff.change ch = (Diff.change) itr.next();
                if (line_no < ch.line0+ch.deleted && line_no <= ch.line1 + ch.inserted && line_no >= ch.line0 && line_no >=ch.line1 && ch.deleted > 0 && ch.inserted > 0)
                    return true;
            }
            return false;
        }
        
        public boolean isDeleted(int line_no) {
            if (isModified(line_no))
                return false;
            
            for (Iterator itr = _changes.iterator(); itr.hasNext();) {
                Diff.change ch = (Diff.change) itr.next();
                if (ch.line0 + ch.deleted > line_no && ch.line0 <= line_no)
                    return true;
            }
            return false;
        }
        
        public boolean isInserted(int line_no) {
            if (isModified(line_no))
                return false;
            
            for (Iterator itr = _changes.iterator(); itr.hasNext();) {
                Diff.change ch = (Diff.change) itr.next();
                if (line_no >= ch.line1 && line_no < ch.line1 + ch.inserted)
                    return true;
            }
            return false;
        }

        public int getDeleteCount(int line_no) {
            if (isModified(line_no))
                return 0;
            
            for (Iterator itr = _changes.iterator(); itr.hasNext();) {
                Diff.change ch = (Diff.change) itr.next();
                if (line_no == ch.line1)
                    return ch.deleted;
            }
            return 0;
        }
        public int getInsertCount(int line_no) {
            if (isModified(line_no))
                return 0;
            
            for (Iterator itr = _changes.iterator(); itr.hasNext();) {
                Diff.change ch = (Diff.change) itr.next();
                if (line_no == ch.line0)
                    return ch.inserted;
            }
            return 0;
        }
    }
    
    public boolean accept(WikiSystem wiki, WebContext wc, WikiUser user) {
        return wc.getRequest().getRequestURI().endsWith ("DiffPage");
    }

    public String getWikiPageName(WikiSystem wiki, WebContext wc) {
        return "DiffPage";
    }

    public void perform(WikiSystem wiki, WebContext wc, WikiUser user, WikiPage page) throws PageAction.PageActionException {
        try {
            String current_version = wc.getForm("V1");
            String old_version = wc.getForm("V2");
            String page_name = wc.getForm("PAGE");
            
            if (page_name == null)
                return;

            WikiPage tmp = wiki.getPage(page_name);
            WikiPage currentPage = wiki.getPage(current_version == null ? page_name : page_name + "." + current_version);
            if (currentPage == null)
                return;
            
            WikiPage oldPage = wiki.getPage(old_version == null ? page_name + "." + (currentPage.getVersion()-1) : page_name + "." + old_version);
            if (oldPage == null)
                oldPage = currentPage;

            boolean is_current = tmp.getVersion() == currentPage.getVersion();
            
            String[] currentPageLines = WikiUtil.delimitedToArray(currentPage.getUnparsedData(), "\n");
            String[] oldPageLines = WikiUtil.delimitedToArray(oldPage.getUnparsedData(), "\n");
                
            Diff diff = new Diff(oldPageLines, currentPageLines);
            Diff.change changes = diff.diff(Diff.forwardScript);
            
            wc.put ("IsCurrent", is_current);
            wc.put ("CurrentPage", currentPage);
            wc.put ("OldPage", oldPage);
            wc.put ("CurrentPageLines", currentPageLines);
            wc.put ("OldPageLines", oldPageLines);
            wc.put ("DiffHelper", new DiffHelper (changes));
        } catch (Exception e) {
            e.printStackTrace();
            throw new PageAction.PageActionException(e.toString());
        }
    }
    public String getTemplateName(WikiSystem wiki, WikiPage page) {
        return wiki.getProperties().getProperty("DiffPageAction.Template");
    }
}
