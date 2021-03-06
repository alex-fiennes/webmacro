/* Generated By:JavaCC: Do not edit this line. WMParser_implConstants.java */
package org.webmacro.parser;

/**
 * Token literal values and constants. Generated by org.javacc.parser.OtherFilesGen#start()
 */
public interface WMParser_implConstants
{

  /** End of File. */
  int EOF = 0;
  /** RegularExpression Id. */
  int _ALPHA_CHAR = 1;
  /** RegularExpression Id. */
  int _NUM_CHAR = 2;
  /** RegularExpression Id. */
  int _ALPHANUM_CHAR = 3;
  /** RegularExpression Id. */
  int _IDENTIFIER_CHAR = 4;
  /** RegularExpression Id. */
  int _IDENTIFIER = 5;
  /** RegularExpression Id. */
  int _NEWLINE = 6;
  /** RegularExpression Id. */
  int _WHITESPACE = 7;
  /** RegularExpression Id. */
  int _QCHAR = 8;
  /** RegularExpression Id. */
  int _RESTOFLINE = 9;
  /** RegularExpression Id. */
  int _COMMENT = 10;
  /** RegularExpression Id. */
  int STUFF = 11;
  /** RegularExpression Id. */
  int RBRACE = 12;
  /** RegularExpression Id. */
  int END = 13;
  /** RegularExpression Id. */
  int BEGIN = 14;
  /** RegularExpression Id. */
  int LBRACE = 15;
  /** RegularExpression Id. */
  int POUNDPOUND = 17;
  /** RegularExpression Id. */
  int RESTOFLINE = 18;
  /** RegularExpression Id. */
  int COMMENT_ELSE = 19;
  /** RegularExpression Id. */
  int DOLLAR = 20;
  /** RegularExpression Id. */
  int QCHAR = 21;
  /** RegularExpression Id. */
  int SLASH = 22;
  /** RegularExpression Id. */
  int POUND = 23;
  /** RegularExpression Id. */
  int QUOTE = 24;
  /** RegularExpression Id. */
  int SQUOTE = 25;
  /** RegularExpression Id. */
  int NULL = 26;
  /** RegularExpression Id. */
  int TRUE = 27;
  /** RegularExpression Id. */
  int FALSE = 28;
  /** RegularExpression Id. */
  int UNDEFINED = 29;
  /** RegularExpression Id. */
  int WS = 30;
  /** RegularExpression Id. */
  int NEWLINE = 31;
  /** RegularExpression Id. */
  int LPAREN = 32;
  /** RegularExpression Id. */
  int RPAREN = 33;
  /** RegularExpression Id. */
  int LBRACKET = 34;
  /** RegularExpression Id. */
  int RBRACKET = 35;
  /** RegularExpression Id. */
  int COLON = 36;
  /** RegularExpression Id. */
  int DOT = 37;
  /** RegularExpression Id. */
  int OP_LT = 38;
  /** RegularExpression Id. */
  int OP_LE = 39;
  /** RegularExpression Id. */
  int OP_GT = 40;
  /** RegularExpression Id. */
  int OP_GE = 41;
  /** RegularExpression Id. */
  int OP_EQ = 42;
  /** RegularExpression Id. */
  int OP_SET = 43;
  /** RegularExpression Id. */
  int OP_NE = 44;
  /** RegularExpression Id. */
  int OP_PLUS = 45;
  /** RegularExpression Id. */
  int OP_MINUS = 46;
  /** RegularExpression Id. */
  int OP_MULT = 47;
  /** RegularExpression Id. */
  int OP_DIV = 48;
  /** RegularExpression Id. */
  int OP_AND = 49;
  /** RegularExpression Id. */
  int OP_OR = 50;
  /** RegularExpression Id. */
  int OP_NOT = 51;
  /** RegularExpression Id. */
  int COMMA = 52;
  /** RegularExpression Id. */
  int SEMI = 53;
  /** RegularExpression Id. */
  int WORD = 54;
  /** RegularExpression Id. */
  int NUMBER = 55;
  /** RegularExpression Id. */
  int OTHER = 56;
  /** RegularExpression Id. */
  int QS_TEXT = 57;
  /** RegularExpression Id. */
  int SQS_TEXT = 58;

  /** Lexical state. */
  int SQS = 0;
  /** Lexical state. */
  int QS = 1;
  /** Lexical state. */
  int COMMENT = 2;
  /** Lexical state. */
  int WM = 3;
  /** Lexical state. */
  int DEFAULT = 4;

  /** Literal token values. */
  String[] tokenImage =
      { "<EOF>", "<_ALPHA_CHAR>", "<_NUM_CHAR>", "<_ALPHANUM_CHAR>", "<_IDENTIFIER_CHAR>",
          "<_IDENTIFIER>", "<_NEWLINE>", "<_WHITESPACE>", "<_QCHAR>", "<_RESTOFLINE>",
          "<_COMMENT>", "<STUFF>", "\"}\"", "\"#end\"", "\"#begin\"", "\"{\"",
          "<token of kind 16>", "\"##\"", "<RESTOFLINE>", "<COMMENT_ELSE>", "\"$\"", "<QCHAR>",
          "\"\\\\\"", "\"#\"", "\"\\\"\"", "\"\\\'\"", "\"null\"", "\"true\"", "\"false\"",
          "\"undefined\"", "<WS>", "<NEWLINE>", "\"(\"", "\")\"", "\"[\"", "\"]\"", "\":\"",
          "\".\"", "\"<\"", "\"<=\"", "\">\"", "\">=\"", "\"==\"", "\"=\"", "<OP_NE>", "\"+\"",
          "\"-\"", "\"*\"", "\"/\"", "<OP_AND>", "<OP_OR>", "<OP_NOT>", "\",\"", "\";\"", "<WORD>",
          "<NUMBER>", "<OTHER>", "<QS_TEXT>", "<SQS_TEXT>", };

}
