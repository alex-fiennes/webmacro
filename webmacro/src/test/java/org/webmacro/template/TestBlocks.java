package org.webmacro.template;

import org.webmacro.Context;
import org.webmacro.engine.DefaultEvaluationExceptionHandler;


/**
 * the block test.  makes sure WM can parse the supported
 * block sytles correctly.
 */
public class TestBlocks extends TemplateTestCase
{

    public TestBlocks (String name)
    {
        super(name);
    }


    public void stuffContext (Context context) throws Exception
    {
        context.setEvaluationExceptionHandler(
                new DefaultEvaluationExceptionHandler());
    }


    public void testBraces () throws Exception
    {
        String tmpl = "#if (true) {pass} #else {fail}";
        assertStringTemplateEquals(tmpl, "pass");
    }


    public void testBeginEnd () throws Exception
    {
        String tmpl = "#if (true) #begin pass #end #else #begin fail #end";
        assertStringTemplateEquals(tmpl, "pass");

        tmpl = "#if (true)\n #begin pass\n #end\n #else\n #begin fail\n #end";
        assertStringTemplateEquals(tmpl, "pass\n");

        tmpl = "#if (true)\n { pass\n }\n #else\n { fail\n }";
        assertStringTemplateEquals(tmpl, " pass\n ");
    }


    public void testOnlyEnd () throws Exception
    {
        String tmpl = "#if (true) pass #end";
        assertStringTemplateEquals(tmpl, "pass");
    }


    public void testIfElseIfEnd () throws Exception
    {
        String tmpl = "#if ($foo)\n"
                + "pass\n"
                + "#elseif ($foo2)\n"
                + "fail\n"
                + "#else\n"
                + "fail\n"
                + "#end";


        _context.put("foo", "foo");
        assertStringTemplateEquals(tmpl, "pass\n");
    }


    public void testIfEnd () throws Exception
    {
        String tmpl = "#if ($foo)\n"
                + "pass\n"
                + "#end";


        _context.put("foo", "foo");
        assertStringTemplateEquals(tmpl, "pass\n");
    }


    public void testNestedIfElseEnd () throws Exception
    {
        String tmpl = "#if (true)\n"
                + "   #if (false)\n"
                + "      should not be here 1\n"
                + "   #else\n"
                + "      #if (true)\n"
                + "         you should see this\n"
                + "      #else\n"
                + "         should not be here 2\n"
                + "      #end\n"
                + "   #end\n"
//                  + " putting anything other than whitespace here makes it work\n"
                + "#else\n"
                + "   should not be here 3\n"
                + "#end\n";

        String output = executeStringTemplate(tmpl);
        assertTrue(output.indexOf("should not be here") == -1);
    }


    public void testNoEND1 () throws Exception
    {
        String tmpl = "#if(true) pass";
        assertStringTemplateThrows(tmpl, org.webmacro.engine.BuildException.class);
    }


    public void testNoEND2 () throws Exception
    {
        String tmpl = "#if(true)#begin pass";
        assertStringTemplateThrows(tmpl, org.webmacro.engine.BuildException.class);
    }


    public void testNoLBRACE () throws Exception
    {
        String tmpl = "#if(true){ pass";
        assertStringTemplateThrows(tmpl, org.webmacro.engine.BuildException.class);
    }
}
