package org.webmacro.template;

import java.io.*;
import org.webmacro.*;
import org.webmacro.engine.StringTemplate;
import org.webmacro.engine.DefaultEvaluationExceptionHandler;

import junit.framework.*;

import org.apache.regexp.RE;


/**
 * test that templates can be #parse'd and files (and URL's)
 * can be #include'd
 *
 * The file /etc/password should exist on your
 * filesystem and contain "root:" somewhere in it
 * 
 * And your computer will need access to http://www.webmacro.org/
 */
public class TestParseInclude extends TemplateTestCase {

   public TestParseInclude (String name) {
      super (name);
   }

   public void stuffContext (Context context) throws Exception {
      context.setEvaluationExceptionHandler (
                new DefaultEvaluationExceptionHandler ());

      context.put ("TemplateName", "org/webmacro/template/test_parse.wm");
   }

   public void testParse () throws Exception {
      // parse in "test_parse.wm"
      //   contains only the word "pass"
      assertStringTemplateMatches ("#parse \"org/webmacro/template/test_parse.wm\"", 
                                  "pass"); 

      assertStringTemplateMatches ("#parse \"$TemplateName\"", 
                                  "pass"); 

      // parse the same template using a variable
      assertStringTemplateMatches ("#parse \"$TemplateName\"",
                                 "pass");
   }

   /** test a template that is greater than 2k in size */
   public void testBigTemplate () throws Exception {
      // read in a big template 
      // and trim off whitespace to make matching easier
      String tmpl = templateFileToString ("org/webmacro/template/big_template.wm");
      tmpl = tmpl.trim ();

      assertStringTemplateMatches (tmpl, "pass$");
   }

   public void testInclude () throws Exception {
      // include /etc/password
      assertStringTemplateMatches ("#include \"/etc/passwd\"", 
                                   ".*root:.*");

      // include http://www.yahoo.com/
      assertStringTemplateMatches ("#include \"http://www.webmacro.org\"",
                                   ".*WebMacro.*");

   }

}
