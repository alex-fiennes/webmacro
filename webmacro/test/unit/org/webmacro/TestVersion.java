package org.webmacro;

import junit.framework.TestCase;

/* Make sure our ANT filter to set the Version of WebMacro actually worked. */

public class TestVersion extends TestCase
{

    public TestVersion (String name)
    {
        super(name);
    }


    protected void setUp ()
    {
        // no need to do any setup work
    }


    /** 
     * ensure the build-time version has been inserted into WebMacro.java
     * by making sure the version does NOT equal the tag @VERSION@  
     */
    public void testVersion () throws Exception
    {
        assertTrue(!WebMacro.VERSION.equals("@VERSION@"));
    }


    public void testBuildDate () throws Exception
    {
        assertTrue(!WebMacro.BUILD_DATE.equals("@BUILD_DATE@"));
    }
}
