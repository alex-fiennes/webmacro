package org.webmacro.template;

import java.io.*;
import java.util.Map;
import java.util.HashMap;
import org.webmacro.*;
import org.webmacro.engine.StringTemplate;
import org.webmacro.engine.DefaultEvaluationExceptionHandler;

import junit.framework.*;

public class TestFunction extends TemplateTestCase {

   public TestFunction(String name) {
      super (name);
   }

   public int modFunc(int num, int modulus){
      return num % modulus;
   }
   
   public void stuffContext (Context context) throws Exception {
      context.putFunction("toList", java.util.Arrays.class, "asList");
      context.putGlobalFunction("escape", org.webmacro.util.HTMLEscaper.class, "escape");
      context.putGlobalFunction("mod", this, "modFunc");
      String abcs = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
      context.putFunction("mySubstr", abcs, "substring");
   }

   public void testFunctionToList() throws Exception {
     assertStringTemplateEquals("#set $a=$toList([1,2,3]) $a.get(2)", "3");
   }
   
   public void testGlobalFunctionEscape() throws Exception {
     assertStringTemplateEquals("$escape(\"<big & bad>\")", "&lt;big &amp; bad&gt;");
   }
   
   public void testModulusFunction() throws Exception {
       assertStringTemplateEquals("$mod(4, 3)", "1");
   }

   public void testSubstringFunction() throws Exception {
       assertStringTemplateEquals("$mySubstr(24)", "YZ");
       assertStringTemplateEquals("$mySubstr(0,3)", "ABC");
   }
}
