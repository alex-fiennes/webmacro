/*
 * TemplateEvaluatorMain.java
 *
 * Created on March 25, 2001, 5:55 PM
 */

import AbstractTemplateEvaluator;

/**
 * Class with main() method one can use to execute custom TemplateTesters.<p>
 *
 * <pre>
 *  Usage:
 *      java TemplateTesterMain <YourTemplateTester> template1.wm ... templateN.wm
 * </pre>
 *
 * Takes an arbitrary number of template filenames as
 * command-line arguments, and runs each template through the same Context
 * instance, displaying the output of each template as it finishes.
 *
 * @author  e_ridge
 * @version 
 */
public class TemplateEvaluatorMain 
{
    private static final String USAGE = "Usage:\n\tTemplateTesterMain <YourTemplateTester> template1.wm ... templateN.wm";
    
    /**
     * main method<p>
     *
     * Make sure that <code>AbstractTemplateTester</code> and 
     * <code>YourTemplateTester</code> and the templates specified on the 
     * command line are <b>in your classpath</b>!
     *
     * <pre>
     * Usage:  
     *   java TemplateTesterMain <YourTemplateTester> template1.wm ... templateN.wm
     * </pre>
     *
     * @param args command line arguments.  a list of template filenames to run through
     * your tester
     * @throws Exception if something bad happens
     */
    public static void main (String[] args)
    {
        if (args.length < 2)
            die (USAGE);
        
        String[] templateFilenames = new String[args.length-1];
        String className = args[0];
        
        // extract template filenames from arguments
        System.arraycopy (args, 1, templateFilenames, 0, templateFilenames.length);

        try
        {
            // dynamically create the tester 
            AbstractTemplateEvaluator tester = 
                     (AbstractTemplateEvaluator) Class.forName (className)
                                                      .newInstance ();

            // initialize tester
            tester.init ();
            
            // and evaluate/test each template
            // each template is sent to System.out
            // and other information (execution time for each template, 
            // possible exceptions, etc) are sent to System.err
            for (int x=0; x<templateFilenames.length; x++)
            {
               System.err.println ("----- template " + templateFilenames[x] + " -----");
               try
               {
                  tester.evaluate (templateFilenames[x], System.out);
               }
               catch (Exception e)
               {
                  System.err.println (templateFilenames[x] + 
                                      " failed with the following exception:");
                  e.printStackTrace (System.err);
               }
//                 System.err.println ("----- end: " + templateFilenames[x]   +
//                                     " (" + tester.getEvaluationTime (1000) + 
//                                     " seconds) -----");
            }
        }
        catch (Exception e)
        {
            die (e, "Exception while testing templates");
        }
        
        // successful exit
        System.exit (0);
    }
    
    /* something went wrong, so let user know and exit the JVM */
    public static final void die (String message)
    {
        die (null, message);
    }
    
    /* something went way wrong, so let user know, print a stack trace and exit the JVM */
    public static final void die (Exception e, String message)
    {
        if (message != null)
            System.err.println (message);
        
        if (e != null)
            e.printStackTrace ();

        // unsuccessful exit
        System.exit (-1);
    }
}
