package org.webmacro.template;

import org.webmacro.Context;
import org.webmacro.PropertyException;
import org.webmacro.engine.CrankyEvaluationExceptionHandler;
import org.webmacro.engine.DefaultEvaluationExceptionHandler;

public class TestIf extends TemplateTestCase
{

    public TestIf (String name)
    {
        super(name);
        //System.setProperty("org.webmacro.LogLevel", "DEBUG");
    }


    public void stuffContext (Context context) throws Exception
    {
        context.put("a", this);
        context.put("b", this);
    }


    public void test1 () throws Exception
    {
        String t = "#if($v==1) {One} #elseif ($v > 1 && $v < 4) {Two-Four} #else {OverThree}";
        assertStringTemplateEquals("#set $v=1\n" + t, "One");
        assertStringTemplateEquals("#set $v=2\n" + t, "Two-Four");
        assertStringTemplateEquals("#set $v=3\n" + t, "Two-Four");
        assertStringTemplateEquals("#set $v=4\n" + t, "OverThree");
        assertStringTemplateEquals("#set $v=5\n" + t, "OverThree");
    }


    public void test2 () throws Exception
    {
        assertStringTemplateEquals("#if ($a==$b) #begin pass #end #else #begin fail #end", "pass");
        assertStringTemplateEquals("#if (!($a==$b)) #begin fail #end #else #begin pass #end", "pass");
        assertStringTemplateEquals("#if ($a!=$b) #begin fail #end #else #begin pass #end", "pass");
    }


    public void test3 () throws Exception
    {
        _context.setEvaluationExceptionHandler(new CrankyEvaluationExceptionHandler(_context.getBroker()));
        assertStringTemplateThrows("#if ($foo.size() == 1){fail}#else{pass}", PropertyException.NoSuchMethodException.class);

        _context.setEvaluationExceptionHandler(new DefaultEvaluationExceptionHandler(_context.getBroker()));
        assertStringTemplateThrows("#if ($foo.size() == 1){fail}#else{pass}", PropertyException.NoSuchMethodException.class);
    }

}
