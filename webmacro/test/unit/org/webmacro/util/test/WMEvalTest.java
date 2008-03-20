package org.webmacro.util.test;

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
        
    }

    /**
     * Test method for {@link org.webmacro.util.WMEval#parseLocalTemplate(java.lang.String)}.
     */
    public void testParseLocalTemplate() {
        
    }

    /**
     * Test method for {@link org.webmacro.util.WMEval#setCurrentTemplate(org.webmacro.Template)}.
     */
    public void testSetCurrentTemplate() {
        
    }

    /**
     * Test method for {@link org.webmacro.util.WMEval#setCurrentContext(org.webmacro.Context)}.
     */
    public void testSetCurrentContext() {
        
    }

    /**
     * Test method for {@link org.webmacro.util.WMEval#setOutputStream(java.io.OutputStream)}.
     */
    public void testSetOutputStream() {
        
    }

    /**
     * Test method for {@link org.webmacro.util.WMEval#eval()}.
     */
    public void testEval() {
        
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
        
    }

}
