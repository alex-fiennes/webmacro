/*
 * Created by IntelliJ IDEA.
 * User: e_ridge
 * Date: Nov 10, 2002
 * Time: 4:01:31 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.webmacro.util;

import junit.framework.TestCase;
import org.webmacro.servlet.TextTool;

public class TestTextTool extends TestCase {
    public TestTextTool(String s) {
        super(s);
    }

    public void testRTrim() {
        String toTrim = "This is a test     ";
        String expected = "This is a test";
        assertEquals (expected, TextTool.rtrim(toTrim));
    }
    public void testLTrim() {
        String toTrim = "      This is a test     ";
        String expected = "This is a test     ";
        assertEquals (expected, TextTool.ltrim(toTrim));
    }
}
