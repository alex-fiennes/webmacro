package org.webmacro.template;

import java.io.*;
import java.util.Map;
import java.util.HashMap;
import org.webmacro.*;
import org.webmacro.engine.StringTemplate;
import org.webmacro.engine.DefaultEvaluationExceptionHandler;

import junit.framework.*;

public class TestIf extends TemplateTestCase {

   public TestIf (String name) {
      super (name);
   }

   public void stuffContext (Context context) throws Exception {
   }

   public void test1() throws Exception {
      String t = "#if($v==1) {One} #elseif ($v > 1 && $v < 4) {Two-Four} #else {OverThree}";
      assertStringTemplateEquals("#set $v=1\n" + t, "One");
      assertStringTemplateEquals("#set $v=2\n" + t, "Two-Four");
      assertStringTemplateEquals("#set $v=3\n" + t, "Two-Four");
      assertStringTemplateEquals("#set $v=4\n" + t, "OverThree");
      assertStringTemplateEquals("#set $v=5\n" + t, "OverThree");
   }

}
