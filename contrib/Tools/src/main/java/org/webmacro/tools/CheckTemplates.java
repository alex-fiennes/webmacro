package org.webmacro.tools;

import org.webmacro.Broker;
import org.webmacro.WM;
import org.webmacro.engine.BuildContext;
import org.webmacro.engine.Builder;
import org.webmacro.engine.Parser;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Program which uses the WebMacro parser to validate templates.
 *
 * @author Brian Goetz
 */

public class CheckTemplates
{

    private static Parser parser;
    private static Broker broker;

    private CheckTemplates() {}
    
    public static void parseTemplate (String name, Reader in)
    {
        try
        {
            Builder bb = parser.parseBlock(name, in);
            bb.build(new BuildContext(broker));
        }
        catch (Exception e)
        {
            System.err.println("Exception parsing template " + name + "\n" + e);
        }
    }

    public static void main (String[] args) throws Exception
    {
        if (!System.getProperties().containsKey("org.webmacro.LogLevel"))
            System.getProperties().setProperty("org.webmacro.LogLevel", "ERROR");
        WM wm = new WM();
        broker = wm.getBroker();
        parser = (Parser) broker.get("parser", "wm");

        if (args.length == 0)
            parseTemplate("Standard in", new InputStreamReader(System.in));
        else
        {
            for (int i = 0; i < args.length; i++)
                parseTemplate(args[i], new FileReader(args[i]));
        }
        ;
    }
}

