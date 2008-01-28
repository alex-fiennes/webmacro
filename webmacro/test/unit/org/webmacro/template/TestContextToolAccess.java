
package org.webmacro.template;

import org.webmacro.Context;

/**
 * Note: Tests in this class must allow for the tool not to be loaded.
 * @author Marc Palmer (<a href="mailto:wj5@wangjammers.org">wj5@wangjammers.org</a>)
 */
public class TestContextToolAccess extends TemplateTestCase
{

    public TestContextToolAccess( String name )
    {
        super( name );
    }

    public void testContextToolMethodCall()
    {
      assertStringTemplateEquals("#if ($Text) {$Text.HTMLEncode('&')} #else {&amp;}", "&amp;");
    }

    protected void stuffContext( Context context ) throws Exception
    {
    }
}
