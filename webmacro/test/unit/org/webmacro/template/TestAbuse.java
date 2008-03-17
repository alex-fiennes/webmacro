package org.webmacro.template;

import org.webmacro.Context;
import org.webmacro.engine.DefaultEvaluationExceptionHandler;


/**
 * The abuse test.  
 * Tries to do really mean things to the parser, 
 * and even expects the parser to do them correctly.
 */
public class TestAbuse extends TemplateTestCase
{

    public TestAbuse (String name)
    {
        super(name);
    }


    public void stuffContext (Context context) throws Exception
    {
        context.setEvaluationExceptionHandler(
                new DefaultEvaluationExceptionHandler());
        context.put("User", "Eric");
    }


    /** We'll start easy. */
    public void testEOLComment () throws Exception
    {
        assertStringTemplateEquals("## a comment", "");
    }


    /** This is easy too. */
    public void testBlockComment () throws Exception
    {
        assertStringTemplateEquals("#comment { this is a comment }", "");
    }


    /** Test a variable reference like: Welcome to the site, $User. */
    public void testTrailingDot () throws Exception
    {
        assertStringTemplateEquals("Hello, $User.",
                "Hello, Eric.");
    }


    /** EOL comments at end of directive blocks.  */
    public void testEOLCommentWithDirective () throws Exception
    {
        String tmpl = "#if (true) {  ## this is a comment\n"
                + "pass\n"
                + "} else { ## this is another comment\n"
                + "fail\n"
                + "}";

        assertStringTemplateMatches(tmpl, ".*pass.*");
    }


    /** WMScript that would fail if it weren't commented out */
    public void testBlockCommentWithBadWMScript () throws Exception
    {
        String tmpl = "#comment { #if ($true) }";
        assertStringTemplateMatches(tmpl, "");
    }


    /** Split an #if across multiple lines.
     *  FIXME Currently produces a parse error.
     */
    public void testMultiLineIf () throws Exception
    {
        System.err.println("NEED TO REVISIT: TestAbuse.testMultiLineIf()");
        assertTrue(true);
        // String tmpl = "#if (true\n&& true && true\n) {pass} #else {fail}";
        // assertStringTemplateEquals (tmpl, "pass");

    }


    /**
     * The error message this produces is: 
     *   Attempt to dereference null value $Object
     * but $Object <b>isn't</b> null.   Only its
     * toString() method returns null.  The message
     * implies that $Object is null. 
     *
     * Can this be detected and say something like:
     *     $Object's toString() method returned null.
     *
     * Probably requires another Exception type for EEH
     * and friends.
     */
    public void testToStringIsNull () throws Exception
    {
        Object obj = new Object()
        {
            public String toString ()
            {
                return null;
            }
        };

        _context.put("Object", obj);

        assertStringTemplateMatches("$Object",
                "<!--.*returns null.*-->");

    }


    /** The infamous "but I want to put Javascript in an #if block!!"

     FIXME Currently fails when using { and }, but passes with 
     #begin and #end.  Any reason why?
     */
    public void testBlockDirectiveWithJavascript () throws Exception
    {
        String jscript = "<script>\n"
                + "   if (true) {\n"
                + "      alert (\"Hello, World\");\n"
                + "   } else {\n"
                + "      alert (\"Goodbye, World\");\n"
                + "   }\n"
                + "</script>";
        String jscriptnl = "<script>\n"
                + "   if (true) {\n"
                + "      alert (\"Hello, World\");\n"
                + "   } else {\n"
                + "      alert (\"Goodbye, World\");\n"
                + "   }\n"
                + "</script>\n";

        // check no new line with #begin and #end
        String tmpl = "#if (true) #begin"
                + jscript
                + "#end";
        assertStringTemplateEquals(tmpl, jscript);
        // check newline with #begin and #end
        tmpl = "#if (true) #begin"
                + jscriptnl
                + "#end";
        assertStringTemplateEquals(tmpl, jscriptnl);
    }


    /**
     * Test that semi-colons are ignored.
     */
    public void testSemi () throws Exception
    {
        String assn = "#set $foo=\"blah\" ";
        assertStringTemplateEquals(assn + "$(foo);", "blah;");
        assertStringTemplateEquals(assn + "$(foo)", "blah");
        assertStringTemplateEquals(assn + "$foo", "blah");
        assertStringTemplateEquals(assn + "$foo;", "blah");
        assertStringTemplateEquals(assn + "$foo.substring(1)", "lah");
        assertStringTemplateEquals(assn + "$foo.substring(1);", "lah;");
    }


    /**
     * Test that a BuildException is thrown.
     */
    public void testNastyEndreTemplate () throws Exception
    {
        String tmpl = templateFileToString("org/webmacro/template/nasty_endre.wm");
        assertStringTemplateThrows(tmpl, org.webmacro.engine.BuildException.class);
    }
}

