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
      public String name;

      public BooleanMan(String n) { name = n; }

      public void reset () {
         whichMethod = null;
      }

      public boolean gimmeTrue () { 
         whichMethod = "gimmeTrue";
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

   static BooleanMan bm1 = new BooleanMan("bm1");
   static BooleanMan bm2 = new BooleanMan("bm2");

   public void stuffContext (Context context) throws Exception {
      context.setEvaluationExceptionHandler (
                new DefaultEvaluationExceptionHandler ());
      context.put ("TRUE", Boolean.TRUE);
      context.put ("BM1", bm1);
      context.put ("BM2", bm2);
   }

   public void testConstOr () throws Exception {
      bm1.reset();
      String tmpl = "#if (true || $BM1.gimmeTrue()) {pass} #else {fail}";
      assertStringTemplateEquals (tmpl, "pass");
      assert (bm1.whichMethod == null);
   }

   public void testVarOr () throws Exception {
      bm1.reset();
      String tmpl = "#if ($TRUE || $BM1.gimmeTrue()) {pass} #else {fail}";
      assertStringTemplateEquals (tmpl, "pass");
      assert (bm1.whichMethod == null);
   }

   public void testConstAndOr () throws Exception {
      bm1.reset ();
      bm2.reset ();
      String tmpl = "#if ((true && $BM1.gimmeTrue()) || $BM2.gimmeTrue()) {pass} #else {fail}";
      assertStringTemplateEquals(tmpl, "pass");
      assert (bm1.whichMethod.equals ("gimmeTrue"));
      assert (bm2.whichMethod == null);
   }
   
   public void testVarAndOr() throws Exception {
      bm1.reset ();
      bm2.reset ();
      String tmpl = "#if (($TRUE && $BM1.gimmeTrue()) || $BM2.gimmeTrue()) {pass} #else {fail}";
      assertStringTemplateEquals(tmpl, "pass");
      assert (bm1.whichMethod.equals ("gimmeTrue"));
      assert (bm2.whichMethod == null);
   }

   public void testConstOrAnd () throws Exception {
      bm1.reset ();
      bm2.reset ();

      String tmpl = "#if ((true || $BM1.gimmeTrue()) || $BM2.gimmeTrue()) {pass} #else {fail}";
      assertStringTemplateEquals (tmpl, "pass");
      assert (bm1.whichMethod == null);
      assert (bm2.whichMethod == null);

      tmpl = "#if (true || $BM1.gimmeTrue() || $BM2.gimmeTrue()) {pass} #else {fail}";
      assertStringTemplateEquals (tmpl, "pass");
      assert (bm1.whichMethod == null);
      assert (bm2.whichMethod == null);
   }

   public void testVarOrAnd () throws Exception {
      bm1.reset ();
      bm2.reset ();

      String tmpl = "#if (($TRUE || $BM1.gimmeTrue()) || $BM2.gimmeTrue()) {pass} #else {fail}";
      assertStringTemplateEquals (tmpl, "pass");
      assert (bm1.whichMethod == null);
      assert (bm2.whichMethod == null);
   }
}
