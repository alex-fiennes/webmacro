package org.webmacro.template;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.webmacro.Context;
import org.webmacro.WM;
import org.webmacro.WebMacro;

import java.io.FileOutputStream;
import java.io.PrintWriter;

/** 
 * A test harness which compares java performance to WM
 * performance for a set of common operations.
 * <p>
 * This test produces its output using PerformanceReport.wm as a template
 * and outputs the result to PerformanceReport.html.
 * A very vanilla template, TemplateOperations.wm, contains the WM 
 * operations and this template works in conjunction with 
 * TemplateOperations.java. 
 */
public class TestPerformanceAreas extends TemplateTestCase
{

    protected int iterationCount;

    private static final String fileName = "org/webmacro/template/PerformanceOperations.wm";
    private static final String reportName = "org/webmacro/template/PerformanceReport.wm";
    private Context context = null;

    // the report variables.
    long tetPublicAccess;
    long tetPrivateAccess;
    PerformanceOperations po;


    public TestPerformanceAreas (String name)
    {
        super(name);
    }


    public static Test suite ()
    {
        TestSuite suite = new TestSuite();

        suite.addTest(new TestPerformanceAreas("Performance")
        {
            protected WebMacro createWebMacro () throws Exception
            {
                return new WM("org/webmacro/template/PERFORMANCE.properties");
            }


            protected void runTest () throws Exception
            {
                this.iterationCount = _wm.getBroker().getIntegerSetting("TestPerformanceAreas.IterationCount", this.iterationCount);
                this.testPublicVersusAccessor();
                po = new PerformanceOperations(iterationCount);
                po.run();
                this.report();
            }
        }
        );
        return suite;
    }


    protected void stuffContext (Context context) throws Exception
    {
        // keep the context throughout the test pattern
        this.context = context;
    }


    /**
     * This method shows the internal differences over the iteration
     * count for getting/setting a reference via a public reference
     * versus get/set.
     */
    public void testPublicVersusAccessor () throws Exception
    {
        long tet, singleTet, start, end;

        PublicValue pub = new PublicValue();
        PrivateValue priv = new PrivateValue();
        long value;
        Object object;
    
        // the public
        start = System.currentTimeMillis();
        for (long index = 0; index < iterationCount; index++)
        {
            value = pub.value; // the get
            object = pub.object; // the get
            pub.object = this; // the set
            pub.value = index;

        }
        tetPublicAccess = System.currentTimeMillis() - start;

        // the private
        start = System.currentTimeMillis();
        for (long index = 0; index < iterationCount; index++)
        {
            value = priv.getValue(); // the get
            object = priv.getObject();
            priv.setObject(this);
            priv.setValue(index);
        }
        tetPrivateAccess = System.currentTimeMillis() - start;

    }


    /** A webmacro report sent to LoadReport.html. */
    protected void report () throws Exception
    {
        context.put("Today", new java.util.Date());
        context.put("SystemProperty", System.getProperties());
        context.put("TotalMemory", Runtime.getRuntime().totalMemory() / 1024);
        context.put("IterationCount", iterationCount);
        context.put("TETPublicAccess", tetPublicAccess);
        context.put("TETPrivateAccess", tetPrivateAccess);
        context.put("WMTETPublicAccess", po.tetPublicAccess);
        context.put("WMTETPrivateAccess", po.tetPrivateAccess);
        String report = executeFileTemplate(reportName);
        PrintWriter p = new PrintWriter(new FileOutputStream("PerformanceReport.html"));
        p.write(report);
        p.close();
    }

}


