package org.webmacro.template;

import java.io.*;
import junit.framework.*;
import org.webmacro.*;

/** 
 * A test harness which compares java performance to WM
 * performance for a set of common operations.
 * <p>
 * This test produces its output using PerformanceReport.wm as a template
 * and outputs the result to PerformanceReport.html.
 * A very vanilla template, PerformanceOperations.wm, contains the WM operations.
 */
public class TestPerformanceAreas extends TemplateTestCase {

  protected int iterationCount;

  private static final String fileName = "org/webmacro/template/PerformanceOperations.wm";
  private static final String reportName = "org/webmacro/template/PerformanceReport.wm";
  private Context context = null;
  
  // the report variables.
  long tetPublicAccess;
  long tetPrivateAccess;
  
  public TestPerformanceAreas (String name) {
    super (name);
  }

  public static Test suite() {
    TestSuite suite= new TestSuite();

    suite.addTest(new TestPerformanceAreas("Performance") {
        protected WebMacro createWebMacro() throws Exception {
          return new WM("org/webmacro/template/PERFORMANCE.properties");
        }
        
        protected void runTest() throws Exception {
          this.iterationCount = _wm.getBroker().getIntegerSetting("TestPerformanceAreas.IterationCount", this.iterationCount);
          this.testPublicVersusAccessor();
          report();
        }
      }
    );
    return suite;
  }


  protected void stuffContext (Context context) throws Exception {
    // keep the context throughout the test pattern
    this.context = context;
  }



  /**
   * This method shows the internal differences over the iteration
   * count for getting/setting a reference via a public reference
   * versus get/set.
   */
  public void testPublicVersusAccessor() throws Exception {
    long tet, singleTet, start, end; 

    PublicValue pub = new PublicValue();
    PrivateValue priv = new PrivateValue();
    long value;
    Object object;
    
    // the public
    start = System.currentTimeMillis();
    for (long index = 0; index < iterationCount; index++) {
      value = pub.value; // the get
      pub.value = index; // the set
      object = pub.object;
      pub.object  = this;
 
    }
    tetPublicAccess = System.currentTimeMillis() - start;

    // the private
    start = System.currentTimeMillis();
    for (long index = 0; index < iterationCount; index++) {
      value = priv.getValue(); // the get
      priv.setValue(index); // the set
      object = priv.getObject();
      priv.setObject(this);
    }
    tetPrivateAccess = System.currentTimeMillis() - start;
    
  }

  /** A webmacro report sent to LoadReport.html. */
  protected void report() throws Exception {
    context.put("IterationCount", iterationCount);
    context.put("TETPublicAccess", tetPublicAccess);
    context.put("TETPrivateAccess", tetPrivateAccess);
    String report = executeFileTemplate(reportName);
   	PrintWriter p = new PrintWriter( new FileOutputStream("PerformanceReport.html") );
   	p.write(report);
   	p.close();
  }

}

class PublicValue {
    public long value;
    public Object object;
}

class PrivateValue {
    private long value;
    private Object object;
    public long getValue() { return value; }
    public void setValue(long value) { this.value = value; }
    public Object getObject() { return object; }
    public void setObject(Object object) { this.object = object; }
}
