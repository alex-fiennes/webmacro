package org.webmacro.template;

import java.io.*;

import org.webmacro.*;

import junit.framework.*;

/**
 * Basic test class to test webmacros handling of encodings.
 * To test a concrete encoding, you have to extends this class
 * and implement the four abstract methods. Be sure to run native2ascii
 * on your source code to have it encoding neutral before checking it into
 * CVS.<br>
 * This class will then create a file of your source template, written in the
 * encoding you specified. Then three tests are run on this file:<br>
 * 1) Parse template and evaluate it into a string.
 * 2) Parse template and output it in the encoding you specified
 * 3) Parse template and output it as utf8
 * <br>
 * In all cases, the output is compared to the expected results.
 * @author Sebastian Kanthak
 */
public abstract class EncodingTestCase extends TestCase {
    public static final String TEMPLATE_FILENAME = "org.webmacro.template.TestEncoding.wm";
    protected WebMacro wm;
    
    protected String template;
    protected String expected;
    protected String encoding;
    
    public EncodingTestCase(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        this.template = getTemplate();
        this.expected = getExpected();
        this.encoding = getEncoding();
        createWebMacro();
        createFile();
    }

    protected void tearDown() throws Exception {
        deleteFile();
    }

    /**
     * Return the template source to test with
     */
    protected abstract String getTemplate();

    /**
     * Return the encoding to test with
     */
    protected abstract String getEncoding();

    /**
     * Return the expected result
     */
    protected abstract String getExpected();

    /**
     * Use this method to stuff additional data
     * in your template
     */
    protected abstract void stuffContext(Context context);
    
    protected void createWebMacro() throws Exception {
        if (System.getProperties().getProperty("org.webmacro.LogLevel") == null)
            System.getProperties().setProperty("org.webmacro.LogLevel", "ERROR");
        System.getProperties().setProperty("org.webmacro.TemplateEncoding",encoding);
        wm = new WM();
    }

    protected void createFile() throws IOException {
        File f = new File(TEMPLATE_FILENAME);
        Writer writer = new OutputStreamWriter(new FileOutputStream(f),encoding);
        writer.write(template);
        writer.close();
    }

    protected void deleteFile() throws IOException {
        File f = new File(TEMPLATE_FILENAME);
        if (f.exists())
            f.delete();
    }

    private Context getContext() {
        Context context = wm.getContext();
        stuffContext(context);
        return context;
    }

    public void testInputEncoding() throws Exception {
        assertEquals("Input Encoding is not "+encoding,
                     wm.getConfig("TemplateEncoding"),encoding);
        Template t = wm.getTemplate(TEMPLATE_FILENAME);
        String evaluated = t.evaluate(getContext()).toString();
        assertEquals("Template evaluated to \""+evaluated+"\" instead of \""+expected+"\"",
                     evaluated,expected);
    }

    public void testOutputEncoding() throws Exception {
        assertEquals("InputEncoding is not "+encoding,
                     wm.getConfig("TemplateEncoding"),encoding);
        Template t = wm.getTemplate(TEMPLATE_FILENAME);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        FastWriter fw = wm.getFastWriter(bos,encoding);
        t.write(fw,getContext());
        fw.flush();
        fw.close();
        assertByteArrayEquals(bos.toByteArray(),expected.getBytes(encoding));
    }

    public void testUTF8OutputEncoding() throws Exception {
        assertEquals("InputEncoding is not "+encoding,
                     wm.getConfig("TemplateEncoding"),encoding);
        Template t = wm.getTemplate(TEMPLATE_FILENAME);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        FastWriter fw = wm.getFastWriter(bos,"UTF8");
        t.write(fw,getContext());
        fw.flush();
        fw.close();
        assertByteArrayEquals(bos.toByteArray(),expected.getBytes("UTF8"));
    }

    protected void assertByteArrayEquals(byte[] a,byte[] b) throws Exception {
        if (a == b) return;
        assert("One byte array is null",
               ((a != null) && (b != null)));
        assert("Size of binary output differs",
               a.length == b.length);
        for (int i=0; i < a.length; i++) {
            assert("Binary output differs",
                   a[i] == b[i]);
        }
    }
}
