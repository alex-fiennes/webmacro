/*
 * AbstractTemplateEvaluator.java
 *
 * Created on March 25, 2001, 6:11 PM
 */

import org.webmacro.WM;
import org.webmacro.WebMacro;
import org.webmacro.Context;
import org.webmacro.Template;
import org.webmacro.FastWriter;

/**
 * Abstract class that all specific template test cases should extend.<p>
 *
 * Provides common functionality of creating WebMacro, a Context, and evaluating
 * an arbitrary number of templates against the Context.<p>
 *
 * Allows subclasses ability to stuff the context with custom variables.
 *
 * @author  e_ridge
 * @version 
 */
public abstract class AbstractTemplateEvaluator
{
    /** array of test templates */
    private String[] _templateFilenames;
    
    /** our WebMacro instance */
    private WebMacro _wm;
    
    /** context to use for each template */
    private Context _context;

    
    /** 
     * stuff the provided context with custom variables and objects<p>
     *
     * @throws Exception if something goes wrong while stuffing context
     */
    public abstract void stuffContext (Context context) throws Exception;
    
    /**
     * initialize this TemplateTester by creating a WebMacro instance
     * and a default Context.
     *
     * @param templateFilenames array of template filenames to run through WM
     * and the Context.
     */
    public void init (String[] templateFilenames) throws Exception
    {
        _wm = new WM ();
        _context = _wm.getContext ();
        _templateFilenames = templateFilenames;

        // let subclasses stuff the context with custom data
        stuffContext (_context);
    }
    
    /**
     * run the list of templates provided during initialization
     * through WM using the same Context for each one.<p>
     *
     * Output is sent to <code>System.out</code>, encoded in UTF8 format.<p>
     *
     * <code>test()</code> also does some simple timing of the template evaluation
     * and outputs this information, along with the template filename, to <b><code>System.err</code></b>,
     * immediately following the output of the template.  This timing information 
     * is sent to System.err so that it can be redirected separately by an automated
     * shell script.
     *
     * @throws Exception if a template could not be found, or if some other
     * error occured during template evaluation
     */
    public void test () throws Exception
    {
        long start, stop;
        double seconds;
        String banner = null;
        
        FastWriter fw = FastWriter.getInstance (System.out, "UTF8");
        for (int x=0; x<_templateFilenames.length; x++)
        {
            // get the next template from WM
            Template template = _wm.getTemplate (_templateFilenames[x]);
            
            start = System.currentTimeMillis ();
            // write the template to the fast writer using our context
            // Notice that all templates use the same context
            template.write (fw, _context);
            stop = System.currentTimeMillis ();

            // make sure to flush the fast writer.
            // but don't close it, otherwise we'll close System.out, and that
            // would suck
            fw.flush ();

            // print a little banner at the end of the output
            seconds = ( ((double) stop - start) / 1000);
            banner = "------" + _templateFilenames[x] + " finished in " + seconds + " seconds --------";
            System.err.println (seconds);
        }
    }    
    
    /**
     * repeat the character <code>c</code> <code>many</code> times in a row
     * and return it as a string.  Doesn't java have something like this built in?
     * I mean, come on, even VisualBasic can do this for you.  Maybe I should be
     * using ASP's instead of servlets and WebMacro.
     */
    private String repeat (char c, int many)
    {
        StringBuffer sb = new StringBuffer ();
        for (int x=0; x<many; x++)
            sb.append (c);
        
        return sb.toString ();
    }
}
