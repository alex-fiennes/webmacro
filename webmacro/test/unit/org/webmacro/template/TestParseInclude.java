package org.webmacro.template;

import org.webmacro.Context;
import org.webmacro.engine.DefaultEvaluationExceptionHandler;


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

    public TestParseInclude(String name) {
        super(name);
    }

    public void stuffContext(Context context) throws Exception {
        context.setEvaluationExceptionHandler(
                new DefaultEvaluationExceptionHandler());

        context.put("TemplateName", "org/webmacro/template/test_parse.wm");
    }

    public void testParse() throws Exception {
        // parse in "test_parse.wm"
        //   contains only the word "pass"
        assertStringTemplateMatches("#parse \"org/webmacro/template/test_parse.wm\"",
                "pass");

        assertStringTemplateMatches("#parse \"$TemplateName\"",
                "pass");

        // parse the same template using a variable
        assertStringTemplateMatches("#parse \"$TemplateName\"",
                "pass");
    }

    /** test a template that is greater than 2k in size */
    public void testBigTemplate() throws Exception {
        // read in a big template
        // and trim off whitespace to make matching easier
        String tmpl = templateFileToString("org/webmacro/template/big_template.wm");
        tmpl = tmpl.trim();

        assertStringTemplateMatches(tmpl, "pass$");
    }

    public void testInclude() throws Exception {
        // include as a template
        assertStringTemplateMatches("#include as template \"org/webmacro/template/test_parse.wm\"",
                "pass");

        // include as text
        assertStringTemplateMatches("#include as text \"org/webmacro/template/test_parse.wm\"",
                "pass");

        // include as <something dynamic>
        assertStringTemplateMatches("#include \"org/webmacro/template/test_parse.wm\"",
                "pass");


        // include /etc/password
        assertStringTemplateMatches("#include \"/etc/passwd\"",
                ".*root:.*");

        // include http://www.yahoo.com/
        assertStringTemplateMatches("#include \"http://www.webmacro.org\"",
                ".*WebMacro.*");

    }

    public void testIncludeAsMacro() throws Exception {
        assertStringTemplateMatches("#include as macro \"org/webmacro/template/macros.wm\"\n"
                            + "#foo1()", "foo1");

        assertStringTemplateMatches("#include as macro \"org/webmacro/template/macros.wm\"\n"
                            + "$a", "\\$a was found");

    }
    
    public void testBasicMacros() throws Exception {
        // now execute each one:
        assertStringTemplateMatches( "#include as macro \"org/webmacro/template/macrosandbox/all.wmm\"\n"
                     + "#hello(\"Brian Goetz & Eric Ridge\")", "Hello, Brian Goetz & Eric Ridge");

        assertStringTemplateMatches( "#include as macro \"org/webmacro/template/macrosandbox/all.wmm\"\n"
                     + "#showVar($nullVar)", "");

        assertStringTemplateMatches( "#include as macro \"org/webmacro/template/macrosandbox/all.wmm\"\n"
                     + "#setVar($foo, \"brian\")\n"
                     + "#showVar($foo)", "brian");
    }
    
    /** executes out of the standard macro/ distribution. */
    public void testEcommMacros() throws Exception {
      // execute the pay pal eCommerce test case and store the output
      String value = executeFileTemplate("org/webmacro/template/ecomm.wm");
      store(System.getProperty("user.dir") + "/" + "ecomm.html", value);
   }
        

    /** executes out of the standard macro/ distribution: html. */
    public void testHrefMacro() throws Exception {
      // execute the pay pal eCommerce test case and store the output
      String value = executeFileTemplate("org/webmacro/template/href.wm");
      store(System.getProperty("user.dir") + "/" + "windows.html", value);
   }
        
    /** executes out of the standard macro/ distribution: html. */
    public void testFrameMacro() throws Exception {
      // execute the pay pal eCommerce test case and store the output
      String value = executeFileTemplate("org/webmacro/template/frame.wm");
      store(System.getProperty("user.dir") + "/" + "winFrame.html", value);
   }
        
    /** executes out of the standard macro/ distribution: html. */
    public void testControlsMacro() throws Exception {
      // execute the pay pal eCommerce test case and store the output
      String value = executeFileTemplate("org/webmacro/template/controlsTest.wm");
      store(System.getProperty("user.dir") + "/" + "controls.html", value);
   }
        
        

    public static void main (String[] args) throws Exception {
        TestParseInclude t = new TestParseInclude("test");
        t.setUp();
        t.testIncludeAsMacro();
    }
}
