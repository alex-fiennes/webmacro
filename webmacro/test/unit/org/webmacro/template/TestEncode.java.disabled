package org.webmacro.template;

import java.io.*;
import java.util.Map;
import java.util.HashMap;
import org.webmacro.*;
import org.webmacro.engine.StringTemplate;
import org.webmacro.engine.DefaultEvaluationExceptionHandler;
import java.net.URLEncoder;

import junit.framework.*;

import org.apache.regexp.RE;

public class TestEncode extends TemplateTestCase {

   public TestEncode(String name) {
      super (name);
   }

   public void stuffContext (Context context) throws Exception {
      context.setEvaluationExceptionHandler (
                new DefaultEvaluationExceptionHandler ());
   }

   public void test1() throws Exception {
     String s = "<tag />'This' & That";
     String s2 = URLEncoder.encode(s);
     assertStringTemplateEquals ("#encode $s\n#set $s=\"" + s + "\"\n$s", s2); 
     assertStringTemplateEquals ("#set $s=\"" + s + "\"\n#encode $s\n$s", s2); 
   }
}
