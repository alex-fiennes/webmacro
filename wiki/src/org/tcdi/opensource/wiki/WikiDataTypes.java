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
 *  this is simply a list of the various types a WikiData object can have.<p>
 *
 *  These constants reside here as they were added long after the first
 *  WikiSystem was being widely used, and I didn't want to risk upsetting the
 *  WikiData object structure, potentially breaking existing serialized pages.
 *
 *@author     Eric B. Ridge
 *@created    16. September 2002
 *@see        org.tcdi.opensource.wiki.WikiData
 */
public interface WikiDataTypes {
	/**
	 *  Description of the Field
	 */
	public final static int UNKNOWN = -1;
	/**
	 *  Description of the Field
	 */
	public final static int PLAIN_TEXT = 0;
	/**
	 *  Description of the Field
	 */
	public final static int HORIZ_LINE = 1;
	/**
	 *  Description of the Field
	 */
	public final static int PAGE_REFERENCE = 2;
	/**
	 *  Description of the Field
	 */
	public final static int LINE_BREAK = 3;
	/**
	 *  Description of the Field
	 */
	public final static int PARAGRAPH_BREAK = 4;
	/**
	 *  Description of the Field
	 */
	public final static int URL = 5;
	/**
	 *  Description of the Field
	 */
	public final static int JAVADOC = 6;
	/**
	 *  Description of the Field
	 */
	public final static int IMAGE = 7;
	/**
	 *  Description of the Field
	 */
	public final static int INDENT = 8;

	/**
	 *  Description of the Field
	 */
	public final static int START_INDENT = 10;
	/**
	 *  Description of the Field
	 */
	public final static int END_INDENT = 11;
	/**
	 *  Description of the Field
	 */
	public final static int START_INDENT_BLOCK = 12;
	/**
	 *  Description of the Field
	 */
	public final static int END_INDENT_BLOCK = 13;

	/**
	 *  Description of the Field
	 */
	public final static int START_BOLD = 20;
	/**
	 *  Description of the Field
	 */
	public final static int END_BOLD = 21;

	/**
	 *  Description of the Field
	 */
	public final static int START_UNDERLINE = 30;
	/**
	 *  Description of the Field
	 */
	public final static int END_UNDERLINE = 31;

	/**
	 *  Description of the Field
	 */
	public final static int START_ITALIC = 40;
	/**
	 *  Description of the Field
	 */
	public final static int END_ITALIC = 41;

	/**
	 *  Description of the Field
	 */
	public final static int START_URL = 50;
	/**
	 *  Description of the Field
	 */
	public final static int END_URL = 51;

	/**
	 *  Description of the Field
	 */
	public final static int START_IMAGE = 60;
	/**
	 *  Description of the Field
	 */
	public final static int END_IMAGE = 61;

	/**
	 *  Description of the Field
	 */
	public final static int START_JAVADOC = 70;
	/**
	 *  Description of the Field
	 */
	public final static int END_JAVADOC = 71;

	/**
	 *  Description of the Field
	 */
	public final static int START_COLOR = 80;
	/**
	 *  Description of the Field
	 */
	public final static int END_COLOR = 81;

	/**
	 *  Description of the Field
	 */
	public final static int START_NAMED_HEADER = 90;
	/**
	 *  Description of the Field
	 */
	public final static int END_NAMED_HEADER = 91;

	/**
	 *  Description of the Field
	 */
	public final static int START_QUOTE = 100;
	/**
	 *  Description of the Field
	 */
	public final static int END_QUOTE = 101;

	/**
	 *  Description of the Field
	 */
	public final static int START_LIST = 110;
	/**
	 *  Description of the Field
	 */
	public final static int LI = 111;
	/**
	 *  Description of the Field
	 */
	public final static int END_LIST = 112;

	/**
	 *  Description of the Field
	 */
	public final static int EMAIL = 200;
	/**
	 *  Description of the Field
	 */
	public final static int QUOTED_BLOCK = 201;
	/**
	 *  Description of the Field
	 */
	public final static int SPACE = 202;
	/**
	 *  Description of the Field
	 */
	public final static int LT = 203;
	/**
	 *  Description of the Field
	 */
	public final static int GT = 204;
}

