package org.webmacro.template;

import org.webmacro.Context;

public class TestEvalTemplet extends TemplateTestCase
{

    public TestEvalTemplet(String name)
    {
        super(name);
    }    

    public void stuffContext (Context context) throws Exception
    {
        context.setEvaluationExceptionHandler(
                new org.webmacro.engine.CrankyEvaluationExceptionHandler());
        
        StringMacro m = new StringMacro("$A $B $C");
        context.put("m", m);
        
        StringMacro recursive = new StringMacro(
            "Title: $Node.Title\n\n" +
            "#foreach $Child in $Node.Children {Level $EvalDepth\n\n#eval $Self using {'Node':$Child}}");
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
    }


    public void testTemplet () throws Exception
    {
        assertStringTemplateEquals("#set $x=99\n#templet $t {$x$x}\n$t", "9999");
    }
    
    public void testEvalWithMacro () throws Exception
    {
        assertStringTemplateEquals("#eval $m using { 'A':1,'B':2,'C':3 }", "1 2 3");
    }

    public void testTempletEval () throws Exception
    {
        String prefix = "Mr.";
        String user = "Jones";
        assertStringTemplateEquals(
        "#templet $t {Dear $prefix $user}\n"
        + "#eval $t using {'prefix':'" + prefix + "', 'user':'" + user + "'}", 
        "Dear " + prefix + " " + user);
    }
    
    public void testEvalRecursive() throws Exception
    {
        assertStringTemplateEquals("#eval $recursive using {'Node':$Tree}", 
        "Title: Root Node\n" +
        "Level 0\n" +
        "Title: Child 1\n" +
        "Level 1\n" +
        "Title: Grandchild 1-1\n" +
        "Level 1\n" +
        "Title: Grandchild 1-2\n" +
        "Level 0\n" +
        "Title: Child 2\n" +
        "Level 1\n" +
        "Title: Grandchild 2-1\n" +
        "Level 1\n" +
        "Title: Grandchild 2-2\n" +
        "Level 1\n" +
        "Title: Grandchild 2-3\n");
    }
    
    /** Convenience class to evaluate a string template as a Macro */
    public class StringMacro implements org.webmacro.Macro {
        private String s = "";
        
        public StringMacro(String text)
        {
            s = text;
        }
        
        public Object evaluate(Context context) throws org.webmacro.PropertyException {
            org.webmacro.engine.StringTemplate t = 
                new org.webmacro.engine.StringTemplate(context.getBroker(), s);
            return t.evaluateAsString(context);
        }

        public void write(org.webmacro.FastWriter fw, Context context)
        {
        }
    }
    
    public class Node {
        private String title;
        private java.util.ArrayList children = new java.util.ArrayList();
        
        public Node(String title){
            this.title = title;
        }    
        public String getTitle(){ return title; }
        public void addChild(Node child){
            children.add(child);
        }
        public java.util.List getChildren(){ return children; }
    }
}
