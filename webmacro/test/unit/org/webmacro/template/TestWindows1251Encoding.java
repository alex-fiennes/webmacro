package org.webmacro.template;

import java.io.*;

import org.webmacro.*;

import junit.framework.*;

/**
 * This test uses russian characters encoded
 * in windows-1251 encoding.
 * As I do not understand russion, I've no idea,
 * what the text acutally means. :)
 * @author Sebastian Kanthak
 */
public class TestWindows1251Encoding extends EncodingTestCase {
    public TestWindows1251Encoding(String name) {
        super(name);
    }

    protected String getTemplate() {
        return "Russian text: \u0420\u0443\u0441\u0441\u043a\u0438\u0439 \u0442\u0435\u043a\u0441\u0442 $chars";
    }

    protected void stuffContext(Context c) {
        c.put("chars","\u0435\u043a\u0441\u0442");
    }

    protected String getExpected() {
        return "Russian text: \u0420\u0443\u0441\u0441\u043a\u0438\u0439 \u0442\u0435\u043a\u0441\u0442 \u0435\u043a\u0441\u0442";
    }

    protected String getEncoding() {
        return "windows-1251";
    }
}
