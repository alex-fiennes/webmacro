package org.webmacro.util;

import java.io.*;

public class SimpleHTMLJava12SyntaxHighlighter 
    extends  BaseJava12SyntaxHighlighter
{
    public SimpleHTMLJava12SyntaxHighlighter(InputStream is) {
        super(is);
    }
    
    public static void main (String [] args) throws Exception {
        SimpleHTMLJava12SyntaxHighlighter parser;
        String filename = null;
        InputStream inputstream = null;
        if (args.length == 0)
        {
            inputstream = System.in;
        } else if (args.length == 1)
        {
            filename = args[0];
            inputstream = new java.io.FileInputStream(filename);
        } else
        {
            return;
        }
        parser = new SimpleHTMLJava12SyntaxHighlighter(inputstream);

        System.out.println("<pre>");
        System.out.println(parser.walk());
        System.out.println("</pre>");
    }

    protected void printToken(Token t) {
        if (t==null) {
            return;
        }
        switch (t.kind) {
          case EOF:
            break;
          case SINGLE_LINE_COMMENT:
          case FORMAL_COMMENT:
          case MULTI_LINE_COMMENT :
            out.write("<kbd class=javacomment>"+t.image+"</kbd>");
//            out.write("["+t.kind+":"+t.image+"]");

            break;

          case ABSTRACT:
          case BREAK:
          case CASE :
          case CATCH :
          case CLASS :
          case CONST :
          case CONTINUE :
          case _DEFAULT :
          case DO :
          case ELSE :
          case EXTENDS :
          case FALSE :
          case FINAL :
          case FINALLY :
          case FOR :
          case GOTO :
          case IF :
          case IMPLEMENTS :
          case IMPORT :
          case INSTANCEOF :
          case INTERFACE :
          case NATIVE :
          case NEW :
          case NULL :
          case PACKAGE :
          case PRIVATE :
          case PROTECTED :
          case PUBLIC :
          case RETURN :
          case STATIC :
          case SUPER :
          case SWITCH :
          case SYNCHRONIZED :
          case THIS :
          case THROW :
          case THROWS :
          case TRANSIENT :
          case TRUE :
          case TRY :
          case VOID :
          case VOLATILE :
          case WHILE :
            out.write("<kbd class=javakeyword>"+t.image+"</kbd>");
            break;
          case FLOAT :
          case INT :
          case LONG :
          case SHORT :
          case BOOLEAN:
          case CHAR :
          case BYTE :
          case DOUBLE :
            out.write("<kbd class=javatype>"+t.image+"</kbd>");
            break;

          case IDENTIFIER :
            out.write(t.image);
            break;
          case CHARACTER_LITERAL :
          case STRING_LITERAL :
            out.write("<kbd class=javastring>"+t.image+"</kbd>");
            break;
          case STRICTFP :
          case INTEGER_LITERAL :
          case DECIMAL_LITERAL :
          case HEX_LITERAL :
          case OCTAL_LITERAL :
          case FLOATING_POINT_LITERAL :
          case EXPONENT :
          case LETTER :
          case DIGIT :
          case LPAREN :
          case RPAREN :
          case LBRACE :
          case RBRACE :
          case LBRACKET :
          case RBRACKET :
          case SEMICOLON :
          case COMMA :
          case DOT :
          case ASSIGN :
          case GT :
          case LT :
          case BANG :
          case TILDE :
          case HOOK :
          case COLON :
          case EQ :
          case LE :
          case GE :
          case NE :
          case SC_OR :
          case SC_AND :
          case INCR :
          case DECR :
          case PLUS :
          case MINUS :
          case STAR :
          case SLASH :
          case BIT_AND :
          case BIT_OR :
          case XOR :
          case REM :
          case LSHIFT :
          case RSIGNEDSHIFT :
          case RUNSIGNEDSHIFT :
          case PLUSASSIGN :
          case MINUSASSIGN :
          case STARASSIGN :
          case SLASHASSIGN :
          case ANDASSIGN :
          case ORASSIGN :
          case XORASSIGN :
          case REMASSIGN :
          case LSHIFTASSIGN :
          case RSIGNEDSHIFTASSIGN :
          case RUNSIGNEDSHIFTASSIGN :
          case IN_SINGLE_LINE_COMMENT :
          case IN_FORMAL_COMMENT :
          case IN_MULTI_LINE_COMMENT :
            out.write(t.image);
            break;
          default:
            out.write("UNKNOWN: "+t.kind);
        }
    }



}
