package org.webmacro.template;

import org.webmacro.Context;
import org.webmacro.engine.DefaultEvaluationExceptionHandler;

public class TestUndefined extends TemplateTestCase
{

    public TestUndefined (String name)
    {
        super(name);
    }


    public void stuffContext (Context context) throws Exception
    {
        context.setEvaluationExceptionHandler(
                new DefaultEvaluationExceptionHandler());
    }


    public void testUndefined () throws Exception
    {
        String tmpl = "#if ($NotDefined == undefined) {true}";
        assertStringTemplateEquals(tmpl, "true");
    }


    public void testUndefinedWithSet () throws Exception
    {
        String tmpl = "#set $Foo=undefined\n#if ($Foo == undefined) {true}";
        assertStringTemplateEquals(tmpl, "true");
    }
}
