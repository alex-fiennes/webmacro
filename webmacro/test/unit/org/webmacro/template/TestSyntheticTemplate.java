package org.webmacro.template;

import java.io.*;
import junit.framework.*;
import org.webmacro.Context;

/** a simple test case to load and execute a file
 * via the path relative to the classpath.
*/
public class TestSyntheticTemplate extends TemplateTestCase {

  private static final String fileName = "org/webmacro/template/synthetictest.wm";
  private static final String reportName = "org/webmacro/template/syntheticreport.wm";
  private static final String[] LOAD = {"1.wm", "2.wm", "3.wm", "4.wm", "5.wm",
                                      "6.wm", "7.wm", "8.wm", "9.wm", "10.wm"};
  private Context context = null;
  
  public TestSyntheticTemplate (String name) {
    super (name);
  }

  protected void stuffContext (Context context) throws Exception {
    // keep the context throughout the test pattern
    this.context = context;
  }

  public void testEvaluate() throws Exception {
    context.put("runLoad", Boolean.FALSE);
    executeFileTemplate(fileName);
  }

  public void testShow() throws Exception {
    context.put("runLoad", Boolean.FALSE);
    String value = templateFileToString(fileName);
    System.out.println("Template:\n" + value);
    String output = executeStringTemplate(value);
    System.out.println("\n\nEvaluated to:\n" + output);
  }

  /** Throw the first test result out due to parsing. */
  public void testLoadAndToss() throws Exception {
    context.put("runLoad", Boolean.TRUE);
    context.put("load", LOAD);
    long tet = System.currentTimeMillis();
    String value = executeFileTemplate(fileName);
    tet = System.currentTimeMillis() - tet;
    System.out.println("Completed testLoadAndToss. Character count=" + value.length() + " TET(millis)=" + tet);
    if (value.length() < 10000) fail("Total character count must exceed 10,000 characters");
  }

  /**
   * This is the loading test and it is self verifying as to correctness.
   * by counting the character stream.
   * <p>
   * Note: this test is not run within a thread decorator.
   * That is the work of clarkware.junitperf.LoadTest
   */
  public void testLoad() throws Exception {
    context.put("runLoad", Boolean.TRUE);
    context.put("load", LOAD);
    long min = 0;
    long max = System.currentTimeMillis();
    String throwAway = executeFileTemplate(fileName);
    max = System.currentTimeMillis() - max;
    min = max;
    int contentSize = throwAway.length();

    long tet, singleTet; int iterationCount = 12;
    // begin load
    tet = System.currentTimeMillis();
    for (int index = 0; index < iterationCount; index++) {
      singleTet = System.currentTimeMillis();
      String value = executeFileTemplate(fileName);
      singleTet = System.currentTimeMillis() - singleTet;
      if (value.length() != contentSize) fail("Load failure: content size mismatch");
      if (singleTet < min) min = singleTet;
      if (singleTet > max) max = singleTet;
    }
    tet = System.currentTimeMillis()-tet;
    report(iterationCount-2,
            tet - max - min,
            contentSize);
  }

  /** A webmacro report sent to LoadReport.html. */
  protected void report(int iterationCount, long totalTime, int characterCount) throws Exception {
    System.out.println("Load test complete and report filed to LoadReport.html in current directory.");
    context.put("IterationCount", iterationCount);
    context.put("TotalElapsedTime", totalTime);
    context.put("AverageTime", totalTime/iterationCount);
    context.put("CharacterCount", characterCount);
    context.put("SystemProperty", System.getProperties());
    context.put("TotalMemory", Runtime.getRuntime().totalMemory()/1024);
    context.put("ScriptValue", templateFileToString(fileName));
    context.put("Today", new java.util.Date());
    String report = executeFileTemplate(reportName);
   	PrintWriter p = new PrintWriter( new FileOutputStream("LoadReport.html") );
   	p.write(report);
   	p.close();
  }


}
