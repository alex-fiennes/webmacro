package org.webmacro.template;

import org.webmacro.Context;

/**
 * This test uses german umlauts encoded in
 * the iso-8859-1 encoding.
 * @author Sebastian Kanthak
 */
public class TestISO88591Encoding extends EncodingTestCase
{
    public TestISO88591Encoding (String name)
    {
        super(name);
    }


    protected String getTemplate ()
    {
        return "\u00e4\u00f6\u00fc\u00df $highascii";
    }


    protected void stuffContext (Context c)
    {
        c.put("highascii", "\u00c4\u00d6\u00dc\u00df");
    }


    protected String getExpected ()
    {
        return "\u00e4\u00f6\u00fc\u00df \u00c4\u00d6\u00dc\u00df";
    }


    protected String getEncoding ()
    {
        return "iso-8859-1";
    }
}
