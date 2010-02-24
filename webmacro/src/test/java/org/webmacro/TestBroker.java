package org.webmacro;

import org.slf4j.Logger;

import junit.framework.TestCase;
import org.webmacro.util.Settings;

/* Test various features of a default-configured Broker */

public class TestBroker extends TestCase
{
    private WebMacro _wm;
    private Broker _broker;


    public TestBroker (String name)
    {
        super(name);
    }


    protected void setUp ()
    {
        try
        {
            if (System.getProperties().getProperty("org.webmacro.LogLevel") == null)
                System.getProperties().setProperty("org.webmacro.LogLevel", "ERROR");
            _wm = new WM();
        }
        catch (Exception e)
        {
            System.err.println("Could not initialize WebMacro!");
        }
        _broker = _wm.getBroker();
    }


    public void testBrokerLocal () throws Exception
    {
        _broker.setBrokerLocal("MyName", "Mud");

        assertTrue(_broker.getBrokerLocal("MyName")
                .toString().equals("Mud"));
    }


    public void testGetSettings () throws Exception
    {
        Settings s = _broker.getSettings();
        assertTrue(s != null);
        assertTrue(_broker.getSetting("ErrorTemplate")
                .equals("error.wm"));
    }


    public void testGetProvider () throws Exception
    {
        Provider p = _broker.getProvider("template");
        assertTrue(p != null);
    }


    public void testGetResource () throws Exception
    {
        java.net.URL url = _broker.getResource("WebMacro.defaults");
        assertTrue(url != null);
    }
}


