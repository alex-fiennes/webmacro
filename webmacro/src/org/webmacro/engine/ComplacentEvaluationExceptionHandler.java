/*
 * Copyright (C) 1998-2001 Semiotek Inc.  All Rights Reserved.  
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted under the terms of either of the following
 * Open Source licenses:
 *
 * The GNU General Public License, version 2, or any later version, as
 * published by the Free Software Foundation
 * (http://www.fsf.org/copyleft/gpl.html);
 *
 *  or 
 *
 * The Semiotek Public License (http://webmacro.org/LICENSE.)  
 *
 * This software is provided "as is", with NO WARRANTY, not even the 
 * implied warranties of fitness to purpose, or merchantability. You
 * assume all risks and liabilities associated with its use.
 *
 * See www.webmacro.org for more information on the WebMacro project.  
 */


/**
 * ComplacentvaluationExceptionHandler
 *
 * An implementation of EvaluationExceptionHandler which simply writes
 * an HTML comment to the output.  Users who are generating non-HTML output
 * should replace the ExceptionHandler in their context with one that
 * generates the appropriate comments.  This should be the only place
 * in WM where HTML comments are generated into the output. 
 *
 * @author Brian Goetz
 * @since 0.96
 */

package org.webmacro.engine;

import org.webmacro.PropertyException;
import org.webmacro.Context;

public class ComplacentEvaluationExceptionHandler 
  implements EvaluationExceptionHandler {

   public void evaluate(Variable variable, 
                        Context context, 
                        Exception problem) 
   throws PropertyException {
      if (problem instanceof PropertyException.NoSuchVariableException
          || problem instanceof PropertyException.NullVariableException
          || problem instanceof PropertyException.NoSuchMethodException
          || problem instanceof PropertyException.NoSuchPropertyException)
         return;
      else if (problem instanceof PropertyException)
         throw (PropertyException) problem;
      else 
         throw new PropertyException("Error evaluating variable " 
                                     + variable.getVariableName() + ": " 
                                     + problem, problem);
   }

   public String expand(Variable variable, 
                        Context context, 
                        Exception problem) 
   throws PropertyException {
      if (problem instanceof PropertyException.NoSuchVariableException) {
         return errorString("Attempt to access nonexistent variable $" 
                            + variable.getVariableName());
      }
      else if (problem instanceof PropertyException)
         return errorString(problem.getMessage());
      else 
         return errorString(variable.getVariableName() + ": " 
                            + problem.toString());
   }


   public String warningString(String warningText) {
      return "<!-- " + warningText + " -->";
   }


   public String errorString(String errorText) {
      return "<!-- " + errorText + " -->";
   }
}

