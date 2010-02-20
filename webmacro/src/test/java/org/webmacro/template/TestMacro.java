package org.webmacro.template;

import org.webmacro.Context;
import org.webmacro.engine.BuildException;

public class TestMacro extends TemplateTestCase
{

    public TestMacro (String name)
    {
        super(name);
    }


    public void stuffContext (Context context) throws Exception
    {
        context.put("int", 3);
        context.put("string", "Foo!");
    }


    public void testUndefinedMacro () throws Exception
    {
        boolean relax = _wm.getBroker().getBooleanSetting("RelaxedDirectiveBuilding");
        if (!relax)
        {
            assertStringTemplateThrows("#foo()", BuildException.class, "#foo: no such [M,m]acro or [D,d]irective at \\w+:\\d+\\.\\d+");
            assertStringTemplateThrows("#foo", BuildException.class, "#foo: no such [M,m]acro or [D,d]irective at \\w+:\\d+\\.\\d+");
        }
    }


    public void testNoArgs () throws Exception
    {
        assertStringTemplateEquals("#macro foo {1} #foo", "1");
        assertStringTemplateEquals("#macro foo {crap} #foo", "crap");
        assertStringTemplateEquals("#const $a=3 $a", "3");
    }


    public void testBadArgs () throws Exception
    {
        assertStringTemplateThrows("#macro foo() {} #foo(1)",
                org.webmacro.engine.BuildException.class,
                "invoked");
        assertStringTemplateThrows("#macro foo {} #foo(1)",
                org.webmacro.engine.BuildException.class,
                "invoked");
        assertStringTemplateThrows("#macro foo($a) {} #foo(1, 2)",
                org.webmacro.engine.BuildException.class,
                "invoked");
        assertStringTemplateThrows("#macro foo($a) {} #foo()",
                org.webmacro.engine.BuildException.class,
                "invoked");
        assertStringTemplateThrows("#macro foo($a) {} #foo",
                org.webmacro.engine.BuildException.class,
                "invoked");
    }


    public void testSetVar () throws Exception
    {
        assertStringTemplateEquals("#macro foo {#set $b=1} #foo $b", "1");
        assertStringTemplateEquals("#macro foo($a) {#set $b=$a} #foo(1) $b", " 1");
        assertStringTemplateEquals("#macro foo($a) {#set $b=$a} #set $c=1 #foo($c) $b", " 1");
        assertStringTemplateEquals("#macro foo($a) {#set $b=$a} #set $b=3 #foo(1) $b", " 1");
        assertStringTemplateEquals("#macro foo($b) {#set $b=3} #set $c=1 #foo($c) $c", " 3");
    }


    public void testNoCapture () throws Exception
    {
        assertStringTemplateEquals("#macro goo {$a} #macro foo($a) {#goo} #set $a=1 #foo(2)", "1");
        assertStringTemplateEquals("#macro goo($a) {#set $c=3 $a} #set $c=1 #goo(\"$c\")", "3");
    }

}
