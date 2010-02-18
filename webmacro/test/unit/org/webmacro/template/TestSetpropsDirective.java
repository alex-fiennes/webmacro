package org.webmacro.template;

import org.webmacro.Context;
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
       System.getProperties().setProperty("org.webmacro.ImpliedPackages",
       "java.util");
	   System.getProperties().setProperty("org.webmacro.AllowedPackages",
       "java.util");
      super.setUp();
   }
   
   public void stuffContext(Context context) throws Exception
   {
      context.setEvaluationExceptionHandler(new CrankyEvaluationExceptionHandler());
      context.put("User", new User());
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
      String tmpl = "#setprops $Prefs class=\"HashMap\"";
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
   public void testSetpropsThrows() throws Exception
   {
      String tmpl = "#setprops $e class=\"java.lang.Exception\"";
      tmpl += "\n{\n";
      tmpl += "}\n";
      tmpl += "$e";
      assertStringTemplateThrows(tmpl, WebMacroException.class);
   }

   /**
    * Tests the "class" option with the AllowedPackages configuration
    * option which restricts the classes that can be loaded.
    */
   public void testSetpropsAllowedPackage() throws Exception
   {
      String tmpl = "#setprops $WM class=\"org.webmacro.WM\"";
      tmpl += "\n{\n";
      tmpl += "}\n";
      tmpl += "$WM";
      System.err.println(_wm.getBroker());
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
