package org.webmacro.template;

import org.webmacro.Context;
import org.webmacro.engine.StringMacro;

public class TestEvalTemplet extends TemplateTestCase
{

   public TestEvalTemplet(String name)
   {
      super(name);
   }

   public void stuffContext(Context context) throws Exception
   {
      context.setEvaluationExceptionHandler(
         new org.webmacro.engine.CrankyEvaluationExceptionHandler());

      StringMacro m = new StringMacro("$A $B $C");
      context.put("m", m);

      StringMacro recursive =
         new StringMacro(
            "Title: $Node.Title\n\n"
               + "#foreach $Child in $Node.Children {Level $EvalDepth\n\n"
               + "#eval $Self using {'Node':$Child}}");
      context.put("recursive", recursive);
      Node root = new Node("Root Node");
      Node n = new Node("Child 1");
      root.addChild(n);
      n.addChild(new Node("Grandchild 1-1"));
      n.addChild(new Node("Grandchild 1-2"));
      n = new Node("Child 2");
      root.addChild(n);
      n.addChild(new Node("Grandchild 2-1"));
      n.addChild(new Node("Grandchild 2-2"));
      n.addChild(new Node("Grandchild 2-3"));
      context.put("Tree", root);
      context.put("MagicNumber", 123);
   }

   public void testTemplet() throws Exception
   {
      assertStringTemplateEquals("#set $x=99\n#templet $t {$x$x}\n$t", "9999");
   }

   public void testEvalWithMacro() throws Exception
   {
      assertStringTemplateEquals(
         "#eval $m using { 'A':1,'B':2,'C':3 }",
         "1 2 3");
   }

   public void testTempletEval() throws Exception
   {
      String prefix = "Mr.";
      String user = "Jones";
      assertStringTemplateEquals(
         "#templet $t {Dear $prefix $user}\n"
            + "#eval $t using {'prefix':'"
            + prefix
            + "', 'user':'"
            + user
            + "'}",
         "Dear " + prefix + " " + user);
   }

   public void testStringEval() throws Exception
   {
      String prefix = "Mr.";
      String user = "Jones";
      assertStringTemplateEquals(
         "#eval 'Dear \\$prefix \\$user'"
            + " using {'prefix':'"
            + prefix
            + "', 'user':'"
            + user
            + "'}",
         "Dear " + prefix + " " + user);
   }

	public void testStringEvalInline() throws Exception
	{
		String s = "#set $tmp='\\#$MagicNumber'\n"
			+ "#eval $tmp";
		assertStringTemplateEquals(s, "#123");	
	}
	
   public void testEvalRecursive() throws Exception
   {
      assertStringTemplateEquals(
         "#eval $recursive using {'Node':$Tree}",
         "Title: Root Node\n"
            + "Level 0\n"
            + "Title: Child 1\n"
            + "Level 1\n"
            + "Title: Grandchild 1-1\n"
            + "Level 1\n"
            + "Title: Grandchild 1-2\n"
            + "Level 0\n"
            + "Title: Child 2\n"
            + "Level 1\n"
            + "Title: Grandchild 2-1\n"
            + "Level 1\n"
            + "Title: Grandchild 2-2\n"
            + "Level 1\n"
            + "Title: Grandchild 2-3\n");
   }

   public class Node
   {
      private String title;
      private java.util.ArrayList children = new java.util.ArrayList();

      public Node(String title)
      {
         this.title = title;
      }
      public String getTitle()
      {
         return title;
      }
      public void addChild(Node child)
      {
         children.add(child);
      }
      public java.util.List getChildren()
      {
         return children;
      }
   }
}
