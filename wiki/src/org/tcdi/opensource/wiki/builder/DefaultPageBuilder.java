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
package org.tcdi.opensource.wiki.builder;

import java.util.*;

import org.tcdi.opensource.wiki.*;

/**
 *  This is the default, and probably the only, implementation of
 *  WikiPageBuilder.
 *
 *@author     e_ridge
 *@created    16. September 2002
 */
public class DefaultPageBuilder implements WikiPageBuilder {
	/**
	 *  Description of the Field
	 */
	protected WikiTermMatcher _matcher;
	/**
	 *  Description of the Field
	 */
	protected boolean _bold, _underline, _italic, _color, _header, _space, _list;
	/**
	 *  Description of the Field
	 */
	protected String _currentHeader = null;
	/**
	 *  Description of the Field
	 */
	protected StringBuffer _text = new StringBuffer();
	/**
	 *  Description of the Field
	 */
	protected List _data = new ArrayList();
	/**
	 *  Description of the Field
	 */
	protected WikiData _currentData = new WikiData();


	/**
	 *  Constructor for the DefaultPageBuilder object
	 */
	public DefaultPageBuilder() {
		this(null);
	}


	/**
	 *  Constructor for the DefaultPageBuilder object
	 *
	 *@param  matcher  Description of the Parameter
	 */
	public DefaultPageBuilder(WikiTermMatcher matcher) {
		_matcher = matcher;
	}


	/**
	 *  Sets the wikiTermMatcher attribute of the DefaultPageBuilder object
	 *
	 *@param  matcher  The new wikiTermMatcher value
	 */
	public void setWikiTermMatcher(WikiTermMatcher matcher) {
		_matcher = matcher;
	}


	/**
	 *  Description of the Method
	 */
	public void endColorOrHeader() {
		if (_color) {
			color(null);
		}
		if (_header) {
			header(_currentHeader);
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@param  url  Description of the Parameter
	 */
	public void url(String url) {
		newData();
		_currentData.setData(url);
		_currentData.setType(WikiDataTypes.URL);
	}


	/**
	 *  Description of the Method
	 */
	public void gt() {
		newData();
		_currentData.setType(WikiDataTypes.GT);
	}


	/**
	 *  Description of the Method
	 */
	public void li() {
		if (!_list) {
			newData();
			_currentData.setType(WikiDataTypes.START_LIST);
			_list = true;
		}
		newData();
		_currentData.setType(WikiDataTypes.LI);
	}


	/**
	 *  Description of the Method
	 */
	public void lt() {
		newData();
		_currentData.setType(WikiDataTypes.LT);
	}


	/**
	 *  Description of the Method
	 */
	public void underline() {
		newData();
		if (!_underline) {
			_currentData.setType(WikiDataTypes.START_UNDERLINE);
		} else {
			_currentData.setType(WikiDataTypes.END_UNDERLINE);
		}

		_underline = !_underline;
	}


	/**
	 *  Description of the Method
	 */
	public void ruler() {
		newData();
		_currentData.setType(WikiDataTypes.HORIZ_LINE);
	}


	/**
	 *  Description of the Method
	 *
	 *@param  headerName  Description of the Parameter
	 */
	public void header(String headerName) {
		newData();

		_currentData.setData(headerName);
		if (!_header) {
			_currentData.setType(WikiDataTypes.START_NAMED_HEADER);
			_currentHeader = headerName;
		} else {
			_currentData.setType(WikiDataTypes.END_NAMED_HEADER);
			_currentHeader = null;
		}

		_header = !_header;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  email  Description of the Parameter
	 */
	public void email(String email) {
		newData();
		_currentData.setData(email);
		_currentData.setType(WikiDataTypes.EMAIL);
	}


	/**
	 *  Description of the Method
	 *
	 *@param  color  Description of the Parameter
	 */
	public void color(String color) {
		newData();

		_currentData.setData(color);
		if (!_color) {
			_currentData.setType(WikiDataTypes.START_COLOR);
		} else {
			_currentData.setType(WikiDataTypes.END_COLOR);
		}

		_color = !_color;
	}


	/**
	 *  return the completed WikiPage
	 *
	 *@return    The page value
	 */
	public WikiPage getPage() {
		if (_currentData != null) {
			newData();
		}

		WikiPage page = new WikiPage();
		page.setWikiData((WikiData[]) _data.toArray(new WikiData[0]));
		return page;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  pageName  Description of the Parameter
	 */
	public void wikiTerm(String pageName) {
		newData();

		_currentData.setData(pageName);
		_currentData.setType(WikiDataTypes.PAGE_REFERENCE);
	}


	/**
	 *  Description of the Method
	 */
	public void paragraph() {
		finishFormatting();
		if (_list) {
			newData();
			_currentData.setType(WikiDataTypes.END_LIST);
			_list = false;
		}
		newData();
		_currentData.setType(WikiDataTypes.PARAGRAPH_BREAK);
	}


	/**
	 *  Description of the Method
	 *
	 *@param  block  Description of the Parameter
	 */
	public void quotedBlock(String block) {
		newData();
		_currentData.setData(block);
		_currentData.setType(WikiDataTypes.QUOTED_BLOCK);
	}


	/**
	 *  Description of the Method
	 */
	public void newline() {
		finishFormatting();
		newData();
		_currentData.setType(WikiDataTypes.LINE_BREAK);
	}


	/**
	 *  Description of the Method
	 */
	public void space() {
		word(" ");
	}


	/**
	 *  Description of the Method
	 */
	public void bold() {
		newData();
		if (!_bold) {
			_currentData.setType(WikiDataTypes.START_BOLD);
		} else {
			_currentData.setType(WikiDataTypes.END_BOLD);
		}

		_bold = !_bold;
	}


	/**
	 *  Description of the Method
	 */
	public void italic() {
		newData();
		if (!_italic) {
			_currentData.setType(WikiDataTypes.START_ITALIC);
		} else {
			_currentData.setType(WikiDataTypes.END_ITALIC);
		}

		_italic = !_italic;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  word  Description of the Parameter
	 */
	public void word(String word) {
		newData();
		_text.append(word);
	}


	/**
	 *  Description of the Method
	 */
	private void finishFormatting() {
		if (_bold) {
			bold();
		}
		if (_underline) {
			underline();
		}
		if (_italic) {
			italic();
		}
		if (_header || _color) {
			endColorOrHeader();
		}
	}


	/**
	 *  Description of the Method
	 */
	private void newData() {
		if (_text.length() > 0) {
			_currentData.setType(WikiDataTypes.PLAIN_TEXT);
			_currentData.setData(_text.toString());
			_text.setLength(0);
		}

		_data.add(_currentData);
		_currentData = new WikiData();
	}


	/**
	 *  Description of the Method
	 *
	 *@param  many  Description of the Parameter
	 */
	public void indent(int many) {
		if (!_list) {
			newData();
			_currentData.setType(WikiDataTypes.INDENT);
			_currentData.setData("" + many);
		}
	}


	/**
	 *  is the specified string a valid WikiTerm?
	 *
	 *@param  word  Description of the Parameter
	 *@return       The wikiTermReference value
	 */
	public boolean isWikiTermReference(String word) {
		return _matcher == null ? false : _matcher.isWikiTermReference(word);
	}
}
