package org.webmacro.template;

import java.io.*;
import java.util.Map;
import java.util.HashMap;
import org.webmacro.*;
import org.webmacro.engine.StringTemplate;
import org.webmacro.engine.DefaultEvaluationExceptionHandler;

import junit.framework.*;

public class TestList extends TemplateTestCase {

  public static class Dumper {
    public String print(Object[] objects) {
      String s="";
      for (int i=0; i<objects.length; i++)
        s += objects[i].toString() + " ";
      return s;
    }
  }

  public static Dumper d = new Dumper();

   public TestList (String name) {
      super (name);
   }

   public void stuffContext (Context context) throws Exception {
      context.put ("d", d);
   }

   public void test1() throws Exception {
     assertStringTemplateEquals("#set $a=[ 1, 2, 3 ] $d.print($a)", "1 2 3 ");
     assertStringTemplateEquals("$d.print([ 1, 2, 3 ])", "1 2 3 ");
   }

}
