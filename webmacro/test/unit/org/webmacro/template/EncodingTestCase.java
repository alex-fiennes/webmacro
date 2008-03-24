package org.webmacro.template;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

import junit.framework.TestCase;

import org.webmacro.Context;
import org.webmacro.Template;
import org.webmacro.WM;
import org.webmacro.WebMacro;

/**
 * Basic test class to test webmacros handling of encodings.
 * To test a concrete encoding, you have to extends this class
 * and implement the four abstract methods. Be sure to run native2ascii
 * on your source code to have it encoding neutral before checking it into
 * CVS.
 * <br>
 * This class will then create a file of your source template, written in the
 * encoding you specified. Then three tests are run on this file:
 * <br>
 * 1) Parse template and evaluate it into a string.
 * 2) Parse template and output it in the encoding you specified
 * 3) Parse template and output it as utf8
 * <br>
 * In all cases, the output is compared to the expected results.
 * 
 * @author Sebastian Kanthak
 */
public abstract class EncodingTestCase extends TestCase
{
    public static final String TEMPLATE_FILENAME = "org.webmacro.template.TestEncoding.wm";
    public static final String TEMPLATE_FILENAME_UTF8 = "org.webmacro.templateTestEncoding-utf8.wm";

    protected WebMacro wm;
    protected WebMacro wmUTF8;

    protected String template;
    protected String expected;
    protected String encoding;


    public EncodingTestCase (String name)
    {
        super(name);
    }


    protected void setUp () throws Exception
    {
        this.template = getTemplate();
        this.expected = getExpected();
        this.encoding = getEncoding();
        createWebMacro();
        createFiles();
    }


    protected void tearDown () throws Exception
    {
        deleteFiles();
    }


    /**
     * Return the template source to test with
     */
    protected abstract String getTemplate ();


    /**
     * Return the encoding to test with
     */
    protected abstract String getEncoding ();


    /**
     * Return the expected result
     */
    protected abstract String getExpected ();


    /**
     * Use this method to stuff additional data
     * in your template
     */
    protected abstract void stuffContext (Context context);


    protected void createWebMacro () throws Exception
    {
        File f = new File("WebMacro.properties-".concat(encoding));
        PrintWriter w = new PrintWriter(new FileWriter(f));
        w.println("TemplateEncoding: ".concat(encoding));
        w.close();

        f = new File("WebMacro.properties-UTF8");
        w = new PrintWriter(new FileWriter(f));
        w.println("TemplateEncoding: UTF8");
        w.close();

        if (System.getProperties().getProperty("org.webmacro.LogLevel") == null)
            System.getProperties().setProperty("org.webmacro.LogLevel", "ERROR");
        wm = new WM("WebMacro.properties-".concat(encoding));

        wmUTF8 = new WM("WebMacro.properties-UTF8");
    }


    protected void createFiles () throws IOException
    {
        File f = new File(TEMPLATE_FILENAME);
        Writer writer = new OutputStreamWriter(new FileOutputStream(f), encoding);
        writer.write(template);
        writer.close();

        f = new File(TEMPLATE_FILENAME_UTF8);
        writer = new OutputStreamWriter(new FileOutputStream(f), "UTF8");
        writer.write(template);
        writer.close();

    }


    protected void deleteFile (String filename) throws IOException
    {
        File f = new File(filename);
        if (f.exists())
            f.delete();
    }


    protected void deleteFiles () throws IOException
    {
        deleteFile(TEMPLATE_FILENAME);
        deleteFile(TEMPLATE_FILENAME_UTF8);
        deleteFile("WebMacro.properties-".concat(encoding));
        deleteFile("WebMacro.properties-UTF8");
    }


    private Context getContext (WebMacro wm)
    {
        Context context = wm.getContext();
        stuffContext(context);
        return context;
    }


    public void testInputEncoding () throws Exception
    {
        assertEquals("Input Encoding is not " + encoding,
                wm.getConfig("TemplateEncoding"), encoding);
        Template t = wm.getTemplate(TEMPLATE_FILENAME);
        String evaluated = t.evaluateAsString(getContext(wm));
        assertEquals("Template evaluated to \"" + evaluated + "\" instead of \"" + expected + "\"",
                evaluated, expected);
    }


    public void testOutputEncoding () throws Exception
    {
        assertEquals("InputEncoding is not " + encoding,
                wm.getConfig("TemplateEncoding"), encoding);
        Template t = wm.getTemplate(TEMPLATE_FILENAME);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        t.write(bos, encoding, getContext(wm));
        assertByteArrayEquals(bos.toByteArray(), expected.getBytes(encoding));
    }


    public void testUTF8OutputEncoding () throws Exception
    {
        assertEquals("InputEncoding is not " + encoding,
                wm.getConfig("TemplateEncoding"), encoding);
        Template t = wm.getTemplate(TEMPLATE_FILENAME);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        t.write(bos, "UTF8", getContext(wm));
        assertByteArrayEquals(bos.toByteArray(), expected.getBytes("UTF8"));
    }


    /**
     * FIXME Works in Eclipse but not Maven on Linux
     * Works in Eclipse and Maven on Windows
     */
    public void testUTF8InputEncoding () throws Exception
    {
        assertEquals("InputEncoding is not UFT8",
                wmUTF8.getConfig("TemplateEncoding"), "UTF8");
        Template t = wmUTF8.getTemplate(TEMPLATE_FILENAME_UTF8);
        String evaluated = t.evaluateAsString(getContext(wmUTF8));
        assertEquals("Template evaluated to \"" + evaluated + "\" instead of \"" + expected + "\"",
                expected, evaluated);
    }


    protected void assertByteArrayEquals (byte[] a, byte[] b) throws Exception
    {
        if (a == b) return;
        assertTrue("One byte array is null",
                ((a != null) && (b != null)));
        if (a != null) { // Eclipse forces som nonsense on us
            assertTrue("Size of binary output differs",
                   a.length == b.length);
            for (int i = 0; i < a.length; i++)
            {
                assertTrue("Binary output differs",
                        a[i] == b[i]);
            }
        }
    }
}
