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

    private static int intValue;
    public static void setInt(int i) { intValue = i; }
    public static int getInt() { return intValue; }

    private static long longValue;
    public static void setLong(long i) { longValue = i; }
    public static long getLong() { return longValue; }
  }

  public static TestObject to = new TestObject();

   public TestGetSet (String name) {
      super (name);
   }

   public void stuffContext (Context context) throws Exception {
      context.setEvaluationExceptionHandler (
                new DefaultEvaluationExceptionHandler ());

      context.put ("TestObject", to);
      context.put ("two", (int) 2);
      context.put ("twoLong", (long) 2);
   }

   public void test1() throws Exception {
     to.intField = 1;
     assertStringTemplateEquals ("$TestObject.intField", "1"); 
     assertStringTemplateEquals ("#set $TestObject.intField=2", "");
     assert(to.intField == 2);

     to.intField = 1;
     assertStringTemplateEquals ("#set $TestObject.intField=$two", "");
     assert(to.intField == 2);

     to.longField = 1;
     assertStringTemplateEquals ("$TestObject.longField", "1"); 
     assertStringTemplateEquals ("#set $TestObject.longField=2", "");
     assert(to.longField == 2);

     to.longField = 1;
     assertStringTemplateEquals ("$TestObject.longField", "1"); 
     assertStringTemplateEquals ("#set $TestObject.longField=$twoLong", "");
     assert(to.longField == 2);

     to.setInt(1);
     assertStringTemplateEquals ("$TestObject.Int", "1"); 
     assertStringTemplateEquals ("#set $TestObject.Int=2", "");
     assert(to.getInt() == 2);

     to.setInt(1);
     assertStringTemplateEquals ("$TestObject.Int", "1"); 
     assertStringTemplateEquals ("#set $TestObject.Int=$two", "");
     assert(to.getInt() == 2);
   }
}
