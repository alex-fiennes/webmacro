/* Generated By:JavaCC: Do not edit this line. WikiParserConstants.java */
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
package org.tcdi.opensource.wiki.parser;

public interface WikiParserConstants {

  int EOF = 0;
  int NL = 1;
  int SPACE = 2;
  int TAB = 3;
  int ALPHANUM = 4;
  int MAILCHARS = 5;
  int URLCHARS = 6;
  int UCASE_ALPHANUM = 7;
  int LCASE_ALPHANUM = 8;
  int NON_ALPHANUM = 9;
  int QUOTED_BLOCK = 10;
  int BOLD = 11;
  int UNDERLINE = 12;
  int ITALIC = 13;
  int LT = 14;
  int GT = 15;
  int LI = 16;
  int LI_NUMBERED = 17;
  int COLOR = 18;
  int HEADER = 19;
  int COLOR_HEADER_TERMINATE = 20;
  int RULE = 21;
  int EMAIL = 22;
  int URL = 23;
  int WIKI_TERM = 24;
  int SHORT_WIKI_TERM = 25;
  int WORD = 26;
  int NEW_PARAGRAPH = 27;
  int LINE_BREAK = 28;
  int INDENT = 29;
  int WHITESPACE = 30;
  int TILDE = 31;
  int ASTERISK = 32;
  int UNDERSCORE = 33;
  int CARET = 34;
  int DOUBLE_LBRACKET = 35;
  int DELIMITERS = 36;

  int DEFAULT = 0;

  String[] tokenImage = {
    "<EOF>",
    "<NL>",
    "\" \"",
    "\"\\t\"",
    "<ALPHANUM>",
    "<MAILCHARS>",
    "<URLCHARS>",
    "<UCASE_ALPHANUM>",
    "<LCASE_ALPHANUM>",
    "<NON_ALPHANUM>",
    "<QUOTED_BLOCK>",
    "\"*\"",
    "\"_\"",
    "\"~\"",
    "\"<\"",
    "\">\"",
    "<LI>",
    "<LI_NUMBERED>",
    "<COLOR>",
    "<HEADER>",
    "\"^\"",
    "\"-----\"",
    "<EMAIL>",
    "<URL>",
    "<WIKI_TERM>",
    "<SHORT_WIKI_TERM>",
    "<WORD>",
    "<NEW_PARAGRAPH>",
    "<LINE_BREAK>",
    "<INDENT>",
    "<WHITESPACE>",
    "\"~~\"",
    "\"**\"",
    "\"__\"",
    "\"^^\"",
    "\"[[[[\"",
    "<DELIMITERS>",
  };

}
