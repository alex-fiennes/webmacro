/*
 * WikiURLRenderer.java
 *
 * Created on October 18, 2001, 3:26 AM
 */

package org.tcdi.opensource.wiki.renderer;

/**
 * Can render a string-based URL to another string representation.  Useful
 * for converting url's such as http://www.webmacro.org/ to 
 * &lt;a href="http://www.webmacro.org/"&gt;www.webmacro.org&lt;/a&gt;
 * @author  e_ridge
 */
public interface WikiURLRenderer {

    public String renderURL (String url);

}