package org.webmacro.template;

import org.webmacro.Context;
import org.webmacro.engine.DefaultEvaluationExceptionHandler;

public class TestSetblock extends TemplateTestCase
{

    public TestSetblock (String name)
    {
        super(name);
    }


    public void stuffContext (Context context) throws Exception
    {
        context.setEvaluationExceptionHandler(
                new DefaultEvaluationExceptionHandler());
    }


    public void testSetblock () throws Exception
    {
        String tmpl = "#setblock $x {Some Text}\n$x";
        assertStringTemplateEquals(tmpl, "Some Text");
    }


    public void testSetblockAsMacro () throws Exception
    {
        String tmpl = "#setblock as macro $mac {[$x]}\n#set $x='ABC'\n$mac";
        assertStringTemplateEquals(tmpl, "[ABC]");
    }
}
