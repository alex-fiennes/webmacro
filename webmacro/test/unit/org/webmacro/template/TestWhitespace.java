package org.webmacro.template;

import java.io.*;
import org.webmacro.*;
import org.webmacro.engine.StringTemplate;
import org.webmacro.engine.DefaultEvaluationExceptionHandler;

import junit.framework.*;

import org.apache.regexp.RE;


/**
 * tests whitespace.
 * most of these fail.  
 *
 * I believe all #directives and block tokens ({,},#begin,#end) should 
 * consume ALL whitespace to their right up to and including the first 
 * EOL character (\n or \r\n combo)
 */
public class TestWhitespace extends TemplateTestCase {

   public TestWhitespace (String name) {
      super (name);
   }

   public void stuffContext (Context context) throws Exception {
      context.setEvaluationExceptionHandler (
                new DefaultEvaluationExceptionHandler ());

      context.put ("Array", new String[] {"one", "two", "three"});
   }

   public void testWithIf () throws Exception {
      String tmpl = "#if (true) { pass } #else { fail }";
      assertStringTemplateEquals (tmpl, "pass ");

      tmpl = "#if (true) {  pass  } #else {  fail  }";
      assertStringTemplateEquals (tmpl, "pass  ");

      tmpl = "#if (true) {pass} #else {fail}";
      assertStringTemplateEquals (tmpl, "pass");
   }

   public void testWithElse () throws Exception {
      String tmpl = "#if (false) { fail } #else { pass }";
      assertStringTemplateEquals (tmpl, "pass ");
   } 
 
   public void testEatLeadingWSNL1 () throws Exception {
      String tmpl = "\n  \n#if (true) {WSNL1} #else {fail}";
      assertStringTemplateEquals (tmpl, "\nWSNL1");
   }
 
   public void testEatLeadingWSNL2 () throws Exception {
      String tmpl = "           \n#if (true) {WSNL2} #else {fail}";
      assertStringTemplateEquals (tmpl, "WSNL2");
   }
  
   public void testEatLeadingWSNL3 () throws Exception {
      String tmpl = "\n   #if (true) {WSNL3} #else {fail}";
      assertStringTemplateEquals (tmpl, "WSNL3");
   }
 
   public void testEatLeadingWSNL4 () throws Exception {
      String tmpl = "\n\n#if (true) {WSNL4} #else {fail}";
      assertStringTemplateEquals (tmpl, "\nWSNL4");
   }
 
   public void testEatLeadingWSNL5 () throws Exception {
      String tmpl = "\n\n   #if (true) {WSNL5} #else {fail}";
      assertStringTemplateEquals (tmpl, "\nWSNL5");
   }

   public void testWithForeach1 () throws Exception {
      String tmpl = "#foreach $a in $Array { $a }";
      assertStringTemplateEquals (tmpl, "one two three ");
   }

   public void testWithForeach2 () throws Exception {
      String tmpl = "#foreach $a in $Array { $a}";
      assertStringTemplateEquals (tmpl, "onetwothree");

      tmpl = "#foreach $a in $Array {\n$a}";
      assertStringTemplateEquals (tmpl, "onetwothree");
   } 

   public void testWithForeach3 () throws Exception {
      String tmpl = "#foreach $a in $Array {\n$a\n}\n";
      assertStringTemplateEquals (tmpl, "one\ntwo\nthree\n");
   }
}
