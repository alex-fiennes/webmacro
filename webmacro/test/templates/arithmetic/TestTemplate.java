import org.webmacro.Context;

public class TestTemplate extends AbstractTemplateEvaluator 
{
    public void stuffContext (Context c) throws Exception
    {
        int[] ints = new int[] { -2, -1, 0, 1, 2 };
        long[] longs = new long[] { -2, -1, 0, 1, 2 };
        short[] shorts = new short[] { -2, -1, 0, 1, 2 };
    
        // fill up the context with our data
        c.put("intn2", new Integer(-2));
        c.put("intn1", new Integer(-1));
        c.put("int0", new Integer(0));
        c.put("int1", new Integer(1));
        c.put("int2", new Integer(2));

        c.put("longn2", new Long((long) -2));
        c.put("longn1", new Long((long) -1));
        c.put("long0", new Long((long) 0));
        c.put("long1", new Long((long) 1));
        c.put("long2", new Long((long) 2));

        c.put("shortn2", new Short((short) -2));
        c.put("shortn1", new Short((short) -1));
        c.put("short0", new Short((short) 0));
        c.put("short1", new Short((short) 1));
        c.put("short2", new Short((short) 2));

        c.put("ints", ints);
        c.put("longs", ints);
        c.put("shorts", ints);
    }
}