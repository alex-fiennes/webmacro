package org.webmacro.template;

import java.io.*;
import java.util.Map;
import java.util.HashMap;
import org.webmacro.*;
import org.webmacro.engine.StringTemplate;
import org.webmacro.engine.DefaultEvaluationExceptionHandler;

import junit.framework.*;

import org.apache.regexp.RE;

public class TestGetSet extends TemplateTestCase {

  public static class TestObject {
    public static int intField;
    public static long longField;
    private Map map = new HashMap ();
    Object obj = null;
    Object[] objArray = new Object[] { "one", "two" };

    private static int intValue;
    public static void setInt(int i) { intValue = i; }
    public static int getInt() { return intValue; }

    private static long longValue;
    public static void setLong(long i) { longValue = i; }
    public static long getLong() { return longValue; }
   
    public void put (String key, Object value) {
       map.put (key, value);
    }

    public Object get (String key) {
       return map.get (key);
    }
 
    public void setObjectValue (Object value) {  
       this.obj = value;
    }

    public Object[] getArray () {
       return objArray;
    }
  }

  public static TestObject to = new TestObject(), to2 = new TestObject();

   public TestGetSet (String name) {
      super (name);
   }

   public void stuffContext (Context context) throws Exception {
      context.setEvaluationExceptionHandler (
                new DefaultEvaluationExceptionHandler ());

      context.put ("TestObject", to);
      context.put ("two", (int) 2);
      context.put ("twoLong", (long) 2);

      to2.intField = 1;
      to2.put ("Value", new TestObject ());
      context.put ("TestObject2", to2);
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

   /** Using WM shorthand syntax, get an object from
    * a map and call a method on it
    */
   public void test2 () throws Exception {
      String tmpl = "#if($TestObject2.Value.getInt() > 0){pass}#else{fail}";
      assertStringTemplateEquals (tmpl, "pass"); 
   }

   /** pass a null as a parameter to a method */
   public void testPassANull1 () throws Exception {
      String tmpl = "$TestObject2.setObjectValue(null)";
      assertStringTemplateEquals (tmpl, "");
   }

   /** same as test3, but use the null via a variable reference */
   public void testPassANull2 () throws Exception {
      assertStringTemplateEquals("#set $foo = null", "");
      assertBooleanExpr("$foo == null", true);
      to2.obj = new Object();
      assertStringTemplateEquals("$TestObject2.setObjectValue($foo)", "");
      assert(to2.obj == null);
   }

   /** call the ".length" field of an array */
   public void testDotLengthOnArray () {
      String tmpl = "$TestObject.getArray().length";
      assertStringTemplateEquals (tmpl, "2");
   }
}
