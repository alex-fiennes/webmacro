package org.webmacro.template;

import java.io.*;

import org.webmacro.*;
import org.webmacro.util.*;

import junit.framework.*;
import org.apache.regexp.RE;

public class TestVariables extends TemplateTestCase {

  public TestVariables(String name) {
    super(name);
  }

  protected void stuffContext(Context context) {
    context.put("TestObject", new TestObject());
  }

  protected void setUp() throws Exception {
    super.setUp();
  }
  
  public static class TestObject
  {
    public String getString() { return "String"; }
    public int getInteger() { return 1; }

    public boolean returnTrue() { return true; }
    public boolean returnFalse() { return false; }

    public String toString () { return "TestObject"; }      

    public String returnNull () { return null; }      

    public void voidMethod() {  }

    public String throwException () throws Exception { 
      throw new Exception ("boo!"); 
    }
  }

  public void testDefaultEEHExpand() throws Exception {
    String result;

    result = executeStringTemplate("$TestObject");
    assert(result.equals("TestObject"));
    result = executeStringTemplate("");
    assert(result.equals(""));
  }
}




