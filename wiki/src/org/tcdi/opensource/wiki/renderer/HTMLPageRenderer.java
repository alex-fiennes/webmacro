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

package org.tcdi.opensource.wiki.renderer;

import java.io.*;
import java.util.*;

import org.tcdi.opensource.wiki.*;
import org.tcdi.opensource.wiki.renderer.*;

/**
 *  HTMLPageRenderer can render a WikiPage to HTML. Supports all the various
 *  formatting options parsed by the WikiParser.<p>
 *
 *  It is possible to construct this page renderer with a null <code>WikiSystem</code>
 *  object. In this case, support for linking WikiTerms, Javadocs
 *  (@deprecated-style), and Named Headers is disabled.
 *
 *@author     e_ridge
 *@created    12. September 2002
 */
public class HTMLPageRenderer extends WikiPageRenderer {

	private WikiSystem _wiki;


	/**
	 *  Create a new HTMLPageRenderer that does not support WikiTerms, Javadocs
	 *  (@deprecated-style), and Named Headers
	 *
	 *@param  urlRenderer  Description of the Parameter
	 */
	public HTMLPageRenderer(WikiURLRenderer urlRenderer) {
		this(urlRenderer, null);
	}


	/**
	 *  Create a new HTMLPageRenderer that supports <b>all</b> the formatting types
	 *  of Wiki
	 *
	 *@param  urlRenderer  Description of the Parameter
	 *@param  wiki         Description of the Parameter
	 */
	public HTMLPageRenderer(WikiURLRenderer urlRenderer, WikiSystem wiki) {
		super(urlRenderer);
		_wiki = wiki;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  data  Description of the Parameter
	 *@return       Description of the Return Value
	 */
	protected String renderUnknown(WikiData data) {
		return "<!-- " + data.getType() + " is an unknown WikiData type -->";
	}


	/**
	 *  Description of the Method
	 *
	 *@param  emailAddress  Description of the Parameter
	 *@return               Description of the Return Value
	 */
	protected String renderEmail(String emailAddress) {
		StringBuffer sb = new StringBuffer(emailAddress.length());
		sb.append("<a href=\"mailto:")
				.append(emailAddress)
				.append("\">")
				.append(emailAddress)
				.append("</a>");
		return sb.toString();
	}


	/**
	 *@param  className  Description of the Parameter
	 *@return            Description of the Return Value
	 *@deprecated        "javadoc" is now a URL type.
	 */
	protected String renderJavaDoc(String className) {
		if (_wiki != null) {
			StringBuffer sb = new StringBuffer(255);
			sb.append("<a target=javadoc href=\"");
			sb.append(_wiki.getProperties().getProperty("JavaDocRoot"));
			sb.append(className.replace('.', '/'));
			sb.append(".html\">");
			sb.append("<font color=\"");
			sb.append(_wiki.getProperties().getProperty("Color.JavadocLink"));
			sb.append("\">");
			sb.append(className);
			sb.append("</font></a>");
			return sb.toString();
		} else {
			return className;
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected String renderGT() {
		return "&gt;";
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected String renderLT() {
		return "&lt;";
	}


	/**
	 *@param  imageLocation  Description of the Parameter
	 *@return                Description of the Return Value
	 *@deprecated            "image" is now a URL type.
	 */
	protected String renderImage(String imageLocation) {
		StringBuffer sb = new StringBuffer(imageLocation);
		sb.append("<img src=\"");
		sb.append(imageLocation);
		sb.append("\">");
		return sb.toString();
	}


	/**
	 *  Description of the Method
	 *
	 *@param  text  Description of the Parameter
	 *@return       Description of the Return Value
	 */
	protected String renderQuotedBlock(String text) {
		StringBuffer sb = new StringBuffer(text.length());
		sb.append("<pre>")
				.append(replace(replace(text, "<", "&lt;"), ">", "&gt;"))
				.append("</pre>");

		return sb.toString();
	}


	/**
	 *  Description of the Method
	 *
	 *@param  many  Description of the Parameter
	 *@return       Description of the Return Value
	 */
	protected String renderIndent(int many) {
		StringBuffer sb = new StringBuffer(many * 5);
		for (int x = 0; x < many; x++) {
			sb.append("&nbsp;");
		}

		return sb.toString();
	}


	/**
	 *  Description of the Method
	 *
	 *@param  headerName  Description of the Parameter
	 *@return             Description of the Return Value
	 */
	protected String renderHeaderStart(String headerName) {
		if (_wiki != null) {
			String header = _wiki.getProperties().getProperty("Headers." + headerName + "_Start");
			if (header == null) {
				// named header not found, so use the default header
				header = _wiki.getProperties().getProperty("Headers._Start") + headerName + " ";
			}

			return header;
		} else {
			return "";
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@param  headerName  Description of the Parameter
	 *@return             Description of the Return Value
	 */
	protected String renderHeaderEnd(String headerName) {
		if (_wiki != null) {
			String header = _wiki.getProperties().getProperty("Headers." + headerName + "_End");
			if (header == null) {
				// named header not found, so use the default header
				header = _wiki.getProperties().getProperty("Headers._End");
			}

			return header;
		} else {
			return "";
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@param  term       Description of the Parameter
	 *@param  pageTitle  Description of the Parameter
	 *@return            Description of the Return Value
	 */
	protected String renderWikiTerm(String term, String pageTitle) {
		if (_wiki != null) {
			StringBuffer sb = new StringBuffer(255);
			// TODO: Make a configurable list of "pages not to link"
			if (pageTitle.equals(term)) {
				sb.append(term);
			} else if (_wiki.containsPage(term)) {
				sb.append("<a href=\"");
				sb.append(term);
				sb.append("\">");
				sb.append(term);
				sb.append("</a>");
			} else {
				sb.append(term);
				sb.append("<a href=\"");
				sb.append("ControllerPage?edit=");
				sb.append(term);
				sb.append("\" TITLE=\"Seite ")
						.append(term)
						.append(" neu erstellen\">?</a>");
			}

			return sb.toString();
		} else {
			return term;
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@param  color  Description of the Parameter
	 *@return        Description of the Return Value
	 */
	protected String renderColorStart(String color) {
		StringBuffer sb = new StringBuffer();
		sb.append("<font color=\"")
				.append(color)
				.append("\">");

		return sb.toString();
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected String renderColorEnd() {
		return "</font>";
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected String renderSpace() {
		return " ";
	}


	/**
	 *  Description of the Method
	 *
	 *@param  text  Description of the Parameter
	 *@return       Description of the Return Value
	 */
	protected String renderPlainText(String text) {
		return (text != null) ? org.webmacro.util.HTMLEscaper.escape(text) : "";
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected String renderUnderlineStart() {
		return "<u>";
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected String renderHorizLine() {
		return "<hr>";
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected String renderItalicEnd() {
		return "</i>";
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected String renderUnderlineEnd() {
		return "</u>";
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected String renderBoldEnd() {
		return "</b>";
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected String renderItalicStart() {
		return "<i>";
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected String renderBoldStart() {
		return "<b>";
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected String renderLineBreak() {
		return "<br>";
	}


	//Christian
	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected String renderParagraphBreak() {
		return "<p>";
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected String renderStartList() {
		return "<ul>";
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected String renderListItem() {
		return "<li>";
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected String renderEndList() {
		return "</ul>";
	}


	/**
	 *  replace all occurrences of 'from' to 'to' in 'src'
	 *
	 *@param  src   String to perform replace on
	 *@param  from  substring that needs to be replaced
	 *@param  to    string to replace 'from' with
	 *@return       Description of the Return Value
	 *@returns      String with all occurrences of 'from' replaced with 'to' or
	 *      'src' if 'from' not found
	 */
	private final static String replace(String src, String from, String to) {
		if (src == null || from == null || to == null) {
			return src;
		}

		int fromlen = from.length();
		int idx = -fromlen;
		int
				lastidx = 0;

		StringBuffer newstr = new StringBuffer();

		while ((idx = src.indexOf(from, lastidx)) > -1) {
			newstr.append(src.substring(lastidx, idx));
			newstr.append(to);

			lastidx = idx + fromlen;
		}
		if (lastidx == 0) {
			return src;
		} else {
			newstr.append(src.substring(lastidx));
		}

		return newstr.toString();
	}
}

