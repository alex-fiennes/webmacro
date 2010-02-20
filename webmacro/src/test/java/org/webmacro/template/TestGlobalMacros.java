package org.webmacro.template;

import org.webmacro.Context;
import org.webmacro.WebMacro;
import org.webmacro.WM;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: skanthak
 * Date: Jul 21, 2004
 * Time: 12:54:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestGlobalMacros extends TemplateTestCase {
    public TestGlobalMacros(String name) {
        super(name);
    }

    protected WebMacro createWebMacro() throws Exception {
        Properties props = new Properties();
        props.put("Macros.Include","org/webmacro/template/globalMacros.wmm");
        return new WM(props);
    }

    protected void stuffContext(Context context) throws Exception {

    }

    public void testGlobalInclude() {
        assertStringTemplateEquals("#test('fine')","Global Macros are working just fine");
    }
}
