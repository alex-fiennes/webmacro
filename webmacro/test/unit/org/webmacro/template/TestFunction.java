package org.webmacro.template;

import org.webmacro.Context;

public class TestFunction extends TemplateTestCase
{

    public TestFunction (String name)
    {
        super(name);
    }


    public int modFunc (int num, int modulus)
    {
        return num % modulus;
    }


    public void voidFunc1 ()
    {
    }


    public void voidFunc2 (String dummyArg)
    {
        if (dummyArg == null) throw new RuntimeException();
    }


    public Object nullFunc ()
    {
        return null;
    }


    public void stuffContext (Context context) throws Exception
    {
        context.setEvaluationExceptionHandler(
                new org.webmacro.engine.CrankyEvaluationExceptionHandler());
        context.putFunction("toList", java.util.Arrays.class, "asList");
        context.putGlobalFunction("escape", org.webmacro.util.HTMLEscaper.class, "escape");
        context.putGlobalFunction("mod", this, "modFunc");
        String abcs = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        context.putFunction("mySubstr", abcs, "substring");
        context.putFunction("voidFunc1", this, "voidFunc1");
        context.putFunction("voidFunc2", this, "voidFunc2");
        context.putFunction("nullFunc", this, "nullFunc");
    }


    public void testFunctionToList () throws Exception
    {
        assertStringTemplateEquals("#set $a=$toList([1,2,3]) $a.get(2)", "3");
    }


    public void testGlobalFunctionEscape () throws Exception
    {
        assertStringTemplateEquals("$escape(\"<big & bad>\")", "&lt;big &amp; bad&gt;");
    }


    public void testModulusFunction () throws Exception
    {
        assertStringTemplateEquals("$mod(4, 3)", "1");
    }


    public void testSubstringFunction () throws Exception
    {
        assertStringTemplateEquals("$mySubstr(24)", "YZ");
        assertStringTemplateEquals("$mySubstr(0,3)", "ABC");
    }


    public void testVoidFunction () throws Exception
    {
        assertStringTemplateEquals("$voidFunc1()", "");
        assertStringTemplateEquals("$voidFunc2('bogus arg')", "");
        assertStringTemplateEquals("#if ($nullFunc()==null){true} #else {false}", "true");
        assertStringTemplateThrows("$nullFunc()", org.webmacro.PropertyException.NullValueException.class);
    }
}
