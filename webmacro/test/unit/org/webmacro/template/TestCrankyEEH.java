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
   
   public void testEvalGoodVariable () throws Exception {
      assertStringTemplateEquals("#set $foo=$TestObject", "");
      assertBooleanExpr("$foo == $TestObject", true);
      assertStringTemplateEquals("$foo", "TestObject");
   }

   public void testGoodMethod () throws Exception {
      assertStringTemplateEquals ("$TestObject.getString()", "String");   
   }

   public void testEvalGoodMethod () throws Exception {
      assertStringTemplateEquals("#set $foo=$TestObject.getString()", "");
      assertStringTemplateEquals("$foo", "String");
   }

   public void testGoodProperty () throws Exception {
      assertStringTemplateEquals ("$TestObject.property", "Property");
   }

   public void testEvalGoodProperty () throws Exception {
      assertStringTemplateEquals("#set $foo = $TestObject.property", "");
      assertStringTemplateEquals("$foo", "Property");
   }
   
   public void testNoSuchVariable () throws Exception {
      assertStringTemplateThrows ("$NotInContext", 
        PropertyException.NoSuchVariableException.class);
   }

   public void testEvalNoSuchVariable () throws Exception {
      assertStringTemplateThrows("#set $foo=$NotInContext", 
        PropertyException.NoSuchVariableException.class);
   }

   public void testNoSuchMethod () throws Exception {
      assertStringTemplateThrows ("$TestObject.noSuchMethod()",
        PropertyException.NoSuchMethodException.class);

   }
  
   public void testEvalNoSuchMethod () throws Exception {
      assertStringTemplateThrows("#set $foo=$TestObject.noSuchMethod()", 
        PropertyException.NoSuchMethodException.class);
   }

   public void testNoSuchProperty () throws Exception {
      assertStringTemplateThrows ("$TestObject.noSuchProperty",
        PropertyException.NoSuchPropertyException.class);
   }

   public void testEvalNoSuchProperty () throws Exception {
      assertStringTemplateThrows ("#set $foo=$TestObject.noSuchProperty",
        PropertyException.NoSuchPropertyException.class);
   }

   public void testVoidMethod () throws Exception {
      assertStringTemplateEquals ("$TestObject.voidMethod()", "");
   }

   public void testEvalVoidMethod () throws Exception {
      assertStringTemplateThrows ("#set $foo=$TestObject.voidMethod()", 
                                  PropertyException.VoidValueException.class);
   }

   public void testNullMethod () throws Exception {
      assertStringTemplateThrows ("$TestObject.nullMethod()", 
                                   PropertyException.NullValueException.class);
   }

   public void testEvalNullMethod () throws Exception {
      assertStringTemplateMatches ("#set $foo=$TestObject.nullMethod()", "");
      assertBooleanExpr("$foo == null", true);
   }

   public void testThrowsMethod() throws Exception {
      assertStringTemplateThrows ("$TestObject.throwException()", 
                                   org.webmacro.PropertyException.class);
   }

   public void testEvalThrowsMethod() throws Exception {
      assertStringTemplateThrows ("$set $foo=$TestObject.throwException()", 
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

   public void testEvalNullVariable () throws Exception {
      assertStringTemplateEquals ("#set $foo=$NullObject", "");
      assertBooleanExpr("$foo == $NullObject", true);
   }
}
