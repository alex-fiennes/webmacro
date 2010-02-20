package org.webmacro.util.test;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;

import org.webmacro.Broker;
import org.webmacro.Context;
import org.webmacro.Template;
import org.webmacro.WM;
import org.webmacro.servlet.TemplateServlet;
import org.webmacro.servlet.WebContext;
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

    // FIXME Hardcoded path to tests
    private static final String PATH_TO_TESTS = "test/unit/";

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
    public void testWMEvalServlet() throws Exception {
        TemplateServlet ts = new TemplateServlet();
        ts.init(new MockServletConfig());
        WMEval it = new WMEval(ts);
        it.getCurrentContext().put("o", "b");
        assertEquals("b",it.getCurrentContext().get("o"));
        it.getNewContext();
        assertNull(it.getCurrentContext().get("o"));
    }

    /**
     * Test method for {@link org.webmacro.util.WMEval#WMEval(javax.servlet.Servlet)}.
     */
    public void testWMEvalServletNull() throws Exception {
        WMEval it = new WMEval((Servlet)null);
        it.getCurrentContext().put("o", "b");
        assertEquals("b",it.getCurrentContext().get("o"));
        it.getNewContext();
        assertNull(it.getCurrentContext().get("o"));
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
        assertEquals(new WMEval().getLog(), new WMEval((Servlet)null).getLog());
    }

    /**
     * Test method for {@link org.webmacro.util.WMEval#init(java.io.InputStream)}.
     */
    public void testInit() throws Exception {
        WMEval it = new WMEval();
        it.init(new FileInputStream(PATH_TO_TESTS + "org/webmacro/util/test/WMEvalTest.wm"));
        assertEquals("Hi!", it.eval(new Context()));
    }

    /**
     * Test method for {@link org.webmacro.util.WMEval#error(java.lang.String, java.lang.Exception)}.
     */
    public void testError() {
        WMEval it = new WMEval();
        it.error("See TemplateServlet", new RuntimeException("Maybe it could go?"));        
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
        WMEval it = new WMEval();
        it.getCurrentContext().put("o", "b");
        assertEquals("b",it.getCurrentContext().get("o"));
        it.getNewContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        assertNull(it.getCurrentContext().get("o"));
        
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
        assertEquals("Hi!", it.eval());
    }

    /**
     * Test method for {@link org.webmacro.util.WMEval#eval(org.webmacro.Context)}.
     */
    public void testEvalContext() throws Exception {
        WM wm = new WM();
        Template t = wm.getTemplate("org/webmacro/util/test/WMEvalTest.wm");
        WMEval it = new WMEval();
        it.setCurrentTemplate(t);
        assertEquals("Hi!", it.eval(new Context()));        
        
    }

    /**
     * Test method for {@link org.webmacro.util.WMEval#eval(org.webmacro.Context, java.lang.String, java.io.OutputStream)}.
     */
    public void testEvalContextStringOutputStream() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        assertEquals("Hi!", new WMEval().eval(new Context(),
                "org/webmacro/util/test/WMEvalTest.wm", out));
        assertEquals("Hi!", out.toString());
    }

    /**
     * Test method for {@link org.webmacro.util.WMEval#eval(java.lang.String)}.
     */
    public void testEvalString() throws Exception {
        assertEquals("Hi!", new WMEval().eval("org/webmacro/util/test/WMEvalTest.wm"));        
    }

    /**
     * Test method for {@link org.webmacro.util.WMEval#eval(org.webmacro.servlet.WebContext, java.lang.String, javax.servlet.http.HttpServletResponse)}.
     */
    public void testEvalWebContextStringHttpServletResponse() throws Exception {
        WMEval it = new WMEval();
        HttpServletResponse response = new MockHttpServletResponse();
        assertEquals("Hi!", it.eval(
                new WebContext(Broker.getBroker(), new MockHttpServletRequest(), response),
                "org/webmacro/util/test/WMEvalTest.wm", 
                response));
    }

    /**
     * Test method for {@link org.webmacro.util.WMEval#eval(org.webmacro.Context, org.webmacro.Template)}.
     */
    public void testEvalContextTemplate() throws Exception {
        WM wm = new WM();
        Template t = wm.getTemplate("org/webmacro/util/test/WMEvalTest.wm");
        assertEquals("Hi!", new WMEval().eval(new Context(), t));        
    }

    /**
     * Test method for {@link org.webmacro.util.WMEval#eval(org.webmacro.Context, java.lang.String, java.io.OutputStream, java.lang.String)}.
     */
    public void testEvalContextStringOutputStreamString() throws Exception {
        assertEquals("Hi!", new WMEval().eval(new Context(), "org/webmacro/util/test/WMEvalTest.wm", "T", false, "UTF-8"));
    }

    /**
     * Test method for {@link org.webmacro.util.WMEval#eval(org.webmacro.Context, java.lang.String, java.lang.String, boolean, java.lang.String)}.
     */
    public void testEvalContextStringStringBooleanString() throws Exception {
        assertEquals("Hi!", new WMEval().eval(new Context(), 
                "org/webmacro/util/test/WMEvalTest.wm", null, "UTF-8"));
        Context c = new Context();
        c.put(WMEval.outputContextKey, "t.tmp");
        assertEquals("Hi!", new WMEval().eval(c, 
                "org/webmacro/util/test/WMEvalTest.wm", null, "UTF-8"));
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
