package org.webmacro;

import junit.framework.TestCase;
import org.webmacro.engine.StringTemplate;

/**
 * Ensure that multiple instances of WM can co-exist in the same JVM.
 */
public class TestMultipleInstances extends TestCase {
    private WebMacro _instanceOne;
    private WebMacro _instanceTwo;

    public TestMultipleInstances(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        // we don't want to see any WebMacro output from this test
        if (System.getProperties().getProperty("org.webmacro.LogLevel") == null)
            System.getProperties().setProperty("org.webmacro.LogLevel", "NONE");

        try {
            _instanceOne = new WM();
        } catch (Exception e) {
            System.err.println("Could not initialize Default WebMacro Instance");
            throw e;
        }

        try {
            _instanceTwo = new WM("org/webmacro/TestMulti_1.properties");
        } catch (Exception e) {
            System.err.println("Could not initialize another WebMacro Instance using org/webmacro/TestMulti_1.properties!");
            throw e;
        }
    }

    public void testFooDirective() throws Exception {
        String template = " #foo ";
        String output = null;
        Context context = null;

        // against the default configured WM isntance, this *should* throw
        // an exception
        System.err.println ("----------- instance 1 -----------------");
        try {
            if (_instanceOne != null)
                executeStringTemplate(_instanceOne, _instanceOne.getContext(), template);
        } catch (Exception e) {
            assert(e.toString().indexOf("No such directive #foo") > -1);
        }

        // against the other WM instance, this should return the string "foo"
        System.err.println ();
        System.err.println ("----------- instance 2 -----------------");
        output = executeStringTemplate(_instanceTwo, _instanceTwo.getContext(), template);
        assert(output.equals("foo"));
    }


    /** Execute a string as a template against the current context,
     *  and return the result. */
    public String executeStringTemplate(WebMacro wm, Context context, String templateText) throws Exception {
        Broker b = wm.getBroker();
        Template template = new StringTemplate(b, templateText);
        FastWriter fw = FastWriter.getInstance(b, null, "UTF8");
        template.write(fw, context);
        String output = fw.toString();
        fw.close();
        return output;
    }
}