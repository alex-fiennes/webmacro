package org.webmacro;

import junit.framework.TestCase;
import org.webmacro.engine.StringTemplate;

import java.util.Properties;

/**
 * Ensure that multiple instances of WM can co-exist in the same JVM.
 */
public class TestMultipleInstances extends TestCase
{
    private WebMacro _instanceOne;
    private WebMacro _instanceTwo;


    public TestMultipleInstances (String name)
    {
        super(name);
    }


    protected void setUp () throws Exception
    {
        // we don't want to see any WebMacro output from this test
        if (System.getProperties().getProperty("org.webmacro.LogLevel") == null)
            System.getProperties().setProperty("org.webmacro.LogLevel", "NONE");

        try
        {
            _instanceOne = new WM();
        }
        catch (Exception e)
        {
            System.err.println("Could not initialize Default WebMacro Instance");
            throw e;
        }

        try
        {
            _instanceTwo = new WM("org/webmacro/TestMulti_1.properties");
        }
        catch (Exception e)
        {
            System.err.println("Could not initialize another WebMacro Instance using org/webmacro/TestMulti_1.properties!");
            throw e;
        }
    }


    public void testFooDirective () throws Exception
    {
        String template = " #foo ";
        String output = null;

        // against the default configured WM isntance, this *should* throw
        // an exception
        System.err.println("----------- instance 1 -----------------");
        try
        {
            if (_instanceOne != null)
                executeStringTemplate(_instanceOne, _instanceOne.getContext(), template);
        }
        catch (Exception e)
        {
            assertTrue(e.toString().indexOf("No such directive #foo") > -1);
        }

        // against the other WM instance, this should return the string "foo"
        System.err.println();
        System.err.println("----------- instance 2 -----------------");
        output = executeStringTemplate(_instanceTwo, _instanceTwo.getContext(), template);
        assertTrue(output.equals("foo"));
    }


    public void testDefaultConstructor () throws InitException
    {
        WM wm1 = new WM();
        WM wm2 = new WM();

        assertEquals(wm1.toString(), wm2.toString());
        assertEquals(wm1.getBroker(), wm2.getBroker());

        Properties p3 = new Properties();
        p3.put("foo", "goo");
        p3.put("moo", "zoo");

        Properties p4 = new Properties();
        p4.put("foo", "goo");
        p4.put("moo", "zoo");

        Properties p5 = new Properties();
        p5.put("foo", "goo");
        p5.put("moo", "zoo");
        p5.put("goo", "flu");

        WM wm3 = new WM(p3);
        WM wm4 = new WM(p4);
        WM wm5 = new WM(p5);
        assertTrue(wm1.getBroker() != wm3.getBroker());
        assertTrue(wm3.getBroker() == wm4.getBroker());
        assertTrue(wm3.getBroker() != wm5.getBroker());

        p3.put("oops", "loops");
        WM wm6 = new WM(p3);
        assertTrue(wm3.getBroker() != wm6.getBroker());
    }


    /** Execute a string as a template against the current context,
     *  and return the result. */
    public String executeStringTemplate (WebMacro wm, Context context, String templateText) throws Exception
    {
        Broker b = wm.getBroker();
        Template template = new StringTemplate(b, templateText);
        String output = template.evaluateAsString(context);
        return output;
    }
}