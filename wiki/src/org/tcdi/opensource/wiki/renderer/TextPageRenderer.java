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
 *  TextPageRenderer can render a WikiPage as ASCII Text. Aside from newlines,
 *  paragraphs, and indents, doesn't support much formatting
 *
 *@author     e_ridge
 *@created    16. September 2002
 */
public class TextPageRenderer extends WikiPageRenderer {
	private WikiSystem _wiki;


	/**
	 *  Constructor for the TextPageRenderer object
	 *
	 *@param  urlRenderer  Description of the Parameter
	 *@param  wiki         Description of the Parameter
	 */
	public TextPageRenderer(WikiURLRenderer urlRenderer, WikiSystem wiki) {
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
		return "";
	}


	/**
	 *  Description of the Method
	 *
	 *@param  emailAddress  Description of the Parameter
	 *@return               Description of the Return Value
	 */
	protected String renderEmail(String emailAddress) {
		return emailAddress;
	}


	/**
	 *@param  className  Description of the Parameter
	 *@return            Description of the Return Value
	 *@deprecated        "javadoc" is now a URL type.
	 */
	protected String renderJavaDoc(String className) {
		return className;
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected String renderGT() {
		return ">";
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected String renderLT() {
		return "<";
	}


	/**
	 *@param  imageLocation  Description of the Parameter
	 *@return                Description of the Return Value
	 *@deprecated            "image" is now a URL type.
	 */
	protected String renderImage(String imageLocation) {
		return imageLocation;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  text  Description of the Parameter
	 *@return       Description of the Return Value
	 */
	protected String renderQuotedBlock(String text) {
		return text;
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
			sb.append(" ");
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
		return "";
	}


	/**
	 *  Description of the Method
	 *
	 *@param  headerName  Description of the Parameter
	 *@return             Description of the Return Value
	 */
	protected String renderHeaderEnd(String headerName) {
		return "";
	}


	/**
	 *  Description of the Method
	 *
	 *@param  term       Description of the Parameter
	 *@param  pageTitle  Description of the Parameter
	 *@return            Description of the Return Value
	 */
	protected String renderWikiTerm(String term, String pageTitle) {
		return term;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  color  Description of the Parameter
	 *@return        Description of the Return Value
	 */
	protected String renderColorStart(String color) {
		return "";
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected String renderColorEnd() {
		return "";
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
		return text;
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected String renderUnderlineStart() {
		return "";
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected String renderHorizLine() {
		return "";
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected String renderItalicEnd() {
		return "";
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected String renderUnderlineEnd() {
		return "";
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected String renderBoldEnd() {
		return "";
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected String renderItalicStart() {
		return "";
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected String renderBoldStart() {
		return "";
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected String renderLineBreak() {
		return System.getProperty("line.separator");
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected String renderParagraphBreak() {
		return System.getProperty("line.separator") + System.getProperty("line.separator");
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected String renderStartList() {
		return "\n";
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected String renderListItem() {
		return "*\t";
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected String renderEndList() {
		return "";
	}
}
