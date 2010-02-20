package org.webmacro.template;

import java.util.Properties;

import org.webmacro.Broker;
import org.webmacro.Context;
import org.webmacro.WM;
import org.webmacro.WebMacroException;
import org.webmacro.engine.CrankyEvaluationExceptionHandler;

public class TestSetpropsDirective extends TemplateTestCase
{

    public TestSetpropsDirective()
    {
       super();
    }
    public TestSetpropsDirective(String name)
    {
       super(name);
    }

   protected void setUp () throws Exception
   {
	   //System.getProperties().setProperty("org.webmacro.LogLevel", "DEBUG");
      super.setUp();
   }
   
   protected void tearDown() throws Exception {
	 super.tearDown();
	 Broker.BROKERS.clear();
   }
   
   public void stuffContext(Context context) throws Exception
   {
      context.setEvaluationExceptionHandler(new CrankyEvaluationExceptionHandler());
      context.put("User", new User());
   }

   public void testSetpropsDoesNotThrowIfUnconfigured() throws Exception
   {
      String tmpl = "#setprops $e class=\"org.webmacro.WM\"";
      tmpl += "\n{\n";
      tmpl += "}\n";
      tmpl += "$e";
      assertStringTemplateEquals(tmpl, "WebMacro(WebMacro.properties)");
   }

   /**
    * Test the basic functionality of the directive: create a new hashtable and
    * sets a simple property.
    */
   public void testSetprops() throws Exception
   {
      String tmpl = "#setprops $p {A=Some Text}\n$p.A";
      assertStringTemplateEquals(tmpl, "Some Text");
   }

   /**
    * Test the creation of an empty map.
    */
   public void testSetpropsEmpty() throws Exception
   {
      String tmpl = "#setprops $p {}\n$p.Empty";
      assertStringTemplateEquals(tmpl, "true");
   }

   /**
    * This version set properties on a POJO instead of the default hashtable.
    */
   public void testSetpropsWithNonMap() throws Exception
   {
      String tmpl = "#setprops $User {\n";
      tmpl += "Name=Keats Kirsch\n";
      tmpl += "Age=45\n";
      tmpl += "Weight=190\n";
      tmpl += "\n}\n";
      tmpl += "$User.Name is $User.Age years old.";
      assertStringTemplateEquals(tmpl, "Keats Kirsch is 45 years old.");
   }

   /**
    * Tests the "class" option with the AllowedPackages configuration
    * option which restricts the classes that can be loaded.
    */
   public void testSetpropsAllowedPackage() throws Exception
   {
	   System.getProperties().setProperty("org.webmacro.AllowedPackages","java.lang,java.util,org.webmacro");
	   // overwrite the wm created in setup
	   Broker.BROKERS.clear();
       _wm = new WM(Broker.getBroker(new Properties()));
       _context = _wm.getContext();
      String tmpl = "#setprops $WM class=\"org.webmacro.WM\"";
      tmpl += "\n{\n";
      tmpl += "}\n";
      tmpl += "$WM";
      assertStringTemplateEquals(tmpl, "WebMacro(WebMacro.properties)");
      System.getProperties().setProperty("org.webmacro.AllowedPackages", "java.lang,java.util");
   }

   /**
    * This version uses the line continuation character. Notice that it needs to
    * be double escaped here (\\\\). In a template file it should be be escaped
    * once (I think).
    */
   public void testSetpropsMultiLine() throws Exception
   {
      String tmpl = "#setprops $User {\n";
      tmpl += "Name= \\\\\nKeats \\\\\nKirsch\n";
      tmpl += "Age=45\n";
      tmpl += "Weight=190\n";
      tmpl += "\n}\n";
      tmpl += "$User.Name weighs $User.Weight.";
      assertStringTemplateEquals(tmpl, "Keats Kirsch weighs 190.");
   }

   /**
    * This tests the "class" option which lets you construct any class with a
    * no-arg constructor.
    */
   public void testSetpropsWithClassName() throws Exception
   {
      String tmpl = "#setprops $Prefs class=\"java.util.HashMap\"";
      tmpl += "\n{\n";
      tmpl += "Color=Red\n";
      tmpl += "Size=Large\n";
      tmpl += "Count=3\n";
      tmpl += "}\n";
      tmpl += "$Prefs.Color $Prefs.Size $Prefs.Count";
      assertStringTemplateEquals(tmpl, "Red Large 3");
   }

   /**
    * Tests the "class" option with the ImpliedPackages configuration
    * option which lets you omit the package from the class name.
    */
   public void testSetpropsImpliedPackage() throws Exception
   {
	   System.getProperties().setProperty("org.webmacro.ImpliedPackages", "java.util");
	   // overwrite the wm created in setup
	   Broker.BROKERS.clear();
	   
       _wm = new WM(Broker.getBroker(new Properties()));
       _context = _wm.getContext();
      String tmpl = "#setprops $Prefs class=\"HashMap\"";
      tmpl += "\n{\n";
      tmpl += "Color=Red\n";
      tmpl += "Size=Large\n";
      tmpl += "Count=3\n";
      tmpl += "}\n";
      tmpl += "$Prefs.Color $Prefs.Size $Prefs.Count";
      assertStringTemplateEquals(tmpl, "Red Large 3");
   }

   public void testSetpropsThrowsOnClassNotFound() throws Exception
   {
      String tmpl = "#setprops $e class=\"org.melati.Melati\"";
      tmpl += "\n{\n";
      tmpl += "}\n";
      tmpl += "$e";
      assertStringTemplateThrows(tmpl, WebMacroException.class);
   }

   public class User
   {
      public int Age;

      private int wgt = -1;

      private String name = null;

      public void setName(String name)
      {
         this.name = name;
      }

      public String getName()
      {
         return name;
      }

      public void setWeight(int wgt)
      {
         this.wgt = wgt;
      }

      public int getWeight()
      {
         return wgt;
      }

   }
}
