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

import org.tcdi.opensource.wiki.*;

/**
 *  WikiPageRenderers can transform a WikiPage into a certain text format.
 *  Default implementes for rendering to Text and HTML are provided.
 *
 *@author     e_ridge
 *@created    16. September 2002
 *@see        com.tcdi.opensource.wiki.renderer.TextPageRenderer
 *@see        com.tcdi.opensource.wiki.renderer.HTMLPageRenderer
 */
public abstract class WikiPageRenderer {

	/**
	 *  Exception thrown when a WikiPage cannot be rendered
	 *
	 *@author     christiana
	 *@created    16. September 2002
	 */
	public static class RenderException extends Exception {
		/**
		 *  Constructor for the RenderException object
		 *
		 *@param  msg  Description of the Parameter
		 */
		public RenderException(String msg) {
			super(msg);
		}
	}


	private final WikiURLRenderer _urlRenderer;


	/**
	 *  Constructor for the WikiPageRenderer object
	 *
	 *@param  urlRenderer  Description of the Parameter
	 */
	public WikiPageRenderer(WikiURLRenderer urlRenderer) {
		_urlRenderer = urlRenderer;
	}


	/**
	 *  Render the specified WikiPage to the specified output stream.<p>
	 *
	 *  This method should <b>not</b> close the output stream.
	 *
	 *@param  page                                  Description of the Parameter
	 *@param  out                                   Description of the Parameter
	 *@exception  IOException                       Description of the Exception
	 *@exception  WikiPageRenderer.RenderException  Description of the Exception
	 */
	public void render(WikiPage page, OutputStream out) throws IOException, WikiPageRenderer.RenderException {
		out.write(render(page).getBytes());
	}


	/**
	 *  Render the specified WikiPage and return the rendered results as a String.
	 *  Delegates each WikiDataType off to implementation methods. Except for
	 *  URL's; they are delegated to the URLRenderer provided during construction
	 *  of this class.
	 *
	 *@param  page                                  Description of the Parameter
	 *@return                                       Description of the Return Value
	 *@exception  WikiPageRenderer.RenderException  Description of the Exception
	 */
	public String render(WikiPage page) throws WikiPageRenderer.RenderException {
		WikiData[] dataElement = page.getData();
		StringBuffer sb = new StringBuffer(2048);

		for (int x = 0; x < dataElement.length; x++) {
			int type = dataElement[x].getType();
			String data = (String) dataElement[x].getData();
			String str = null;

			switch (type) {
						case WikiDataTypes.START_BOLD:
							str = renderBoldStart();
							break;
						case WikiDataTypes.END_BOLD:
							str = renderBoldEnd();
							break;
						case WikiDataTypes.START_UNDERLINE:
							str = renderUnderlineStart();
							break;
						case WikiDataTypes.END_UNDERLINE:
							str = renderUnderlineEnd();
							break;
						case WikiDataTypes.START_ITALIC:
							str = renderItalicStart();
							break;
						case WikiDataTypes.END_ITALIC:
							str = renderItalicEnd();
							break;
						case WikiDataTypes.PARAGRAPH_BREAK:
							str = renderParagraphBreak();
							break;
						case WikiDataTypes.LINE_BREAK:
							str = renderLineBreak();
							break;
						case WikiDataTypes.HORIZ_LINE:
							str = renderHorizLine();
							break;
						case WikiDataTypes.PLAIN_TEXT:
							str = renderPlainText(data);
							break;
						case WikiDataTypes.PAGE_REFERENCE:
							str = renderWikiTerm(data, page.getTitle());
							break;
						case WikiDataTypes.INDENT:
							int many = (data != null && data.length() > 0) ? Integer.parseInt(data) : 1;
							str = renderIndent(many);
							break;
						case WikiDataTypes.START_NAMED_HEADER:
							str = renderHeaderStart(data);
							break;
						case WikiDataTypes.END_NAMED_HEADER:
							str = renderHeaderEnd(data);
							break;
						case WikiDataTypes.URL:
							str = _urlRenderer.renderURL(data);
							break;
						// @deprecated "image" is now a URL type.
						case WikiDataTypes.IMAGE:
							str = renderImage(data);
							break;
						// @deprecated "javadoc" is now a URL type.
						case WikiDataTypes.JAVADOC:
							str = renderJavaDoc(data);
							break;
						case WikiDataTypes.START_COLOR:
							str = renderColorStart(data);
							break;
						case WikiDataTypes.END_COLOR:
							str = renderColorEnd();
							break;
						case WikiDataTypes.EMAIL:
							str = renderEmail(data);
							break;
						case WikiDataTypes.QUOTED_BLOCK:
							str = renderQuotedBlock(data);
							break;
						case WikiDataTypes.SPACE:
							str = renderSpace();
							break;
						case WikiDataTypes.LT:
							str = renderLT();
							break;
						case WikiDataTypes.GT:
							str = renderGT();
							break;
						case WikiDataTypes.START_LIST:
							str = renderStartList();
							break;
						case WikiDataTypes.LI:
							str = renderListItem();
							break;
						case WikiDataTypes.END_LIST:
							str = renderEndList();
							break;
						default:
							str = renderUnknown(dataElement[x]);
			}
			// esac

			if (str != null) {
				sb.append(str);
			}
		}
		// for x

		return sb.toString();
	}


	//
	// protectd, abstract methods
	//
	/**
	 *  Description of the Method
	 *
	 *@param  text  Description of the Parameter
	 *@return       Description of the Return Value
	 */
	protected abstract String renderPlainText(String text);


	/**
	 *  Description of the Method
	 *
	 *@param  toReference      Description of the Parameter
	 *@param  currentPageName  Description of the Parameter
	 *@return                  Description of the Return Value
	 */
	protected abstract String renderWikiTerm(String toReference, String currentPageName);


	/**
	 *  Description of the Method
	 *
	 *@param  many  Description of the Parameter
	 *@return       Description of the Return Value
	 */
	protected abstract String renderIndent(int many);


	/**
	 *  Description of the Method
	 *
	 *@param  headerName  Description of the Parameter
	 *@return             Description of the Return Value
	 */
	protected abstract String renderHeaderStart(String headerName);


	/**
	 *  Description of the Method
	 *
	 *@param  headerName  Description of the Parameter
	 *@return             Description of the Return Value
	 */
	protected abstract String renderHeaderEnd(String headerName);


	/**
	 *@param  imageLocation  Description of the Parameter
	 *@return                Description of the Return Value
	 *@deprecated            "image" is now a URL type.
	 */
	protected abstract String renderImage(String imageLocation);


	/**
	 *@param  className  Description of the Parameter
	 *@return            Description of the Return Value
	 *@deprecated        "javadoc" is now a URL type.
	 */
	protected abstract String renderJavaDoc(String className);


	/**
	 *  Description of the Method
	 *
	 *@param  color  Description of the Parameter
	 *@return        Description of the Return Value
	 */
	protected abstract String renderColorStart(String color);


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected abstract String renderColorEnd();


	/**
	 *  Description of the Method
	 *
	 *@param  emailAddress  Description of the Parameter
	 *@return               Description of the Return Value
	 */
	protected abstract String renderEmail(String emailAddress);


	/**
	 *  Description of the Method
	 *
	 *@param  text  Description of the Parameter
	 *@return       Description of the Return Value
	 */
	protected abstract String renderQuotedBlock(String text);


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected abstract String renderSpace();


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected abstract String renderLT();


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected abstract String renderGT();


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected abstract String renderStartList();


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected abstract String renderListItem();


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected abstract String renderEndList();


	/**
	 *  Description of the Method
	 *
	 *@param  data  Description of the Parameter
	 *@return       Description of the Return Value
	 */
	protected abstract String renderUnknown(WikiData data);


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected abstract String renderBoldStart();


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected abstract String renderBoldEnd();


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected abstract String renderUnderlineStart();


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected abstract String renderUnderlineEnd();


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected abstract String renderItalicStart();


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected abstract String renderItalicEnd();


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected abstract String renderParagraphBreak();


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected abstract String renderLineBreak();


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	protected abstract String renderHorizLine();

}
