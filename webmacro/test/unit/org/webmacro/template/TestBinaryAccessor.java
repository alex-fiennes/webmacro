/*
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.mozilla.org/MPL/
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Initial Developer of this code is Wangjammers
 * Copyright (C) 2003 Wangjammers.
 *
 * More information about the Ignition Web-Application can be found at:
 * http://www.wangjammers.org/ignition
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of Wangjammers and/or the WebMacro project.
 * For more information on the Wangjammers and WebMacro, please see
 * <http://www.wangjammers.org/> and <http://www.webmacro.org/>
 * respectively.
 *
 */
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
