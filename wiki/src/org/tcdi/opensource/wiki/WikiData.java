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
package org.tcdi.opensource.wiki;

import java.io.Serializable;

/**
 *  A WikiData object contains an individual piece of data (usually just a
 *  java.lang.String) and has a type. WikiData is usually used as an array from
 *  the WikiPage object.
 *
 *@author     Eric B. Ridge
 *@created    20. September 2002
 */
public class WikiData implements Serializable {
	/**
	 *@deprecated    these constants have been replaced by constants in
	 *      WikiDataTypes. Their values should be ignored
	 */
	public final static int TYPE_UNKNOWN = -1;

	/**
	 *@deprecated    these constants have been replaced by constants in
	 *      WikiDataTypes. Their values should be ignored
	 */
	public final static int TYPE_PAGE_REFERENCE = 0;

	/**
	 *@deprecated    these constants have been replaced by constants in
	 *      WikiDataTypes. Their values should be ignored
	 */
	public final static int TYPE_PLAIN_TEXT = 1;

	/**
	 *@deprecated    these constants have been replaced by constants in
	 *      WikiDataTypes. Their values should be ignored
	 */
	public final static int TYPE_USER = 1000;

	private Object _text;
	private int _type = WikiDataTypes.PLAIN_TEXT;

	final static long serialVersionUID = 0L;


	//
	// constructors
	//

	/**
	 *  Empty constructor. Does nothing.
	 */
	public WikiData() {
		;
	}


	/**
	 *  Constructor to set data and type
	 *
	 *@param  text  the data of this object
	 *@param  type  this object's type
	 */
	public WikiData(Object text, int type) {
		_text = text;
		_type = type;
	}


	//
	// get methods
	//

	/**
	 *@return    the data from this object
	 */
	public Object getData() {
		return _text;
	}


	/**
	 *@return    this object's type
	 */
	public int getType() {
		return _type;
	}


	//
	// set methods
	//

	/**
	 *  Set the data for this object
	 *
	 *@param  data      The new data value
	 */
	public void setData(Object data) {
		_text = data;
	}


	/**
	 *  Set the type for this object
	 *
	 *@param  new_type  the type of this object
	 */
	public void setType(int new_type) {
		_type = new_type;
	}


	//
	// other methods
	//

	/**
	 *  is this data object a reference to another page?
	 *
	 *@return    The pageReference value
	 */
	public boolean isPageReference() {
		return _type == WikiDataTypes.PAGE_REFERENCE;
	}


	/**
	 *  is this data object plain text?
	 *
	 *@return    The text value
	 */
	public boolean isText() {
		return _type == WikiDataTypes.PLAIN_TEXT;
	}


	/**
	 *  is this data object of the type specified?
	 *
	 *@param  type  Description of the Parameter
	 *@return       true if object is of specified type
	 */
	public boolean isOfType(int type) {
		return _type == type;
	}


	/**
	 *  Overloaded to return the toString() value of the internal data structure
	 *
	 *@return    Description of the Return Value
	 */
	public String toString() {
		return _text != null ? _text.toString() : "";
	}

}
