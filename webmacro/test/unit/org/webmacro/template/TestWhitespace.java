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
   }

   public void testWithElse () throws Exception {
      String tmpl = "#if (false) { fail } #else { pass }";
      assertStringTemplateEquals (tmpl, "pass ");
   } 

   public void testWithForeach1 () throws Exception {
      String tmpl = "#foreach $a in $Array { $a }";
      assertStringTemplateEquals (tmpl, "one two three ");
   }

   public void testWithForeach2 () throws Exception {
      String tmpl = "#foreach $a in $Array { $a}";
      assertStringTemplateEquals (tmpl, "onetwothree");
   } 

   public void testWithForeach3 () throws Exception {
      String tmpl = "#foreach $a in $Array {\n$a\n}\n";
      assertStringTemplateEquals (tmpl, "one\ntwo\three\n");
   }
}
