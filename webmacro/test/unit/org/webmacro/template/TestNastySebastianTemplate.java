package org.webmacro.template;

import junit.framework.*;
import org.webmacro.*;

public class TestNastySebastianTemplate extends TemplateTestCase {

    public TestNastySebastianTemplate (String name) {
       super (name);
    }

    public void stuffContext (Context context) {
       context.put ("location", Boolean.FALSE);
    }

    public void testIt () throws Exception {
      assertTemplateEquals ("org/webmacro/template/nasty_sebastian.wm", "HEADFOOT");
    }
}
