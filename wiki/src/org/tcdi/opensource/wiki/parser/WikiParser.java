/* Generated By:JavaCC: Do not edit this line. WikiParser.java */
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

import java.io.*;
import org.tcdi.opensource.wiki.*;
import org.tcdi.opensource.wiki.builder.*;

public class WikiParser implements WikiParserConstants {
   private String _headerName = null;
   private String _colorName = null;

  final public WikiPage parse(WikiPageBuilder builder) throws ParseException {
                                             Token t;
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case QUOTED_BLOCK:
      case BOLD:
      case UNDERLINE:
      case ITALIC:
      case LT:
      case GT:
      case COLOR:
      case HEADER:
      case COLOR_HEADER_TERMINATE:
      case RULE:
      case EMAIL:
      case URL:
      case WIKI_TERM:
      case SHORT_WIKI_TERM:
      case WORD:
      case NEW_PARAGRAPH:
      case LINE_BREAK:
      case INDENT:
      case WHITESPACE:
      case ASTERISK:
      case UNDERSCORE:
      case CARET:
      case DOUBLE_LBRACKET:
      case DELIMITERS:
        ;
        break;
      default:
        jj_la1[0] = jj_gen;
        break label_1;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case LT:
        t = jj_consume_token(LT);
                          builder.lt();
        break;
      case GT:
        t = jj_consume_token(GT);
                          builder.gt();
        break;
      case BOLD:
        t = jj_consume_token(BOLD);
                          builder.bold();
        break;
      case UNDERLINE:
        t = jj_consume_token(UNDERLINE);
                          builder.underline();
        break;
      case ITALIC:
        t = jj_consume_token(ITALIC);
                          builder.italic();
        break;
      case COLOR:
        t = jj_consume_token(COLOR);
        if (_headerName != null || _colorName != null) {
            builder.endColorOrHeader();
            String word = t.image.substring(1);
            if (builder.isWikiTermReference (word))
                builder.wikiTerm (word);
            else
                builder.word (word);

            _colorName = null;
            _headerName = null;
        } else {
            _colorName = t.image.substring(1);
            builder.color (_colorName);
        }
        break;
      case HEADER:
        t = jj_consume_token(HEADER);
        _headerName = t.image.substring(2);
        builder.header (_headerName);
        break;
      case COLOR_HEADER_TERMINATE:
        t = jj_consume_token(COLOR_HEADER_TERMINATE);
        if (_headerName == null && _colorName == null)
            builder.word ("^");
        else {
            builder.endColorOrHeader ();
            _headerName = null;
        }
        break;
      case RULE:
        t = jj_consume_token(RULE);
                          builder.ruler();
        break;
      case EMAIL:
        t = jj_consume_token(EMAIL);
                          builder.email(t.image);
        break;
      case WIKI_TERM:
        t = jj_consume_token(WIKI_TERM);
        if (builder.isWikiTermReference (t.image))
            builder.wikiTerm (t.image);
        else
            builder.word (t.image);
        break;
      case SHORT_WIKI_TERM:
        t = jj_consume_token(SHORT_WIKI_TERM);
                            builder.wikiTerm (t.image.substring(0, t.image.length()-1));
        break;
      case WORD:
        t = jj_consume_token(WORD);
                          builder.word (t.image);
        break;
      case NEW_PARAGRAPH:
        t = jj_consume_token(NEW_PARAGRAPH);
                          builder.paragraph ();
        break;
      case LINE_BREAK:
        t = jj_consume_token(LINE_BREAK);
                          builder.newline();
        break;
      case INDENT:
        t = jj_consume_token(INDENT);
                          builder.indent(2);
        break;
      case WHITESPACE:
        t = jj_consume_token(WHITESPACE);
                          builder.space();
        break;
      case ASTERISK:
        t = jj_consume_token(ASTERISK);
                          builder.word ("*");
        break;
      case UNDERSCORE:
        t = jj_consume_token(UNDERSCORE);
                          builder.word ("_");
        break;
      case CARET:
        t = jj_consume_token(CARET);
                          builder.word ("^");
        break;
      case DOUBLE_LBRACKET:
        t = jj_consume_token(DOUBLE_LBRACKET);
                            builder.word ("[[");
        break;
      case DELIMITERS:
        t = jj_consume_token(DELIMITERS);
                          builder.word (t.image);
        break;
      case URL:
        t = jj_consume_token(URL);
                          builder.url (t.image);
        break;
      case QUOTED_BLOCK:
        t = jj_consume_token(QUOTED_BLOCK);
                          builder.quotedBlock (t.image.substring(2, t.image.length()-2));
        break;
      default:
        jj_la1[1] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
        {if (true) return builder.getPage ();}
    throw new Error("Missing return statement in function");
  }

  public WikiParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  public Token token, jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[2];
  final private int[] jj_la1_0 = {0xfffffe00,0xfffffe00,};
  final private int[] jj_la1_1 = {0x1,0x1,};

  public WikiParser(java.io.InputStream stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new WikiParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 2; i++) jj_la1[i] = -1;
  }

  public void ReInit(java.io.InputStream stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 2; i++) jj_la1[i] = -1;
  }

  public WikiParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new WikiParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 2; i++) jj_la1[i] = -1;
  }

  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 2; i++) jj_la1[i] = -1;
  }

  public WikiParser(WikiParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 2; i++) jj_la1[i] = -1;
  }

  public void ReInit(WikiParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 2; i++) jj_la1[i] = -1;
  }

  final private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  final private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.Vector jj_expentries = new java.util.Vector();
  private int[] jj_expentry;
  private int jj_kind = -1;

  final public ParseException generateParseException() {
    jj_expentries.removeAllElements();
    boolean[] la1tokens = new boolean[33];
    for (int i = 0; i < 33; i++) {
      la1tokens[i] = false;
    }
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 2; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 33; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.addElement(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = (int[])jj_expentries.elementAt(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  final public void enable_tracing() {
  }

  final public void disable_tracing() {
  }

}
