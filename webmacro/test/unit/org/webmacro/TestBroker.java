package org.webmacro;

import java.io.*;

import org.webmacro.*;
import org.webmacro.util.*;

import junit.framework.*;

/* Test various features of a default-configured Broker */
public class TestBroker extends TestCase {
  private WebMacro _wm;
  private Broker _broker;

  public TestBroker(String name) {
    super(name);
  }

  protected void setUp() {
     try {
       if (System.getProperties().getProperty("org.webmacro.LogLevel") == null)
         System.getProperties().setProperty("org.webmacro.LogLevel", "ERROR");
       _wm = new WM ();
     } catch (Exception e) {
       System.err.println ("Could not initialize WebMacro!");
     }
     _broker = _wm.getBroker ();
  }
  
  public void testBrokerLocal () throws Exception {
     _broker.setBrokerLocal ("MyName", "Mud");

     assert (_broker.getBrokerLocal ("MyName")
                    .toString().equals ("Mud"));
  }

  public void testGetSettings() throws Exception {
     Settings s = _broker.getSettings();
     assert (s != null);
     assert (_broker.getSetting("ErrorTemplate")
                    .equals ("error.wm"));
  }

  public void testGetProvider() throws Exception {
     Provider p = _broker.getProvider ("template");
     assert (p != null);
  }

  public void testGetLog() throws Exception {
     Log l = _broker.getLog ("template");
     assert (l != null);

     l = _broker.getLog ("NewLog");
     assert (l != null);
  }

  public void testGetResource() throws Exception {
     java.net.URL url = _broker.getResource ("WebMacro.defaults");
     assert (url != null);
  }
}


