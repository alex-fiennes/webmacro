package org.webmacro;

import java.io.*;

import org.webmacro.engine.StringTemplate;
import org.webmacro.engine.DefaultEvaluationExceptionHandler;
import org.webmacro.template.TemplateTestCase;

import junit.framework.*;

import org.apache.regexp.RE;

public class TestContext extends TemplateTestCase {

    public TestContext(String name) {
        super(name);
    }

    public void stuffContext(Context context) throws Exception {
        context.setEvaluationExceptionHandler(
                new DefaultEvaluationExceptionHandler());
    }




    public void testPutGet_NULL() throws Exception {
        _context.put ("Foo", null);
        Object o = _context.get("Foo");
        assert (_context.get("Foo") == null);
    }

    public void testPutGet_int() throws Exception {
        _context.put ("Foo", 123);
        Integer i = (Integer) _context.get ("Foo");
        assert (i.intValue() == 123);
    }

    public void testPutGet_boolean() throws Exception {
        _context.put ("Foo", true);
        Boolean b = (Boolean) _context.get ("Foo");
        assert (b.booleanValue() == true);
    }


    public void testPutGet_byte() throws Exception {
        _context.put ("Foo", (byte)123);
        Byte b = (Byte) _context.get ("Foo");
        assert (b.byteValue() == (byte) 123);
    }

    public void testPutGet_char() throws Exception {
        _context.put ("Foo", (char)123);
        Character c = (Character) _context.get ("Foo");
        assert (c.charValue() == (char)123);
    }

    public void testPutGet_Class() throws Exception {
        _context.put ("Foo", this.getClass());
        Object o = _context.get ("Foo");
        assert (o instanceof org.webmacro.engine.StaticClassWrapper);
    }

    public void testPutGet_double() throws Exception {
        _context.put ("Foo", 123.4D);
        Double d= (Double) _context.get ("Foo");
        assert (d.doubleValue() == 123.4D);
    }

    public void testPutGet_long() throws Exception {
        _context.put ("Foo", 123L);
        Long l = (Long) _context.get ("Foo");
        assert (l.longValue() == 123L);
    }

    public void testPutGet_short() throws Exception {
        _context.put ("Foo", (short) 123);
        Short s = (Short) _context.get ("Foo");
        assert (s.shortValue() == (short) 123);
    }








}
