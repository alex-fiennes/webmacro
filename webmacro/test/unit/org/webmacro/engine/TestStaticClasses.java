/*
 * Created by IntelliJ IDEA.
 * User: e_ridge
 * Date: Nov 10, 2002
 * Time: 4:28:49 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.webmacro.engine;

import org.webmacro.Context;
import org.webmacro.template.TemplateTestCase;

public class TestStaticClasses extends TemplateTestCase
{

    public static class Simple
    {
        public static String foo (String arg, String arg2)
        {
            if (arg == null)
                return "arg is null";
            else
                return arg;
        }
    }


    public TestStaticClasses (String s)
    {
        super(s);
    }


    protected void stuffContext (Context context) throws Exception
    {
        _context.put("SimpleInstance", new Simple());
        _context.put("SimpleClass", Simple.class);
    }


    public void testStaticClassWithNullAsFirstArg_1 () throws Exception
    {
        assertEvaluationEquals("$SimpleClass.foo(null, null)", "arg is null");
        assertEvaluationEquals("$SimpleInstance.foo(null, null)", "arg is null");
    }


    public void testStaticClassWithNullAsFirstArg_2 () throws Exception
    {
        executeStringTemplate("$SimpleClass.foo(null, null)");
        executeStringTemplate("$SimpleInstance.foo(null, null)");
    }
}
