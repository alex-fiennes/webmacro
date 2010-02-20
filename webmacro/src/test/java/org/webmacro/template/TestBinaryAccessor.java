
package org.webmacro.template;

import org.webmacro.Context;

import java.util.Hashtable;
import java.util.Map;

/**
 * @author Marc Palmer (<a href="mailto:wj5@wangjammers.org">wj5@wangjammers.org</a>)
 */
public class TestBinaryAccessor extends TemplateTestCase
{

    public TestBinaryAccessor( String name )
    {
        super( name );
    }

    public static class Wrapper
    {

        private Map params = new Hashtable();

        public Object getParameters( String key )
        {
            return params.get( key );
        }

        public void setParameters( String key, Object value )
        {
            params.put( key, value );
        }
    }

    public void testBinaryGet()
    {
        assertStringTemplateEquals("$obj1.Parameters.Name", "Marc");
    }

    public void testBinarySet()
    {
        assertStringTemplateEquals("#set $obj1.Parameters.Name = 'Eric'", "");
    }

    public void testBinarySetGet()
    {
        assertStringTemplateEquals(
                "#set $obj1.Parameters.Name = 'Eric'\n" +
                "$obj1.Parameters.Name", "Eric");
    }

    public void testBinaryMultiLevelGet()
    {
        assertStringTemplateEquals("$obj2.Parameters.BinaryLevelTwo.Parameters.Name", "Brian");
    }

    public void testBinaryMultiLevelSet()
    {
        assertStringTemplateEquals("#set $obj2.Parameters.BinaryLevelTwo.Parameters.Name = 'Keats'", "");
    }

    public void testBinaryMultiLevelSetGet()
    {
        assertStringTemplateEquals(
           "#set $obj2.Parameters.BinaryLevelTwo.Parameters.Name = 'Lane'\n"+
           "$obj2.Parameters.BinaryLevelTwo.Parameters.Name", "Lane");
    }

    protected void stuffContext( Context context ) throws Exception
    {
        final Wrapper value1 = new Wrapper();
        value1.setParameters( "Name", "Marc");

        context.put( "obj1", value1 );

        final Wrapper value2 = new Wrapper();
        final Wrapper subWrapper = new Wrapper();
        subWrapper.setParameters( "Name", "Brian");
        value2.setParameters( "BinaryLevelTwo", subWrapper);
        context.put( "obj2", value2 );
    }
}
