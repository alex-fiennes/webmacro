package org.webmacro.template;

import java.io.*;

import org.webmacro.*;
import org.webmacro.util.*;
import org.webmacro.engine.*;

import junit.framework.*;

public class TestArithmetic extends TemplateTestCase {

  public TestArithmetic(String name) {
    super(name);
  }

  private int N = 7;
  private String[] 
    integers = new String[] { "in5", "in2", "in1", "i0", "i1", "i2", "i5" },
    longs = new String[] { "ln5", "ln2", "ln1", "l0", "l1", "l2", "l5" },
    shorts = new String[] { "sn5", "sn2", "sn1", "s0", "s1", "s2", "s5" },
    bS = new String[] { "btrue", "bfalse" };
  private int[] values = new int[] { -5, -2, -1, 0, 1, 2, 5};
  private boolean[] bV = new boolean[] { true, false };

  protected void stuffContext(Context c) {
    for (int i=0; i<N; i++) {
      c.put(integers[i], new Integer((int) values[i]));
      c.put(longs[i], new Long(values[i]));
      c.put(shorts[i], new Short((short) values[i]));
    }
    for (int i=0; i<2; i++) 
      c.put(bS[i], new Boolean(bV[i]));
  }

  protected void setUp() throws Exception {
    super.setUp();
  }
  
  public void assertBinaryComparison(String a, String b, String op, 
                                     boolean yesno) {
    assertExpr("$" + a + " " + op + " " + "$" + b, yesno);
  }

  /** Asserts that two variables are equal according to the WM ==
   * operator */
  public void assertVarsEqual(String a, String b, boolean yesno) {
    assertBinaryComparison(a, b, "==", yesno);
  }

  public void assertArithmeticExpr(String expr, int result) {
    assertStringTemplateEquals("#set $result=(" + expr + ") $result", 
                               Integer.toString(result));
  }
  
  public void assertBinaryExpr(String a, String b, String op, 
                               int result) {
    assertArithmeticExpr("$" + a + " " + op + " " + "$" + b, result);
  }

  public void testAssociativity() {
    assertArithmeticExpr("1 + 2 + 3 - 2 -1 + -4 - -4", 
                          1 + 2 + 3 - 2 -1 + -4 - -4);
    assertArithmeticExpr("1 + 1 - 1 + 1", 
                          1 + 1 - 1 + 1);
    assertArithmeticExpr("1 * 2 * 3 / 2 * -4 / -4", 
                          1 * 2 * 3 / 2 * -4 / -4);
  }

  public void testParenRounding() {
    assertArithmeticExpr("(((48984375/100)*100)/100)*100-100+100",
                          (((48984375/100)*100)/100)*100-100+100
                        );
    // embed some spaces for readability:
    assertArithmeticExpr("(((48984375 / 100) * 100) / 100) * 100 - 100 + 100",
                          (((48984375 / 100) * 100) / 100) * 100 - 100 + 100
                        );
  }

  public void testBoolean() {
    for (int i=0; i<2; i++) 
      for (int j=0; j<2; j++) {
        assertBooleanExpr("$" + bS[i] + " AND " + "$" + bS[j],
                          bV[i] && bV[j]);
        assertBooleanExpr("$" + bS[i] + " && " + "$" + bS[j],
                          bV[i] && bV[j]);
        assertBooleanExpr("$" + bS[i] + " OR " + "$" + bS[j],
                          bV[i] || bV[j]);
        assertBooleanExpr("$" + bS[i] + " || " + "$" + bS[j],
                          bV[i] || bV[j]);

        assertBooleanExpr("$" + bS[i] + " && " + "!$" + bS[j],
                          bV[i] && !bV[j]);
        assertBooleanExpr("!$" + bS[i] + " && " + "$" + bS[j],
                          !bV[i] && bV[j]);
        assertBooleanExpr("!$" + bS[i] + " && " + "!$" + bS[j],
                          !bV[i] && !bV[j]);

        assertBooleanExpr("$" + bS[i] + " || " + "!$" + bS[j],
                          bV[i] || !bV[j]);
        assertBooleanExpr("!$" + bS[i] + " || " + "$" + bS[j],
                          !bV[i] || bV[j]);
        assertBooleanExpr("!$" + bS[i] + " || " + "!$" + bS[j],
                          !bV[i] || !bV[j]);

        // De Morgan's laws
        assertBooleanExpr("!($" + bS[i] + " || " + "$" + bS[j] + ")",
                          !bV[i] && !bV[j]);
        assertBooleanExpr("!($" + bS[i] + " && " + "$" + bS[j] + ")",
                          !bV[i] || !bV[j]);
        assertBooleanExpr("(!$" + bS[i] + " || " + "!$" + bS[j] + ")",
                          !(bV[i] && bV[j]));
        assertBooleanExpr("(!$" + bS[i] + " && " + "!$" + bS[j] + ")",
                          !(bV[i] || bV[j]));
      }
  }

  public void testBinary()  {
    for (int i=0; i<N; i++) 
      for (int j=0; j<N; j++) {
        assertBinaryExpr(integers[i], integers[j], 
                         "+", values[i] + values[j]);
        assertBinaryExpr(integers[i], integers[j], 
                         "-", values[i] - values[j]);
        assertBinaryExpr(integers[i], integers[j], 
                         "*", values[i] * values[j]);
        if (values[j] != 0)
          assertBinaryExpr(integers[i], integers[j], 
                           "/", values[i] / values[j]);
      }
  }

  public void testCompare()  {
    for (int i=0; i<N; i++) 
      for (int j=0; j<N; j++) {
        assertBinaryComparison(integers[i], integers[j], 
                         "==", values[i] == values[j]);
        assertBinaryComparison(integers[i], integers[j], 
                         "!=", values[i] != values[j]);
        assertBinaryComparison(integers[i], integers[j], 
                         "<", values[i] < values[j]);
        assertBinaryComparison(integers[i], integers[j], 
                         ">", values[i] > values[j]);
        assertBinaryComparison(integers[i], integers[j], 
                         "<=", values[i] <= values[j]);
        assertBinaryComparison(integers[i], integers[j], 
                         ">=", values[i] >= values[j]);
      }
  }

  public void testEquality()  {
    for (int i=0; i<N; i++) {
      for (int j=0; j<N; j++) {
        assertVarsEqual(integers[i], longs[j], i == j);
        assertVarsEqual(integers[i], shorts[j], i == j);
        assertVarsEqual(longs[i], shorts[j], i == j);
        assertVarsEqual(integers[i], integers[j], i == j);
        assertVarsEqual(longs[i], longs[j], i == j);
        assertVarsEqual(shorts[i], shorts[j], i == j);
      }
    }
  }
}
