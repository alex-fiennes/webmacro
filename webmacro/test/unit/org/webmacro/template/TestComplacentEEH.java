package org.webmacro.template;

import java.io.*;
import org.webmacro.*;
import org.webmacro.engine.StringTemplate;
import org.webmacro.engine.ComplacentEvaluationExceptionHandler;

import junit.framework.*;

import org.apache.regexp.RE;

/** 
 * In the event of a bad variable/method/property reference, 
 * ComplacentEEH should always output a warning to the stream
 * instead of throwing.
 */
public class TestComplacentEEH extends AbstractVariableTestCase {

   public TestComplacentEEH (String name) {
      super (name);
   }

   public void stuffContext (Context context) throws Exception {
      context.setEvaluationExceptionHandler (
                new ComplacentEvaluationExceptionHandler ());

      context.put ("TestObject", new TestObject());
      context.put ("NullTestObject", new NullTestObject());
   }

   public void testGoodVariable () throws Exception {
      assertStringTemplateEquals ("$TestObject", "TestObject"); 
   }
   
   public void testGoodMethod () throws Exception {
      assertStringTemplateEquals ("$TestObject.getString()", "String");   
   }

   public void testGoodProperty () throws Exception {
      assertStringTemplateEquals ("$TestObject.property", "Property");
   }


   public void testNoSuchVariable () throws Exception {
      assertStringTemplateMatches ("$NotInContext", 
        "^<!--.* nonexistent .*\\$NotInContext.*-->$");
   }

   public void testNoSuchMethod () throws Exception {
      assertStringTemplateMatches ("$TestObject.noSuchMethod()",
                                   "^<!--.*-->$");

   }
  
   public void testNoSuchProperty () throws Exception {
      assertStringTemplateMatches ("$TestObject.noSuchProperty",
                                   "^<!--.*-->$");
   }


   public void testVoidMethod () throws Exception {
      assertStringTemplateEquals ("$TestObject.voidMethod()", "");
   }

   public void testNullMethod () throws Exception {
      assertStringTemplateMatches ("$TestObject.nullMethod()", 
        "^<!--.*null .*\\$TestObject.nullMethod.*-->$");
   }

   public void testThrowsMethod() throws Exception {
      assertStringTemplateMatches ("$TestObject.throwException()", 
                                   "^<!--.*-->$");
   }

   // this is correct behavior for ComplacentEEH and NullVariables
   public void testNullVariable () throws Exception {
     assertStringTemplateMatches ("$NullObject", 
          "^<!--.*nonexistent variable.*\\$NullObject.*-->$");
   }
}
