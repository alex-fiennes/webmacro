package org.webmacro.template;



/** 
 * Abstract variable test case for ROGUE variables/properties
 *  and how they're handled by each EvaluationExceptionHandler.
 *  
 *   It is expected that at least 3 subclasses will exist:  TestDefaultEEH,
 *   TestCrankyEEH, TestComplacentEEH.
 */
public abstract class AbstractVariableTestCase extends TemplateTestCase
{

    public static class ObjectWithNullMethod
    {
        public Object getNull ()
        {
            return null;
        }
    }


    public AbstractVariableTestCase (String name)
    {
        super(name);
    }


    public void testGoodVariable () throws Exception
    {
        assertTrue(false);
    }


    public void testGoodMethod () throws Exception
    {
        assertTrue(false);
    }


    public void testGoodProperty () throws Exception
    {
        assertTrue(false);
    }


    public void testNoSuchVariable () throws Exception
    {
        assertTrue(false);
    }


    public void testNoSuchMethod () throws Exception
    {
        assertTrue(false);
    }


    public void testNoSuchProperty () throws Exception
    {
        assertTrue(false);
    }


    public void testVoidMethod () throws Exception
    {
        assertTrue(false);
    }


    public void testNullMethod () throws Exception
    {
        assertTrue(false);
    }


    public void testThrowsMethod () throws Exception
    {
        assertTrue(false);
    }


    /** A variable in the context who's .toString() method returns null. */
    public void testNullVariable () throws Exception
    {
        assertTrue(false);
    }


    public void testEvalGoodVariable () throws Exception
    {
        assertTrue(false);
    }


    public void testEvalGoodMethod () throws Exception
    {
        assertTrue(false);
    }


    public void testEvalGoodProperty () throws Exception
    {
        assertTrue(false);
    }


    public void testEvalNoSuchVariable () throws Exception
    {
        assertTrue(false);
    }


    public void testEvalNoSuchMethod () throws Exception
    {
        assertTrue(false);
    }


    public void testNoSuchMethodWithArguments () throws Exception
    {
        assertTrue(false);
    }


    public void testEvalNoSuchMethodWithArguments () throws Exception
    {
        assertTrue(false);
    }


    public void testEvalNoSuchProperty () throws Exception
    {
        assertTrue(false);
    }


    public void testEvalVoidMethod () throws Exception
    {
        assertTrue(false);
    }


    public void testEvalNullMethod () throws Exception
    {
        assertTrue(false);
    }


    public void testEvalThrowsMethod () throws Exception
    {
        assertTrue(false);
    }


    public void testEvalNullVariable () throws Exception
    {
        assertTrue(false);
    }


    /**
     * This is the old "... --possibly null?" error message.
     * It has always thrown a PropertyExcpetion, and probably still should.
     */
    public void testEvaluationOfNullReturnValue () throws Exception
    {
        assertTrue(false);
    }


    /** Below are a few objects that should be used to
     do the actual testing
     */

    public static class TestObject
    {

        public String property = "Property";


        public String toString ()
        {
            return "TestObject";
        }


        public String getString ()
        {
            return "String";
        }


        public void voidMethod ()
        {
            ;
        }


        public Object nullMethod ()
        {
            return null;
        }


        public void throwException () throws Exception
        {
            throw new Exception("GoodTestObject.throwException() was called");
        }
    }

    public static class NullTestObject extends TestObject
    {
        public String toString ()
        {
            return null;
        }
    }
}
