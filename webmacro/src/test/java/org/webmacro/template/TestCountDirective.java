/*
 * Created by IntelliJ IDEA.
 * User: e_ridge
 * Date: Nov 10, 2002
 * Time: 10:58:34 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.webmacro.template;

import org.webmacro.Context;

public class TestCountDirective extends TemplateTestCase
{
    public static class Counter
    {
        private int _cnt = 0;


        public void next ()
        {
            _cnt++;
        }


        public int getCount ()
        {
            return _cnt;
        }


        public String toString ()
        {
            return _cnt + "";
        }

    }


    public TestCountDirective (String name)
    {
        super(name);
    }


    protected void stuffContext (Context context) throws Exception
    {
        _context.put("Counter", new Counter());
    }


    public void testCountForwards () throws Exception
    {
        executeStringTemplate("#count $i from 1 to 10 { $Counter.next() }");
        Integer i = (Integer) _context.get("i");
        Counter c = (Counter) _context.get("Counter");
        assertTrue(i.intValue() == c.getCount());
    }


    public void testCountBackwardsImplicitStep () throws Exception
    {
        executeStringTemplate("#count $i from 10 to 1 { $Counter.next() }");
        Integer i = (Integer) _context.get("i");
        Counter c = (Counter) _context.get("Counter");
        assertTrue(c.toString(), c.getCount() == 10);
        assertTrue(i.toString(), i.intValue() == 1);
    }


    public void testCountBackwards () throws Exception
    {
        executeStringTemplate("#count $i from 10 to 1 step -1 { $Counter.next() }");
        Integer i = (Integer) _context.get("i");
        Counter c = (Counter) _context.get("Counter");
        assertTrue(c.toString(), c.getCount() == 10);
        assertTrue(i.toString(), i.intValue() == 1);
    }


    public void testCountByTwo () throws Exception
    {
        executeStringTemplate("#count $i from 1 to 10 step 2 { $Counter.next() }");
        Integer i = (Integer) _context.get("i");
        Counter c = (Counter) _context.get("Counter");
        assertTrue(c.toString(), c.getCount() == 5);
        assertTrue(i.toString(), i.intValue() == 9);
    }


    public void testCountByZero () throws Exception
    {
       // NOTE: this requires use of the DefaultEvaluationExceptionHandler to suppress the exception!
        executeStringTemplate("#count $i from 1 to 10 step 0 { $Counter.next() }");
        Integer i = (Integer) _context.get("i");
        Counter c = (Counter) _context.get("Counter");
        assertTrue(c.toString(), c.getCount() == 0);
        assertTrue(i == null);
    }


    public void testUndefArgs () throws Exception
    {
       // NOTE: this requires use of the DefaultEvaluationExceptionHandler to suppress the exception!
       executeStringTemplate("#count $i from 1 to 10 step $stepUndef { $Counter.next() }");
       Integer i = (Integer) _context.get("i");
       Counter c = (Counter) _context.get("Counter");
       assertTrue(c.toString(), c.getCount() == 0);
       assertTrue(i == null);
       executeStringTemplate("#count $i from $startUndef to 10 step 1 { $Counter.next() }");
       i = (Integer) _context.get("i");
       c = (Counter) _context.get("Counter");
       assertTrue(c.toString(), c.getCount() == 0);
       assertTrue(i == null);
       executeStringTemplate("#count $i from 1 to $stopUndef step 1 { $Counter.next() }");
       i = (Integer) _context.get("i");
       c = (Counter) _context.get("Counter");
       assertTrue(c.toString(), c.getCount() == 0);
       assertTrue(i == null);
    }

    
    public void testCountForwardsWithNegativeStep () throws Exception
    {
        executeStringTemplate("#count $i from 1 to 10 step -1 { $Counter.next() }");
        Integer i = (Integer) _context.get("i");
        Counter c = (Counter) _context.get("Counter");
        assertTrue(c.toString(), c.getCount() == 0);
        assertTrue(i == null);
    }


    public void testCountBackwardsWithPositiveStep () throws Exception
    {
        executeStringTemplate("#count $i from 10 to 1 step 1 { $Counter.next() }");
        Integer i = (Integer) _context.get("i");
        Counter c = (Counter) _context.get("Counter");
        assertTrue(c.toString(), c.getCount() == 0);
        assertTrue(i == null);
    }
}