
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

        public String getParameters( String key )
        {
            return (String) params.get( key );
        }

        public void setParameters( String key, Object value )
        {
            params.put( key, value );
        }
    }

    public void testBinaryGet()
    {
        assertStringTemplateEquals("$obj.Parameters.Name", "Marc");
    }

    public void testBinarySet()
    {
        assertStringTemplateEquals("#set $obj.Parameters.Name = 'Eric'", "");
    }

    public void testBinarySetGet()
    {
        assertStringTemplateEquals(
                "#set $obj.Parameters.Name = 'Eric'\n" +
                "$obj.Parameters.Name", "Eric");
    }

    protected void stuffContext( Context context ) throws Exception
    {
        final Wrapper value = new Wrapper();
        value.setParameters( "Name", "Marc");

        context.put( "obj", value );
    }
}
