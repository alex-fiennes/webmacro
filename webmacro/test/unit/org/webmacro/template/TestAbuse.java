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
public class TestAbuse extends TemplateTestCase {

   public TestAbuse (String name) {
      super (name);
   }

   public void stuffContext (Context context) throws Exception {
      context.setEvaluationExceptionHandler (
                new DefaultEvaluationExceptionHandler ());
      context.put ("User", "Eric");
   }

   /** We'll start easy */
   public void testEOLComment () throws Exception {
      assertStringTemplateEquals ("## a comment", "");
   }

   /** this is easy too */
   public void testBlockComment () throws Exception {
      assertStringTemplateEquals ("#comment { this is a comment }", "");
   }

   /** test a variable reference like: Welcome to the site, $User. */
   public void testTrailingDot () throws Exception {
      assertStringTemplateEquals ("Hello, $User.",
                                  "Hello, Eric.");
   }

   /** EOL comments at end of directive blocks.  */
   public void testEOLCommentWithDirective () throws Exception {
      String tmpl = "#if (true) {  ## this is a comment\n" 
                    + "pass\n"
                    + "} else { ## this is another comment\n"
                    + "fail\n" 
                    + "}";

      assertStringTemplateMatches (tmpl, ".*pass.*");
   }

   /** WMScript that would fail if it weren't commented out */
   public void testBlockCommentWithBadWMScript () throws Exception {
      String tmpl = "#comment { #if ($true) }";
      assertStringTemplateMatches (tmpl, "");
   }

   /** the infamous "but I want to put Javascript in an #if block!!" 

       Currently fails when using { and }, but passes with 
       #begin and #end.  Any reason why?
   */
   public void testBlockDirectiveWithJavascript () throws Exception {
      String jscript = "<script>\n"
                     + "   if (true) {\n"
                     + "      alert (\"Hello, World\");\n"
                     + "   } else {\n"
                     + "      alert (\"Goodbye, World\");\n"
                     + "   }\n"
                     + "</script>\n";

      // check it with #begin and #end
      String tmpl = "#if (true) #begin"
                  + jscript  
                  + "#end";
      assertStringTemplateEquals (tmpl, jscript);
        
      System.out.println ("\t-->Brian, look @ TestAbuse.java:76<--"); 
/*      
      // check it with { and }
      // NOTE:  THIS FAILS!
      tmpl = "#if (true) {"
           + jscript
           + "}";

      assertStringTemplateEquals (tmpl, jscript);
*/
   }
}
