package org.webmacro.template;

import java.io.*;
import org.webmacro.*;
import org.webmacro.engine.StringTemplate;
import org.webmacro.engine.DefaultEvaluationExceptionHandler;

import junit.framework.*;

import org.apache.regexp.RE;

public class TestGetSet extends TemplateTestCase {

  public static class TestObject {
    public static int intField;
    public static long longField;
  }

  public static TestObject to = new TestObject();

   public TestGetSet (String name) {
      super (name);
   }

   public void stuffContext (Context context) throws Exception {
      context.setEvaluationExceptionHandler (
                new DefaultEvaluationExceptionHandler ());

      context.put ("TestObject", to);
   }

   public void test1() throws Exception {
     to.intField = 1;
     assertStringTemplateEquals ("$TestObject.intField", "1"); 
     assertStringTemplateEquals ("#set $TestObject.intField=2", "");
     assert(to.intField == 2);
   }
   
}
