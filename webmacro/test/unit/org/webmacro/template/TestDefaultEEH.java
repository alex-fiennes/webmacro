package org.webmacro.template;

import java.io.*;
import org.webmacro.*;
import org.webmacro.engine.StringTemplate;
import org.webmacro.engine.DefaultEvaluationExceptionHandler;

import junit.framework.*;

import org.apache.regexp.RE;

public class TestDefaultEEH extends AbstractVariableTestCase {

   public TestDefaultEEH (String name) {
      super (name);
   }

   public void stuffContext (Context context) throws Exception {
      context.setEvaluationExceptionHandler (
                new DefaultEvaluationExceptionHandler ());

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
                                   "^<!--.*-->$");
   }

   public void testNoSuchMethod () throws Exception {
      assertStringTemplateThrows ("$TestObject.noSuchMethod()",
                                   PropertyException.NoSuchMethodException.class);

   }
  
   public void testNoSuchProperty () throws Exception {
      assertStringTemplateThrows ("$TestObject.noSuchProperty",
                                   PropertyException.NoSuchPropertyException.class);
   }


   public void testVoidMethod () throws Exception {
      assertStringTemplateEquals ("$TestObject.voidMethod()", "");
   }

   public void testNullMethod () throws Exception {
      assertStringTemplateMatches ("$TestObject.nullMethod()", 
                                   "^<!--.*-->$");
   }

   public void testThrowsMethod() throws Exception {
      assertStringTemplateThrows ("$TestObject.throwException()", 
                                   org.webmacro.PropertyException.class);
   }

   /** A variable in the context who's .toString() method returns null 
       This test currently failes under DEEH.  Need to determine correct
       behavior.
    */
   public void testNullVariable () throws Exception {
      assertStringTemplateThrows ("$NullObject", 
                                  PropertyException.NullVariableException.class);
   }
}
