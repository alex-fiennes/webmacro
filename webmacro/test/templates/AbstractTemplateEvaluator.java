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
 * Abstract class to easily allow evaluation of a template.<p>
 *
 * Provides common functionality of creating WebMacro, a Context, and 
 *
 * Allows subclasses ability to stuff the context with custom variables.
 *
 * @author  e_ridge
 * @version 
 */
public abstract class AbstractTemplateEvaluator
{
   /** our WebMacro instance */
   protected WebMacro _wm;
       
   /** context to use for each template */
   protected Context _context;
   
   protected long _evalTime = -1;
   
   /**
    * initialize this TemplateTester by creating a WebMacro instance
    * and a default Context.
    */
   public void init () throws Exception
   {
      _wm = createWebMacro ();
      _context = _wm.getContext ();

      // let subclasses stuff the context with custom data
      stuffContext (_context);
   }
   /**
    * create a default-configured instance of WebMacro.  Subclasses may 
    * override if WM needs to be created/configured differently.
    */
   {
      return new WM ();
   }
    * stuff the provided context with custom variables and objects<p>
    *
    * @throws Exception if something goes wrong while stuffing context
    */
   protected abstract void stuffContext (Context context) throws Exception;
  
   /**
    * get the time time it took to evaluate the template in
    * <code>(double)time/units</code>.
    */
   {
      return ((double) _evalTime)/units;
   }
   /**
    * Evaluate and write specified template to a specified output stream.  
    * Output is always encoded in UTF8.<p>
    *
    * <code>evaluate()</code> also does some simple timing of the template 
    * 
    * @throws Exception if a template could not be found, or if some other
    * error occured during template evaluation
    */
   public void evaluate (String templateName, OutputStream out) throws Exception
   {
      long start, stop;
      double seconds;
      String banner;

      FastWriter fw = FastWriter.getInstance (out, "UTF8");

      // get the template from WM
      Template template = _wm.getTemplate (templateName);
                        
      // write the template to the fast writer using our context
      // Notice that all templates use the same context
      start = System.currentTimeMillis ();
      template.write (fw, _context);
      stop = System.currentTimeMillis ();

      // make sure to flush the fast writer.
      // but don't close it, otherwise we'll close System.out, and that
      // would suck
      fw.flush ();

      // print a little banner at the end of the output
      _evalTime = stop - start;
   }    
}