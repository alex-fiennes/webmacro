package org.webmacro.parser;

import org.webmacro.*;
import org.webmacro.engine.*;

/** 
 * ParserBlockBuilder extends BlockBuilder, and should only be used by
 * the WMParser_impl parser.  It adds methods for eating trailing 
 * <WHITESPACE>  or  <WHITESPACE> <NL> <WHITESPACE>  that precede directives.
 * It assumes that literal text will not span elements (true for the 
 * current parser) so if the parser changes, this will need to change too.
 */


public class ParserBlockBuilder extends BlockBuilder {
  private int literalMark = 0;

  final public void markLiteral() {
    literalMark = size();
  } 

  final public void eatTrailingWs() {
    int i, j, nChars=0;

    i = size() - 1;
    if ( i < 0 || i+1 == literalMark ) 
      return;

    Object o = elementAt(i);
    if (! (o instanceof String)) 
      return;
    String s = (String) o;
    j = s.length() - 1;
    while (j >= 0 && Character.isSpaceChar(s.charAt(j)))
      j--;

    if (j < 0)
      remove(i);
    else if (j < s.length() - 1)
      setElementAt(s.substring(0, j+1), i);
    markLiteral();
  }


  final public void eatTrailingWsNl() {
    int i, j, nChars=0;

    i = size() - 1;
    if ( i < 0 || i+1 == literalMark ) 
      return;

    Object o = elementAt(i);
    if (! (o instanceof String)) 
      return;
    String s = (String) o;
    j = s.length() - 1;
    while (j >= 0 && Character.isSpaceChar(s.charAt(j)))
      j--;
    if (j >= 0) {
      if (s.charAt(j) == '\r')
        j--;
      else if (s.charAt(j) == '\n') {
        j--;
        if (j >= 0 && s.charAt(j) == '\r')
          j--;
      }
      while (j >= 0 && Character.isSpaceChar(s.charAt(j)))
        j--;
    }

    if (j < 0) 
      remove(i);
    else if (j < s.length() - 1)
      setElementAt(s.substring(0, j+1), i);
    markLiteral();
  }

  final public boolean directiveOk() {
    if (size() == 0 || size() == literalMark) 
      return true;
    else {
      Object o = elementAt(size() - 1);
      if (!(o instanceof String))
        return true;
      else {
        String s = (String) o;
        char ch = s.charAt(s.length()-1);
        if (ch == '=' || ch == '\'' || ch == '\"' 
            || Character.isLetterOrDigit(ch))
          return false;
      }
    }
    return true;
  }
}
