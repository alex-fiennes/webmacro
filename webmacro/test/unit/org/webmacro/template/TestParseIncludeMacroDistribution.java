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
 * And your computer will need access to http://www.amazon.com/
 */
public class TestParseIncludeMacroDistribution extends TemplateTestCase
{

    public TestParseIncludeMacroDistribution (String name)
    {
        super(name);
    }


    public void stuffContext (Context context) throws Exception
    {
        context.setEvaluationExceptionHandler(
                new DefaultEvaluationExceptionHandler());

        context.put("TemplateName", "org/webmacro/template/test_parse.wm");
    }


    /** executes out of the standard macro/ distribution. */
    public void testEcommMacros () throws Exception
    {
        // execute the pay pal eCommerce test case and store the output
        String value = executeFileTemplate("org/webmacro/template/ecomm.wm");
        store(System.getProperty("user.dir") + "/" + "ecomm.html", value);
    }

    /** executes out of the standard macro/ distribution. */
    public void testVerisignMacros () throws Exception
    {
        // execute the pay pal eCommerce test case and store the output
        String value = executeFileTemplate("org/webmacro/template/verisign.wm");
        store(System.getProperty("user.dir") + "/" + "verisign.html", value);
    }


    /** executes out of the standard macro/ distribution: html. */
    public void testHrefMacro () throws Exception
    {
        // execute the pay pal eCommerce test case and store the output
        String value = executeFileTemplate("org/webmacro/template/href.wm");
        store(System.getProperty("user.dir") + "/" + "windows.html", value);
    }


    /** executes out of the standard macro/ distribution: html. */
    public void testFrameMacro () throws Exception
    {
        // execute the pay pal eCommerce test case and store the output
        String value = executeFileTemplate("org/webmacro/template/frame.wm");
        store(System.getProperty("user.dir") + "/" + "winFrame.html", value);
    }


    /** executes out of the standard macro/ distribution: html. */
    public void testControlsMacro () throws Exception
    {
        // execute the pay pal eCommerce test case and store the output
        String value = executeFileTemplate("org/webmacro/template/controlsTest.wm");
        store(System.getProperty("user.dir") + "/" + "controls.html", value);
    }


}
