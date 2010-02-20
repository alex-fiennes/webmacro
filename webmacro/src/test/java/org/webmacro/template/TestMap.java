package org.webmacro.template;

import org.webmacro.Context;

import java.util.Map;

public class TestMap extends TemplateTestCase
{

    public TestMap (String name)
    {
        super(name);
    }

    public void stuffContext (Context context) throws Exception
    {
        _context = context;
    }

    public void testNoWhiteSpace () throws Exception
    {
        executeStringTemplate("#set $a={}");
    }

    public void testWhiteSpace () throws Exception
    {
        executeStringTemplate("#set $a={ }");
    }

    public void testLostOfWhiteSpace () throws Exception
    {
        executeStringTemplate("#set $a={                                           }");
    }

    public void testNewLine () throws Exception
    {
        executeStringTemplate("#set $a={\n}");
    }

    public void testWhiteSpaceNewLine () throws Exception
    {
        executeStringTemplate("#set $a={    \n    }");
    }

    public void testUniqueMaps() throws Exception {
        executeStringTemplate("#set $a={ }");
        executeStringTemplate("#set $b={ }");
        Map a = (Map) _context.get("a");
        Map b = (Map) _context.get("b");
        assertTrue(a != b);
    }

    public void testEmpty() throws Exception {
        executeStringTemplate("#set $a={ }");
        Map a = (Map) _context.get("a");
        assertTrue(a.size() == 0);
    }

    public void testEmpty2() throws Exception {
        executeStringTemplate("#set $a={ 'key':'value' }\n#set $b={}");
        Map b = (Map) _context.get("b");
        assertTrue(b.size() == 0);
    }

    public void testEmpty3() throws Exception {
        executeStringTemplate("#set $a={ 'key':'value' }\n#include as template 'org/webmacro/template/test_map.wm'\n#set $b={}");
        Map b = (Map) _context.get("b");
        Map map = (Map) _context.get("map");    // from #include'd file
        assertTrue(b.size() == 0);
        assertTrue(map.size() == 1);
    }



}
