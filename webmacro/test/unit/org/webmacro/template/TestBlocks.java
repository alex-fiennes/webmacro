package org.webmacro.template;

import java.io.*;
import org.webmacro.*;
import org.webmacro.engine.StringTemplate;
import org.webmacro.engine.DefaultEvaluationExceptionHandler;

import junit.framework.*;

import org.apache.regexp.RE;


/**
 * the abuse test.  Tries to do really mean things to
 * the parser, and even expects the parser to do them 
 * correctly.
 */
public class TestBlocks extends TemplateTestCase {

   public TestBlocks (String name) {
      super (name);
   }

   public void stuffContext (Context context) throws Exception {
      context.setEvaluationExceptionHandler (
                new DefaultEvaluationExceptionHandler ());
   }


   public void testBraces () throws Exception {
      String tmpl = "#if (true) {pass} #else {fail}";
      assertStringTemplateEquals (tmpl, "pass");
   }
  
   public void testBeginEnd () throws Exception {
      String tmpl = "#if (true) #begin pass #end #else #begin fail #end";
      assertStringTemplateEquals (tmpl, "pass ");
   }

   public void testOnlyEnd () throws Exception {
      String tmpl = "#if (true) pass #end";
      assertStringTemplateEquals (tmpl, "pass ");
   }

   /**
    * until Brian adds support for #if #elseif #else #end
    * this will fail.
    *
    * The point of this is really to make sure the parser
    * doesn't throw exceptions, not to test the conditionals
    */
   public void testIfElseIfEnd () throws Exception {
      String tmpl = "#if ($foo)\n"
                  + "pass\n"
                  + "#elseif ($foo2)\n"
                  + "pass\n"
                  + "#else\n"
                  + "pass\n"
                  + "#end";


      _context.put ("foo", "foo");
      assertStringTemplateEquals (tmpl, "pass");
   }
   
}
