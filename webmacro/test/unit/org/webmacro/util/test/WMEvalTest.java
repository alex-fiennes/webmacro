package org.webmacro.util.test;

import org.webmacro.Context;
import org.webmacro.Template;
import org.webmacro.util.Settings;
import org.webmacro.util.WMEval;

import junit.framework.TestCase;

/**
 * 
 * @author timp
 * @since 20 Mar 2008
 *
 */
public class WMEvalTest extends TestCase
{

    /**
     * @param name
     */
    public WMEvalTest(String name) {
        super(name);
    }

    /** 
     * {@inheritDoc}
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /** 
     * {@inheritDoc}
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test method for {@link org.webmacro.util.WMEval#WMEval(javax.servlet.Servlet)}.
     */
    public void testWMEvalServlet() {
        
    }

    /**
     * Test method for {@link org.webmacro.util.WMEval#WMEval()}.
     */
    public void testWMEval() {
        
    }

    /**
     * Test method for {@link org.webmacro.util.WMEval#getSettings()}.
     */
    public void testGetSettings() {
        WMEval it = new WMEval();
        Settings s = it.getSettings();
        String[] them = s.getKeys();
        int i = 0;
        for (; i < them.length; i++) { 
            System.err.println(them[i] + "=" + s.getSetting(them[i]));
        }
        assertTrue("Settings not found", i > 12);
    }

    /**
     * Test method for {@link org.webmacro.util.WMEval#getLog()}.
     */
    public void testGetLog() {
        assertNull(new WMEval().getLog());
        
    }

    /**
     * Test method for {@link org.webmacro.util.WMEval#init(java.io.InputStream)}.
     */
    public void testInit() {
        
    }

    /**
     * Test method for {@link org.webmacro.util.WMEval#error(java.lang.String, java.lang.Exception)}.
     */
    public void testError() {
        
    }

    /**
     * Test method for {@link org.webmacro.util.WMEval#getNewContext()}.
     */
    public void testGetNewContext() {
        WMEval it = new WMEval();
        it.getCurrentContext().put("o", "b");
        assertEquals("b",it.getCurrentContext().get("o"));
        it.getNewContext();
        assertNull(it.getCurrentContext().get("o"));
    }

    /**
     * Test method for {@link org.webmacro.util.WMEval#getNewContext(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
     */
    public void testGetNewContextHttpServletRequestHttpServletResponse() {
        
    }

    /**
     * Test method for {@link org.webmacro.util.WMEval#getCurrentContext()}.
     */
    public void testGetCurrentContext() {
        
    }

    /**
     * Test method for {@link org.webmacro.util.WMEval#getCurrentTemplate()}.
     */
    public void testGetCurrentTemplate() {
        assertNull(new WMEval().getCurrentTemplate());
        
    }

    /**
     * Test method for {@link org.webmacro.util.WMEval#parseLocalTemplate(java.lang.String)}.
     */
    public void testParseLocalTemplate() throws Exception {
        Template t = new WMEval().parseLocalTemplate("org/webmacro/util/test/WMEvalTest.wm");
        assertEquals("Hi!", t.evaluateAsString(new Context()));
    }

    /**
     * Test method for {@link org.webmacro.util.WMEval#setCurrentTemplate(org.webmacro.Template)}.
     */
    public void testSetCurrentTemplate() throws Exception {
        Template t = new WMEval().parseLocalTemplate("org/webmacro/util/test/WMEvalTest.wm");
        WMEval it = new WMEval();
        it.setCurrentTemplate(t);
        assertEquals("Hi!", it.eval(new Context()));
    }

    /**
     * Test method for {@link org.webmacro.util.WMEval#setCurrentContext(org.webmacro.Context)}.
     */
    public void testSetCurrentContext() throws Exception {
        WMEval it = new WMEval();
        Context c = new Context();
        c.put("o", "b");
        it.setCurrentContext(c);
        assertEquals("b",it.getCurrentContext().get("o"));
        
    }

    /**
     * Test method for {@link org.webmacro.util.WMEval#setOutputStream(java.io.OutputStream)}.
     */
    public void testSetOutputStream() {
        
    }

    /**
     * Test method for {@link org.webmacro.util.WMEval#eval()}.
     */
    public void testEval() throws Exception {
        WMEval it = new WMEval("org/webmacro/util/test/WMEvalTest.wm");
        assertEquals("Hi!", it.eval(new Context()));
        
    }

    /**
     * Test method for {@link org.webmacro.util.WMEval#eval(org.webmacro.Context)}.
     */
    public void testEvalContext() {
        
    }

    /**
     * Test method for {@link org.webmacro.util.WMEval#eval(org.webmacro.Context, java.lang.String, java.io.OutputStream)}.
     */
    public void testEvalContextStringOutputStream() {
        
    }

    /**
     * Test method for {@link org.webmacro.util.WMEval#eval(java.lang.String)}.
     */
    public void testEvalString() {
        
    }

    /**
     * Test method for {@link org.webmacro.util.WMEval#eval(org.webmacro.servlet.WebContext, java.lang.String, javax.servlet.http.HttpServletResponse)}.
     */
    public void testEvalWebContextStringHttpServletResponse() {
        
    }

    /**
     * Test method for {@link org.webmacro.util.WMEval#eval(org.webmacro.Context, org.webmacro.Template)}.
     */
    public void testEvalContextTemplate() {
        
    }

    /**
     * Test method for {@link org.webmacro.util.WMEval#eval(org.webmacro.Context, java.lang.String, java.io.OutputStream, java.lang.String)}.
     */
    public void testEvalContextStringOutputStreamString() {
        
    }

    /**
     * Test method for {@link org.webmacro.util.WMEval#eval(org.webmacro.Context, java.lang.String, java.lang.String, boolean, java.lang.String)}.
     */
    public void testEvalContextStringStringBooleanString() {
        
    }

    /**
     * Test method for {@link org.webmacro.util.WMEval#destroy()}.
     */
    public void testDestroy() {
        WMEval it = new WMEval();
        it.getCurrentContext().put("o", "b");
        assertEquals("b",it.getCurrentContext().get("o"));
        it.destroy();
        assertNull(it.getCurrentContext());
        assertNull(it.getCurrentTemplate());
    }

}
