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

  public void testGoodVariable () throws Exception {
    assert(false);
  }

  public void testGoodMethod () throws Exception {
    assert(false);
  }

  public void testGoodProperty () throws Exception {
    assert(false);
  }

  public void testNoSuchVariable () throws Exception {
    assert(false);
  }

  public void testNoSuchMethod () throws Exception {
    assert(false);
  }

  public void testNoSuchProperty () throws Exception {
    assert(false);
  }

  public void testVoidMethod () throws Exception {
    assert(false);
  }

  public void testNullMethod () throws Exception {
    assert(false);
  }

  public void testThrowsMethod() throws Exception {
    assert(false);
  }

   /** A variable in the context who's .toString() method returns null */
  public void testNullVariable () throws Exception {
    assert(false);
  }


  public void testEvalGoodVariable () throws Exception {
    assert(false);
  }

  public void testEvalGoodMethod () throws Exception {
    assert(false);
  }

  public void testEvalGoodProperty () throws Exception {
    assert(false);
  }

  public void testEvalNoSuchVariable () throws Exception {
    assert(false);
  }

  public void testEvalNoSuchMethod () throws Exception {
    assert(false);
  }

  public void testEvalNoSuchProperty () throws Exception {
    assert(false);
  }

  public void testEvalVoidMethod () throws Exception {
    assert(false);
  }

  public void testEvalNullMethod () throws Exception {
    assert(false);
  }

  public void testEvalThrowsMethod() throws Exception {
    assert(false);
  }

  public void testEvalNullVariable () throws Exception {
    assert(false);
  }



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
