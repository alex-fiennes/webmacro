package org.webmacro.template;

import java.io.*;
import org.webmacro.*;
import org.webmacro.engine.StringTemplate;
import org.webmacro.engine.DefaultEvaluationExceptionHandler;

import junit.framework.*;

import org.apache.regexp.RE;


/**
 * tests whitespace.
 */
public class TestWhitespace extends TemplateTestCase {

   public TestWhitespace (String name) {
      super (name);
   }

   public void stuffContext (Context context) throws Exception {
      context.setEvaluationExceptionHandler (
                new DefaultEvaluationExceptionHandler ());

      context.put ("Array", new String[] {"one", "two", "three"});
   }


   public void testTabs() throws Exception { 
     assertStringTemplateEquals("\t#if (true) { pass }\n\t#else { fail }", 
                                " pass ");
     assertStringTemplateEquals("\t#if (true) \t#begin pass \t#end\t#else \t#begin fail \t#end", 
                                "pass ");
   }

   //
   // test whitespace before a comment
   //
   public void testBeforeComment () throws Exception {
      // do it with the implicit "begin"
      String tmpl = "#if (true)\n   ## Don't say anything\n#end";
      assertStringTemplateEquals (tmpl, "\n");

      tmpl = "#if (true)\n## Don't say anything\n#end";
      assertStringTemplateEquals (tmpl, "\n");



      // do it with the explicit #begin
      tmpl = "#if (true)\n#begin\n   ## Don't say anything\n#end";
      assertStringTemplateEquals (tmpl, "\n");

      tmpl = "#if (true)\n#begin\n## Don't say anything\n#end";
      assertStringTemplateEquals (tmpl, "\n");



      // Geeks do it with braces
      tmpl = "#if (true){   ## Don't say anything\n}";
      assertStringTemplateEquals (tmpl, "");

      tmpl = "#if (true){## Don't say anything\n}";
      assertStringTemplateEquals (tmpl, "");



      // don't do it in a block
      tmpl = "   ## Don't say anything";
      assertStringTemplateEquals (tmpl, "");
   }

   //
   // test whitespace before a directive
   //

   public void testBeforeDirective () throws Exception {
      assertStringTemplateEquals ("  \n#if(true){pass}", "  pass");
      assertStringTemplateEquals ("\n   #if(true){pass}", "pass");
      assertStringTemplateEquals ("\n  \n#if(true){pass}", "\n  pass");

      assertStringTemplateEquals("#set $a=1\n#set $b=2\n", "");
      assertStringTemplateEquals("Hello\n#if(true)\n{ Brian }\n", "Hello Brian ");
   }
 
   public void testBeforeSubdirective () throws Exception {
      assertStringTemplateEquals ("#if(true){pass} #else{fail}", "pass");
      assertStringTemplateEquals ("#if(true){pass} \n#else{fail}", "pass");
      assertStringTemplateEquals ("#if(true){pass} \n #else{fail}", "pass");
   }

   //
   // test whitespace after a directive
   //

   public void testAfterDirective () throws Exception {
      assertStringTemplateEquals ("#set $a = \"foo\"   \npass", "pass");
      assertStringTemplateEquals ("  #set  $a = \"foo\"   \npass", "pass");
   }


   // 
   // test whitespace after a begin 
   //

   public void testAfterBegin () throws Exception {
      assertStringTemplateEquals ("#if(true)#begin  \npass#end", "pass");
      assertStringTemplateEquals ("#if(true)#begin  \n pass#end", " pass");
      assertStringTemplateEquals ("#if(true)#begin  pass#end", " pass");
      assertStringTemplateEquals ("#if(true)#begin pass#end", "pass");
      assertStringTemplateEquals ("#if(true) {pass}", "pass");
      assertStringTemplateEquals ("#if(true){ pass}", " pass");
      assertStringTemplateEquals ("#if(true){ pass }", " pass ");
      assertStringTemplateEquals ("#if(true){\n pass}", " pass");
      assertStringTemplateEquals ("#if(true){ \n pass}", " pass");
      assertStringTemplateEquals ("#if(true){  \npass}", "pass");
      assertStringTemplateEquals ("#if(true)  \npass #end", "pass");
      assertStringTemplateEquals ("#if(true) pass #end", "pass");
      assertStringTemplateEquals ("#if(true)  \n pass #end", " pass");
   }


   //
   // test whitespace before an end
   //

   public void testBeforeEnd () throws Exception {
      assertStringTemplateEquals ("#if(true)#begin pass #end", "pass");
      assertStringTemplateEquals ("#if(true)#begin  pass  #end", " pass ");
      assertStringTemplateEquals ("#if(true)#begin pass \n #end", "pass \n");
      assertStringTemplateEquals ("#if(true)#begin pass \n#end", "pass \n");
      assertStringTemplateEquals ("#if(true)#begin pass\n #end", "pass\n");
      assertStringTemplateEquals ("#if(true)#begin pass  \n  #end", "pass  \n ");
      assertStringTemplateEquals ("#if(true){pass}", "pass");
      assertStringTemplateEquals ("#if(true){pass \n}", "pass \n");
      assertStringTemplateEquals ("#if(true){pass\n }", "pass\n ");
      assertStringTemplateEquals ("#if(true){pass  }", "pass  ");
      assertStringTemplateEquals ("#if(true){pass  \n  }", "pass  \n  ");
      assertStringTemplateEquals ("#if(true) pass #end", "pass");
      assertStringTemplateEquals ("#if(true) pass  #end", "pass ");
      assertStringTemplateEquals ("#if(true) pass \n#end", "pass \n");
      assertStringTemplateEquals ("#if(true) pass\n #end", "pass\n");
      assertStringTemplateEquals ("#if(true) pass \n #end", "pass \n");
      assertStringTemplateEquals ("#if(true) pass  \n  #end", "pass  \n ");
   }
 
   //
   // test whitespace after an end
   //

   public void testAfterEnd () throws Exception {
      assertStringTemplateEquals ("#if(true)#begin pass #end", "pass");
      assertStringTemplateEquals ("#if(true)#begin pass #end\n", "pass");
      assertStringTemplateEquals ("#if(true)#begin pass #end \n", "pass");
      assertStringTemplateEquals ("#if(true)#begin pass #end\n ", "pass ");
      assertStringTemplateEquals ("#if(true)#begin pass #end \n ", "pass ");
      assertStringTemplateEquals ("#if(true){pass}", "pass");
      assertStringTemplateEquals ("#if(true){pass}\n", "pass");
      assertStringTemplateEquals ("#if(true){pass} \n", "pass");
      assertStringTemplateEquals ("#if(true){pass}\n ", "pass ");
      assertStringTemplateEquals ("#if(true){pass} \n ", "pass ");
      assertStringTemplateEquals ("#if(true){pass}  \n  ", "pass  ");
      assertStringTemplateEquals ("#if(true) pass #end", "pass");
      assertStringTemplateEquals ("#if(true) pass #end\n", "pass");
      assertStringTemplateEquals ("#if(true) pass #end \n", "pass");
      assertStringTemplateEquals ("#if(true) pass #end\n ", "pass ");
      assertStringTemplateEquals ("#if(true) pass #end \n ", "pass ");
      assertStringTemplateEquals ("#if(true) pass #end  \n  ", "pass  ");
   }

   public void testSpacesInParens() throws Exception {
      String assn = "#set $s=\"String\" ";
      assertStringTemplateEquals(assn + "$s", "String");
      assertStringTemplateEquals(assn + "$s.substring(1,4)", "tri");
      assertStringTemplateEquals(assn + "$s.substring( 1,4)", "tri");
      assertStringTemplateEquals(assn + "$s.substring( 1 ,4)", "tri");
      assertStringTemplateEquals(assn + "$s.substring( 1 , 4)", "tri");
      assertStringTemplateEquals(assn + "$s.substring( 1 , 4 )", "tri");
   }

   public void testForeach() throws Exception {
      assertStringTemplateEquals("#foreach $i in $Array {$i}", "onetwothree");
      assertStringTemplateEquals("#foreach $i in $Array { $i}", " one two three");
      assertStringTemplateEquals("#foreach $i in $Array {$i }", "one two three ");
      assertStringTemplateEquals("#foreach $i in $Array { $i }", " one  two  three ");
      assertStringTemplateEquals("#foreach $i in $Array {\n$i}", "onetwothree");
      assertStringTemplateEquals("#foreach $i in $Array {$i\n}", "one\ntwo\nthree\n");
      assertStringTemplateEquals("#foreach $i in $Array { \n $i}", " one two three");
      assertStringTemplateEquals("#foreach $i in $Array {$i \n }", "one \n two \n three \n ");
      assertStringTemplateEquals("#foreach $i in $Array { \n $i \n }", " one \n  two \n  three \n ");

      assertStringTemplateEquals("#foreach $i in $Array #begin $i#end", "onetwothree");
      assertStringTemplateEquals("#foreach $i in $Array #begin  $i#end", " one two three");
      assertStringTemplateEquals("#foreach $i in $Array #begin $i #end", "onetwothree");
      assertStringTemplateEquals("#foreach $i in $Array #begin  $i #end", " one two three");
      assertStringTemplateEquals("#foreach $i in $Array #begin \n$i#end", "onetwothree");
      assertStringTemplateEquals("#foreach $i in $Array #begin $i\n#end", "one\ntwo\nthree\n");
      assertStringTemplateEquals("#foreach $i in $Array #begin  \n $i#end", " one two three");
      assertStringTemplateEquals("#foreach $i in $Array #begin $i \n #end", "one \ntwo \nthree \n");
      assertStringTemplateEquals("#foreach $i in $Array #begin  \n $i \n #end", " one \n two \n three \n");

      assertStringTemplateEquals("#foreach $i in $Array $i#end", "onetwothree");
      assertStringTemplateEquals("#foreach $i in $Array  $i#end", "onetwothree");
      assertStringTemplateEquals("#foreach $i in $Array $i #end", "onetwothree");
      assertStringTemplateEquals("#foreach $i in $Array  $i #end", "onetwothree");
      assertStringTemplateEquals("#foreach $i in $Array \n$i#end", "onetwothree");
      assertStringTemplateEquals("#foreach $i in $Array $i\n#end", "one\ntwo\nthree\n");
      assertStringTemplateEquals("#foreach $i in $Array  \n $i#end", " one two three");
      assertStringTemplateEquals("#foreach $i in $Array $i \n #end", "one \ntwo \nthree \n");
      assertStringTemplateEquals("#foreach $i in $Array  \n $i \n #end", " one \n two \n three \n");
   }

   public void testColor() throws Exception {
      assertStringTemplateEquals("layer-background-color:#FFFFCC", 
                                 "layer-background-color:#FFFFCC");
   }

}
