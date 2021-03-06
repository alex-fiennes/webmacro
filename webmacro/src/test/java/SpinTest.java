
import java.io.*;

import org.webmacro.Context;
import org.webmacro.Template;
import org.webmacro.WM;

public class SpinTest
{

    public static void main (String[] args) throws Exception
    {
        long initStart, loopStart, loopEnd;

        String template = args[0];
        int count = Integer.parseInt(args[1]);

        initStart = System.currentTimeMillis();
        WM wm = new WM();

        loopStart = System.currentTimeMillis();

        for (int i = 0; i < count; i++)
        {
            Context c = wm.getContext();

            c.put("One", "1");
            c.put("Two", "2");
            c.put("Three", "3");

            Template t = wm.getTemplate(template);

            t.write(new FileOutputStream("/dev/null"), "US-ASCII", c);
        }

        loopEnd = System.currentTimeMillis();
        System.out.println("Total time: "
                + ((double) (loopEnd - initStart) / 1000)
                + "s, loop time "
                + ((double) (loopEnd - loopStart) / 1000)
                + "s, template time "
                + ((double) (loopEnd - loopStart) / count) + "ms");

    }
}
