package org.webmacro.template;

import java.io.*;
import org.webmacro.*;
import org.webmacro.engine.StringTemplate;
import org.webmacro.engine.DefaultEvaluationExceptionHandler;

import junit.framework.*;

import org.apache.regexp.RE;

/**
 * Tests the cases from DEEH that throw/return errors to make sure
 * the message includes line/column information.
 */
public class TestLineNumbers extends AbstractVariableTestCase {
   public static final String LINECOL_REGEX = " at unknown:\\d+.\\d+";

   public TestLineNumbers (String name) {
      super (name);
   }

   public void stuffContext (Context context) throws Exception {
      context.setEvaluationExceptionHandler (
                new DefaultEvaluationExceptionHandler ());

      context.put ("TestObject", new TestObject());
      context.put ("NullTestObject", new NullTestObject());
   }

   public void testNoSuchVariable () throws Exception {
      assertStringTemplateMatches ("$NotInContext", LINECOL_REGEX);
   }


   public void testNoSuchMethod () throws Exception {
      assertStringTemplateThrows ("$TestObject.noSuchMethod()",
        PropertyException.NoSuchMethodException.class, LINECOL_REGEX);

   }

   public void testNoSuchMethodWithArguments () throws Exception {
      assertStringTemplateThrows ("$TestObject.toString('foo', false, 1)",
        PropertyException.NoSuchMethodWithArgumentsException.class, LINECOL_REGEX);
   }

   public void testEvalNoSuchMethod () throws Exception {
      assertStringTemplateThrows("#set $foo=$TestObject.noSuchMethod()",
        PropertyException.NoSuchMethodException.class, LINECOL_REGEX);
   }

   public void testEvalNoSuchMethodWithArguments () throws Exception {
      assertStringTemplateThrows ("#set $foo=$TestObject.toString('foo', false, 1)",
        PropertyException.NoSuchMethodWithArgumentsException.class, LINECOL_REGEX);
   }

   public void testNoSuchProperty () throws Exception {
      assertStringTemplateThrows ("$TestObject.noSuchProperty",
        PropertyException.NoSuchPropertyException.class, LINECOL_REGEX);
   }

   public void testEvalNoSuchProperty () throws Exception {
      assertStringTemplateThrows ("#set $foo=$TestObject.noSuchProperty",
        PropertyException.NoSuchPropertyException.class, LINECOL_REGEX);
   }


   public void testVoidMethod () throws Exception {
   }

   public void testEvalVoidMethod () throws Exception {
      assertStringTemplateThrows ("#set $foo=$TestObject.voidMethod()",
         PropertyException.VoidValueException.class, LINECOL_REGEX);
   }

   public void testNullMethod () throws Exception {
      assertStringTemplateMatches ("$TestObject.nullMethod()",
        LINECOL_REGEX);
   }

   public void testThrowsMethod() throws Exception {
      assertStringTemplateThrows ("$TestObject.throwException()",
                                   PropertyException.class, LINECOL_REGEX);
   }

   public void testEvalThrowsMethod() throws Exception {
      assertStringTemplateThrows ("$set $foo=$TestObject.throwException()",
                                   PropertyException.class, LINECOL_REGEX);
   }

  // @@@ The behavior should probably be changed for this
   public void testNullVariable () throws Exception {
      assertStringTemplateMatches ("$NullObject", LINECOL_REGEX);
   }


   public void testEvalNullMethod () throws Exception {
   }

   public void testGoodVariable () throws Exception {
   }

   public void testEvalGoodVariable () throws Exception {
   }

   public void testGoodMethod () throws Exception {
   }

   public void testEvalGoodMethod () throws Exception {
   }

   public void testGoodProperty () throws Exception {
   }

   public void testEvalGoodProperty () throws Exception {
   }

   public void testEvalNullVariable () throws Exception {
   }

   public void testEvalNoSuchVariable () throws Exception {
   }
}
