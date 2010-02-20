package org.webmacro.template;

import org.webmacro.Context;

public class TestQuote extends TemplateTestCase
{

    public TestQuote (String name)
    {
        super(name);
    }


    public void stuffContext (Context context) throws Exception
    {
    }


    public void testDq () throws Exception
    {
        assertStringTemplateEquals("#set $a=\"blah\" $a", "blah");
        assertStringTemplateEquals("#set $a=\"'blah'\" $a", "'blah'");
        assertStringTemplateEquals("#set $a=\"\" #if($a == \"\") {empty}",
                "empty");
    }


    public void testSq () throws Exception
    {
        assertStringTemplateEquals("#set $a='blah' $a", "blah");
        assertStringTemplateEquals("#set $a='\"blah\"' $a", "\"blah\"");
        assertStringTemplateEquals("#set $a='' #if($a == '') {empty}",
                "empty");
    }


    public void testMixed () throws Exception
    {
        assertStringTemplateEquals("#set $a=\"\" #if($a == '') {empty}",
                "empty");
    }


    public void testInline () throws Exception
    {
        assertStringTemplateEquals("blah'blah", "blah'blah");
        assertStringTemplateEquals("blah\"blah", "blah\"blah");
        assertStringTemplateEquals("blah'\"blah", "blah'\"blah");
    }

}
