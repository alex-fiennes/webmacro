/*
 * Created by IntelliJ IDEA.
 * User: e_ridge
 * Date: Nov 10, 2002
 * Time: 4:15:21 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.webmacro.util;

import junit.framework.TestCase;
import org.webmacro.servlet.MathTool;

public class TestMathTool extends TestCase {
    public TestMathTool(String s) {
        super(s);
    }

    public void testRandom () {
        for (int x=0; x<1000000; x++) {
            int rnd = MathTool.random(3, 5);
            assert (""+rnd, rnd >= 3 && rnd <=5);
        }
    }
}
