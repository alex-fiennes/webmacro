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
package org.tcdi.opensource.wiki;

import java.io.Serializable;

/**
 * this is simply a list of the various types a WikiData object can have.<p>
 * 
 * These constants reside here as they were added long after the first WikiSystem
 * was being widely used, and I didn't want to risk upsetting the WikiData object 
 * structure, potentially breaking existing serialized pages.
 * 
 * @see org.tcdi.opensource.wiki.WikiData
 * 
 * @author Eric B. Ridge
 */
public interface WikiDataTypes {
    public static final int UNKNOWN = -1;
    public static final int PLAIN_TEXT = 0;
    public static final int HORIZ_LINE = 1;
    public static final int PAGE_REFERENCE = 2;
    public static final int LINE_BREAK = 3;
    public static final int PARAGRAPH_BREAK = 4;
    public static final int URL = 5;
    public static final int JAVADOC = 6;
    public static final int IMAGE = 7;
    public static final int INDENT = 8;

    public static final int START_INDENT = 10;
    public static final int END_INDENT = 11;
    public static final int START_INDENT_BLOCK = 12;
    public static final int END_INDENT_BLOCK = 13;

    public static final int START_BOLD = 20;
    public static final int END_BOLD = 21;

    public static final int START_UNDERLINE = 30;
    public static final int END_UNDERLINE = 31;

    public static final int START_ITALIC = 40;
    public static final int END_ITALIC = 41;

    public static final int START_URL = 50;
    public static final int END_URL = 51;

    public static final int START_IMAGE = 60;
    public static final int END_IMAGE = 61;

    public static final int START_JAVADOC = 70;
    public static final int END_JAVADOC = 71;

    public static final int START_COLOR = 80;
    public static final int END_COLOR = 81;

    public static final int START_NAMED_HEADER = 90;
    public static final int END_NAMED_HEADER = 91;

    public static final int START_QUOTE = 100;
    public static final int END_QUOTE = 101;

    public final static int START_LIST = 110;
    public final static int LI = 111;
    public final static int END_LIST = 112;

    public final static int START_NUMBERED_LIST = 113;
    public final static int END_NUMBERED_LIST = 114;

    public static final int EMAIL = 200;
    public static final int QUOTED_BLOCK = 201;
    public static final int SPACE = 202;
    public static final int LT = 203;
    public static final int GT = 204;
}
