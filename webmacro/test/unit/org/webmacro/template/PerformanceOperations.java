package org.webmacro.template;

import java.io.*;
import java.util.*;
import org.webmacro.*;

/** 
 * This is a standalone general purpose driver
 * for common WM operations.
 * <p>
 * The way to add a test case is to implement a subclass
 * and add it to the list of runnables. You should also implement your own
 * template for each test case.
 * <p>
 * All performance metrics should be left as publics which can then be
 * introspected for reporting purposes. Just declare the metrics which you
 * want to report on.
 * <p>
 * All times should be reported in milliseconds and can be reformatted as
 * required.
 */
public class PerformanceOperations implements Runnable {
  
  // the classes which are to be run:
  Runnable[] testCases = {
                    new PublicVersusPrivate()
                        };
                        
  // The WebMacro instance which can be shared for each test case.
  private WebMacro wm;
  
  // the public variables available for introspection
  /** The iteration count used in the test. Defaults to 100K.*/
  public int iterationCount = 100000;
  
  /** The total elapsed time for public access. */
  public long tetPublicAccess;
  
  /** The total elapsed time for private access. */
  public long tetPrivateAccess;
  
  public PerformanceOperations() {}
  
  public PerformanceOperations(int iterationCount) {
    this.iterationCount = iterationCount;
  }
  
  public void run() {
    try {
      wm = new WM();
    }
    catch (Exception e) {
      throw new IllegalStateException(e.toString());
    }
    for (int index = 0; index < testCases.length; index++)
      testCases[index].run();
  }
  
  /**
   * The private versus public test case.
   */
  class PublicVersusPrivate implements Runnable {
    static final String publicTemplate = 
      "org/webmacro/template/Public.wm";
    static final String privateTemplate = 
      "org/webmacro/template/Private.wm";
      
    
    private Context setupPublic() {
      // the public values:
      PublicValue pub = new PublicValue();
      pub.object = this;
      // the lists for iteration:
      List pubList = new ArrayList(iterationCount);
      // populate:
      for (int index = 0; index < iterationCount; index++){
        pubList.add(pub);
      }
      Context context = wm.getContext();
      context.put("PublicList", pubList);
      return context;
    }
  
    private Context setupPrivate() {
      // the public values:
      PrivateValue priv = new PrivateValue();
      priv.setObject(this);
      // the lists for iteration:
       List privList = new ArrayList(iterationCount);
      // populate:
      for (int index = 0; index < iterationCount; index++){
        privList.add(priv);
      }
      Context context = wm.getContext();
      context.put("PrivateList", privList);
      return context;
    }
  
    public void run(){
      try {
        Context c = setupPublic();
        long start = System.currentTimeMillis();
        wm.writeTemplate(publicTemplate, System.out, c);
        tetPublicAccess = System.currentTimeMillis() - start;
        
        c = setupPrivate();
        start = System.currentTimeMillis();
        wm.writeTemplate(privateTemplate, System.out, c);
        tetPrivateAccess = System.currentTimeMillis() - start;
      }
      catch (Exception e) {
        throw new IllegalStateException(e.toString());
      }
    }
  }
}
