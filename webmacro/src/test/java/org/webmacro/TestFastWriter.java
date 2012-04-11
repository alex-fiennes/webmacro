/*
 * Created by IntelliJ IDEA.
 * User: e_ridge
 * Date: Dec 17, 2002
 * Time: 1:24:35 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.webmacro;

import junit.framework.TestCase;

public class TestFastWriter extends TestCase
{

    private WebMacro wm;


    public TestFastWriter (String name)
    {
        super(name);
    }


    protected void setUp () throws Exception
    {
        java.lang.System.setProperty("org.webmacro.LogLevel", "NONE");
        wm = new WM();
    }


    public void testFastWriter () throws Exception
    {
        doIt(4 * 1024);
        doIt(10 * 1024);
        doIt(100 * 1024);
        doIt(1000 * 1024);
        doIt(4000 * 1024);
    }


    private void doIt (int size) throws Exception
    {
        String data = makeData(size);

        long start = System.currentTimeMillis();
        FastWriter fw = FastWriter.getInstance(wm.getBroker(), null, "ISO8859_1");
        fw.write(data);
        String after = fw.toString();
        fw.close();
        long end = System.currentTimeMillis();

        System.err.println(size + " bytes in " + ((end - start) / 1000D) + " seconds.");

        assertTrue(after.equals(data));
    }


    private String makeData (int size)
    {
        StringBuilder sb = new StringBuilder(size);
        for (int x = 0; x < size; x++)
            sb.append('x');

        return sb.toString();
    }
}
