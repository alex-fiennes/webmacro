package org.webmacro.template;

import java.io.*;
import org.webmacro.*;
import org.webmacro.engine.StringTemplate;
import org.webmacro.engine.DefaultEvaluationExceptionHandler;

import junit.framework.*;

import org.apache.regexp.RE;


/**
 * ensures WM short-circuit's boolean expressions correctly
 */
public class TestShortCircuit extends TemplateTestCase {
   
   public static class BooleanMan {
      public String whichMethod = null;
 
      public void reset () {
         whichMethod = null;
      }

      public boolean gimmeTrue () { 
         whichMethod = "gimmeTrue";
         return true;
      }
      
      public boolean gimmeTrue2 () {
         whichMethod = "gimmeTrue2";
         return true;
      }

      public boolean gimmeFalse () {
         whichMethod = "gimmeFalse";
         return false;
      } 

   }

   public TestShortCircuit (String name) {
      super (name);
   }

   public void stuffContext (Context context) throws Exception {
      context.setEvaluationExceptionHandler (
                new DefaultEvaluationExceptionHandler ());
      context.put ("BM", new BooleanMan ());
   }

   public void testSC1 () throws Exception {
      BooleanMan bm = (BooleanMan) _context.get ("BM");
      bm.reset ();

      // #if shouldn't try to eval $BM.gimmeTrue() 
      String tmpl = "#if (true || $BM.gimmeTrue()) {pass} #else {fail}";
      executeStringTemplate (tmpl);
      assert (bm.whichMethod == null);
      assertStringTemplateEquals (tmpl, "pass");
   }

   /** this test fails.  */
   public void testSC2 () throws Exception {
      BooleanMan bm = (BooleanMan) _context.get ("BM");   
      bm.reset ();

      // #if shouldn't try to eval $DM.gimmeTrue2()    
      String tmpl = "#if ((true && $BM.gimmeTrue()) || $BM.gimmeTrue2()) {pass} #else {fail}";
      executeStringTemplate (tmpl);
      assert (bm.whichMethod.equals ("gimmeTrue"));
      assertStringTemplateEquals (tmpl, "pass");
   }

   public void testSC3 () throws Exception {
      BooleanMan bm = (BooleanMan) _context.get ("BM");
      bm.reset ();

      // #if shouldn't try to eval anything in $BM
      String tmpl = "#if ((true || $BM.gimmeTrue()) || $BM.gimmeTrue2()) {pass} #else {fail}";
      executeStringTemplate (tmpl);
      assert (bm.whichMethod == null);
      assertStringTemplateEquals (tmpl, "pass");
   }
}
