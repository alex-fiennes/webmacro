package org.webmacro.template;

import org.webmacro.Context;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class TestAlternate extends TemplateTestCase
{

    public static class Dumper
    {
        public String print (Object[] objects)
        {
            String s = "";
            for (int i = 0; i < objects.length; i++)
                s += objects[i].toString() + " ";
            return s;
        }
    }

    public static Dumper d = new Dumper();


    public TestAlternate (String name)
    {
        super(name);
    }


    public void stuffContext (Context context) throws Exception
    {
        final List list = new ArrayList();
        list.add("a");
        list.add("b");
        list.add("c");

        final Object[] array = list.toArray();

        context.put("List", list);
        context.put("Array", array);
        context.put("Iterator", list.iterator());
        context.put("Enumeration", new Enumeration()
        {
            int idx = 0;


            public boolean hasMoreElements ()
            {
                return idx < array.length;
            }


            public Object nextElement ()
            {
                return array[idx++];
            }
        });

    }


    public void testList () throws Exception
    {
        String template = "#alternate $i through $List\n$i$i$i$i";
        assertStringTemplateEquals(template, "abca");
    }


    public void testIterator () throws Exception
    {
        String template = "#alternate $i through $Iterator\n$i$i$i$i";
        assertStringTemplateEquals(template, "abca");
    }


    public void testArray () throws Exception
    {
        String template = "#alternate $i through $Array\n$i$i$i$i";
        assertStringTemplateEquals(template, "abca");
    }


    public void testEnumeration () throws Exception
    {
        String template = "#alternate $i through $Enumeration\n$i$i$i$i";
        assertStringTemplateEquals(template, "abca");
    }


    public void testInlineArray () throws Exception
    {
        String template = "#alternate $i through [\"red\",\"blue\",\"green\"]\n$i$i$i$i";
        assertStringTemplateEquals(template, "redbluegreenred");
    }
}
