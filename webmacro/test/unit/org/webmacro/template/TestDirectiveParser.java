package org.webmacro.template;

import java.io.*;
import org.webmacro.*;
import org.webmacro.engine.*;
import org.webmacro.directive.*;
import org.webmacro.engine.StringTemplate;
import org.webmacro.engine.DefaultEvaluationExceptionHandler;

import junit.framework.*;

import org.apache.regexp.RE;


/**
 * Test the pluggable directive parser.  
 */
public class TestDirectiveParser extends TemplateTestCase {

  public TestDirectiveParser (String name) {
    super (name);
  }
  
  public void init() throws Exception {
    System.getProperties().setProperty("org.webmacro.LogLevel", "NONE");
    super.init();
  }

  public void stuffContext (Context context) throws Exception {
  }
  
  public void registerDirective(String name, String clazz) throws Exception {
    DirectiveProvider dp = (DirectiveProvider) _wm.getBroker().getProvider(DirectiveProvider.DIRECTIVE_KEY);
    dp.register(clazz, name);
  }

  public static class DirectiveOne extends Directive {

    private static final ArgDescriptor[] 
      myArgs = new ArgDescriptor[] {
        new KeywordArg(1, "one"),
        new KeywordArg(2, "two"),
        new OptionalGroup(1), 
          new KeywordArg(3, "three"),
        new OptionalGroup(1), 
          new KeywordArg(4, "four")
            };

    private static final DirectiveDescriptor 
      myDescr = new DirectiveDescriptor(null, null, myArgs, null);

    public static DirectiveDescriptor getDescriptor() {
      return myDescr;
    }

    public DirectiveOne() {}
    
    public Object build(DirectiveBuilder builder, BuildContext bc) { 
      return null; 
    }
    
    public void write(FastWriter out, Context context) {}
  }

  public void testKeywords() throws Exception {
    registerDirective("one", 
                      "org.webmacro.template.TestDirectiveParser$DirectiveOne");
    assertStringTemplateEquals("#one one two", "");
    assertStringTemplateEquals("#one one two three", "");
    assertStringTemplateEquals("#one one two four", "");
    assertStringTemplateEquals("#one one two three four", "");
    assertStringTemplateMatches("#one one three", "^<!--.*failed.*");
  }
}
