package org.webmacro.tools;

import java.util.*;
import java.io.*;

import org.webmacro.*;
import org.webmacro.util.WMEval;

/**
 * EvalTemplates supports an ant task execution to evaluate a lot
 * of templates.
 * <p>
 * This class has been structured to make use of WMEval which maintains
 * an instance of WM and has support methods for evaluating templates
 * and routing output.
 * @author Lane Sharman
 */

public class EvalTemplates {

  private WMEval wm = new WMEval();
  
  /**
   * Evaluates a single template file argument.
   * Exceptions are reported to standard error, not thrown.
   */
  public void run(String inputTemplate) {
    System.out.println("[EvalTemplates] " + inputTemplate); 
    try {
      wm.eval(wm.getNewContext(), inputTemplate, null, false, null); 
    }
    catch (Exception e) {
      System.err.println("[EvalTemplates] Unable to evaluate input. " + 
        e.toString());
    }
  }

  /**
   * Normally this main is invoked from an ant task but it can
   * be invoked from any main launcher including the 
   * a command line.
   * @param args One or more file references to an input template.
   */
  public static void main(String[] args) throws Exception {
    if (args == null) return;
    EvalTemplates ev = new EvalTemplates();
    for (int i=0; i<args.length; i++) { 
      ev.run(args[i]);
    }
  }
}

