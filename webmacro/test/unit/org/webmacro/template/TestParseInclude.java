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
 * And your computer will need access to http://www.yahoo.com/
 */
public class TestParseInclude extends TemplateTestCase {

   public TestParseInclude (String name) {
      super (name);
   }

   public void stuffContext (Context context) throws Exception {
      context.setEvaluationExceptionHandler (
                new DefaultEvaluationExceptionHandler ());

      context.put ("TemplateName", "test_parse.wm");
   }

   public void testParse () throws Exception {
      // parse in "test_parse.wm"
      //   contains only the word "pass"
      assertStringTemplateMatches ("#parse \"test_parse.wm\"", 
                                  "pass"); 

      // parse the same template using a variable
      assertStringTemplateMatches ("#parse \"$TemplateName\"",
                                 "pass");
   }

   public void testInclude () throws Exception {
      // include /etc/password
      assertStringTemplateMatches ("#include \"/etc/passwd\"", 
                                   ".*root:.*");

      // include http://www.yahoo.com/
      assertStringTemplateMatches ("#include \"http://www.yahoo.com\"",
                                   ".*Yahoo\\!.*");

   }

}
