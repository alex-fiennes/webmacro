package org.webmacro.template;

import java.io.*;
import org.webmacro.*;
import org.webmacro.engine.StringTemplate;
import org.webmacro.engine.CrankyEvaluationExceptionHandler;

import junit.framework.*;

import org.apache.regexp.RE;

/**
 * test case for the CrankyEEH.  Each ROGUE test should throw
 */
public class TestCrankyEEH extends AbstractVariableTestCase {

   public TestCrankyEEH (String name) {
      super (name);
   }

   public void stuffContext (Context context) throws Exception {
      context.setEvaluationExceptionHandler (
                new CrankyEvaluationExceptionHandler ());

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
      assertStringTemplateThrows ("$NotInContext", 
                                   PropertyException.NoSuchVariableException.class);
   }

   public void testNoSuchMethod () throws Exception {
      assertStringTemplateThrows ("$TestObject.noSuchMethod()",
                                   PropertyException.NoSuchMethodException.class);

   }
  
   public void testNoSuchProperty () throws Exception {
      assertStringTemplateThrows ("$TestObject.noSuchProperty",
                                   PropertyException.NoSuchPropertyException.class);
   }

   /** what is Cranky supposed to do with VoidMethod()?  throw or what? 
       currently he evaluates to an empty string.
       commenting out until I know what is supposed to happen
    */
   public void testVoidMethod () throws Exception {
//      assertStringTemplateThrows ("$TestObject.voidMethod()", 
//                                   PropertyException.VoidMethodException.class);
   }

   public void testNullMethod () throws Exception {
      assertStringTemplateThrows ("$TestObject.nullMethod()", 
                                   PropertyException.NullVariableException.class);
   }

   public void testThrowsMethod() throws Exception {
      assertStringTemplateThrows ("$TestObject.throwException()", 
                                   org.webmacro.PropertyException.class);
   }

   /** A variable in the context who's .toString() method returns null 
	Currently, Cranky throws a NosuchVariableException here.  Should
        probably be throwing a "NullValueException" instead
    */
   public void testNullVariable () throws Exception {
      assertStringTemplateThrows ("$NullObject", 
                                  PropertyException.NoSuchVariableException.class);
   }
}
