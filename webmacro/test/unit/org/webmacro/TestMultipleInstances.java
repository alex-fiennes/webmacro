package org.webmacro;

import java.io.*;

import org.webmacro.*;
import org.webmacro.engine.StringTemplate;
import org.webmacro.util.*;

import junit.framework.*;

/* Test various features of a default-configured Broker */
public class TestMultipleInstances extends TestCase {
  private WebMacro _defaultWM;
  private WebMacro _anotherWM;

  public TestMultipleInstances(String name) {
    super(name);
  }

  protected void setUp() {
      if (System.getProperties().getProperty("org.webmacro.LogLevel") == null)
        System.getProperties().setProperty("org.webmacro.LogLevel", "INFO");

    try {
         _defaultWM = new WM ();
     } catch (Exception e) {
       System.err.println ("Could not initialize Default WebMacro Instance");
     }

      try {
          _anotherWM = new WM ("org/webmacro/TestMulti_1.properties");
      } catch (Exception e) {
        System.err.println ("Could not initialize another WebMacro Instance using org/webmacro/TestMulti_1.properties!");
      }
  }

    public void testFooDirective () throws Exception {
        String template = "#foo";
        String output = null;
        Context context = null;

        // against the default configured WM isntance, this *should* throw
        // an exception
        try {
            if (_defaultWM != null)
                executeStringTemplate (_defaultWM, _defaultWM.getContext(), template);
        } catch (Exception e) {
            assert (e.toString().indexOf("No such directive #foo") > -1);
        }

        // against the other WM instance, this should return the string "foo"
        output = executeStringTemplate (_anotherWM, _anotherWM.getContext(), template);
        assert (output.equals ("foo"));
    }



  /** Execute a string as a template against the current context,
   *  and return the result. */
  public String executeStringTemplate(WebMacro wm, Context context, String templateText) throws Exception {
    Template template = new StringTemplate(wm.getBroker(), templateText);
    FastWriter fw = FastWriter.getInstance(wm.getBroker(), null, "UTF8");
    template.write (fw, context);
    String output = fw.toString();
    fw.close();
    return output;
  }
}


