package org.webmacro.template;

import java.io.*;

import org.webmacro.*;
import org.webmacro.engine.StringTemplate;

import junit.framework.*;


public abstract class TemplateTestCase extends TestCase {

   /** our WebMacro instance */
   protected WebMacro _wm;
       
   /** context to use for each template */
   protected Context _context;

   public TemplateTestCase(String name) {
      super(name);
   }

   protected void setUp() throws Exception {
      init();
   }
  
   /**
    * create a default-configured instance of WebMacro.  Subclasses may 
    * override if WM needs to be created/configured differently.
    */
   protected WebMacro createWebMacro () throws Exception
   {
      return new WM ();
   }


   /**
    * initialize this TemplateTester by creating a WebMacro instance
    * and a default Context.
    */
   public void init () throws Exception
   {
      System.getProperties().setProperty("org.webmacro.LogLevel", "WARNING");
      _wm = createWebMacro ();
      _context = _wm.getContext ();

      // let subclasses stuff the context with custom data
      stuffContext (_context);
   }
   
   /** 
    * stuff the provided context with custom variables and objects<p>
    *
    * @throws Exception if something goes wrong while stuffing context
    */
   protected abstract void stuffContext (Context context) throws Exception;

  public String executeStringTemplate(String templateText) throws Exception {
    Template template = new StringTemplate(_wm.getBroker(), templateText);
    FastWriter fw = FastWriter.getInstance(_wm.getBroker(), null, "UTF8");
    template.write (fw, _context);
    String output = fw.toString();
    fw.close();
    return output;
  }
}
