package org.webmacro.tools;

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

public class EvalTemplates {

   private WMEval wm = new WMEval(); // constructs an encapsulation of WM

   /**
    * Evaluates a single template file argument.
    * Exceptions are reported to standard error, not thrown.
    */
   public void run(String inputTemplate) {
      System.out.println("Template File=" + inputTemplate);
      try {
         wm.eval(wm.getNewContext(), inputTemplate, null, false, null);
      }
      catch (Exception e) {
         System.err.println("Unable to evaluate input.");
         e.printStackTrace();
      }
   }

   /**
    * Normally this main is invoked from an ant task but it can
    * be invoked from any main launcher including
    * a command line.
    * @param args One or more file references to an input template file name.
    */
   public static void main(String[] args) throws Exception {
      if (args == null) return;
      EvalTemplates ev = new EvalTemplates();
      for (int i = 0; i < args.length; i++) {
         ev.run(args[i]);
      }
   }
}

