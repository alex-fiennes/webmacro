package org.webmacro.tools;
import java.io.FileOutputStream;
import org.webmacro.Context;
import org.webmacro.util.WMEval;


/**
 * EvalTemplates supports an ant task execution to evaluate a set of
 * templates.
 * <p>
 * This class has been structured to make use of WMEval which maintains
 * an instance of WM and has support methods for evaluating templates
 * and routing output.
 * <p>
 * This is a batch oriented class which is referenced normally as part
 * of an ant build script. See the WebMacro build script for actual usage.
 * @see TemplateEvalAntTask
 * @author Lane Sharman
 */

public class EvalTemplates
{

    private WMEval wm = new WMEval(); // constructs an encapsulation of WM

    /**
     * Evaluates a single template file argument.
     * Exceptions are reported to standard error, not thrown.
     */
    public void run (String inputTemplate)
    {
        System.out.println("Template File=" + inputTemplate);
        try
        {
            wm.eval(wm.getNewContext(), inputTemplate, null, null);
        }
        catch (Exception e)
        {
            System.err.println("Unable to evaluate input.");
            e.printStackTrace();
        }
    }

    /**
     * Evaluates a single template file argument.
     * Exceptions are reported to standard error, not thrown.
     * <p>This method provides programmer control
     * over the evaluation options.
     * @param inputTemplate A template in the resource path.
     * @param outFile An output file for the template output. If
     * null, the template must set the output.
     * @param append If true, output will be appended to existing file.
     * @param encoding The encoding to use on the output file, null allowed.
     */
    public Context run (String inputTemplate, String outFile, boolean append,
                        String encoding)
    {
        try
        {
            wm.eval(wm.getNewContext(), inputTemplate, outFile, append, encoding);
            return wm.getCurrentContext();
        }
        catch (Exception e)
        {
            System.err.println("Unable to evaluate input.");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * After a template has been run, this method
     * will return an object in the context by name.
     */
    public Object getContextElement (String key)
    {
        Context context = wm.getCurrentContext();
        return context.get(key);
    }

    /**
     * Normally this main is invoked from an ant task but it can
     * be invoked from any main launcher including
     * a command line.
     * @param args One or more file references to an input template file name.
     */
    public static void main (String[] args) throws Exception
    {
        if (args == null) return;
        EvalTemplates ev = new EvalTemplates();
        for (int i = 0; i < args.length; i++)
        {
            ev.run(args[i]);
        }
    }
}

