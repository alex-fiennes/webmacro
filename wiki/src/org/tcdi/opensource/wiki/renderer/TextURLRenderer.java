/*
 * HTMLUrlRenderer.java
 *
 * Created on October 18, 2001, 3:32 AM
 */

package org.tcdi.opensource.wiki.renderer;

import org.tcdi.opensource.wiki.*;

/**
 *
 * @author  e_ridge
 */
public class TextURLRenderer implements WikiURLRenderer {
    
    public String renderURL(String url) {
        return url;
    }
    
}
