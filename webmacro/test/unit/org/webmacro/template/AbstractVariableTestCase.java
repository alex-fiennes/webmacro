package org.webmacro.template;

import java.io.*;

import org.webmacro.*;
import org.webmacro.engine.StringTemplate;

import junit.framework.*;

/** abstract variable test case for ROGUE variables/properties
 and how they're handled by each EvaluationExceptionHandler.
 It is expected that atleast 3 subclasses will exist:  TestDefaultEEH,
 TestCrankyEEH, TestComplacentEEH
*/
public abstract class AbstractVariableTestCase extends TemplateTestCase {
	
   public AbstractVariableTestCase (String name) {
      super (name);
   }

   public abstract void testGoodVariable () throws Exception;
   public abstract void testGoodMethod () throws Exception;
   public abstract void testGoodProperty () throws Exception;


   public abstract void testNoSuchVariable () throws Exception;
   public abstract void testNoSuchMethod () throws Exception;
   public abstract void testNoSuchProperty () throws Exception;

   
   public abstract void testVoidMethod () throws Exception;
   public abstract void testNullMethod () throws Exception;
   public abstract void testThrowsMethod() throws Exception;


   /** A variable in the context who's .toString() method returns null */
   public abstract void testNullVariable () throws Exception;


   /** Below are a few objects that should be used to
       do the actual testing
     */

   public static class TestObject {

      public String property = "Property";
   
      public String toString () { return "TestObject"; }

      public String getString () { return "String"; }

      public void voidMethod () { ; }

      public Object nullMethod () { return null; }

      public void throwException() throws Exception {
         throw new Exception ("GoodTestObject.throwException() was called");
      }
   }

   public static class NullTestObject extends TestObject {
      public String toString () { return null; }
   }
}
