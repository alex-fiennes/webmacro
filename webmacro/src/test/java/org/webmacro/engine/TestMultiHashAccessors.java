/*
 */
package org.webmacro.engine;

import org.webmacro.Context;
import org.webmacro.template.TemplateTestCase;

public class TestMultiHashAccessors extends TemplateTestCase
{
   
   // class with two "get" methods
   public static class MultiGet1
   {
      public Object get(String arg)
      {
         return arg;
      }
      public Object get(int arg)
      {
         return new Integer(arg);
      }
   }
   
   // reorder methods
   public static class MultiGet2
   {
      public Object get(int arg)
      {
         return new Integer(arg);
      }
      public Object get(String arg)
      {
         return arg;
      }
   }
   
   public TestMultiHashAccessors(String s)
   {
      super(s);
   }
   
   
   protected void stuffContext(Context context) throws Exception
   {
      _context.put("MultiGet1", new MultiGet1());
      _context.put("MultiGet2", new MultiGet2());
   }
   
   public void testMultiGet1() throws Exception
   {
      assertEvaluationEquals("$MultiGet1.Test", "Test");
      assertEvaluationEquals("$MultiGet1.get('Test')", "Test");
      assertEvaluationEquals("$MultiGet1.get(100)", new Integer(100));
   }
   
   public void testMultiGet2() throws Exception
   {
      assertEvaluationEquals("$MultiGet2.Test", "Test");
      assertEvaluationEquals("$MultiGet2.get('Test')", "Test");
      assertEvaluationEquals("$MultiGet2.get(100)", new Integer(100));
   }
}
